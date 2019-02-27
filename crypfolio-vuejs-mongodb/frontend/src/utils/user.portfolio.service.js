import AXIOS from './axios-instance';

export const userPortfolioService = {
    setPortfolioShowedCurrency
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