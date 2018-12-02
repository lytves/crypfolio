package tk.crypfolio.view;

import tk.crypfolio.model.PortfolioEntity;
import tk.crypfolio.model.UserEntity;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.logging.Logger;

@Named
@SessionScoped
public class ActiveUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(ActiveUser.class.getName());

    private UserEntity user;

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public UserEntity getUser() {
        return this.user;
    }

    public Long getId() {
        return isPresent() ? user.getId() : null;
    }

    public boolean isPresent() {
        return getUser() != null;
    }

    public void setPortfolio(PortfolioEntity portfolio){
        getUser().setPortfolio(portfolio);
    }
}