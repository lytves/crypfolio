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
    private Long portId;

    @Basic
    @Column(name = "port_name", nullable = false, length = 45)
    private String portName;

    @Basic
    @Column(name = "port_is_share", nullable = false)
    private Boolean portIsShare;

    @Basic
    @Column(name = "port_is_show_amounts", nullable = false)
    private Boolean portIsShowAmounts;

    @Basic
    @Column(name = "port_bought_cost_usd", nullable = true, precision = 8)
    private BigDecimal portBoughtCostUsd;

    @Basic
    @Column(name = "port_bought_cost_eur", nullable = true, precision = 8)
    private BigDecimal portBoughtCostEur;

    @Basic
    @Column(name = "port_bought_cost_btc", nullable = true, precision = 8)
    private BigDecimal portBoughtCostBtc;

    @Basic
    @Column(name = "port_bought_cost_eth", nullable = true, precision = 8)
    private BigDecimal portBoughtCostEth;

    @OneToMany(mappedBy="portfolio", fetch=FetchType.EAGER)
    private List<ItemEntity> listItems = new ArrayList<>();

    public Long getPortId() {
        return portId;
    }

    public void setPortId(Long portId) {
        this.portId = portId;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public Boolean getPortIsShare() {
        return portIsShare;
    }

    public void setPortIsShare(Boolean portIsShare) {
        this.portIsShare = portIsShare;
    }

    public Boolean getPortIsShowAmounts() {
        return portIsShowAmounts;
    }

    public void setPortIsShowAmounts(Boolean portIsShowAmounts) {
        this.portIsShowAmounts = portIsShowAmounts;
    }

    public BigDecimal getPortBoughtCostUsd() {
        return portBoughtCostUsd;
    }

    public void setPortBoughtCostUsd(BigDecimal portBoughtCostUsd) {
        this.portBoughtCostUsd = portBoughtCostUsd;
    }

    public BigDecimal getPortBoughtCostEur() {
        return portBoughtCostEur;
    }

    public void setPortBoughtCostEur(BigDecimal portBoughtCostEur) {
        this.portBoughtCostEur = portBoughtCostEur;
    }

    public BigDecimal getPortBoughtCostBtc() {
        return portBoughtCostBtc;
    }

    public void setPortBoughtCostBtc(BigDecimal portBoughtCostBtc) {
        this.portBoughtCostBtc = portBoughtCostBtc;
    }

    public BigDecimal getPortBoughtCostEth() {
        return portBoughtCostEth;
    }

    public void setPortBoughtCostEth(BigDecimal portBoughtCostEth) {
        this.portBoughtCostEth = portBoughtCostEth;
    }

    public List<ItemEntity> getListItems() {
        return listItems;
    }

    public void setListItems(List<ItemEntity> listItems) {
        this.listItems = listItems;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortfolioEntity that = (PortfolioEntity) o;
        return Objects.equals(getPortId(), that.getPortId()) &&
                Objects.equals(getPortName(), that.getPortName()) &&
                Objects.equals(getPortIsShare(), that.getPortIsShare()) &&
                Objects.equals(getPortIsShowAmounts(), that.getPortIsShowAmounts()) &&
                Objects.equals(getPortBoughtCostUsd(), that.getPortBoughtCostUsd()) &&
                Objects.equals(getPortBoughtCostEur(), that.getPortBoughtCostEur()) &&
                Objects.equals(getPortBoughtCostBtc(), that.getPortBoughtCostBtc()) &&
                Objects.equals(getPortBoughtCostEth(), that.getPortBoughtCostEth()) &&
                Objects.equals(getListItems(), that.getListItems());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getPortId(), getPortName(), getPortIsShare(), getPortIsShowAmounts(), getPortBoughtCostUsd(), getPortBoughtCostEur(), getPortBoughtCostBtc(), getPortBoughtCostEth(), getListItems());
    }

}
