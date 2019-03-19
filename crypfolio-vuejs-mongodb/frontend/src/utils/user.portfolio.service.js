import AXIOS from './axios-instance';

export const userPortfolioService = {
    setPortfolioShowedCurrency,
    addTransaction
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

async function addTransaction(payload) {
    return await AXIOS.post('/portfolio-add-transaction', {
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