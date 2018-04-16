package tk.crypfolio.domain;

import tk.crypfolio.common.CurrencyType;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "users_watch_coins")
@IdClass(UserWatchCoinsEntityId.class)
public class UserWatchCoinsEntity {

    @Id
    @ManyToOne
    @JoinColumn(name="users_us_id")
    private UserEntity userId;

    @Id
    @ManyToOne
    @JoinColumn(name="coins_coin_id")
    private UserEntity coinId;

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name="us_coin_currency")
    private CurrencyType currencyType;

    public UserEntity getUserId() {
        return userId;
    }

    public void setUserId(UserEntity userId) {
        this.userId = userId;
    }

    public UserEntity getCoinId() {
        return coinId;
    }

    public void setCoinId(UserEntity coinId) {
        this.coinId = coinId;
    }

    public CurrencyType getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(CurrencyType currencyType) {
        this.currencyType = currencyType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserWatchCoinsEntity that = (UserWatchCoinsEntity) o;
        return Objects.equals(getUserId(), that.getUserId()) &&
                Objects.equals(getCoinId(), that.getCoinId()) &&
                getCurrencyType() == that.getCurrencyType();
    }

    @Override
    public int hashCode() {

        return Objects.hash(getUserId(), getCoinId(), getCurrencyType());
    }
}
