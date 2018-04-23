package tk.crypfolio.domain;

import tk.crypfolio.common.CurrencyType;
import tk.crypfolio.util.StringGenerator;

import javax.persistence.*;
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

    @Column(name = "port_is_share", nullable = false)
    private Boolean isShare = false;

    @Column(name = "port_share_link", length = 8, nullable = false)
    private String shareLink;

    @Column(name = "port_is_show_amounts", nullable = false)
    private Boolean isShowAmounts = false;

    @Enumerated(EnumType.STRING)
    @Column(name="port_showed_currency", nullable = false)
    private CurrencyType showedCurrency = CurrencyType.USD;

    @Column(name = "port_bought_cost_usd", precision = 8)
    private BigDecimal boughtCostUsd;

    @Column(name = "port_bought_cost_eur", precision = 8)
    private BigDecimal boughtCostEur;

    @Column(name = "port_bought_cost_btc", precision = 8)
    private BigDecimal boughtCostBtc;

    @Column(name = "port_bought_cost_eth", precision = 8)
    private BigDecimal boughtCostEth;

    @OneToOne
    @JoinColumn(name = "users_us_id")
    @MapsId
    private UserEntity user;

    @OneToMany(mappedBy = "portfolio", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
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

    public void addItem(ItemEntity item){
        this.items.add(item);
        // setting also for new item this portfolio-parent
        item.setPortfolio(this);
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
                Objects.equals(getIsShare(), that.getIsShare()) &&
                getShowedCurrency() == that.getShowedCurrency() &&
                Objects.equals(getShareLink(), that.getShareLink()) &&
                Objects.equals(getIsShowAmounts(), that.getIsShowAmounts()) &&
                Objects.equals(getBoughtCostUsd(), that.getBoughtCostUsd()) &&
                Objects.equals(getBoughtCostEur(), that.getBoughtCostEur()) &&
                Objects.equals(getBoughtCostBtc(), that.getBoughtCostBtc()) &&
                Objects.equals(getBoughtCostEth(), that.getBoughtCostEth()) &&
                Objects.equals(getUser(), that.getUser()) &&
                Objects.equals(getItems(), that.getItems());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getIsShare(), getShowedCurrency(), getShareLink(), getIsShowAmounts(), getBoughtCostUsd(), getBoughtCostEur(), getBoughtCostBtc(), getBoughtCostEth(), getUser(), getItems());
    }

    @Override
    public String toString() {
        return "PortfolioEntity{" +
                "id=" + id +
                ", isShare=" + isShare +
                ", shareLink='" + shareLink + '\'' +
                ", isShowAmounts=" + isShowAmounts +
                ", showedCurrency=" + showedCurrency +
                ", boughtCostUsd=" + boughtCostUsd +
                ", boughtCostEur=" + boughtCostEur +
                ", boughtCostBtc=" + boughtCostBtc +
                ", boughtCostEth=" + boughtCostEth +
                ", items=" + items +
                ", user.id=" + user.getId() +
                '}';
    }
}
