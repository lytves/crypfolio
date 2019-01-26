package tk.crypfolio.model;

import tk.crypfolio.common.CurrencyType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "users_watch_coins")
@IdClass(UserWatchCoinEntityId.class)
public class UserWatchCoinEntity implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name="users_us_id", nullable = false)
    private UserEntity userId;

    @Id
    @ManyToOne
    @JoinColumn(name="coins_coin_id", nullable = false)
    private CoinEntity coinId;

    @Enumerated(EnumType.STRING)
    @Column(name="us_watchcoin_currency", nullable = false)
    private CurrencyType showedCurrency = CurrencyType.USD;

    public UserEntity getUserId() {
        return userId;
    }

    public void setUserId(UserEntity userId) {
        this.userId = userId;
    }

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
        return Objects.equals(getUserId(), that.getUserId()) &&
                Objects.equals(getCoinId(), that.getCoinId()) &&
                getShowedCurrency() == that.getShowedCurrency();
    }

    @Override
    public int hashCode() {

        return Objects.hash(getUserId(), getCoinId(), getShowedCurrency());
    }

    @Override
    public String toString() {
        return "UserWatchCoinEntity{" +
              "user.id=" + userId.getId() +
                ", coinId=" + coinId +
                ", showedCurrency=" + showedCurrency +
                '}';
    }
}