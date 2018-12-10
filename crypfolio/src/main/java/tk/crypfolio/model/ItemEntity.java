package tk.crypfolio.model;

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

    //    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @ManyToOne
    @JoinColumn(name = "portfolios_users_us_id", referencedColumnName = "users_us_id", nullable = false)
    private PortfolioEntity portfolio;

    //    @ManyToOne(fetch = FetchType.LAZY)
    @ManyToOne
    @JoinColumn(name = "coins_coin_id", referencedColumnName = "coin_id", nullable = false)
    private CoinEntity coin;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
//    @OneToMany(mappedBy = "item")
    private List<TransactionEntity> transactions = new ArrayList<>();

    public ItemEntity() {
    }

    public ItemEntity(CoinEntity coin) {
        this.coin = coin;
    }

    public void addTransaction(TransactionEntity transaction) {

        this.transactions.add(transaction);
        // setting also for this new transaction this item as a parent
        transaction.setItem(this);

        if (TransactionType.BUY.equals(transaction.getType())) {

            setAmount(getAmount().add(transaction.getAmount()));

            // set item not-archived if it already has tokens
            if (getAmount().compareTo(BigDecimal.ZERO) >= 1 ) {
                setArchived(false);
            }

            // recount Net Cost values in all currencies
            setNetCostUsd(getNetCostUsd().add(transaction.getAmount().multiply(transaction.getBoughtPriceUsd()))
                    .setScale(8, BigDecimal.ROUND_HALF_DOWN));
            setNetCostEur(getNetCostEur().add(transaction.getAmount().multiply(transaction.getBoughtPriceEur()))
                    .setScale(8, BigDecimal.ROUND_HALF_DOWN));
            setNetCostBtc(getNetCostBtc().add(transaction.getAmount().multiply(transaction.getBoughtPriceBtc()))
                    .setScale(8, BigDecimal.ROUND_HALF_DOWN));
            setNetCostEth(getNetCostEth().add(transaction.getAmount().multiply(transaction.getBoughtPriceEth()))
                    .setScale(8, BigDecimal.ROUND_HALF_DOWN));

            // **START:** counts average prices in all currencies of the item
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

            setAverageBoughtPriceUsd(tempAverTotalUsd.divide(tempBoughtTotalAmount, 8, BigDecimal.ROUND_HALF_DOWN));

            setAverageBoughtPriceEur(tempAverTotalEur.divide(tempBoughtTotalAmount, 8, BigDecimal.ROUND_HALF_DOWN));

            setAverageBoughtPriceBtc(tempAverTotalBtc.divide(tempBoughtTotalAmount, 8, BigDecimal.ROUND_HALF_DOWN));

            setAverageBoughtPriceEth(tempAverTotalEth.divide(tempBoughtTotalAmount, 8, BigDecimal.ROUND_HALF_DOWN));
            // **END**

        } else if (TransactionType.SELL.equals(transaction.getType())) {
            // if it's SELL transaction, we should increase the amount,
            // but it never can be negative amount, so return positive or ZERO
            setAmount(getAmount().subtract(transaction.getAmount()).max(BigDecimal.ZERO));

            // recount Net Cost values in all currencies (here is used currencies' average prices!)
            setNetCostUsd(getNetCostUsd().subtract(transaction.getAmount().multiply(this.averageBoughtPriceUsd))
                    .max(BigDecimal.ZERO));
            setNetCostEur(getNetCostEur().subtract(transaction.getAmount().multiply(this.averageBoughtPriceEur))
                    .max(BigDecimal.ZERO));
            setNetCostBtc(getNetCostBtc().subtract(transaction.getAmount().multiply(this.averageBoughtPriceBtc))
                    .max(BigDecimal.ZERO));
            setNetCostEth(getNetCostEth().subtract(transaction.getAmount().multiply(this.averageBoughtPriceEth))
                    .max(BigDecimal.ZERO));

            // set item archived if it has no tokens more
            if (getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                setArchived(true);
            }
        }
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
        this.amount = amount.setScale(8, BigDecimal.ROUND_HALF_DOWN);
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

    public void setNetCostUsd(BigDecimal netCostUsd) {
        this.netCostUsd = netCostUsd.setScale(8, BigDecimal.ROUND_HALF_DOWN);
    }

    public BigDecimal getNetCostEur() {
        return netCostEur;
    }

    public void setNetCostEur(BigDecimal netCostEur) {
        this.netCostEur = netCostEur.setScale(8, BigDecimal.ROUND_HALF_DOWN);
    }

    public BigDecimal getNetCostBtc() {
        return netCostBtc;
    }

    public void setNetCostBtc(BigDecimal netCostBtc) {
        this.netCostBtc = netCostBtc.setScale(8, BigDecimal.ROUND_HALF_DOWN);
    }

    public BigDecimal getNetCostEth() {
        return netCostEth;
    }

    public void setNetCostEth(BigDecimal netCostEth) {
        this.netCostEth = netCostEth.setScale(8, BigDecimal.ROUND_HALF_DOWN);
    }

    public BigDecimal getNetCostByCurrency(CurrencyType currencyType){

        switch (currencyType.getCurrency()) {
            case "USD":
                return getNetCostUsd();

            case "EUR":
                return getNetCostEur();

            case "BTC":
                return getNetCostBtc();

            case "ETH":
                return getNetCostEth();

        }
        return BigDecimal.ZERO;
    }

    public Boolean getArchived() {
        return isArchived;
    }

    public void setArchived(Boolean archived) {
        isArchived = archived;
    }

    public BigDecimal getAverageBoughtPriceUsd() {
        return averageBoughtPriceUsd;
    }

    public void setAverageBoughtPriceUsd(BigDecimal averageBoughtPriceUsd) {
        this.averageBoughtPriceUsd = averageBoughtPriceUsd.setScale(8, BigDecimal.ROUND_HALF_DOWN);
    }

    public BigDecimal getAverageBoughtPriceEur() {
        return averageBoughtPriceEur;
    }

    public void setAverageBoughtPriceEur(BigDecimal averageBoughtPriceEur) {
        this.averageBoughtPriceEur = averageBoughtPriceEur.setScale(8, BigDecimal.ROUND_HALF_DOWN);
    }

    public BigDecimal getAverageBoughtPriceBtc() {
        return averageBoughtPriceBtc;
    }

    public void setAverageBoughtPriceBtc(BigDecimal averageBoughtPriceBtc) {
        this.averageBoughtPriceBtc = averageBoughtPriceBtc.setScale(8, BigDecimal.ROUND_HALF_DOWN);
    }

    public BigDecimal getAverageBoughtPriceEth() {
        return averageBoughtPriceEth;
    }

    public void setAverageBoughtPriceEth(BigDecimal averageBoughtPriceEth) {
        this.averageBoughtPriceEth = averageBoughtPriceEth.setScale(8, BigDecimal.ROUND_HALF_DOWN);
    }

    public BigDecimal getAverageBoughtPriceByCurrency(){

        BigDecimal getAverageBoughtPriceByCurrency = BigDecimal.ZERO;

        switch (getShowedCurrency().getCurrency()) {
            case "USD":
                getAverageBoughtPriceByCurrency = getAverageBoughtPriceUsd();
                break;

            case "EUR":
                getAverageBoughtPriceByCurrency = getAverageBoughtPriceEur();
                break;

            case "BTC":
                getAverageBoughtPriceByCurrency = getAverageBoughtPriceBtc();
                break;

            case "ETH":
                getAverageBoughtPriceByCurrency = getAverageBoughtPriceEth();
                break;
        }
        return MathRounders.roundBigDecimalByCurrency(getAverageBoughtPriceByCurrency, getShowedCurrency());
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