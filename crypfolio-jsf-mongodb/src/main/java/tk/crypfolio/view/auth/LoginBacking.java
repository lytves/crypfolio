package tk.crypfolio.view.auth;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named
@RequestScoped
public class LoginBacking extends AuthBacking {

    public String login() {

        return authenticate();
    }

    public void resendEmail() {

        resendVerifEmail();
    }
}
