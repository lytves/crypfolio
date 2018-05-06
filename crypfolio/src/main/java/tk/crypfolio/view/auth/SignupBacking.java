package tk.crypfolio.view.auth;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named
@RequestScoped
public class SignupBacking extends AuthBacking {

    public String signup(){

        return register();
    }
}
