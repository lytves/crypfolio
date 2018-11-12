package tk.crypfolio.view;

import tk.crypfolio.business.ApplicationContainer;
import tk.crypfolio.business.UserService;
import tk.crypfolio.common.CurrencyType;
import tk.crypfolio.model.CoinEntity;
import tk.crypfolio.model.PositionEntity;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Named
@ViewScoped
public class PortfolioBacking implements Serializable {

    private static final Logger logger = Logger.getLogger(WatchlistBacking.class.getName());

    // application scoped
    @Inject
    private ApplicationContainer applicationContainer;

    // session scoped
    @Inject
    private ActiveUser activeUser;

    // stateless business
    @Inject
    private UserService userService;

    private CurrencyType[] currencies;

    private CoinEntity coinTemp;

    private PositionEntity positionTemp;

    private BigDecimal positionPriceTemp;

    private BigDecimal positionTotalTemp;

    private String positionComment;

    public void test(){
        System.out.println("coinTemp " + getCoinTemp().toString());
    }

    @PostConstruct
    public void init(){
        System.out.println("PortfolioBaking init");
        this.positionTemp = new PositionEntity();
        System.out.println("positionTemp " + getPositionTemp().getBoughtCurrency());

    }

    @PreDestroy
    public void destroy(){
        System.out.println("PortfolioBaking destroy");
        // executing every time when add-item modal window is closed
        setCoinTemp(null);
        setPositionTemp(null);
    }

    public ActiveUser getActiveUser() {
        return activeUser;
    }

    public void setActiveUser(ActiveUser activeUser) {
        this.activeUser = activeUser;
    }

    public CurrencyType[] getCurrencies() {
        return CurrencyType.values();
    }

    public CoinEntity getCoinTemp() {
        return coinTemp;
    }

    public void setCoinTemp(CoinEntity coinTemp) {
        this.coinTemp = coinTemp;
    }

    public PositionEntity getPositionTemp() {
        return positionTemp;
    }

    public void setPositionTemp(PositionEntity positionTemp) {
        this.positionTemp = positionTemp;
    }

    public BigDecimal getPositionPriceTemp() {
        return positionPriceTemp;
    }

    public void setPositionPriceTemp(BigDecimal positionPriceTemp) {
        this.positionPriceTemp = positionPriceTemp;
    }

    public BigDecimal getPositionTotalTemp() {
        return positionTotalTemp;
    }

    public void setPositionTotalTemp(BigDecimal positionTotalTemp) {
        this.positionTotalTemp = positionTotalTemp;
    }

    public String getPositionComment() {
        return positionComment;
    }

    public void setPositionComment(String positionComment) {
        this.positionComment = positionComment;
    }

    // autocomplete search method
    public List<CoinEntity> completeCoinTemp(String query){
        List<CoinEntity> filteredCoins = new ArrayList<>();

        for (CoinEntity coin : applicationContainer.getAllCoinsListing()) {

            if (coin.getName().toLowerCase().startsWith(query.toLowerCase().trim())
                    || coin.getSymbol().toLowerCase().startsWith(query.toLowerCase().trim())) {
                filteredCoins.add(coin);
            }
        }
        return filteredCoins;
    }

    // TODO to implement portfolio archive
    public void doSubmitMoveCoinToArchive(){

    }

}