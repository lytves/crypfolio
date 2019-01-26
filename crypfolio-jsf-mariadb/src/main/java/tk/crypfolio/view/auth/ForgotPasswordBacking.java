package tk.crypfolio.view.auth;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named
@RequestScoped
public class ForgotPasswordBacking extends AuthBacking {

    public void sendRequestResetPassword() {

        requestResetPassword();
    }

    public String sendSaveNewPassword(String code){

        return saveNewPassword(code);
    }
}
