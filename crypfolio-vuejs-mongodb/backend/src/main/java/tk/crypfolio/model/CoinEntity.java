package tk.crypfolio.model;

import com.google.gson.annotations.Expose;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "coins")
public class CoinEntity implements Serializable {

    @Expose
    @Id
    @Column(nullable = false)
    private Long id;

    @Expose
    @Column(name = "coin_name", nullable = false, length = 255)
    private String name;

    @Expose
    @Column(name = "coin_symbol", nullable = false, length = 127)
    private String symbol;

    @Expose
    @Column(name = "coin_slug", nullable = false, length = 255, unique = true)
    private String slug;

    public CoinEntity() {
    }

    public CoinEntity(Long id, String name, String symbol, String slug) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.slug = slug;
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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoinEntity that = (CoinEntity) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(getSymbol(), that.getSymbol()) &&
                Objects.equals(getSlug(), that.getSlug());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getName(), getSymbol(), getSlug());
    }

    @Override
    public String toString() {
        return "CoinEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", symbol='" + symbol + '\'' +
                ", slug='" + slug + '\'' +
                '}';
    }
}