package tk.crypfolio.model;

import tk.crypfolio.common.CurrencyType;
import tk.crypfolio.util.MathRounders;
import tk.crypfolio.util.StringGenerator;

import javax.persistence.*;
import javax.validation.constraints.DecimalMax;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "portfolios")
public class PortfolioEntity implements Serializable {

    @Id
    private Long id;

    @Column(name = "port_name", nullable = false, length = 128)
    private String name;

    @Column(name = "port_is_share", nullable = false)
    private Boolean isShare = false;

    @Column(name = "port_share_link", length = 8, nullable = false)
    private String shareLink;

    @Column(name = "port_is_showed_amounts", nullable = false)
    private Boolean isShowAmounts = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "port_showed_currency", nullable = false)
    private CurrencyType showedCurrency = CurrencyType.USD;

    @DecimalMax("999999999999.99999999")
    @Column(name = "port_net_cost_usd", precision = 20, scale = 8, nullable = false)
    private BigDecimal netCostUsd = BigDecimal.ZERO;

    @DecimalMax("999999999999.99999999")
    @Column(name = "port_net_cost_eur", precision = 20, scale = 8, nullable = false)
    private BigDecimal netCostEur = BigDecimal.ZERO;

    @DecimalMax("999999999999.99999999")
    @Column(name = "port_net_cost_btc", precision = 20, scale = 8, nullable = false)
    private BigDecimal netCostBtc = BigDecimal.ZERO;

    @DecimalMax("999999999999.99999999")
    @Column(name = "port_net_cost_eth", precision = 20, scale = 8, nullable = false)
    private BigDecimal netCostEth = BigDecimal.ZERO;

    @OneToOne
    @JoinColumn(name = "users_us_id")
    @MapsId
    private UserEntity user;

