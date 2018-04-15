package tk.crypfolio.domain;

import tk.crypfolio.util.LocalDateAttributeConverter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "positions")
public class PositionEntity {

    @Id
    @Column(name = "pos_id", nullable = false)
    private Long posId;

    @Basic
    @Column(name = "pos_amount", nullable = false, precision = 8)
    private BigDecimal posAmount;

    @Basic
    @Column(name = "pos_bought_date", nullable = false)
    @Convert(converter = LocalDateAttributeConverter.class)    private LocalDate posBoughtDate;

    @Basic
    @Column(name = "pos_bought_price_usd", nullable = true, precision = 8)
    private BigDecimal posBoughtPriceUsd;

    @Basic
    @Column(name = "pos_bought_price_eur", nullable = true, precision = 8)
    private BigDecimal posBoughtPriceEur;

    @Basic
    @Column(name = "pos_bought_price_btc", nullable = true, precision = 8)
    private BigDecimal posBoughtPriceBtc;

    @Basic
    @Column(name = "pos_bought_price_eth", nullable = true, precision = 8)
    private BigDecimal posBoughtPriceEth;

    @ManyToOne
/*    @JoinColumns({
            @JoinColumn(name = "items_item_id", nullable = true),
            @JoinColumn(name = "items_portfolios_port_id", nullable = true),
            @JoinColumn(name = "items_portfolios_users_us_id", nullable = true)
    })*/
    private ItemEntity item;

    @OneToOne
    @JoinColumn(name="coins_coin_id")
    private CoinEntity coin;

    public Long getPosId() {
        return posId;
    }

    public void setPosId(Long posId) {
        this.posId = posId;
    }

    public BigDecimal getPosAmount() {
        return posAmount;
    }

    public void setPosAmount(BigDecimal posAmount) {
        this.posAmount = posAmount;
    }

    public LocalDate getPosBoughtDate() {
        return posBoughtDate;
    }

    public void setPosBoughtDate(LocalDate posBoughtDate) {
        this.posBoughtDate = posBoughtDate;
    }

    public BigDecimal getPosBoughtPriceUsd() {
        return posBoughtPriceUsd;
    }

    public void setPosBoughtPriceUsd(BigDecimal posBoughtPriceUsd) {
        this.posBoughtPriceUsd = posBoughtPriceUsd;
    }

    public BigDecimal getPosBoughtPriceEur() {
        return posBoughtPriceEur;
    }

    public void setPosBoughtPriceEur(BigDecimal posBoughtPriceEur) {
        this.posBoughtPriceEur = posBoughtPriceEur;
    }

    public BigDecimal getPosBoughtPriceBtc() {
        return posBoughtPriceBtc;
    }

    public void setPosBoughtPriceBtc(BigDecimal posBoughtPriceBtc) {
        this.posBoughtPriceBtc = posBoughtPriceBtc;
    }

    public BigDecimal getPosBoughtPriceEth() {
        return posBoughtPriceEth;
    }

    public void setPosBoughtPriceEth(BigDecimal posBoughtPriceEth) {
        this.posBoughtPriceEth = posBoughtPriceEth;
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
        return Objects.equals(getPosId(), that.getPosId()) &&
                Objects.equals(getPosAmount(), that.getPosAmount()) &&
                Objects.equals(getPosBoughtDate(), that.getPosBoughtDate()) &&
                Objects.equals(getPosBoughtPriceUsd(), that.getPosBoughtPriceUsd()) &&
                Objects.equals(getPosBoughtPriceEur(), that.getPosBoughtPriceEur()) &&
                Objects.equals(getPosBoughtPriceBtc(), that.getPosBoughtPriceBtc()) &&
                Objects.equals(getPosBoughtPriceEth(), that.getPosBoughtPriceEth()) &&
                Objects.equals(getItem(), that.getItem());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getPosId(), getPosAmount(), getPosBoughtDate(), getPosBoughtPriceUsd(), getPosBoughtPriceEur(), getPosBoughtPriceBtc(), getPosBoughtPriceEth(), getItem());
    }
}
