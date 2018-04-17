package tk.crypfolio.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "items")
public class ItemEntity {

    @Id
    @Column(name = "item_id", nullable = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Basic
    @Column(name = "item_amount", nullable = false, precision = 8)
    private BigDecimal amount;

    @Basic
    @Column(name = "item_bought_cost_usd", nullable = true, precision = 8)
    private BigDecimal boughtCostUsd;

    @Basic
    @Column(name = "item_bought_cost_eur", nullable = true, precision = 8)
    private BigDecimal boughtCostEur;

    @Basic
    @Column(name = "item_bought_cost_btc", nullable = true, precision = 8)
    private BigDecimal boughtCostBtc;

    @Basic
    @Column(name = "item_bought_cost_eth", nullable = true, precision = 8)
    private BigDecimal boughtCostEth;

    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "items_item_id", referencedColumnName = "item_id")
    private List<PositionEntity> positions = new ArrayList<>();

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

    public BigDecimal getBoughtCostUsd() {
        return boughtCostUsd;
    }

    public void setBoughtCostUsd(BigDecimal boughtCostUsd) {
        this.boughtCostUsd = boughtCostUsd;
    }

    public BigDecimal getBoughtCostEur() {
        return boughtCostEur;
    }

    public void setBoughtCostEur(BigDecimal boughtCostEur) {
        this.boughtCostEur = boughtCostEur;
    }

    public BigDecimal getBoughtCostBtc() {
        return boughtCostBtc;
    }

    public void setBoughtCostBtc(BigDecimal boughtCostBtc) {
        this.boughtCostBtc = boughtCostBtc;
    }

    public BigDecimal getBoughtCostEth() {
        return boughtCostEth;
    }

    public void setBoughtCostEth(BigDecimal boughtCostEth) {
        this.boughtCostEth = boughtCostEth;
    }

    public List<PositionEntity> getPositions() {
        return positions;
    }

    public void setPositions(List<PositionEntity> positions) {
        this.positions = positions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemEntity that = (ItemEntity) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getAmount(), that.getAmount()) &&
                Objects.equals(getBoughtCostUsd(), that.getBoughtCostUsd()) &&
                Objects.equals(getBoughtCostEur(), that.getBoughtCostEur()) &&
                Objects.equals(getBoughtCostBtc(), that.getBoughtCostBtc()) &&
                Objects.equals(getBoughtCostEth(), that.getBoughtCostEth()) &&
                Objects.equals(getPositions(), that.getPositions());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getAmount(), getBoughtCostUsd(), getBoughtCostEur(), getBoughtCostBtc(), getBoughtCostEth(), getPositions());
    }
}
