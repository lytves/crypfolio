package tk.crypfolio.domain;

import tk.crypfolio.common.CurrencyType;
import tk.crypfolio.util.LocalDateTimeAttributeConverter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
public class UserEntity implements Serializable {

    @Id
    @Column(name = "us_id", nullable = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name = "us_email", nullable = false, length = 255, unique = true)
    private String email;

    @Column(name = "us_password", nullable = false, length = 128)
    private String password;

    @Column(name = "us_is_email_verified", nullable = false)
    private Boolean isEmailVerified = false;

    @Column(name = "us_signup_date", nullable = false)
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    private LocalDateTime signUpDate = LocalDateTime.now();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private PortfolioEntity portfolio;

    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL)
    private List<UserWatchCoinsEntity> userWatchCoins = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "users_has_users",
    joinColumns = @JoinColumn(name = "users_us_id", referencedColumnName = "us_id"),
    inverseJoinColumns = @JoinColumn(name = "users_us_followee_id", referencedColumnName = "us_id"))
    private List<UserEntity> usersFollowees = new ArrayList<>();

    public UserEntity() {
    }

    public UserEntity(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public void addWatchCoin(CoinEntity coin){

        UserWatchCoinsEntity userWatchCoin = new UserWatchCoinsEntity();

        userWatchCoin.setCoinId(coin);
        userWatchCoin.setUserId(this);

        this.userWatchCoins.add(userWatchCoin);
    }

    public void addWatchCoin(CoinEntity coin, CurrencyType currencyType){

        UserWatchCoinsEntity userWatchCoin = new UserWatchCoinsEntity();

        userWatchCoin.setCoinId(coin);
        userWatchCoin.setUserId(this);
        userWatchCoin.setShowedCurrency(currencyType);

        this.userWatchCoins.add(userWatchCoin);
    }

    public void addUserFollowee(UserEntity user){

        if (!user.getId().equals(this.getId()) ){
            this.usersFollowees.add(user);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getIsEmailVerified() {
        return isEmailVerified;
    }

    public void setIsEmailVerified(Boolean isEmailVerified) {
        this.isEmailVerified = isEmailVerified;
    }

    public LocalDateTime getSignUpDate() {
        return signUpDate;
    }

    public void setSignUpDate(LocalDateTime signUpDate) {
        this.signUpDate = signUpDate;
    }

    public PortfolioEntity getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(PortfolioEntity portfolio) {
        this.portfolio = portfolio;
        // setting almost for portfolio this User
        portfolio.setUser(this);
    }

    public List<UserWatchCoinsEntity> getUserWatchCoins() {
        return userWatchCoins;
    }

    public void setUserWatchCoins(List<UserWatchCoinsEntity> userWatchCoins) {
        this.userWatchCoins = userWatchCoins;
    }

    public List<UserEntity> getUsersFollowees() {
        return usersFollowees;
    }

    public void setUsersFollowees(List<UserEntity> users) {
        this.usersFollowees = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getEmail(), that.getEmail()) &&
                Objects.equals(getPassword(), that.getPassword()) &&
                Objects.equals(getIsEmailVerified(), that.getIsEmailVerified()) &&
                Objects.equals(getSignUpDate(), that.getSignUpDate()) &&
                Objects.equals(getPortfolio(), that.getPortfolio()) &&
                Objects.equals(getUserWatchCoins(), that.getUserWatchCoins()) &&
                Objects.equals(getUsersFollowees(), that.getUsersFollowees());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getEmail(), getPassword(), getIsEmailVerified(), getSignUpDate(), getPortfolio(), getUserWatchCoins(), getUsersFollowees());
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", isEmailVerified=" + isEmailVerified +
                ", signupDate=" + signUpDate +
                ", portfolio=" + portfolio +
//                ", userWatchCoins=" + userWatchCoins +
//                ", users=" + users +
                '}';
    }
}
