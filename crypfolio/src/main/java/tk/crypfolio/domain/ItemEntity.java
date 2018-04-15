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
    private Long itemId;

    @Basic
    @Column(name = "item_amount", nullable = false, precision = 8)
    private BigDecimal itemAmount;

    @Basic
    @Column(name = "item_bought_cost_usd", nullable = true, precision = 8)
    private BigDecimal itemBoughtCostUsd;

    @Basic
    @Column(name = "item_bought_cost_eur", nullable = true, precision = 8)
    private BigDecimal itemBoughtCostEur;

    @Basic
    @Column(name = "item_bought_cost_btc", nullable = true, precision = 8)
    private BigDecimal itemBoughtCostBtc;

    @Basic
    @Column(name = "item_bought_cost_eth", nullable = true, precision = 8)
    private BigDecimal itemBoughtCostEth;

    @ManyToOne
/*    @JoinColumns({
            @JoinColumn(name = "portfolios_port_id", nullable = true),
            @JoinColumn(name = "portfolios_users_us_id", nullable = true)
    })*/
    private PortfolioEntity portfolio;

    @OneToMany(mappedBy="item", fetch=FetchType.EAGER)
    private List<PositionEntity> listItems = new ArrayList<>();
    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public BigDecimal getItemAmount() {
        return itemAmount;
    }

    public void setItemAmount(BigDecimal itemAmount) {
        this.itemAmount = itemAmount;
    }

    public BigDecimal getItemBoughtCostUsd() {
        return itemBoughtCostUsd;
    }

    public void setItemBoughtCostUsd(BigDecimal itemBoughtCostUsd) {
        this.itemBoughtCostUsd = itemBoughtCostUsd;
    }

    public BigDecimal getItemBoughtCostEur() {
        return itemBoughtCostEur;
    }

    public void setItemBoughtCostEur(BigDecimal itemBoughtCostEur) {
        this.itemBoughtCostEur = itemBoughtCostEur;
    }

    public BigDecimal getItemBoughtCostBtc() {
        return itemBoughtCostBtc;
    }

    public void setItemBoughtCostBtc(BigDecimal itemBoughtCostBtc) {
        this.itemBoughtCostBtc = itemBoughtCostBtc;
    }

    public BigDecimal getItemBoughtCostEth() {
        return itemBoughtCostEth;
    }

    public void setItemBoughtCostEth(BigDecimal itemBoughtCostEth) {
        this.itemBoughtCostEth = itemBoughtCostEth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemEntity that = (ItemEntity) o;
        return Objects.equals(getItemId(), that.getItemId()) &&
                Objects.equals(getItemAmount(), that.getItemAmount()) &&
                Objects.equals(getItemBoughtCostUsd(), that.getItemBoughtCostUsd()) &&
                Objects.equals(getItemBoughtCostEur(), that.getItemBoughtCostEur()) &&
                Objects.equals(getItemBoughtCostBtc(), that.getItemBoughtCostBtc()) &&
                Objects.equals(getItemBoughtCostEth(), that.getItemBoughtCostEth()) &&
                Objects.equals(portfolio, that.portfolio);
    }

    @Override
    public int hashCode() {

        return Objects.hash(getItemId(), getItemAmount(), getItemBoughtCostUsd(), getItemBoughtCostEur(), getItemBoughtCostBtc(), getItemBoughtCostEth(), portfolio);
    }

}
