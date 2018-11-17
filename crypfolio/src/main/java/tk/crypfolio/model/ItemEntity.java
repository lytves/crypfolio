package tk.crypfolio.model;

import tk.crypfolio.common.CurrencyType;

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
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_amount", nullable = false, precision = 8)
    private BigDecimal amount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name="item_showed_currency", nullable = false)
    private CurrencyType showedCurrency = CurrencyType.USD;

    @Basic
    @Column(name = "item_bought_cost_usd", precision = 8)
    private BigDecimal boughtCostUsd = BigDecimal.ZERO;

    @Column(name = "item_bought_cost_eur", precision = 8)
    private BigDecimal boughtCostEur = BigDecimal.ZERO;

    @Column(name = "item_bought_cost_btc", precision = 8)
    private BigDecimal boughtCostBtc = BigDecimal.ZERO;

    @Column(name = "item_bought_cost_eth", precision = 8)
    private BigDecimal boughtCostEth = BigDecimal.ZERO;

//    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @ManyToOne
    @JoinColumn(name="portfolios_users_us_id", referencedColumnName="users_us_id", nullable = false)
    private PortfolioEntity portfolio;

//    @ManyToOne(fetch = FetchType.LAZY)
    @ManyToOne
    @JoinColumn(name = "coins_coin_id", referencedColumnName="coin_id", nullable = false)
    private CoinEntity coin;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
//    @OneToMany(mappedBy = "item")
    private List<TransactionEntity> transactions = new ArrayList<>();

    public ItemEntity() {
    }

    public ItemEntity(CoinEntity coin) {
        this.coin = coin;
    }

    public void addTransaction(TransactionEntity transaction){
        this.transactions.add(transaction);
        // setting also for new transaction this item-parent
        transaction.setItem(this);

        this.amount = this.amount.add(transaction.getAmount());
        this.boughtCostUsd = this.boughtCostUsd.add(transaction.getBoughtPriceUsd());
        this.boughtCostEur = this.boughtCostEur.add(transaction.getBoughtPriceEur());
        this.boughtCostBtc = this.boughtCostBtc.add(transaction.getBoughtPriceBtc());
        this.boughtCostEth = this.boughtCostEth.add(transaction.getBoughtPriceEth());
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

    public BigDecimal getBoughtCostUsd() {
        return boughtCostUsd;
    }

    public void setBoughtCostUsd(BigDecimal boughtCostUsd) {
        this.boughtCostUsd = boughtCostUsd;
    }

    public BigDecimal getBoughtCostEur() {
        return boughtCostEur;
    }

    public void setBoughtCostEur(BigDecimal boughtCostEur) {
        this.boughtCostEur = boughtCostEur;
    }

    public BigDecimal getBoughtCostBtc() {
        return boughtCostBtc;
    }

    public void setBoughtCostBtc(BigDecimal boughtCostBtc) {
        this.boughtCostBtc = boughtCostBtc;
    }

    public BigDecimal getBoughtCostEth() {
        return boughtCostEth;
    }

    public void setBoughtCostEth(BigDecimal boughtCostEth) {
        this.boughtCostEth = boughtCostEth;
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
                Objects.equals(getBoughtCostUsd(), that.getBoughtCostUsd()) &&
                Objects.equals(getBoughtCostEur(), that.getBoughtCostEur()) &&
                Objects.equals(getBoughtCostBtc(), that.getBoughtCostBtc()) &&
                Objects.equals(getBoughtCostEth(), that.getBoughtCostEth()) &&
                Objects.equals(getPortfolio(), that.getPortfolio()) &&
                Objects.equals(getCoin(), that.getCoin()) &&
                Objects.equals(getTransactions(), that.getTransactions());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getAmount(), getShowedCurrency(), getBoughtCostUsd(), getBoughtCostEur(),
                getBoughtCostBtc(), getBoughtCostEth(), getPortfolio(), getCoin(), getTransactions());
    }

    @Override
    public String toString() {
        return "ItemEntity{" +
                "id=" + id +
                ", amount=" + amount +
                ", showedCurrency=" + showedCurrency +
                ", boughtCostUsd=" + boughtCostUsd +
                ", boughtCostEur=" + boughtCostEur +
                ", boughtCostBtc=" + boughtCostBtc +
                ", boughtCostEth=" + boughtCostEth +
                ", coin=" + coin +
                ", transactions=" + transactions +
                '}';
    }
}