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
    private Long usId;

    @Basic
    @Column(name = "us_email", nullable = false, length = 255)
    private String usEmail;

    @Basic
    @Column(name = "us_password", nullable = false, length = 128)
    private String usPassword;

    @Basic
    @Column(name = "us_is_email_verified", nullable = false)
    private Boolean usIsEmailVerified;

    @Basic
    @Column(name = "us_signup_date", nullable = false)
    @Convert(converter = LocalDateAttributeConverter.class)
    private LocalDate usSignupDate;

    @OneToMany(mappedBy = "userId")
    private List<UserWatchCoinsEntity> listUserWatchCoins = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "users_has_users",
    joinColumns = @JoinColumn(name = "users_us_id", referencedColumnName = "us_id"),
    inverseJoinColumns = @JoinColumn(name = "users_us_id1", referencedColumnName = "us_id"))
    private List<UserEntity> listUsers = new ArrayList<>();

    public Long getUsId() {
        return usId;
    }

    public void setUsId(Long usId) {
        this.usId = usId;
    }

    public String getUsEmail() {
        return usEmail;
    }

    public void setUsEmail(String usEmail) {
        this.usEmail = usEmail;
    }

    public String getUsPassword() {
        return usPassword;
    }

    public void setUsPassword(String usPassword) {
        this.usPassword = usPassword;
    }

    public Boolean getUsIsEmailVerified() {
        return usIsEmailVerified;
    }

    public void setUsIsEmailVerified(Boolean usIsEmailVerified) {
        this.usIsEmailVerified = usIsEmailVerified;
    }

    public LocalDate getUsSignupDate() {
        return usSignupDate;
    }

    public void setUsSignupDate(LocalDate usSignupDate) {
        this.usSignupDate = usSignupDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(getUsId(), that.getUsId()) &&
                Objects.equals(getUsEmail(), that.getUsEmail()) &&
                Objects.equals(getUsPassword(), that.getUsPassword()) &&
                Objects.equals(getUsIsEmailVerified(), that.getUsIsEmailVerified()) &&
                Objects.equals(getUsSignupDate(), that.getUsSignupDate()) &&
                Objects.equals(listUserWatchCoins, that.listUserWatchCoins);
    }

    @Override
    public int hashCode() {

        return Objects.hash(getUsId(), getUsEmail(), getUsPassword(), getUsIsEmailVerified(), getUsSignupDate(), listUserWatchCoins);
    }
}
