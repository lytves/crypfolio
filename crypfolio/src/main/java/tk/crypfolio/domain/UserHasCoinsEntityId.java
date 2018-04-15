package tk.crypfolio.domain;

import java.io.Serializable;
import java.util.Objects;

public class UserHasCoinsEntityId implements Serializable {

    private Long userId;

    private Long coinId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserHasCoinsEntityId that = (UserHasCoinsEntityId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(coinId, that.coinId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(userId, coinId);
    }
}
