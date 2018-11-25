package tk.crypfolio.model;

import tk.crypfolio.common.CurrencyType;
import tk.crypfolio.common.TransactionType;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static tk.crypfolio.common.Settings.MATH_CONTEXT_8_PRECISION;

@Entity
@Table(name = "items")
public class ItemEntity implements Serializable {

    @Id
    @Column(name = "item_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_amount", nullable = false, precision = 8)
    private BigDecimal amount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_showed_currency", nullable = false)
    private CurrencyType showedCurrency = CurrencyType.USD;

    @Column(name = "item_net_cost_usd", precision = 8, nullable = false)
    private BigDecimal netCostUsd = BigDecimal.ZERO;

    @Column(name = "item_net_cost_eur", precision = 8, nullable = false)
    private BigDecimal netCostEur = BigDecimal.ZERO;

    @Column(name = "item_net_cost_btc", precision = 8, nullable = false)
    private BigDecimal netCostBtc = BigDecimal.ZERO;

    @Column(name = "item_net_cost_eth", precision = 8, nullable = false)
    private BigDecimal netCostEth = BigDecimal.ZERO;

    @Column(name = "item_is_archived", nullable = false)
    private Boolean isArchived = false;

    @Column(name = "item_average_bought_price_usd", precision = 8, nullable = false)
    private BigDecimal averageBoughtPriceUsd = BigDecimal.ZERO;

    @Column(name = "item_average_bought_price_eur", precision = 8, nullable = false)
    private BigDecimal averageBoughtPriceEur = BigDecimal.ZERO;

    @Column(name = "item_average_bought_price_btc", precision = 8, nullable = false)
    private BigDecimal averageBoughtPriceBtc = BigDecimal.ZERO;

    @Column(name = "item_average_bought_price_eth", precision = 8, nullable = false)
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
        // setting also for this transaction this item-parent
        transaction.setItem(this);

        if (transaction.getType().equals(TransactionType.BUY)) {

            this.amount = this.amount.add(transaction.getAmount());

            // recount Net Cost values in all currencies
            this.netCostUsd = this.netCostUsd.add(transaction.getAmount()
                    .multiply(transaction.getBoughtPriceUsd()), MATH_CONTEXT_8_PRECISION);
            this.netCostEur = this.netCostEur.add(transaction.getAmount()
                    .multiply(transaction.getBoughtPriceEur()), MATH_CONTEXT_8_PRECISION);
            this.netCostBtc = this.netCostBtc.add(transaction.getAmount()
                    .multiply(transaction.getBoughtPriceBtc()), MATH_CONTEXT_8_PRECISION);
            this.netCostEth = this.netCostEth.add(transaction.getAmount()
                    .multiply(transaction.getBoughtPriceEth()), MATH_CONTEXT_8_PRECISION);

            // *START* counts average prices in all currencies of the item
            BigDecimal tempAverTotalUsd = BigDecimal.ZERO;
            BigDecimal tempAverTotalEur = BigDecimal.ZERO;
            BigDecimal tempAverTotalBtc = BigDecimal.ZERO;
            BigDecimal tempAverTotalEth = BigDecimal.ZERO;

            for (TransactionEntity tempTransaction : getTransactions()) {

                if (tempTransaction.getType().equals(TransactionType.BUY)) {

                    tempAverTotalUsd = tempAverTotalUsd.add(tempTransaction.getAmount()
                            .multiply(tempTransaction.getBoughtPriceUsd()), MATH_CONTEXT_8_PRECISION);

                    tempAverTotalEur = tempAverTotalEur.add(tempTransaction.getAmount()
                            .multiply(tempTransaction.getBoughtPriceEur()), MATH_CONTEXT_8_PRECISION);

                    tempAverTotalBtc = tempAverTotalBtc.add(tempTransaction.getAmount()
                            .multiply(tempTransaction.getBoughtPriceBtc()), MATH_CONTEXT_8_PRECISION);

                    tempAverTotalEth = tempAverTotalEth.add(tempTransaction.getAmount()
                            .multiply(tempTransaction.getBoughtPriceEth()), MATH_CONTEXT_8_PRECISION);
                }
            }

            setAverageBoughtPriceUsd(tempAverTotalUsd.divide(this.getAmount(), MATH_CONTEXT_8_PRECISION));
            setAverageBoughtPriceEur(tempAverTotalEur.divide(this.getAmount(), MATH_CONTEXT_8_PRECISION));
            setAverageBoughtPriceBtc(tempAverTotalBtc.divide(this.getAmount(), MATH_CONTEXT_8_PRECISION));
            setAverageBoughtPriceEth(tempAverTotalEth.divide(this.getAmount(), MATH_CONTEXT_8_PRECISION));
            // *END*

        } else if (transaction.getType().equals(TransactionType.SELL)) {
            // if it's SELL transaction, we should increase the amount,
            // but it never can be negative amount, so return positive or ZERO
            this.amount = this.amount.subtract(transaction.getAmount()).max(BigDecimal.ZERO);

            // recount Net Cost values in all currencies (is used currencies' average prices!)
            this.netCostUsd = this.netCostUsd.subtract(transaction.getAmount()
                    .multiply(this.averageBoughtPriceUsd), MATH_CONTEXT_8_PRECISION);
            this.netCostEur = this.netCostEur.subtract(transaction.getAmount()
                    .multiply(this.averageBoughtPriceEur), MATH_CONTEXT_8_PRECISION);
            this.netCostBtc = this.netCostBtc.subtract(transaction.getAmount()
                    .multiply(this.averageBoughtPriceBtc), MATH_CONTEXT_8_PRECISION);
            this.netCostEth = this.netCostEth.subtract(transaction.getAmount()
                    .multiply(this.averageBoughtPriceEth), MATH_CONTEXT_8_PRECISION);
        }

