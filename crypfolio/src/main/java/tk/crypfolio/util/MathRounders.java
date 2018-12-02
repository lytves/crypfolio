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

    public static BigDecimal roundBigDecimalByCurrency(BigDecimal value, @NotNull CurrencyType currencyType) {

        switch (currencyType.getCurrency()) {

            case "USD":

                return value.setScale(2, BigDecimal.ROUND_HALF_DOWN);

            case "EUR":
                break;

            case "BTC":
                break;

            case "ETH":
                break;

        }

        return value;
    }
}