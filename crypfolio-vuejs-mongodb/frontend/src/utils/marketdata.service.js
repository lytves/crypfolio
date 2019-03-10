import AXIOS from './axios-instance';

export const marketdataService = {
    getUserCoinsData,
    getCoinData,
    getAllCoinsListData
};

async function getUserCoinsData(allUserCoinsIds) {
    return await AXIOS.get('/user-coins-data')
        .then(
            response => {
                // returns only JSON object with coins actual market data grouped by coin's IDs
                return response.data.data;
            })
        .catch(error => {
            return Promise.reject(error.response);
        });
}

async function getCoinData(coinId) {
    return await AXIOS.get(`/coin-data/` + coinId)
        .then(
            response => {
                // returns only JSON object with a coin actual market data
                return response.data.data;
            })
        .catch(error => {
            return Promise.reject(error.response);
        });
}

async function getAllCoinsListData() {
    return await AXIOS.get('/all-coins-list-data')
        .then(
            response => {
                // returns only JSON object with coins actual market data grouped by coin's IDs
                return response.data.data;
            })
        .catch(error => {
            return Promise.reject(error.response);
        });
}