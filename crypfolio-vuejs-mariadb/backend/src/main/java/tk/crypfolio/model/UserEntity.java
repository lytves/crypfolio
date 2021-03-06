package tk.crypfolio.model;

import com.google.gson.annotations.Expose;
import tk.crypfolio.common.CurrencyType;
import tk.crypfolio.util.LocalDateTimeAttributeConverter;
import tk.crypfolio.util.LocalDateTimeAttributeMongoDBConverter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
public class UserEntity implements Serializable {

    @Expose
    @Id
    @Column(name = "us_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Expose
    @Column(name = "us_email", nullable = false, length = 255, unique = true)
    private String email;

    @Expose
    @Column(name = "us_password", nullable = false, length = 64)
    private String password;

    @Expose
    @Column(name = "us_signup_datetime", nullable = false)
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    private LocalDateTime signupDateTime = LocalDateTime.now();

    @Expose
    @Column(name = "us_is_email_verified", nullable = false)
    private Boolean isEmailVerified = false;

    @Expose
    @Column(name = "us_email_verif_code", length = 36)
    private String emailVerifCode;

    @Expose
    @Column(name = "us_email_verif_code_request_datetime")
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    private LocalDateTime emailVerifCodeRequestDateTime = LocalDateTime.now();

    @Expose
    @Column(name = "us_password_reset_code", length = 36)
    private String passwordResetCode;

    @Expose
    @Column(name = "us_password_reset_code_datetime")
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    private LocalDateTime passwordResetCodeRequestDateTime;

    @Expose
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private PortfolioEntity portfolio;

    @Expose
    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserWatchCoinEntity> userWatchCoins = new ArrayList<>();

/*    @ManyToMany
    @JoinTable(name = "users_has_users",
            joinColumns = @JoinColumn(name = "users_us_id", referencedColumnName = "us_id"),
            inverseJoinColumns = @JoinColumn(name = "users_us_followee_id", referencedColumnName = "us_id"))
    private List<UserEntity> usersFollowees = new ArrayList<>();*/

    public UserEntity() {
    }

    public UserEntity(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public UserEntity(PortfolioEntity portfolio) {
        this.portfolio = portfolio;
        portfolio.setUser(this);
    }

    public void addWatchCoin(CoinEntity coin, CurrencyType currencyType) {

        UserWatchCoinEntity userWatchCoin = new UserWatchCoinEntity();

        userWatchCoin.setCoinId(coin);
        userWatchCoin.setShowedCurrency(currencyType);

        if (!this.userWatchCoins.contains(userWatchCoin)) {
            this.userWatchCoins.add(userWatchCoin);
        }
    }

    public void removeWatchCoin(UserWatchCoinEntity userWatchCoin) {

        this.userWatchCoins.remove(userWatchCoin);
    }

/*    public void addUserFollowee(UserEntity user) {

        if (!user.getId().equals(this.getId())) {
            this.usersFollowees.add(user);
        }
    }*/

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

    public LocalDateTime getSignupDateTime() {
        return signupDateTime;
    }

    public void setSignupDateTime(LocalDateTime signUpDate) {
        this.signupDateTime = signUpDate;
    }

    public Boolean getIsEmailVerified() {
        return isEmailVerified;
    }

    public void setIsEmailVerified(Boolean isEmailVerified) {
        this.isEmailVerified = isEmailVerified;
    }

    public String getEmailVerifCode() {
        return emailVerifCode;
    }

    public void setEmailVerifCode(String emailVerifCode) {
        this.emailVerifCode = emailVerifCode;
    }

    public String getPasswordResetCode() {
        return passwordResetCode;
    }

    public void setPasswordResetCode(String passwordResetCode) {
        this.passwordResetCode = passwordResetCode;
    }

    public LocalDateTime getEmailVerifCodeRequestDateTime() {
        return emailVerifCodeRequestDateTime;
    }

    public void setEmailVerifCodeRequestDateTime(LocalDateTime emailVerifCodeRequestDateTime) {
        this.emailVerifCodeRequestDateTime = emailVerifCodeRequestDateTime;
    }

    public LocalDateTime getPasswordResetCodeRequestDateTime() {
        return passwordResetCodeRequestDateTime;
    }

    public void setPasswordResetCodeRequestDateTime(LocalDateTime passwordResetCodeRequestDateTime) {
        this.passwordResetCodeRequestDateTime = passwordResetCodeRequestDateTime;
    }

    public PortfolioEntity getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(PortfolioEntity portfolio) {
        this.portfolio = portfolio;
        // setting almost for portfolio this User
        portfolio.setUser(this);
    }

    public List<UserWatchCoinEntity> getUserWatchCoins() {
        return userWatchCoins;
    }

    public void setUserWatchCoins(List<UserWatchCoinEntity> userWatchCoins) {
        this.userWatchCoins = userWatchCoins;
    }

/*    public List<UserEntity> getUsersFollowees() {
        return usersFollowees;
    }

    public void setUsersFollowees(List<UserEntity> users) {
        this.usersFollowees = users;
    }*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getEmail(), that.getEmail()) &&
                Objects.equals(getPassword(), that.getPassword()) &&
                Objects.equals(getSignupDateTime(), that.getSignupDateTime()) &&
                Objects.equals(getIsEmailVerified(), that.getIsEmailVerified()) &&
                Objects.equals(getEmailVerifCode(), that.getEmailVerifCode()) &&
                Objects.equals(getEmailVerifCodeRequestDateTime(), that.getEmailVerifCodeRequestDateTime()) &&
                Objects.equals(getPasswordResetCode(), that.getPasswordResetCode()) &&
                Objects.equals(getPasswordResetCodeRequestDateTime(), that.getPasswordResetCodeRequestDateTime()) &&
                Objects.equals(getPortfolio().getId(), that.getPortfolio().getId());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getEmail(), getPassword(), getSignupDateTime(), getIsEmailVerified(),
                getEmailVerifCode(), getEmailVerifCodeRequestDateTime(), getPasswordResetCode(),
                getPasswordResetCodeRequestDateTime(), getPortfolio().getId());
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", signupDateTime=" + signupDateTime +
                ", isEmailVerified=" + isEmailVerified +
                ", emailVerifCode='" + emailVerifCode + '\'' +
                ", emailVerifCodeRequestDateTime=" + emailVerifCodeRequestDateTime +
                ", passwordResetCode='" + passwordResetCode + '\'' +
                ", passwordResetCodeRequestDateTime=" + passwordResetCodeRequestDateTime +
                ", portfolio=" + portfolio +
                ", userWatchCoins=" + userWatchCoins +
//                ", usersFollowees=" + usersFollowees +
                '}';
    }
}