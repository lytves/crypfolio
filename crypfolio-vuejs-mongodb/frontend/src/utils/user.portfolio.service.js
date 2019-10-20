import AXIOS from './axios-instance';

export const userPortfolioService = {
    setPortfolioShowedCurrency,
    addTransaction,
    editTransaction,
    deleteTransaction,
    setItemShowedCurrency,
    deleteItem,
    updatePortfolio
};

async function setPortfolioShowedCurrency(currency) {
    return await AXIOS.put('/portfolio-currency', {
        "currency": currency,
    })
        .then(
            response => {
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
                // return data for check the response status and then update item & portfolio data
                return response.data;
            })
        .catch(error => {
            return Promise.reject(error.response);
        });
}

function editTransaction(payload) {
    return AXIOS.post('/portfolio-edit-transaction', {
        "transId": payload.transId,
        "transCurrency": payload.transCurrency,
        "transType": payload.transType,
        // always converts numbers to strings
        "transAmount": String(payload.transAmount),
        "transPrice": String(payload.transPrice),
        "transDate": payload.transDate,
        "transComment": payload.transComment,
        "itemId": Number(payload.itemId)
    })
        .then(
            response => {
                // return data for check the response status and then update item & portfolio data
                return response.data;
            })
        .catch(error => {
            return Promise.reject(error.response);
        });
}

function deleteTransaction(itemId, transId) {
    return AXIOS.delete('/portfolio-delete-transaction', {
        data: {
            "itemId": itemId,
            "transId": transId,
        }
    })
        .then(
            response => {
                // return data for check the response status and then update item & portfolio data
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

function deleteItem(itemId) {
    return AXIOS.delete(`/portfolio-delete-item/` + itemId)
        .then(
            response => {
                // return data for check the response status and update portfolio data
                return response.data;
            })
        .catch(error => {
            return Promise.reject(error.response);
        });
}

function updatePortfolio(isShare, isShowAmounts, portfolioName) {
    return AXIOS.post('/portfolio-update/', {
        "isShare": isShare,
        "isShowAmounts": isShowAmounts,
        "portfolioName": portfolioName
    })
        .then(
            response => {
                // return data for update portfolio data
                return response.data.data;
            })
        .catch(error => {
            return Promise.reject(error.response);
        });
}