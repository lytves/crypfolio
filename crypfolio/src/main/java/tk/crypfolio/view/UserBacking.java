package tk.crypfolio.view;

import tk.crypfolio.business.ApplicationContainer;
import tk.crypfolio.business.UserService;
import tk.crypfolio.model.UserEntity;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named
@ViewScoped
public class UserBacking implements Serializable {

    // session scoped
    @Inject
    private ActiveUser activeUser;

    // application scoped
    @Inject
    private ApplicationContainer applicationContainer;

    // stateless business
    @Inject
    private UserService userService;

    private UserEntity user;

    @PostConstruct
    private void init(){

        this.user = activeUser.getUser();

    }

    public UserEntity getUser() {
        return user;
    }

    public ApplicationContainer getApplicationContainer() {
        return applicationContainer;
    }
}
