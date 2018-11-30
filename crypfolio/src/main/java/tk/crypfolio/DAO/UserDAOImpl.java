package tk.crypfolio.DAO;

import tk.crypfolio.business.exception.AppDAOException;
import tk.crypfolio.model.UserEntity;

import javax.persistence.EntityManager;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAOImpl extends DAOImpl<Long, UserEntity> implements UserDAO {

    private static final Logger LOGGER = Logger.getLogger(UserDAOImpl.class.getName());

    protected UserDAOImpl(EntityManager em) {
        super(em, UserEntity.class);
    }

    @Override
    public UserEntity getUserById(Long id) {
        return this.getById(id);
    }

    @Override
    public UserEntity getUserByEmail(String usEmail) {
        return this.findByUniqueStringColumn("email", usEmail);
    }

    @Override
    public UserEntity getUserByEmailVerifCode(String usEmailVerifCode) {
        return this.findByUniqueStringColumn("emailVerifCode", usEmailVerifCode);
    }

    @Override
    public UserEntity getUserByResetPasswordCode(String usResetPasswordCode) {
        return this.findByUniqueStringColumn("passwordResetCode", usResetPasswordCode);
    }

    @Override
    public void createUser(UserEntity user) {

        if (getUserByEmail(user.getEmail()) == null) {
            this.create(user);
        }
    }

    @Override
    public UserEntity updateUser(UserEntity user) {

        try {
            return this.update(user);

        } catch (AppDAOException ex) {
            LOGGER.log(Level.WARNING, ex.toString());
        }
        return user;
    }
}