package tk.crypfolio.view;

import org.apache.logging.log4j.LogManager;
import tk.crypfolio.model.PortfolioEntity;
import tk.crypfolio.model.UserEntity;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
public class ActiveUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(ActiveUser.class);

    private UserEntity user;

    public void setUser(UserEntity user) {
        this.user = user;
        LOGGER.info("activeUser.user: " + user.getPortfolio().toString());
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

    public void setPortfolio(PortfolioEntity portfolio) {
        getUser().setPortfolio(portfolio);
    }
}