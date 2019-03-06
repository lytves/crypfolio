import {MARKETDATA_ERROR, MARKETDATA_LISTINGS_SUCCESS, MARKETDATA_USERCOINS_SUCCESS} from '../actions/marketdata'
import {marketdataService} from "../../utils";
import {SNACKBAR_ERROR} from "../actions/snackbar";

const state = {
    userCoinsMarketData: [],
};

const getters = {
    isUserCoinsMarketDataLoaded: state => !!state.userCoinsMarketData,
};

const actions = {
    [MARKETDATA_USERCOINS_SUCCESS]: ({commit, dispatch}, allUserCoinsIds) => {
        return new Promise((resolve, reject) => {

            return marketdataService.getUserCoinsData(allUserCoinsIds)
                .then(resp => {

                    // parsing of response to have a map like a JSON
                    let userCoinsMarketData = JSON.parse(resp);

                    commit(MARKETDATA_USERCOINS_SUCCESS, userCoinsMarketData)
                })
                .catch(err => {
                    dispatch(SNACKBAR_ERROR, "Error on receiving actual market data!");
                    reject(err)
                })
        })
    }
};

const mutations = {
    [MARKETDATA_USERCOINS_SUCCESS]: (state, userCoinsMarketData) => {

        state.userCoinsMarketData = userCoinsMarketData;
        console.log('userCoinsMarketData!!!', state.userCoinsMarketData);
    },
    [MARKETDATA_ERROR]: (state) => {
        // state.userWatchlist = {};
    }
};

export default {
    state,
    getters,
    actions,
    mutations,
}