import AXIOS from './axios-instance';

export const userWatchlistService = {
    setCoinShowedCurrency,
    addNewWatchlistCoin
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

async function addNewWatchlistCoin(coinId, currency) {
    return await AXIOS.put('/watchlist-add-new-coin', {
        "coinId": coinId,
        "currency": currency,
    })
        .then(
            response => {
                // will return the status of the request to get know if the adding was done successfully
                // or there was an error and the coin is already in the watchlist
                return response.data.status;
            })
        .catch(error => {
            return Promise.reject(error.response);
        });
}