package tk.crypfolio.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "coins")
public class CoinEntity {

    @Id
    @Column(name = "coin_id", nullable = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Basic
    @Column(name = "coin_name", nullable = false, length = 255)
    private String name;

    @Basic
    @Column(name = "coin_symbol", nullable = false, length = 5)
    private String symbol;

    @Basic
    @Column(name = "coin_api_id", nullable = false, length = 255, unique = true)
    private String apiId;

    public CoinEntity() {
    }

    public CoinEntity(String name, String symbol, String apiId) {
        this.name = name;
        this.symbol = symbol;
        this.apiId = apiId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long coinId) {
        this.id = coinId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoinEntity that = (CoinEntity) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(getSymbol(), that.getSymbol()) &&
                Objects.equals(getApiId(), that.getApiId());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getName(), getSymbol(), getApiId());
    }

    @Override
    public String toString() {
        return "CoinEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", symbol='" + symbol + '\'' +
                ", apiId='" + apiId + '\'' +
                '}';
    }
}
