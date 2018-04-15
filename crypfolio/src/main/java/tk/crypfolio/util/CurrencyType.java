package tk.crypfolio.util;

public enum CurrencyType {

    USD("USD"),
    EUR("EUR"),
    BTC("BTC"),
    ETH("ETH");

    private String currency;

    CurrencyType(String currency) {
        this.currency = currency;
    }

    public String getCurrency(){
        return currency;
    }

}
