package tk.crypfolio.view.auth;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

@Named
@RequestScoped
public class LogoutBacking extends AuthBacking {

    public String logout() {

        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();

        return "login?faces-redirect=true";
    }
}
