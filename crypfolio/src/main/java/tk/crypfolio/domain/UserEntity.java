package tk.crypfolio.domain;

import tk.crypfolio.util.LocalDateAttributeConverter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @Column(name = "us_id", nullable = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Basic
    @Column(name = "us_email", nullable = false, length = 255, unique = true)
    private String email;

    @Basic
    @Column(name = "us_password", nullable = false, length = 128)
    private String password;

    @Basic
    @Column(name = "us_is_email_verified")
    private Boolean isEmailVerified = false;

    @Basic
    @Column(name = "us_signup_date", nullable = false)
    @Convert(converter = LocalDateAttributeConverter.class)
    private LocalDate signupDate;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private PortfolioEntity portfolio;

    @OneToMany(mappedBy = "userId", orphanRemoval = true)
    private List<UserWatchCoinsEntity> userWatchCoins = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "users_has_users",
    joinColumns = @JoinColumn(name = "users_us_id", referencedColumnName = "us_id"),
    inverseJoinColumns = @JoinColumn(name = "users_us_id1", referencedColumnName = "us_id"))
    private List<UserEntity> users = new ArrayList<>();

    public UserEntity() {
    }

    public UserEntity(String email, String password, LocalDate signupDate) {
        this.email = email;
        this.password = password;
        this.signupDate = signupDate;
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

    public LocalDate getSignupDate() {
        return signupDate;
    }

    public void setSignupDate(LocalDate signupDate) {
        this.signupDate = signupDate;
    }

    public PortfolioEntity getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(PortfolioEntity portfolio) {
        this.portfolio = portfolio;
        // setting to portfolio this User too
        portfolio.setUser(this);
    }

    public List<UserWatchCoinsEntity> getUserWatchCoins() {
        return userWatchCoins;
    }

    public void setUserWatchCoins(List<UserWatchCoinsEntity> userWatchCoins) {
        this.userWatchCoins = userWatchCoins;
    }

    public List<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(List<UserEntity> users) {
        this.users = users;
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
                Objects.equals(getSignupDate(), that.getSignupDate()) &&
                Objects.equals(getPortfolio(), that.getPortfolio()) &&
                Objects.equals(getUserWatchCoins(), that.getUserWatchCoins()) &&
                Objects.equals(getUsers(), that.getUsers());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getEmail(), getPassword(), getIsEmailVerified(), getSignupDate(), getPortfolio(), getUserWatchCoins(), getUsers());
    }
}
