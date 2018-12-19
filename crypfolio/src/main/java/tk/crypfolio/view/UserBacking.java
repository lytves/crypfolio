package tk.crypfolio.view;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tk.crypfolio.business.UserService;
import tk.crypfolio.model.UserEntity;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named
@ViewScoped
public class UserBacking implements Serializable {

    private static final Logger LOGGER = LogManager.getLogger(UserBacking.class);

    // session scoped
    @Inject
    private ActiveUser activeUser;

    // stateless business
    @Inject
    private UserService userService;

    private String newPassword;

    private String oldPassword;

    @PostConstruct
    private void init() {
        LOGGER.info("UserBacking.init()");
    }

    /*
     * * * * * * * * * * * * * * * * * * * * * Setters and Getters * * * * * * * * * * * * * * * * * * * * *
     * */
    public ActiveUser getActiveUser() {
        return activeUser;
    }

    public void setActiveUser(ActiveUser activeUser) {
        this.activeUser = activeUser;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    /*
     * * * * * * * * * * * * * * * * * * * * * Bean's methods * * * * * * * * * * * * * * * * * * * * *
     * */
    public void settingsFormReset() {
        LOGGER.info("UserBacking.watchCoinAddFormReset");
        // executing every time when settings modal windows is closed
        setNewPassword(null);
        setOldPassword(null);
    }

    public void doSubmitSaveUserSettings() {
        LOGGER.info("UserBacking.doSubmitSaveUserSettings");

        if (oldPassword != null && newPassword != null) {

            UserEntity user = userService.setUserNewPasswordDB(activeUser.getUser(), oldPassword, newPassword);

            if (user != null) {

                activeUser.setUser(user);

                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "New password has been saved successfully!",
                        ""));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Error on change password! Make sure you entered correct old password",
                        ""));
            }
            return;
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Can't save new password!",
                ""));
    }

    public void doSubmitSavePortfolioSettings() {
        LOGGER.info("UserBacking.doSubmitSavePortfolioSettings");

    }
}