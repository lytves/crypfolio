import AXIOS from './axios-instance';

export const userWatchlistService = {
    setCoinShowedCurrency,
};

async function setCoinShowedCurrency(coinId, currency) {
    return await AXIOS.put('/watchlist-coin-currency', {
        "coinId": coinId,
        "currency": currency,
    })
        .then(
            response => {
                // we don't need to pass any data,
                // coz it's a request only for set new user's watch coin currency
                // if response was success, so set Coin ShowedCurrency has been done successfully
            })
        .catch(error => {
            return Promise.reject(error.response);
        });
}