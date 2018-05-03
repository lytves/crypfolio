package tk.crypfolio.model;

import tk.crypfolio.common.CurrencyType;
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
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pos_amount", nullable = false, precision = 8)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(name = "pos_bought_date", nullable = false)
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    private LocalDateTime posBoughtDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name="pos_bought_currency", nullable = false)
    private CurrencyType boughtCurrency = CurrencyType.USD;

    @Column(name = "pos_bought_price_usd", precision = 8)
    private BigDecimal boughtPriceUsd = BigDecimal.ZERO;

    @Column(name = "pos_bought_price_eur", precision = 8)
    private BigDecimal boughtPriceEur = BigDecimal.ZERO;

    @Column(name = "pos_bought_price_btc", precision = 8)
    private BigDecimal boughtPriceBtc = BigDecimal.ZERO;

    @Column(name = "pos_bought_price_eth", precision = 8)
    private BigDecimal boughtPriceEth = BigDecimal.ZERO;

//    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumns({
            @JoinColumn(name = "items_item_id", referencedColumnName = "item_id", nullable = false),
            @JoinColumn(name = "items_portfolios_users_us_id", referencedColumnName = "portfolios_users_us_id", nullable = false),
            @JoinColumn(name = "items_coins_coin_id", referencedColumnName = "coins_coin_id", nullable = false)
    })
    private ItemEntity item;

    public PositionEntity() { }

    public PositionEntity(BigDecimal amount, LocalDateTime posBoughtDate) {
        this.amount = amount;
        this.posBoughtDate = posBoughtDate;
    }

    public PositionEntity(BigDecimal amount, LocalDateTime posBoughtDate, CurrencyType boughtCurrency) {
        this.amount = amount;
        this.posBoughtDate = posBoughtDate;
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

    public LocalDateTime getPosBoughtDate() {
        return posBoughtDate;
    }

    public void setPosBoughtDate(LocalDateTime boughtDate) {
        this.posBoughtDate = boughtDate;
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
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getAmount(), that.getAmount()) &&
                Objects.equals(getPosBoughtDate(), that.getPosBoughtDate()) &&
                getBoughtCurrency() == that.getBoughtCurrency() &&
                Objects.equals(getBoughtPriceUsd(), that.getBoughtPriceUsd()) &&
                Objects.equals(getBoughtPriceEur(), that.getBoughtPriceEur()) &&
                Objects.equals(getBoughtPriceBtc(), that.getBoughtPriceBtc()) &&
                Objects.equals(getBoughtPriceEth(), that.getBoughtPriceEth()) &&
                Objects.equals(getItem(), that.getItem());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getAmount(), getPosBoughtDate(), getBoughtCurrency(), getBoughtPriceUsd(), getBoughtPriceEur(), getBoughtPriceBtc(), getBoughtPriceEth(), getItem());
    }

    @Override
    public String toString() {
        return "PositionEntity{" +
                "id=" + id +
                ", amount=" + amount +
                ", posBoughtDate=" + posBoughtDate +
                ", boughtCurrency=" + boughtCurrency +
                ", boughtPriceUsd=" + boughtPriceUsd +
                ", boughtPriceEur=" + boughtPriceEur +
                ", boughtPriceBtc=" + boughtPriceBtc +
                ", boughtPriceEth=" + boughtPriceEth +
                ", item.id=" + item.getId() +
                '}';
    }
}
