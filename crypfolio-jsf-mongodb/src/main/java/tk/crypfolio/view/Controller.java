package tk.crypfolio.view;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@RequestScoped
public class Controller {

    // session managed bean
    @Inject
    private ActiveUser activeUser;

    /*
     * Methods to check that user can access to jsf pages
     * */
    public String allowLoadLoginPage() {

        if (activeUser.isPresent() && activeUser.getUser().getIsEmailVerified()) {
            return "user?faces-redirect=true";
        }

        return null;
    }

    public String allowLoadUserPage() {

        if (!activeUser.isPresent() || !activeUser.getUser().getIsEmailVerified()) {
            return "login?faces-redirect=true";
        }

        return null;
    }

    public String allowLoadResetPage() {

        if (activeUser.isPresent() && activeUser.getUser().getIsEmailVerified()) {
            return "user?faces-redirect=true";
        }
        return null;
    }
}
