import AXIOS from './axios-instance';

export const marketdataService = {
    getUserCoinsData,
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