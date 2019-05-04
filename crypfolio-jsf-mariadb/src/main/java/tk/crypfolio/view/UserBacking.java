package tk.crypfolio.view;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tk.crypfolio.business.PortfolioService;
import tk.crypfolio.business.UserService;
import tk.crypfolio.model.UserEntity;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named
@ViewScoped
public class UserBacking implements Serializable {

    private static final Logger LOGGER = LogManager.getLogger(UserBacking.class);

    @Inject
    FacesContext facesContext;

    // session scoped
    @Inject
    private ActiveUser activeUser;

    // stateless business
    @Inject
    private UserService userService;

    // stateless business
    @Inject
    private PortfolioService portfolioService;

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

                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "New password has been saved successfully!",
                        ""));
            } else {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Error on change password! Make sure you entered correct old password",
                        ""));
            }
            return;
        }
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Can't save new password!",
                ""));
    }

    public void doSubmitPortfolioNameEdit(){
        LOGGER.info("UserBacking.doSubmitPortfolioNameEdit");

        activeUser.setPortfolio(portfolioService.updatePortfolioDB(activeUser.getUser().getPortfolio()));
    }
    public void doSubmitSharePortfolioSettings() {
        LOGGER.info("UserBacking.doSubmitSavePortfolioSettings");

        activeUser.setPortfolio(portfolioService.updatePortfolioDB(activeUser.getUser().getPortfolio()));
    }

    public void doSubmitShowHoldingsAmountsSettings() {
        LOGGER.info("UserBacking.doSubmitSavePortfolioSettings");

        activeUser.setPortfolio(portfolioService.updatePortfolioDB(activeUser.getUser().getPortfolio()));
    }

    public void clipboardListener() {
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Portfolio sharing link has been copied to clipboard!",
                ""));
    }

    public String getPortfolioSharingLink() {
        ExternalContext ectx = facesContext.getExternalContext();

        return ectx.getRequestScheme()
                + "://" + ectx.getRequestServerName()
                + ":" + ectx.getRequestServerPort()
                + ectx.getRequestContextPath()
                + "/" + activeUser.getUser().getPortfolio().getShareLink();
    }
}