        // set item archived if it have no tokens more
        if (amount.equals(BigDecimal.ZERO)) {
            setArchived(true);
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
        this.amount = amount;
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
        this.netCostUsd = netCostUsd;
    }

    public BigDecimal getNetCostEur() {
        return netCostEur;
    }

    public void setNetCostEur(BigDecimal netCostEur) {
        this.netCostEur = netCostEur;
    }

    public BigDecimal getNetCostBtc() {
        return netCostBtc;
    }

    public void setNetCostBtc(BigDecimal netCostBtc) {
        this.netCostBtc = netCostBtc;
    }

    public BigDecimal getNetCostEth() {
        return netCostEth;
    }

    public void setNetCostEth(BigDecimal netCostEth) {
        this.netCostEth = netCostEth;
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
        this.averageBoughtPriceUsd = averageBoughtPriceUsd;
    }

    public BigDecimal getAverageBoughtPriceEur() {
        return averageBoughtPriceEur;
    }

    public void setAverageBoughtPriceEur(BigDecimal averageBoughtPriceEur) {
        this.averageBoughtPriceEur = averageBoughtPriceEur;
    }

    public BigDecimal getAverageBoughtPriceBtc() {
        return averageBoughtPriceBtc;
    }

    public void setAverageBoughtPriceBtc(BigDecimal averageBoughtPriceBtc) {
        this.averageBoughtPriceBtc = averageBoughtPriceBtc;
    }

    public BigDecimal getAverageBoughtPriceEth() {
        return averageBoughtPriceEth;
    }

    public void setAverageBoughtPriceEth(BigDecimal averageBoughtPriceEth) {
        this.averageBoughtPriceEth = averageBoughtPriceEth;
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
                getAverageBoughtPriceBtc(), getAverageBoughtPriceEth(), getPortfolio(), getCoin(), getTransactions());
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
//                ", portfolio=" + portfolio +
                ", coin=" + coin +
                ", transactions=" + transactions +
                '}';
    }
}