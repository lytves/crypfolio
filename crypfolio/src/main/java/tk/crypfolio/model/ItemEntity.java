package tk.crypfolio.model;

import tk.crypfolio.common.CurrencyType;
import tk.crypfolio.common.TransactionType;

import javax.persistence.*;
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

    @Column(name = "item_amount", nullable = false, precision = 8)
    private BigDecimal amount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_showed_currency", nullable = false)
    private CurrencyType showedCurrency = CurrencyType.USD;

    @Basic
    @Column(name = "item_net_cost_usd", precision = 8)
    private BigDecimal netCostUsd = BigDecimal.ZERO;

    @Column(name = "item_net_cost_eur", precision = 8)
    private BigDecimal netCostEur = BigDecimal.ZERO;

    @Column(name = "item_net_cost_btc", precision = 8)
    private BigDecimal netCostBtc = BigDecimal.ZERO;

    @Column(name = "item_net_cost_eth", precision = 8)
    private BigDecimal netCostEth = BigDecimal.ZERO;

    @Column(name = "item_is_archived", nullable = false)
    private Boolean isArchived = false;

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
        // setting also for new transaction this item-parent
        transaction.setItem(this);

        if (transaction.getType().equals(TransactionType.BUY)) {
            this.amount = this.amount.add(transaction.getAmount());

        } else if (transaction.getType().equals(TransactionType.SELL)) {
            // if is SELL transaction and we should increase the amount,
            // but we should never have negative final amount, so return positive or ZERO
            this.amount = this.amount.subtract(transaction.getAmount()).max(BigDecimal.ZERO);
        }

        this.netCostUsd = this.netCostUsd.add(transaction.getBoughtPriceUsd());
        this.netCostEur = this.netCostEur.add(transaction.getBoughtPriceEur());
        this.netCostBtc = this.netCostBtc.add(transaction.getBoughtPriceBtc());
        this.netCostEth = this.netCostEth.add(transaction.getBoughtPriceEth());
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
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getAmount(), that.getAmount()) &&
                getShowedCurrency() == that.getShowedCurrency() &&
                Objects.equals(getNetCostUsd(), that.getNetCostUsd()) &&
                Objects.equals(getNetCostEur(), that.getNetCostEur()) &&
                Objects.equals(getNetCostBtc(), that.getNetCostBtc()) &&
                Objects.equals(getNetCostEth(), that.getNetCostEth()) &&
                Objects.equals(getPortfolio(), that.getPortfolio()) &&
                Objects.equals(getCoin(), that.getCoin()) &&
                Objects.equals(getTransactions(), that.getTransactions());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getAmount(), getShowedCurrency(), getNetCostUsd(), getNetCostEur(),
                getNetCostBtc(), getNetCostEth(), getPortfolio(), getCoin(), getTransactions());
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
                ", coin=" + coin +
                ", transactions=" + transactions +
                '}';
    }
}