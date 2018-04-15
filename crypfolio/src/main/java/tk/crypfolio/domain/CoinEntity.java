package tk.crypfolio.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "coins")
public class CoinEntity {

    @Id
    @Column(name = "coin_id", nullable = false)
    private Long coinId;

    @Basic
    @Column(name = "coin_name", nullable = false, length = 255)
    private String coinName;

    @Basic
    @Column(name = "coin_symbol", nullable = false, length = 5)
    private String coinSymbol;

    @Basic
    @Column(name = "coin_api_id", nullable = false, length = 255)
    private String coinApiId;

    @OneToMany(mappedBy = "coinId")
    private List<UserHasCoinsEntity> userHasCoinsEntity = new ArrayList<>();

    public Long getCoinId() {
        return coinId;
    }

    public void setCoinId(Long coinId) {
        this.coinId = coinId;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public String getCoinSymbol() {
        return coinSymbol;
    }

    public void setCoinSymbol(String coinSymbol) {
        this.coinSymbol = coinSymbol;
    }

    public String getCoinApiId() {
        return coinApiId;
    }

    public void setCoinApiId(String coinApiId) {
        this.coinApiId = coinApiId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoinEntity that = (CoinEntity) o;
        return Objects.equals(getCoinId(), that.getCoinId()) &&
                Objects.equals(getCoinName(), that.getCoinName()) &&
                Objects.equals(getCoinSymbol(), that.getCoinSymbol()) &&
                Objects.equals(getCoinApiId(), that.getCoinApiId()) &&
                Objects.equals(userHasCoinsEntity, that.userHasCoinsEntity);
    }

    @Override
    public int hashCode() {

        return Objects.hash(getCoinId(), getCoinName(), getCoinSymbol(), getCoinApiId(), userHasCoinsEntity);
    }
}
