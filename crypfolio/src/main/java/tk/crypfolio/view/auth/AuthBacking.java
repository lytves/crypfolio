package tk.crypfolio.view.auth;

import tk.crypfolio.business.UserService;
import tk.crypfolio.model.PortfolioEntity;
import tk.crypfolio.model.UserEntity;
import tk.crypfolio.view.ActiveUser;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

public abstract class AuthBacking {

    protected UserEntity user;

    private String password;

    private PortfolioEntity portfolio;

    private Boolean showEmailResendLink;

    private Boolean showSignUpForm;

    // session scoped
    @Inject
    private ActiveUser activeUser;

    // stateless business
    @Inject
    protected UserService userService;

    @PostConstruct
    public void init() {

        System.out.println("AuthBacking init");

        this.user = new UserEntity();
        this.portfolio = new PortfolioEntity();
        setShowSignUpForm(true);
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

        // complete user entity with portfolio.name
        user.setPortfolio(portfolio);

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

        // to remove signup form && show email-activate message
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

        System.out.println("code:" + code);
        System.out.println("password:" + password);

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

    public PortfolioEntity getPortfolio() {
        return portfolio;
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
