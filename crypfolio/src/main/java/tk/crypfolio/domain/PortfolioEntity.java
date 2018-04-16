package tk.crypfolio.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "portfolios")
public class PortfolioEntity {

    @Id
    @Column(name = "port_id", nullable = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Basic
    @Column(name = "port_name", nullable = false, length = 45)
    private String name;

    @Basic
    @Column(name = "port_is_share", nullable = false)
    private Boolean isShare;

    @Basic
    @Column(name = "port_is_show_amounts", nullable = false)
    private Boolean isShowAmounts;

    @Basic
    @Column(name = "port_bought_cost_usd", nullable = true, precision = 8)
    private BigDecimal boughtCostUsd;

    @Basic
    @Column(name = "port_bought_cost_eur", nullable = true, precision = 8)
    private BigDecimal boughtCostEur;

    @Basic
    @Column(name = "port_bought_cost_btc", nullable = true, precision = 8)
    private BigDecimal boughtCostBtc;

    @Basic
    @Column(name = "port_bought_cost_eth", nullable = true, precision = 8)
    private BigDecimal boughtCostEth;

    @OneToMany
    @JoinColumn(name="portfolios_port_id")
//    ????????? ,referencedColumnName="?????")
    private List<ItemEntity> items = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsShare() {
        return isShare;
    }

    public void setIsShare(Boolean isShare) {
        this.isShare = isShare;
    }

    public Boolean getIsShowAmounts() {
        return isShowAmounts;
    }

    public void setIsShowAmounts(Boolean isShowAmounts) {
        this.isShowAmounts = isShowAmounts;
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

    public List<ItemEntity> getItems() {
        return items;
    }

    public void setItems(List<ItemEntity> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortfolioEntity that = (PortfolioEntity) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(getIsShare(), that.getIsShare()) &&
                Objects.equals(getIsShowAmounts(), that.getIsShowAmounts()) &&
                Objects.equals(getBoughtCostUsd(), that.getBoughtCostUsd()) &&
                Objects.equals(getBoughtCostEur(), that.getBoughtCostEur()) &&
                Objects.equals(getBoughtCostBtc(), that.getBoughtCostBtc()) &&
                Objects.equals(getBoughtCostEth(), that.getBoughtCostEth()) &&
                Objects.equals(getItems(), that.getItems());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getName(), getIsShare(), getIsShowAmounts(), getBoughtCostUsd(), getBoughtCostEur(), getBoughtCostBtc(), getBoughtCostEth(), getItems());
    }
}
