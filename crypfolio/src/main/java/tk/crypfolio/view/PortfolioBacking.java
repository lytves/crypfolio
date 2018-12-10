package tk.crypfolio.view;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import tk.crypfolio.business.ApplicationContainer;
import tk.crypfolio.business.PortfolioService;
import tk.crypfolio.common.Constants;
import tk.crypfolio.common.CurrencyType;
import tk.crypfolio.model.CoinEntity;
import tk.crypfolio.model.ItemEntity;
import tk.crypfolio.util.MathRounders;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Named
@ViewScoped
public class PortfolioBacking implements Serializable {

    private static final Logger LOGGER = LogManager.getLogger(PortfolioBacking.class);

    // application scoped
    @Inject
    private ApplicationContainer applicationContainer;

    // session scoped
    @Inject
    private ActiveUser activeUser;

    // stateless business
    @Inject
    private PortfolioService portfolioService;

    // it's used in p:selectOneMenu form
    private CurrencyType[] currencies;

    // is used in jsf-views p:datatable, because table sorting is worked
    // only with bean's parameters and not method call
    private List<ItemEntity> archivedItems;
    private List<ItemEntity> notArchivedItems;

    @PostConstruct
    public void init() {
        LOGGER.info("PortfolioBacking @PostConstruct");

        setArchivedItems(loadArchivedAndNotArchivedItems("archived"));
        setNotArchivedItems(loadArchivedAndNotArchivedItems("notArchived"));
    }

