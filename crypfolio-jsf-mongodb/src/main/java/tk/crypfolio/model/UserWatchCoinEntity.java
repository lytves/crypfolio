package tk.crypfolio.model;

import org.hibernate.search.annotations.Indexed;
import tk.crypfolio.common.CurrencyType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Indexed
@Table(name = "users_watch_coins")
public class UserWatchCoinEntity implements Serializable {

    @Id
    /*  Identifier generation strategies:
    https://docs.jboss.org/hibernate/stable/ogm/reference/en-US/html_single/#_identifier_generation_strategies
    */
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "user_watchcoins")
    @TableGenerator(
            name = "user_watchcoins",
            table = "entities_id_generators",
            // but pkColumnName won't be used, because MongoDB always stores identifiers using the _id field
            pkColumnName = "key",
            valueColumnName = "next_id",
            pkColumnValue = "us_watchcoin_id",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false)
    private UserEntity userOwner;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(nullable = false)
    private CoinEntity coinId;

    @Enumerated(EnumType.STRING)
    @Column(name = "us_watchcoin_currency", nullable = false)
    private CurrencyType showedCurrency = CurrencyType.USD;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getUserOwner() {
        return userOwner;
    }

    public void setUserOwner(UserEntity userOwner) {
        this.userOwner = userOwner;
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
        return Objects.equals(getUserOwner().getId(), that.getUserOwner().getId()) &&
                Objects.equals(getCoinId().getId(), that.getCoinId().getId()) &&
                getShowedCurrency() == that.getShowedCurrency();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserOwner().getId(), getCoinId().getId(), getShowedCurrency());
    }

    @Override
    public String toString() {
        return "UserWatchCoinEntity{" +
                "id=" + id +
                ", userOwner.id=" + userOwner.getId() +
                ", coin.id=" + coinId.getId() +
                ", showedCurrency=" + showedCurrency +
                '}';
    }
}