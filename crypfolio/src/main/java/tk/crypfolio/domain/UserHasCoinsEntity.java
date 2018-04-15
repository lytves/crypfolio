package tk.crypfolio.domain;

import tk.crypfolio.util.CurrencyType;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "users_has_coins")
@IdClass(UserHasCoinsEntityId.class)
public class UserHasCoinsEntity {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserHasCoinsEntity that = (UserHasCoinsEntity) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(coinId, that.coinId) &&
                currencyType == that.currencyType;
    }

    @Override
    public int hashCode() {

        return Objects.hash(userId, coinId, currencyType);
    }
}
