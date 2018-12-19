package tk.crypfolio.view.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tk.crypfolio.business.UserService;
import tk.crypfolio.model.PortfolioEntity;
import tk.crypfolio.model.UserEntity;
import tk.crypfolio.view.ActiveUser;
import tk.crypfolio.view.UserBacking;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

public abstract class AuthBacking {

    private static final Logger LOGGER = LogManager.getLogger(UserBacking.class);

    // stateless business
    @Inject
    protected UserService userService;
    // session scoped
    @Inject
    private ActiveUser activeUser;

    protected UserEntity user;

    private String password;

    private Boolean showEmailResendLink;

    // (rendered) by default we show signUp form until user submitted form successfully,
    // then is showed email activating notification
    private Boolean showSignUpForm = true;

    @PostConstruct
    public void init() {
        LOGGER.info("AuthBacking init");

        this.user = new UserEntity(new PortfolioEntity());
    }

    protected String authenticate() {

        user = userService.doLoginDB(user, password);

        setPassword(null);

        if (user != null) {

            activeUser.setUser(user);

            if (user.getIsEmailVerified()) {

                return "user?faces-redirect=true";

            } else {

                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                        "Your email address hasn't been confirmed yet.",
                        ""));
                setShowEmailResendLink(true);

            }

        } else {

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Invalid login credentials! Please Try Again.",
                    ""));
        }

        return null;
    }

    protected String register() {

        user = userService.doRegisterDB(user, password);

        if (user != null) {

            activeUser.setUser(user);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "In order to complete your registration, please click the confirmation link" +
                            " in the email that we just have sent to you.",
                    ""));
        } else {

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Invalid operation! May be you are already registered",
                    ""));
        }

        // to remove (do not rendered) signUp form && show (rendered) email activationg notification
        setShowSignUpForm(false);

        return null;
    }

    protected void resendVerifEmail() {

        if (activeUser.isPresent() && !activeUser.getUser().getIsEmailVerified()) {

            userService.sendConfEmailDB(activeUser.getUser());

            setShowEmailResendLink(false);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "Please check your email and click the confirmation link.",
                    ""));
        }
    }

    protected void requestResetPassword() {

        Boolean wasSentResetPasswordEmail = userService.setUserResetPasswordCodeDB(user);

        if (wasSentResetPasswordEmail) {

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "Please check the email we have just sent to you and follow the link to reset your password.",
                    ""));
        } else {

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Cannot reset password! Maybe you are not registered yet.",
                    ""));
        }
    }

    protected String saveNewPassword(String code) {

        if (code != null && password != null) {

            user = userService.setUserNewPasswordDB(code, password);

            if (user != null) {

                activeUser.setUser(user);

                return "user?faces-redirect=true";
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Cannot save new password!",
                ""));

        return null;
    }

    public UserEntity getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getShowEmailResendLink() {
        return showEmailResendLink;
    }

    public void setShowEmailResendLink(Boolean showEmailResendLink) {
        this.showEmailResendLink = showEmailResendLink;
    }

    public Boolean getShowSignUpForm() {
        return showSignUpForm;
    }

    public void setShowSignUpForm(Boolean showSignUpForm) {
        this.showSignUpForm = showSignUpForm;
    }
}