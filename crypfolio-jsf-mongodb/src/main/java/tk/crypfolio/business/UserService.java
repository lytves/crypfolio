package tk.crypfolio.business;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tk.crypfolio.DAO.AbstractDAOFactory;
import tk.crypfolio.DAO.UserDAO;
import tk.crypfolio.common.Settings;
import tk.crypfolio.common.SettingsDB;
import tk.crypfolio.model.UserEntity;
import tk.crypfolio.util.CodeGenerator;
import tk.crypfolio.util.EmailSender;
import tk.crypfolio.util.StringEncoder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class UserService implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LogManager.getLogger(UserService.class);

    private UserDAO getUserDAO() {
        LOGGER.info("UserService getUserDAO()");
        AbstractDAOFactory myFactory = AbstractDAOFactory.getDAOFactory(SettingsDB.APP_DB_TYPE);
        return myFactory.getUserDAO();
    }

    /*
     * user login
     * */
    public UserEntity doLoginDB(UserEntity user, String password) {

        UserEntity userDB = getUserDAO().getUserByEmail(user.getEmail());

        // look that there is a userDB with the same email and its encoded password is correct
        if (userDB != null && StringEncoder.encodePassword(user.getEmail(), password).equals(userDB.getPassword())) {
            return userDB;
        }
        return null;
    }

    /*
     * new user registration
     * */
    public UserEntity doRegisterDB(UserEntity user, String password) {

        // complete user with email verification code
        // and crypt the password before persist user into DB
        user.setEmailVerifCode(CodeGenerator.generateCodeUUID());
        user.setPassword(StringEncoder.encodePassword(user.getEmail(), password));

        getUserDAO().createUser(user);

        if (user.getId() != null) {

            EmailSender.sendConfirmationEmail(user.getEmail(), user.getEmailVerifCode());
            return user;
        }
        return null;
    }

    /*
     * resend to user new verification email
     * */
    public void sendConfEmailDB(UserEntity user) {

        user.setEmailVerifCode(CodeGenerator.generateCodeUUID());
        user.setEmailVerifCodeRequestDateTime(LocalDateTime.now());

        UserEntity userDB = getUserDAO().updateUser(user);

        if (userDB != null) {

            EmailSender.sendConfirmationEmail(userDB.getEmail(), userDB.getEmailVerifCode());
        }

    }

    /*
     * confirm user's email by URL confirmation link
     * */
    public UserEntity doConfirmEmailDB(String verificationCode) {

        UserEntity userDB = getUserDAO().getUserByEmailVerifCode(verificationCode);

        if (userDB != null && !userDB.getIsEmailVerified() && (ChronoUnit.SECONDS.between(
                userDB.getEmailVerifCodeRequestDateTime(), LocalDateTime.now()) < Settings.EMAIL_VERIFICATION_LINK_TIMELIFE_SECONDS)) {

            userDB.setIsEmailVerified(true);
            userDB.setEmailVerifCode(null);
            userDB.setEmailVerifCodeRequestDateTime(null);

            userDB = getUserDAO().updateUser(userDB);

            if (userDB != null) {

                return userDB;
            }
        }
        return null;
    }

    /*
     * reset user password and send an email to user
     * */
    public Boolean setUserResetPasswordCodeDB(UserEntity user) {

        UserEntity userDB = getUserDAO().getUserByEmail(user.getEmail());

        if (userDB != null) {

            userDB.setPasswordResetCode(CodeGenerator.generateCodeUUID());
            userDB.setPasswordResetCodeRequestDateTime(LocalDateTime.now());

            userDB = getUserDAO().updateUser(userDB);

            if (userDB != null) {

                EmailSender.sendResetPasswordEmail(userDB.getEmail(), userDB.getPasswordResetCode());

                return true;
            }
        }

        return false;
    }

    /*
     * is used in ResetPasswordFilter, to check reset password code
     * */
    public UserEntity searchUserResetPasswordCodeDB(String passwordResetCode) {

        UserEntity userDB = getUserDAO().getUserByResetPasswordCode(passwordResetCode);

        if (userDB != null && ChronoUnit.SECONDS.between(
                userDB.getPasswordResetCodeRequestDateTime(), LocalDateTime.now()) < Settings.PASSWORD_RESTORE_LINK_TIMELIFE_SECONDS) {

            return userDB;
        }
        return null;
    }

    /*
     * set new user password, with checking old password
     * */
    public UserEntity setUserNewPasswordDB(UserEntity user, String oldPassword, String newPassword) {

        UserEntity userDB = getUserDAO().getUserById(user.getId());

        if (userDB != null && StringEncoder.encodePassword(user.getEmail(), oldPassword).equals(userDB.getPassword())) {

            userDB.setPassword(StringEncoder.encodePassword(userDB.getEmail(), newPassword));

            userDB = getUserDAO().updateUser(userDB);

            if (userDB != null) {

                return userDB;
            }
        }
        return null;
    }

    /*
     * set new user password after reset password and using email reset password code
     * */
    public UserEntity setUserNewPasswordDB(String resetPasswordCode, String password) {

        UserEntity userDB = getUserDAO().getUserByResetPasswordCode(resetPasswordCode);

        if (userDB != null && ChronoUnit.SECONDS.between(
                userDB.getPasswordResetCodeRequestDateTime(), LocalDateTime.now()) < Settings.PASSWORD_RESTORE_LINK_TIMELIFE_SECONDS) {

            userDB.setPassword(StringEncoder.encodePassword(userDB.getEmail(), password));
            userDB.setIsEmailVerified(true);
            userDB.setEmailVerifCode(null);
            userDB.setEmailVerifCodeRequestDateTime(null);
            userDB.setPasswordResetCode(null);
            userDB.setPasswordResetCodeRequestDateTime(null);

            userDB = getUserDAO().updateUser(userDB);

            if (userDB != null) {

                return userDB;
            }
        }
        return null;
    }

    /*
     * update user in DB
     * */
    public UserEntity updateUserDB(UserEntity user) {

        return getUserDAO().updateUser(user);

    }
}