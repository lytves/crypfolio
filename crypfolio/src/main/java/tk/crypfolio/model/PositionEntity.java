package tk.crypfolio.model;

import tk.crypfolio.common.CurrencyType;
import tk.crypfolio.common.PositionType;
import tk.crypfolio.util.LocalDateTimeAttributeConverter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "positions")
public class PositionEntity implements Serializable {

    @Id
    @Column(name = "pos_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pos_amount", nullable = false, precision = 8)
//    private BigDecimal amount = BigDecimal.ZERO;
    private BigDecimal amount;

    @Column(name = "pos_bought_datetime", nullable = false)
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    private LocalDateTime boughtDateTime = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "pos_type", nullable = false)
    private PositionType type = PositionType.BUY;

    @Enumerated(EnumType.STRING)
    @Column(name = "pos_bought_currency", nullable = false)
    private CurrencyType boughtCurrency = CurrencyType.USD;

    @Column(name = "pos_bought_price_usd", precision = 8)
    private BigDecimal boughtPriceUsd = BigDecimal.ZERO;

    @Column(name = "pos_bought_price_eur", precision = 8)
    private BigDecimal boughtPriceEur = BigDecimal.ZERO;

    @Column(name = "pos_bought_price_btc", precision = 8)
    private BigDecimal boughtPriceBtc = BigDecimal.ZERO;

    @Column(name = "pos_bought_price_eth", precision = 8)
    private BigDecimal boughtPriceEth = BigDecimal.ZERO;

    @Column(name = "pos_comment")
    private String comment;

    //    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "items_item_id", referencedColumnName = "item_id", nullable = false)
    private ItemEntity item;

    public PositionEntity() {
    }

    public PositionEntity(ItemEntity item) {
        this.item = item;
    }

    public PositionEntity(BigDecimal amount, LocalDateTime boughtDateTime) {
        this.amount = amount;
        this.boughtDateTime = boughtDateTime;
    }

    public PositionEntity(BigDecimal amount, LocalDateTime boughtDateTime, CurrencyType boughtCurrency) {
        this.amount = amount;
        this.boughtDateTime = boughtDateTime;
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

    public LocalDateTime getBoughtDateTime() {
        return boughtDateTime;
    }

    public void setBoughtDateTime(LocalDateTime boughtDate) {
        this.boughtDateTime = boughtDate;
    }

    public PositionType getType() {
        return type;
    }

    public void setType(PositionType type) {
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
        PositionEntity that = (PositionEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(boughtDateTime, that.boughtDateTime) &&
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

        return Objects.hash(getId(), getAmount(), getBoughtDateTime(), getBoughtCurrency(), getBoughtPriceUsd(),
                getBoughtPriceEur(), getBoughtPriceBtc(), getBoughtPriceEth(), getComment(), getItem());
    }

    @Override
    public String toString() {
        return "PositionEntity{" +
                "id=" + id +
                ", amount=" + amount +
                ", boughtDateTime='" + boughtDateTime + '\'' +
                ", type='" + type + '\'' +
                ", boughtCurrency='" + boughtCurrency + '\'' +
                ", boughtPriceUsd=" + boughtPriceUsd + '\'' +
                ", boughtPriceEur=" + boughtPriceEur + '\'' +
                ", boughtPriceBtc=" + boughtPriceBtc + '\'' +
                ", boughtPriceEth=" + boughtPriceEth + '\'' +
                ", comment='" + comment +
                ", item.id=" + item.getId() +
                '}';
    }
}