    @PreDestroy
    public void destroy() {
        LOGGER.info("PortfolioBacking @PreDestroy");
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

    public CurrencyType[] getCurrencies() {
        return CurrencyType.values();
    }

    public List<ItemEntity> getArchivedItems() {
        return archivedItems;
    }

    public void setArchivedItems(List<ItemEntity> archivedItems) {
        this.archivedItems = archivedItems;
    }

    public List<ItemEntity> getNotArchivedItems() {
        return notArchivedItems;
    }

    public void setNotArchivedItems(List<ItemEntity> notArchivedItems) {
        this.notArchivedItems = notArchivedItems;
    }

    /*
     * * * * * * * * * * * * * * * * * * * * * Bean's methods * * * * * * * * * * * * * * * * * * * * *
     * */
    public void updatePortfolioValues() {
        LOGGER.info("updatePortfolioValues...");

        // is used only to save changed Portfolio showerCurrency
        activeUser.setPortfolio(portfolioService.updatePortfolioDB(activeUser.getUser().getPortfolio()));
    }

    public BigDecimal countMarketValue() {

        BigDecimal portfolioMarketValue = BigDecimal.ZERO;

        for (ItemEntity item : activeUser.getUser().getPortfolio().getItems()) {

            portfolioMarketValue = portfolioMarketValue
                    .add(getItemMarketValue(item, activeUser.getUser().getPortfolio().getShowedCurrency()))
                    .setScale(8, BigDecimal.ROUND_HALF_DOWN);
        }
        return MathRounders.roundBigDecimalByCurrency(portfolioMarketValue, activeUser.getUser().getPortfolio().getShowedCurrency());
    }

    public BigDecimal countProfit() {

        try {

            return MathRounders.roundBigDecimalByCurrency(countMarketValue().subtract(
                    activeUser.getUser().getPortfolio().getNetCostByCurrentCurrency()), activeUser.getUser().getPortfolio().getShowedCurrency());

        } catch (NullPointerException ex) {
            LOGGER.warn(ex.toString());
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getProfitPercentage() {

        try {

            return MathRounders.roundBigDecimalToTwoDecimal(countProfit().multiply(new BigDecimal("100"))
                    .divide(activeUser.getUser().getPortfolio().getNetCostByCurrentCurrency(), 8, BigDecimal.ROUND_HALF_DOWN));

        } catch (ArithmeticException | NullPointerException ex) {
            LOGGER.warn(ex.toString());
        }
        return BigDecimal.ZERO;
    }

    /**
     * Method which returns BigDecimal value of item's coin total current Market Value
     *
     * @param item - current ItemEntity
     * @return BigDecimal value to be showed and/or get sorted in the column: total market value or 0
     */
    public BigDecimal getItemMarketValue(@NotNull ItemEntity item) {

        // this condition is used to avoid some use cases when we still don't have
        // a coin assigned to the item, e.g. to load item detail modal window!!!
        if (item != null && item.getCoin() != null) {

            try {

                BigDecimal coinMarketPrice = getCoinPrice(item.getCoin(), item.getShowedCurrency());

                return item.getAmount().multiply(coinMarketPrice).setScale(8, BigDecimal.ROUND_HALF_DOWN);

            } catch (NullPointerException ex) {
                LOGGER.warn(ex.toString());
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * Method which returns BigDecimal value of item's coin total current Market Value
     * is used to count portfolio Market Value
     *
     * @param item         - current ItemEntity
     * @param currencyType
     * @return BigDecimal value to be showed and/or get sorted in the column: total market value or 0
     */
    public BigDecimal getItemMarketValue(@NotNull ItemEntity item, CurrencyType currencyType) {

        // this condition is used to avoid some use cases when we still don't have
        // a coin assigned to the item, e.g. to load item detail modal window!!!
        if (item != null && item.getCoin() != null) {

            try {

                BigDecimal coinMarketPrice = getCoinPrice(item.getCoin(), currencyType);

                return item.getAmount().multiply(coinMarketPrice).setScale(8, BigDecimal.ROUND_HALF_DOWN);

            } catch (NullPointerException ex) {
                LOGGER.warn(ex.toString());
            }
        }

        return BigDecimal.ZERO;
    }

    /**
     * Method which returns BigDecimal value of coin actual Market Price
     *
     * @param coin           - specified CoinEntity
     * @param showedCurrency - specified сurrency to show the coin's price
     * @return BigDecimal coin price value or 0
     */
    public BigDecimal getCoinPrice(@NotNull CoinEntity coin, @NotNull CurrencyType showedCurrency) {

/*        switch (showedCurrency.getCurrency()) {

            case "USD":
                return new BigDecimal("1");

            case "EUR":
                return new BigDecimal("2");

            case "BTC":
                return new BigDecimal("3");

            case "ETH":
                return new BigDecimal("4");

        }
        return BigDecimal.ZERO;*/

        try {

            Double coinPrice = coinCurrentData("price", coin.getId(), showedCurrency);
            return BigDecimal.valueOf(coinPrice);

        } catch (NullPointerException ex) {
            LOGGER.warn(ex.toString());
        }
        return BigDecimal.ZERO;
    }

    /**
     * Method which returns String value of percentage of coin price changed by "percent_change_24hour"
     *
     * @param item - current itemEntity
     * @return String value to show in the column: percentage or 0
     */
    public String getCoinChange24H(@NotNull ItemEntity item) {

        // this conditional is using to avoid "null" values on the add/delete coins,
        // because it provokes NullPointerException
        // && this condition is used to avoid some use cases when we still don't have
        // a coin assigned to the item, e.g. to load item detail modal window!!!
        if (item != null && item.getCoin() != null) {

            Double change24h = coinCurrentData("percent_change_24h", item.getCoin().getId(), item.getShowedCurrency());

            if (change24h != null) {

                return String.valueOf(MathRounders.roundDoubleToTwoDecimal(change24h));
            }
        }
        return "0";
    }

    public BigDecimal getItemProfit(@NotNull ItemEntity item) {

        BigDecimal profit = BigDecimal.ZERO;

        // this condition is used to avoid some use cases when we still don't have
        // a coin assigned to the item, e.g. to load item detail modal window!!!
        if (item != null && item.getCoin() != null) {

            switch (item.getShowedCurrency().getCurrency()) {

                case "USD":

                    profit = (getCoinPrice(item.getCoin(), item.getShowedCurrency()).multiply(
                            item.getAmount())).subtract(item.getNetCostUsd()).setScale(8, BigDecimal.ROUND_HALF_DOWN);
                    break;

                case "EUR":

                    profit = (getCoinPrice(item.getCoin(), item.getShowedCurrency()).multiply(
                            item.getAmount())).subtract(item.getNetCostEur()).setScale(8, BigDecimal.ROUND_HALF_DOWN);
                    break;

                case "BTC":

                    profit = (getCoinPrice(item.getCoin(), item.getShowedCurrency()).multiply(
                            item.getAmount())).subtract(item.getNetCostBtc()).setScale(8, BigDecimal.ROUND_HALF_DOWN);
                    break;

                case "ETH":

                    profit = (getCoinPrice(item.getCoin(), item.getShowedCurrency()).multiply(
                            item.getAmount())).subtract(item.getNetCostEth()).setScale(8, BigDecimal.ROUND_HALF_DOWN);
                    break;
            }
        }
        return profit;
    }

    public String getItemProfitForSortBy(@NotNull ItemEntity item) {

        return String.valueOf(getItemProfit(item));

    }

    public String getItemProfitPercentage(@NotNull ItemEntity item) {

        BigDecimal profitPercentage = BigDecimal.ZERO;

        try {

            switch (item.getShowedCurrency().getCurrency()) {

                case "USD":

                    profitPercentage = getItemProfit(item).multiply(new BigDecimal("100")
                            .divide(item.getNetCostUsd(), 8, BigDecimal.ROUND_HALF_DOWN));
                    break;

                case "EUR":

                    profitPercentage = getItemProfit(item).multiply(new BigDecimal("100")
                            .divide(item.getNetCostEur(), 8, BigDecimal.ROUND_HALF_DOWN));
                    break;

                case "BTC":

                    profitPercentage = getItemProfit(item).multiply(new BigDecimal("100")
                            .divide(item.getNetCostBtc(), 8, BigDecimal.ROUND_HALF_DOWN));
                    break;

                case "ETH":

                    profitPercentage = getItemProfit(item).multiply(new BigDecimal("100")
                            .divide(item.getNetCostEth(), 8, BigDecimal.ROUND_HALF_DOWN));
                    break;
            }

        } catch (ArithmeticException | NullPointerException ex) {
            LOGGER.warn(ex.toString());
        }

        return String.valueOf(MathRounders.roundBigDecimalToTwoDecimal(profitPercentage));
    }

    public String getItemSharePercentage(@NotNull ItemEntity item) {

        BigDecimal sharePercentage = BigDecimal.ZERO;

        try {

            sharePercentage = getItemMarketValue(item, activeUser.getUser().getPortfolio().getShowedCurrency())
                    .multiply(new BigDecimal("100")).divide(countMarketValue(), 8, BigDecimal.ROUND_HALF_DOWN);

        } catch (NullPointerException | ArithmeticException ex) {
            LOGGER.warn(ex.toString());
        }
        return String.valueOf(MathRounders.roundBigDecimalToTwoDecimal(sharePercentage));

    }

    /**
     * Universal method to do BigDecimal rounding to use it in jsf-view
     * (identical one is in WatchlistBacking)
     */
    public String roundingForView(BigDecimal value, CurrencyType currencyType) {

        String pattern = "###,###.########";
        DecimalFormat df = new DecimalFormat(pattern, new DecimalFormatSymbols(Constants.mainLocale));

        return df.format(MathRounders.roundBigDecimalByCurrency(value, currencyType).stripTrailingZeros());

//        return MathRounders.roundBigDecimalByCurrency(value, currencyType).stripTrailingZeros().toPlainString();
    }

    private List<ItemEntity> loadArchivedAndNotArchivedItems(String itemsType) {

        LOGGER.info("loadArchivedAndNotArchivedItems " + itemsType);

        List<ItemEntity> notArchivedItems = new ArrayList<>();

        if (("archived").equals(itemsType)) {
            for (ItemEntity item : activeUser.getUser().getPortfolio().getItems()) {

                if (item.getArchived()) {
                    notArchivedItems.add(item);
                }
            }
        } else if (("notArchived").equals(itemsType)) {
            for (ItemEntity item : activeUser.getUser().getPortfolio().getItems()) {

                if (!item.getArchived()) {
                    notArchivedItems.add(item);
                }
            }
        }
        return notArchivedItems;
    }

    /**
     * Universal method which returns actual coin value by type(data) from @ApplicationScoped bean
     * (identical to WatchlistBacking)
     */
    private Double coinCurrentData(String data, Long id, @NotNull CurrencyType showedCurrency) {

        switch (showedCurrency.getCurrency()) {

            case "USD":

                for (Map.Entry<Long, Map<String, Double>> entry : applicationContainer.getAllCoinsByTickerInUsd().entrySet()) {

                    Long key = entry.getKey();
                    Map<String, Double> value = entry.getValue();

                    if (key.equals(id)) {
                        return value.get(data);
                    }
                }
                break;

            case "EUR":

                for (Map.Entry<Long, Map<String, Double>> entry : applicationContainer.getAllCoinsByTickerInEur().entrySet()) {

                    Long key = entry.getKey();
                    Map<String, Double> value = entry.getValue();

                    if (id.equals(key)) {
                        return value.get(data);
                    }

                }
                break;

            case "BTC":

                for (Map.Entry<Long, Map<String, Double>> entry : applicationContainer.getAllCoinsByTickerInBtc().entrySet()) {

                    Long key = entry.getKey();
                    Map<String, Double> value = entry.getValue();

                    if (id.equals(key)) {
                        return value.get(data);
                    }
                }
                break;

            case "ETH":

                for (Map.Entry<Long, Map<String, Double>> entry : applicationContainer.getAllCoinsByTickerInEth().entrySet()) {

                    Long key = entry.getKey();
                    Map<String, Double> value = entry.getValue();

                    if (id.equals(key)) {
                        return value.get(data);
                    }
                }
                break;
        }
        return 0.0;
    }

    // to sort dataTable columns elements correct in order to numbers (identical to WatchlistBacking)
    public int sortByModel(Object obj1, Object obj2) {

        try {

            Double id1 = Double.parseDouble((String) obj1);
            Double id2 = Double.parseDouble((String) obj2);

            if (id1 < id2) {
                return -1;
            } else if (id1.equals(id2)) {
                return 0;
            } else {
                return 1;
            }

        } catch (NumberFormatException ex) {
            LOGGER.warn(ex.toString());
        }
        return 0;
    }
}