//    @OneToMany(mappedBy = "portfolio", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemEntity> items = new ArrayList<>();

    public PortfolioEntity() {
        setShareLink(StringGenerator.setStringAlphanumeric(8));
    }

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

    public String getShareLink() {
        return shareLink;
    }

    public void setShareLink(String shareLink) {
        this.shareLink = shareLink;
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

    public CurrencyType getShowedCurrency() {
        return showedCurrency;
    }

    public void setShowedCurrency(CurrencyType showedCurrency) {
        this.showedCurrency = showedCurrency;
    }

    public BigDecimal getNetCostUsd() {
        return netCostUsd;
    }

    public void setNetCostUsd(BigDecimal netCostUsd) {
        this.netCostUsd = netCostUsd;
    }

    public BigDecimal getNetCostEur() {
        return netCostEur;
    }

    public void setNetCostEur(BigDecimal netCostEur) {
        this.netCostEur = netCostEur;
    }

    public BigDecimal getNetCostBtc() {
        return netCostBtc;
    }

    public void setNetCostBtc(BigDecimal netCostBtc) {
        this.netCostBtc = netCostBtc;
    }

    public BigDecimal getNetCostEth() {
        return netCostEth;
    }

    public void setNetCostEth(BigDecimal netCostEth) {
        this.netCostEth = netCostEth;
    }

    public BigDecimal getNetCostByCurrentCurrency() {

        BigDecimal netCostByCurrentCurrency = BigDecimal.ZERO;

        switch (getShowedCurrency().getCurrency()) {
            case "USD":
                netCostByCurrentCurrency = getNetCostUsd();
                break;

            case "EUR":
                netCostByCurrentCurrency = getNetCostEur();
                break;

            case "BTC":
                netCostByCurrentCurrency = getNetCostBtc();
                break;

            case "ETH":
                netCostByCurrentCurrency = getNetCostEth();
                break;

        }
        return MathRounders.roundBigDecimalByCurrency(netCostByCurrentCurrency, getShowedCurrency());
    }

    public List<ItemEntity> getItems() {
        return items;
    }

    public void setItems(List<ItemEntity> items) {
        this.items = items;
    }

    public void addItem(ItemEntity item) {
        this.items.add(item);
        // !!! setting also for new item this portfolio as a parent
        item.setPortfolio(this);
    }

    public void removeItem(ItemEntity item) {
        // FIXME: gotta implement this method
        this.items.remove(item);
        //!!! unsetting also for new item this portfolio-parent
        item.setPortfolio(null);
    }

    public void recountNetCosts() {

        BigDecimal tempNetCostUsd = BigDecimal.ZERO;
        BigDecimal tempNetCostEur = BigDecimal.ZERO;
        BigDecimal tempNetCostBtc = BigDecimal.ZERO;
        BigDecimal tempNetCostEth = BigDecimal.ZERO;

        BigDecimal tempAllItemsTotalAmount = BigDecimal.ZERO;

        // if there is items in the portfolio then we will recount net costs,
        // otherwise all values should be replaced with BigDecimal.ZERO
        if (!getItems().isEmpty()) {

            for (ItemEntity item : getItems()) {

                // recount Net Cost values in all currencies
                tempNetCostUsd = tempNetCostUsd.add(item.getNetCostUsd()).setScale(8, BigDecimal.ROUND_HALF_DOWN);
                tempNetCostEur = tempNetCostEur.add(item.getNetCostEur()).setScale(8, BigDecimal.ROUND_HALF_DOWN);
                tempNetCostBtc = tempNetCostBtc.add(item.getNetCostBtc()).setScale(8, BigDecimal.ROUND_HALF_DOWN);
                tempNetCostEth = tempNetCostEth.add(item.getNetCostEth()).setScale(8, BigDecimal.ROUND_HALF_DOWN);

                tempAllItemsTotalAmount = tempAllItemsTotalAmount.add(item.getAmount())
                        .setScale(8, BigDecimal.ROUND_HALF_DOWN);
            }

            // if there is items but none has amount, then net costs should be ZERO too
            // (just in case for some recounting issues)
            if (tempAllItemsTotalAmount.compareTo(BigDecimal.ZERO) == 0) {
                tempNetCostUsd = tempNetCostEur = tempNetCostBtc = tempNetCostEth = BigDecimal.ZERO;
            }
        }

        setNetCostUsd(tempNetCostUsd);
        setNetCostEur(tempNetCostEur);
        setNetCostBtc(tempNetCostBtc);
        setNetCostEth(tempNetCostEth);
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortfolioEntity that = (PortfolioEntity) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(getIsShare(), that.getIsShare()) &&
                Objects.equals(getShareLink(), that.getShareLink()) &&
                Objects.equals(getIsShowAmounts(), that.getIsShowAmounts()) &&
                getShowedCurrency() == that.getShowedCurrency() &&
                Objects.equals(getNetCostUsd(), that.getNetCostUsd()) &&
                Objects.equals(getNetCostEur(), that.getNetCostEur()) &&
                Objects.equals(getNetCostBtc(), that.getNetCostBtc()) &&
                Objects.equals(getNetCostEth(), that.getNetCostEth()) &&
//                Objects.equals(getUser(), that.getUser()) &&
                Objects.equals(getItems(), that.getItems());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getName(), getIsShare(), getShareLink(), getIsShowAmounts(), getShowedCurrency(),
                getNetCostUsd(), getNetCostEur(), getNetCostBtc(), getNetCostEth(), getItems());
    }

    @Override
    public String toString() {
        return "PortfolioEntity{" +
                "id=" + id +
                ", name=" + name +
                ", isShare=" + isShare +
                ", shareLink='" + shareLink + '\'' +
                ", isShowAmounts=" + isShowAmounts +
                ", showedCurrency=" + showedCurrency +
                ", netCostUsd=" + netCostUsd +
                ", netCostEur=" + netCostEur +
                ", netCostBtc=" + netCostBtc +
                ", netCostEth=" + netCostEth +
                ", items=" + items +
                ", user.id=" + user.getId() +
                '}';
    }
}