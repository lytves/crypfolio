package tk.crypfolio.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import tk.crypfolio.common.CurrencyType;

import java.math.BigDecimal;

public abstract class MathRounders {

    public static BigDecimal roundBigDecimalToTwoDecimal(@NotNull BigDecimal value) {

        return value.setScale(2, BigDecimal.ROUND_HALF_DOWN);
    }

    @NotNull
    @Contract(pure = true)
    public static Double roundDoubleToTwoDecimal(Double value) {

        return (double) Math.round(value * 100) / 100;
    }

    public static BigDecimal roundBigDecimalByCurrency(@NotNull BigDecimal value, @NotNull CurrencyType currencyType) {

        if (value.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        switch (currencyType.getCurrency()) {

            // same rounding method for USD && EUR
            case "USD":
            case "EUR":

                if (value.abs().compareTo(new BigDecimal("1")) >= 1) {
                    return value.setScale(2, BigDecimal.ROUND_HALF_DOWN);

                } else if (value.setScale(4, BigDecimal.ROUND_HALF_DOWN).compareTo(new BigDecimal("0")) != 0) {
                    return value.setScale(4, BigDecimal.ROUND_HALF_DOWN);

                } else if (value.setScale(6, BigDecimal.ROUND_HALF_DOWN).compareTo(new BigDecimal("0")) != 0) {
                    return value.setScale(6, BigDecimal.ROUND_HALF_DOWN);
                }
                break;

            // same rounding method for BTC && ETH
            case "BTC":
            case "ETH":

                if (value.setScale(6, BigDecimal.ROUND_HALF_DOWN).compareTo(BigDecimal.ZERO) != 0) {
                    return value.setScale(6, BigDecimal.ROUND_HALF_DOWN);

                } else if (value.setScale(7, BigDecimal.ROUND_HALF_DOWN).compareTo(BigDecimal.ZERO) != 0) {
                    return value.setScale(7, BigDecimal.ROUND_HALF_DOWN);
                }
                break;
        }
        return value.setScale(8, BigDecimal.ROUND_HALF_UP);
    }
}