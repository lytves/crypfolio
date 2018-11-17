package tk.crypfolio.model;

import tk.crypfolio.common.CurrencyType;
import tk.crypfolio.common.TransactionType;
import tk.crypfolio.util.LocalDateAttributeConverter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "transactions")
public class TransactionEntity implements Serializable {

    @Id
    @Column(name = "trans_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trans_amount", nullable = false, precision = 8)
//    private BigDecimal amount = BigDecimal.ZERO;
    private BigDecimal amount;

    @Column(name = "trans_bought_datetime", nullable = false)
    @Convert(converter = LocalDateAttributeConverter.class)
    private LocalDate boughtDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "trans_type", nullable = false)
    private TransactionType type = TransactionType.BUY;

    @Enumerated(EnumType.STRING)
    @Column(name = "trans_bought_currency", nullable = false)
    private CurrencyType boughtCurrency = CurrencyType.USD;

    @Column(name = "trans_bought_price_usd", precision = 8)
    private BigDecimal boughtPriceUsd = BigDecimal.ZERO;

    @Column(name = "trans_bought_price_eur", precision = 8)
    private BigDecimal boughtPriceEur = BigDecimal.ZERO;

    @Column(name = "trans_bought_price_btc", precision = 8)
    private BigDecimal boughtPriceBtc = BigDecimal.ZERO;

    @Column(name = "trans_bought_price_eth", precision = 8)
    private BigDecimal boughtPriceEth = BigDecimal.ZERO;

    @Column(name = "trans_comment")
    private String comment;

    //    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "items_item_id", referencedColumnName = "item_id", nullable = false)
    private ItemEntity item;

    public TransactionEntity() {
    }

    public TransactionEntity(ItemEntity item) {
        this.item = item;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
                Objects.equals(comment, that.comment) &&
                Objects.equals(item, that.item);
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getAmount(), getBoughtDate(), getBoughtCurrency(), getBoughtPriceUsd(),
                getBoughtPriceEur(), getBoughtPriceBtc(), getBoughtPriceEth(), getComment(), getItem());
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
//                ", item.id=" + item.getId() +
                '}';
    }
}