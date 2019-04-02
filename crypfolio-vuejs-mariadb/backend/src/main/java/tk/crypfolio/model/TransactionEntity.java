package tk.crypfolio.model;

import com.google.gson.annotations.Expose;
import tk.crypfolio.common.CurrencyType;
import tk.crypfolio.common.TransactionType;
import tk.crypfolio.util.LocalDateAttributeConverter;
import tk.crypfolio.util.MathRounders;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "transactions")
public class TransactionEntity implements Serializable {

    @Expose
    @Id
    @Column(name = "trans_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Expose
    @Column(name = "trans_amount", precision = 8, nullable = false)
    private BigDecimal amount;

    @Expose
    @Column(name = "trans_bought_date", nullable = false)
    @Convert(converter = LocalDateAttributeConverter.class)
    private LocalDate boughtDate;

    @Expose
    @Enumerated(EnumType.STRING)
    @Column(name = "trans_type", nullable = false)
    private TransactionType type = TransactionType.BUY;

    @Expose
    @Enumerated(EnumType.STRING)
    @Column(name = "trans_bought_currency", nullable = false)
    private CurrencyType boughtCurrency = CurrencyType.USD;

    @Expose
    @Column(name = "trans_bought_price_usd", precision = 8, nullable = false)
    private BigDecimal boughtPriceUsd = BigDecimal.ZERO;

    @Expose
    @Column(name = "trans_bought_price_eur", precision = 8, nullable = false)
    private BigDecimal boughtPriceEur = BigDecimal.ZERO;

    @Expose
    @Column(name = "trans_bought_price_btc", precision = 8, nullable = false)
    private BigDecimal boughtPriceBtc = BigDecimal.ZERO;

    @Expose
    @Column(name = "trans_bought_price_eth", precision = 8, nullable = false)
    private BigDecimal boughtPriceEth = BigDecimal.ZERO;

    @Expose
    @Column(name = "trans_comment")
    private String comment;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE})
    @JoinColumn(name = "items_item_id", referencedColumnName = "item_id", nullable = false)
    private ItemEntity item;

    public TransactionEntity() {
    }

    public TransactionEntity(BigDecimal amount, LocalDate boughtDate) {
        this.amount = amount;
        this.boughtDate = boughtDate;
    }

    public TransactionEntity(BigDecimal amount, LocalDate boughtDate, CurrencyType boughtCurrency) {
        this.amount = amount;
        this.boughtDate = boughtDate;
        this.boughtCurrency = boughtCurrency;
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

    public LocalDate getBoughtDate() {
        return boughtDate;
    }

    public void setBoughtDate(LocalDate boughtDate) {
        this.boughtDate = boughtDate;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public CurrencyType getBoughtCurrency() {
        return boughtCurrency;
    }

    public void setBoughtCurrency(CurrencyType boughtCurrency) {
        this.boughtCurrency = boughtCurrency;
    }

    public BigDecimal getBoughtPriceUsd() {
        return boughtPriceUsd;
    }

    public void setBoughtPriceUsd(BigDecimal boughtPriceUsd) {
        this.boughtPriceUsd = boughtPriceUsd;
    }

    public BigDecimal getBoughtPriceEur() {
        return boughtPriceEur;
    }

    public void setBoughtPriceEur(BigDecimal boughtPriceEur) {
        this.boughtPriceEur = boughtPriceEur;
    }

    public BigDecimal getBoughtPriceBtc() {
        return boughtPriceBtc;
    }

    public void setBoughtPriceBtc(BigDecimal boughtPriceBtc) {
        this.boughtPriceBtc = boughtPriceBtc;
    }

    public BigDecimal getBoughtPriceEth() {
        return boughtPriceEth;
    }

    public void setBoughtPriceEth(BigDecimal boughtPriceEth) {
        this.boughtPriceEth = boughtPriceEth;
    }

    public BigDecimal getPriceByCurrentCurrency() {

        BigDecimal priceByBoughtCurrency = BigDecimal.ZERO;

        switch (getBoughtCurrency().getCurrency()) {
            case "USD":
                priceByBoughtCurrency = getBoughtPriceUsd();
                break;

            case "EUR":
                priceByBoughtCurrency = getBoughtPriceEur();
                break;

            case "BTC":
                priceByBoughtCurrency = getBoughtPriceBtc();
                break;

            case "ETH":
                priceByBoughtCurrency = getBoughtPriceEth();
                break;
        }
        return MathRounders.roundBigDecimalByCurrency(priceByBoughtCurrency, getBoughtCurrency());
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public BigDecimal getTotalPriceByCurrency() {

        BigDecimal totalPriceByCurrency = BigDecimal.ZERO;

        switch (getBoughtCurrency().getCurrency()) {
            case "USD":
                totalPriceByCurrency = getAmount().multiply(getBoughtPriceUsd());
                break;

            case "EUR":
                totalPriceByCurrency = getAmount().multiply(getBoughtPriceEur());
                break;

            case "BTC":
                totalPriceByCurrency = getAmount().multiply(getBoughtPriceBtc());
                break;

            case "ETH":
                totalPriceByCurrency = getAmount().multiply(getBoughtPriceEth());
                break;
        }
        return MathRounders.roundBigDecimalByCurrency(totalPriceByCurrency, getBoughtCurrency());
    }

    public ItemEntity getItem() {
        return item;
    }

    public void setItem(ItemEntity item) {
        this.item = item;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionEntity that = (TransactionEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(boughtDate, that.boughtDate) &&
                type == that.type &&
                boughtCurrency == that.boughtCurrency &&
                Objects.equals(boughtPriceUsd, that.boughtPriceUsd) &&
                Objects.equals(boughtPriceEur, that.boughtPriceEur) &&
                Objects.equals(boughtPriceBtc, that.boughtPriceBtc) &&
                Objects.equals(boughtPriceEth, that.boughtPriceEth) &&
                Objects.equals(comment, that.comment);
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getAmount(), getBoughtDate(), getBoughtCurrency(), getBoughtPriceUsd(),
                getBoughtPriceEur(), getBoughtPriceBtc(), getBoughtPriceEth(), getComment());
    }

    @Override
    public String toString() {
        return "TransactionEntity{" +
                "id=" + id +
                ", amount=" + amount +
                ", boughtDate='" + boughtDate + '\'' +
                ", type='" + type + '\'' +
                ", boughtCurrency='" + boughtCurrency + '\'' +
                ", boughtPriceUsd=" + boughtPriceUsd + '\'' +
                ", boughtPriceEur=" + boughtPriceEur + '\'' +
                ", boughtPriceBtc=" + boughtPriceBtc + '\'' +
                ", boughtPriceEth=" + boughtPriceEth + '\'' +
                ", comment='" + comment +
                '}';
    }
}