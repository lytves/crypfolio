package tk.crypfolio.model;

import java.io.Serializable;
import java.util.Objects;

public class UserWatchCoinEntityId implements Serializable {

    private Long userId;

    private Long coinId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserWatchCoinEntityId that = (UserWatchCoinEntityId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(coinId, that.coinId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(userId, coinId);
    }
}
