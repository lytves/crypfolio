import AXIOS from './axios-instance';

export const userPortfolioService = {
    setPortfolioShowedCurrency,
    addTransaction,
    setItemShowedCurrency
};

async function setPortfolioShowedCurrency(currency) {
    return await AXIOS.put('/portfolio-currency', {
        "currency": currency,
    })
        .then(
            response => {
                // console.log(currency);

                // we don't need to pass any data,
                // coz it's a request only for set new portfolio currency
                // if response was success, so setPortfolioShowedCurrency has been done successfully
            })
        .catch(error => {
            return Promise.reject(error.response);
        });
}
function addTransaction(payload) {
    return AXIOS.post('/portfolio-add-transaction', {
        "transCoinId": payload.transCoinId,
        "transCurrency": payload.transCurrency,
        "transType": payload.transType,
        // always to convert numbers to strings
        "transAmount": String(payload.transAmount),
        "transPrice": String(payload.transPrice),
        "transDate": payload.transDate,
        "transComment": payload.transComment,
    })
        .then(
            response => {

                return response.data;
            })
        .catch(error => {
            return Promise.reject(error.response);
        });
}
function setItemShowedCurrency(coinId, currency) {
    return AXIOS.put('/portfolio-item-currency', {
        "coinId": coinId,
        "currency": currency,
    })
        .then(
            response => {
                // we don't need to pass any data,
                // coz it's a request only for set new item's currency
                // if response was success, so set Item ShowedCurrency has been done successfully
            })
        .catch(error => {
            return Promise.reject(error.response);
        });
}