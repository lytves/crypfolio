package tk.crypfolio.common;

public abstract class SettingsParseAPI {

    /* CoinMarketCap Public API
    *  was worked until 4-12-2018
    * */
    public static final String CMC_ALL_COINS_LIST = "https://api.coinmarketcap.com/v2/listings/";

    // https://api.coinmarketcap.com/v2/ticker/?start=1&limit=100&convert=EUR
    public static final String CMC_COIN_BY_TICKER_ID = "https://api.coinmarketcap.com/v2/ticker/";

    public static final String CMC_GLOBAL_DATA = "https://api.coinmarketcap.com/v1/global/";

    /* CryptoCompare API Free */
    // https://min-api.cryptocompare.com/data/pricehistorical?fsym=BTC&tsyms=USD,EUR,ETH&ts=1529020800
    public static final String CC_COIN_HISTORICAL_DATA = "https://min-api.cryptocompare.com/data/pricehistorical?fsym=";

    /* CoinMarketCap testing sandbox API
    *  is worked after 4-12-2018
    *  INFO: latest data endpoints will always return OLDER market data
    * */
    public static final String CMC_SANDBOX_API_KEY = "53f31550-3f48-4127-b20b-688b8348cc3a";

    public static final String CMC_SANDBOX_ALL_COINS_FOUR_CURRENCIES = "" +
            "https://sandbox-api.coinmarketcap.com/v1/cryptocurrency/listings/latest" +
            "?start=1&limit=5000&convert=USD,EUR,BTC,ETH&CMC_PRO_API_KEY=";
}