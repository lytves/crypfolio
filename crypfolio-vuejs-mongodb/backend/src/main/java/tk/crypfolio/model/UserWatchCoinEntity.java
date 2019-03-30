package tk.crypfolio.model;

import com.google.gson.annotations.Expose;
import org.hibernate.search.annotations.Indexed;
import tk.crypfolio.common.CurrencyType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserWatchCoinEntity implements Serializable {

    @Expose
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(nullable = false)
    private CoinEntity coinId;

    @Expose
    @Enumerated(EnumType.STRING)
    @Column(name = "us_watchcoin_currency", nullable = false)
    private CurrencyType showedCurrency = CurrencyType.USD;

    public CoinEntity getCoinId() {
        return coinId;
    }

    public void setCoinId(CoinEntity coinId) {
        this.coinId = coinId;
    }

    public CurrencyType getShowedCurrency() {
        return showedCurrency;
    }

    public void setShowedCurrency(CurrencyType showedCurrency) {
        this.showedCurrency = showedCurrency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserWatchCoinEntity that = (UserWatchCoinEntity) o;
        return Objects.equals(getCoinId().getId(), that.getCoinId().getId()) &&
                getShowedCurrency() == that.getShowedCurrency();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCoinId().getId(), getShowedCurrency());
    }

    @Override
    public String toString() {
        return "UserWatchCoinEntity{" +
                ", coin.id=" + coinId.getId() +
                ", showedCurrency=" + showedCurrency +
                '}';
    }
}