package tk.crypfolio.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import tk.crypfolio.common.CurrencyType;
import tk.crypfolio.common.TransactionType;
import tk.crypfolio.util.MathRounders;

import javax.persistence.*;
import javax.validation.constraints.DecimalMax;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "items")
public class ItemEntity implements Serializable {

    private static final Logger LOGGER = LogManager.getLogger(ItemEntity.class);

    @Id
    @Column(name = "item_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DecimalMax("999999999999.99999999")
    @Column(name = "item_amount", precision = 20, scale = 8, nullable = false)
    private BigDecimal amount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_showed_currency", nullable = false)
    private CurrencyType showedCurrency = CurrencyType.USD;

    @DecimalMax("999999999999.99999999")
    @Column(name = "item_net_cost_usd", precision = 20, scale = 8, nullable = false)
    private BigDecimal netCostUsd = BigDecimal.ZERO;

    @DecimalMax("999999999999.99999999")
    @Column(name = "item_net_cost_eur", precision = 20, scale = 8, nullable = false)
    private BigDecimal netCostEur = BigDecimal.ZERO;

    @DecimalMax("999999999999.99999999")
    @Column(name = "item_net_cost_btc", precision = 20, scale = 8, nullable = false)
    private BigDecimal netCostBtc = BigDecimal.ZERO;

    @DecimalMax("999999999999.99999999")
    @Column(name = "item_net_cost_eth", precision = 20, scale = 8, nullable = false)
    private BigDecimal netCostEth = BigDecimal.ZERO;

    @Column(name = "item_is_archived", nullable = false)
    private Boolean isArchived = false;

    @DecimalMax("999999999999.99999999")
    @Column(name = "item_average_bought_price_usd", precision = 20, scale = 8, nullable = false)
    private BigDecimal averageBoughtPriceUsd = BigDecimal.ZERO;

    @DecimalMax("999999999999.99999999")
    @Column(name = "item_average_bought_price_eur", precision = 20, scale = 8, nullable = false)
    private BigDecimal averageBoughtPriceEur = BigDecimal.ZERO;

    @DecimalMax("999999999999.99999999")
    @Column(name = "item_average_bought_price_btc", precision = 20, scale = 8, nullable = false)
    private BigDecimal averageBoughtPriceBtc = BigDecimal.ZERO;

    @DecimalMax("999999999999.99999999")
    @Column(name = "item_average_bought_price_eth", precision = 20, scale = 8, nullable = false)
    private BigDecimal averageBoughtPriceEth = BigDecimal.ZERO;

    // @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL) - causes deletion of all items on delete one transaction!
    @ManyToOne
    @JoinColumn(name = "portfolios_users_us_id", referencedColumnName = "users_us_id", nullable = false)
    private PortfolioEntity portfolio;

    @ManyToOne
    @JoinColumn(name = "coins_coin_id", referencedColumnName = "coin_id", nullable = false)
    private CoinEntity coin;

    @OneToMany(mappedBy = "item", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<TransactionEntity> transactions = new ArrayList<>();

    public ItemEntity() {
    }

    public ItemEntity(CoinEntity coin) {
        this.coin = coin;
    }

    public void addTransaction(TransactionEntity transaction) {

        transactions.add(transaction);
        // setting also for this new transaction this item as a parent
        transaction.setItem(this);

        if (TransactionType.BUY.equals(transaction.getType())) {

            setAmount(getAmount().add(transaction.getAmount()));

            //recounts average bought prices in all currencies
            recountAveragesBoughtPrices();

        } else if (TransactionType.SELL.equals(transaction.getType())) {
            // if it's SELL transaction, we should decrease the amount,
            // but it never can be negative amount, so return positive or ZERO
            // this is 2nd checking for amount 0, 1st was in the TransactionBacking
            setAmount(getAmount().subtract(transaction.getAmount()).max(BigDecimal.ZERO));
        }

        // set item as archived/not-archived depends of the tokens amount
        checkItemAmountToSetUnsetArchived();

        // recount Net Cost values in all currencies
        recountNetCost();
    }

    public Boolean removeTransaction(TransactionEntity transaction) {

        Boolean isTransactionValid = false;

        if (TransactionType.BUY.equals(transaction.getType())) {

            // if it's BUY transaction and we cancel it, we should decrease the item's amount,
            // but we never can have negative amount, so will compare future amount (after subtract operation)
            // if it will be more than ZERO
            if (getAmount().subtract(transaction.getAmount()).compareTo(BigDecimal.ZERO) >= 0) {

                isTransactionValid = true;

                setAmount(getAmount().subtract(transaction.getAmount()).setScale(8, BigDecimal.ROUND_HALF_DOWN));
            }

        } else if (TransactionType.SELL.equals(transaction.getType())) {

            isTransactionValid = true;

            setAmount(getAmount().add(transaction.getAmount()).setScale(8, BigDecimal.ROUND_HALF_DOWN));
        }

        // IF WE CAN REMOVE the TRANSACTION without problems of item amount, so make the removing
        if (isTransactionValid) {

            transactions.remove(transaction);

            // only if it was BUY transaction, then we should to recount average bought prices
            if (TransactionType.BUY.equals(transaction.getType())) {
                recountAveragesBoughtPrices();
            }

            // set item as archived/not-archived depends of the tokens amount
            checkItemAmountToSetUnsetArchived();

            // recount Net Cost values in all currencies
            recountNetCost();
        }

        return isTransactionValid;
    }

    public Boolean editTransaction(TransactionEntity transactionOld, TransactionEntity transactionNew) {

        Boolean isTransactionValid = false;

        /*
         * !!!   IS PROHIBITED TO CHANGE THE TRANSACTION TYPE   !!!
         *  so always would/should be transactionOld.getType() == transactionNew.getType()
         * */

        // do it just in case, coz before there is a condition for checking transactions types
        if (!transactionOld.getType().equals(transactionNew.getType())) {
            return isTransactionValid;
        }

        // if it's BUY transaction and we will edit it (maybe decrease or increase the  amount)
        // but we never can have negativefinal  amount of the ITEM, so will compare future amount
        // (after subtract old amount and adding new amount) if it will be more than ZERO
        if (TransactionType.BUY.equals(transactionOld.getType())) {

            if (getAmount().subtract(transactionOld.getAmount()).add(transactionNew.getAmount()).compareTo(BigDecimal.ZERO) >= 0) {

                isTransactionValid = true;

                setAmount(getAmount().subtract(transactionOld.getAmount()).add(transactionNew.getAmount()));
            }

            // if it's SELL transaction and we will edit it (maybe decrease or increase the amount)
            // but we never can have negative final amount of the ITEM, so will compare future amount
            // (after subtract new amount and adding old amount) if it will be more than ZERO
        } else if (TransactionType.SELL.equals(transactionOld.getType())) {

            if (getAmount().subtract(transactionNew.getAmount()).add(transactionOld.getAmount()).compareTo(BigDecimal.ZERO) >= 0) {

                isTransactionValid = true;

                setAmount(getAmount().subtract(transactionNew.getAmount()).add(transactionOld.getAmount()));
            }
        }

        // IF WE CAN EDIT the TRANSACTION without problems of item amount, so will make the editing
        if (isTransactionValid) {

            transactions.remove(transactionOld);
            transactions.add(transactionNew);

            // only if it was BUY transaction, then we should to recount average bought prices
            if (TransactionType.BUY.equals(transactionOld.getType())) {
                recountAveragesBoughtPrices();
            }

            // set item as archived/not-archived depends of the tokens amount
            checkItemAmountToSetUnsetArchived();

            // recount Net Cost values in all currencies
            recountNetCost();
        }
        return isTransactionValid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount.setScale(8, BigDecimal.ROUND_HALF_DOWN).max(BigDecimal.ZERO);
    }

    public CurrencyType getShowedCurrency() {
        return showedCurrency;
    }

    public void setShowedCurrency(CurrencyType showedCurrency) {
        this.showedCurrency = showedCurrency;
    }

    public BigDecimal getNetCostUsd() {
        return netCostUsd;
    }

    // net costs values should never can be negative, so return positive or ZERO
    public void setNetCostUsd(BigDecimal netCostUsd) {
        this.netCostUsd = netCostUsd.setScale(8, BigDecimal.ROUND_HALF_DOWN).max(BigDecimal.ZERO);
    }

    public BigDecimal getNetCostEur() {
        return netCostEur;
    }

    public void setNetCostEur(BigDecimal netCostEur) {
        this.netCostEur = netCostEur.setScale(8, BigDecimal.ROUND_HALF_DOWN).max(BigDecimal.ZERO);
    }

    public BigDecimal getNetCostBtc() {
        return netCostBtc;
    }

    public void setNetCostBtc(BigDecimal netCostBtc) {
        this.netCostBtc = netCostBtc.setScale(8, BigDecimal.ROUND_HALF_DOWN).max(BigDecimal.ZERO);
    }

    public BigDecimal getNetCostEth() {
        return netCostEth;
    }

    public void setNetCostEth(BigDecimal netCostEth) {
        this.netCostEth = netCostEth.setScale(8, BigDecimal.ROUND_HALF_DOWN).max(BigDecimal.ZERO);
    }

    public BigDecimal getNetCostByCurrentCurrency() {

        BigDecimal netCostByCurrentCurrency = BigDecimal.ZERO;

        switch (getShowedCurrency().getCurrency()) {
            case "USD":
                netCostByCurrentCurrency = getNetCostUsd();
                break;

            case "EUR":
                netCostByCurrentCurrency = getNetCostEur();
                break;

            case "BTC":
                netCostByCurrentCurrency = getNetCostBtc();
                break;

            case "ETH":
                netCostByCurrentCurrency = getNetCostEth();
                break;
        }
        return MathRounders.roundBigDecimalByCurrency(netCostByCurrentCurrency, getShowedCurrency());
    }

    private void recountNetCost() {

        setNetCostUsd(getAmount().multiply(getAverageBoughtPriceUsd().setScale(8, BigDecimal.ROUND_HALF_DOWN)));
        setNetCostEur(getAmount().multiply(getAverageBoughtPriceEur().setScale(8, BigDecimal.ROUND_HALF_DOWN)));
        setNetCostBtc(getAmount().multiply(getAverageBoughtPriceBtc().setScale(8, BigDecimal.ROUND_HALF_DOWN)));
        setNetCostEth(getAmount().multiply(getAverageBoughtPriceEth().setScale(8, BigDecimal.ROUND_HALF_DOWN)));

    }

    public Boolean getArchived() {
        return isArchived;
    }

    public void setArchived(Boolean archived) {
        isArchived = archived;
    }

    private void checkItemAmountToSetUnsetArchived() {

        // set item as archived if it has no more tokens
        if (getAmount().compareTo(BigDecimal.ZERO) == 0) {
            setArchived(true);

            // (just in case for some recounting issues)
            setAverageBoughtPriceUsd(BigDecimal.ZERO);
            setAverageBoughtPriceEur(BigDecimal.ZERO);
            setAverageBoughtPriceBtc(BigDecimal.ZERO);
            setAverageBoughtPriceEth(BigDecimal.ZERO);

            // (just in case for some recounting issues)
            setNetCostUsd(BigDecimal.ZERO);
            setNetCostEur(BigDecimal.ZERO);
            setNetCostBtc(BigDecimal.ZERO);
            setNetCostEth(BigDecimal.ZERO);

            // set item as not-archived if it already has some tokens
        } else {
            setArchived(false);
        }
    }

    public BigDecimal getAverageBoughtPriceUsd() {
        return averageBoughtPriceUsd;
    }

    public void setAverageBoughtPriceUsd(BigDecimal averageBoughtPriceUsd) {
        this.averageBoughtPriceUsd = averageBoughtPriceUsd.setScale(8, BigDecimal.ROUND_HALF_DOWN).max(BigDecimal.ZERO);
    }

    public BigDecimal getAverageBoughtPriceEur() {
        return averageBoughtPriceEur;
    }

    public void setAverageBoughtPriceEur(BigDecimal averageBoughtPriceEur) {
        this.averageBoughtPriceEur = averageBoughtPriceEur.setScale(8, BigDecimal.ROUND_HALF_DOWN).max(BigDecimal.ZERO);
    }

    public BigDecimal getAverageBoughtPriceBtc() {
        return averageBoughtPriceBtc;
    }

    public void setAverageBoughtPriceBtc(BigDecimal averageBoughtPriceBtc) {
        this.averageBoughtPriceBtc = averageBoughtPriceBtc.setScale(8, BigDecimal.ROUND_HALF_DOWN).max(BigDecimal.ZERO);
    }

    public BigDecimal getAverageBoughtPriceEth() {
        return averageBoughtPriceEth;
    }

    public void setAverageBoughtPriceEth(BigDecimal averageBoughtPriceEth) {
        this.averageBoughtPriceEth = averageBoughtPriceEth.setScale(8, BigDecimal.ROUND_HALF_DOWN).max(BigDecimal.ZERO);
    }

    public BigDecimal getAverageBoughtPriceByCurrency() {

        BigDecimal averageBoughtPriceByCurrency = BigDecimal.ZERO;

        switch (getShowedCurrency().getCurrency()) {
            case "USD":
                averageBoughtPriceByCurrency = getAverageBoughtPriceUsd();
                break;

            case "EUR":
                averageBoughtPriceByCurrency = getAverageBoughtPriceEur();
                break;

            case "BTC":
                averageBoughtPriceByCurrency = getAverageBoughtPriceBtc();
                break;

            case "ETH":
                averageBoughtPriceByCurrency = getAverageBoughtPriceEth();
                break;
        }
        return MathRounders.roundBigDecimalByCurrency(averageBoughtPriceByCurrency, getShowedCurrency());
    }

    private void recountAveragesBoughtPrices() {

        BigDecimal tempAverTotalUsd = BigDecimal.ZERO;
        BigDecimal tempAverTotalEur = BigDecimal.ZERO;
        BigDecimal tempAverTotalBtc = BigDecimal.ZERO;
        BigDecimal tempAverTotalEth = BigDecimal.ZERO;

        BigDecimal tempBoughtTotalAmount = BigDecimal.ZERO;

        for (TransactionEntity tempTransaction : getTransactions()) {

            if (TransactionType.BUY.equals(tempTransaction.getType())) {

                tempAverTotalUsd = tempAverTotalUsd.add(tempTransaction.getAmount()
                        .multiply(tempTransaction.getBoughtPriceUsd())).setScale(8, BigDecimal.ROUND_HALF_DOWN);

                tempAverTotalEur = tempAverTotalEur.add(tempTransaction.getAmount()
                        .multiply(tempTransaction.getBoughtPriceEur())).setScale(8, BigDecimal.ROUND_HALF_DOWN);

                tempAverTotalBtc = tempAverTotalBtc.add(tempTransaction.getAmount()
                        .multiply(tempTransaction.getBoughtPriceBtc())).setScale(8, BigDecimal.ROUND_HALF_DOWN);

                tempAverTotalEth = tempAverTotalEth.add(tempTransaction.getAmount()
                        .multiply(tempTransaction.getBoughtPriceEth())).setScale(8, BigDecimal.ROUND_HALF_DOWN);

                tempBoughtTotalAmount = tempBoughtTotalAmount.add(tempTransaction.getAmount());
            }
        }

        try {

            setAverageBoughtPriceUsd(tempAverTotalUsd.divide(tempBoughtTotalAmount, 8, BigDecimal.ROUND_HALF_DOWN));

            setAverageBoughtPriceEur(tempAverTotalEur.divide(tempBoughtTotalAmount, 8, BigDecimal.ROUND_HALF_DOWN));

            setAverageBoughtPriceBtc(tempAverTotalBtc.divide(tempBoughtTotalAmount, 8, BigDecimal.ROUND_HALF_DOWN));

            setAverageBoughtPriceEth(tempAverTotalEth.divide(tempBoughtTotalAmount, 8, BigDecimal.ROUND_HALF_DOWN));

        } catch (ArithmeticException | NullPointerException ex) {
            LOGGER.warn(ex.toString());
        }
    }

    public PortfolioEntity getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(PortfolioEntity portfolio) {
        this.portfolio = portfolio;
    }

    public CoinEntity getCoin() {
        return coin;
    }

    public void setCoin(CoinEntity coin) {
        this.coin = coin;
    }

    public List<TransactionEntity> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionEntity> transactions) {
        this.transactions = transactions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemEntity that = (ItemEntity) o;
        return getId().equals(that.getId()) &&
                getAmount().equals(that.getAmount()) &&
                getShowedCurrency() == that.getShowedCurrency() &&
                getNetCostUsd().equals(that.getNetCostUsd()) &&
                getNetCostEur().equals(that.getNetCostEur()) &&
                getNetCostBtc().equals(that.getNetCostBtc()) &&
                getNetCostEth().equals(that.getNetCostEth()) &&
                isArchived.equals(that.isArchived) &&
                getAverageBoughtPriceUsd().equals(that.getAverageBoughtPriceUsd()) &&
                getAverageBoughtPriceEur().equals(that.getAverageBoughtPriceEur()) &&
                getAverageBoughtPriceBtc().equals(that.getAverageBoughtPriceBtc()) &&
                getAverageBoughtPriceEth().equals(that.getAverageBoughtPriceEth()) &&
                getPortfolio().equals(that.getPortfolio()) &&
                getCoin().equals(that.getCoin()) &&
                getTransactions().equals(that.getTransactions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getAmount(), getShowedCurrency(), getNetCostUsd(), getNetCostEur(),
                getNetCostBtc(), getNetCostEth(), isArchived, getAverageBoughtPriceUsd(), getAverageBoughtPriceEur(),
                getAverageBoughtPriceBtc(), getAverageBoughtPriceEth(), getCoin(), getTransactions());
    }

    @Override
    public String toString() {
        return "ItemEntity{" +
                "id=" + id +
                ", amount=" + amount +
                ", showedCurrency=" + showedCurrency +
                ", netCostUsd=" + netCostUsd +
                ", netCostEur=" + netCostEur +
                ", netCostBtc=" + netCostBtc +
                ", netCostEth=" + netCostEth +
                ", isArchived=" + isArchived +
                ", averageBoughtPriceUsd=" + averageBoughtPriceUsd +
                ", averageBoughtPriceEur=" + averageBoughtPriceEur +
                ", averageBoughtPriceBtc=" + averageBoughtPriceBtc +
                ", averageBoughtPriceEth=" + averageBoughtPriceEth +
                ", coin=" + coin +
                ", transactions=" + transactions +
                '}';
    }
}