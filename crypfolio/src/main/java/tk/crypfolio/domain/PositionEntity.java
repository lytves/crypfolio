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
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Basic
    @Column(name = "pos_amount", nullable = false, precision = 8)
    private BigDecimal amount;

    @Basic
    @Column(name = "pos_bought_date", nullable = false)
    @Convert(converter = LocalDateAttributeConverter.class)    private LocalDate posBoughtDate;

    @Basic
    @Column(name = "pos_bought_price_usd", nullable = true, precision = 8)
    private BigDecimal boughtPriceUsd;

    @Basic
    @Column(name = "pos_bought_price_eur", nullable = true, precision = 8)
    private BigDecimal boughtPriceEur;

    @Basic
    @Column(name = "pos_bought_price_btc", nullable = true, precision = 8)
    private BigDecimal boughtPriceBtc;

    @Basic
    @Column(name = "pos_bought_price_eth", nullable = true, precision = 8)
    private BigDecimal boughtPriceEth;

    @OneToOne
    @JoinColumn(name="coins_coin_id")
    private CoinEntity coin;

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

    public LocalDate getPosBoughtDate() {
        return posBoughtDate;
    }

    public void setPosBoughtDate(LocalDate boughtDate) {
        this.posBoughtDate = boughtDate;
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

    public CoinEntity getCoin() {
        return coin;
    }

    public void setCoin(CoinEntity coin) {
        this.coin = coin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PositionEntity that = (PositionEntity) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getAmount(), that.getAmount()) &&
                Objects.equals(getPosBoughtDate(), that.getPosBoughtDate()) &&
                Objects.equals(getBoughtPriceUsd(), that.getBoughtPriceUsd()) &&
                Objects.equals(getBoughtPriceEur(), that.getBoughtPriceEur()) &&
                Objects.equals(getBoughtPriceBtc(), that.getBoughtPriceBtc()) &&
                Objects.equals(getBoughtPriceEth(), that.getBoughtPriceEth()) &&
                Objects.equals(getCoin(), that.getCoin());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getAmount(), getPosBoughtDate(), getBoughtPriceUsd(), getBoughtPriceEur(), getBoughtPriceBtc(), getBoughtPriceEth(), getCoin());
    }
}
