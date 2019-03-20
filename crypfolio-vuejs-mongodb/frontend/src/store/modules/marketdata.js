import {
    MARKETDATA_ADDCOIN_TO_USERCOINS,
    MARKETDATA_ALLCOINSLIST_SUCCESS,
    MARKETDATA_ERROR,
    MARKETDATA_USERCOINS_SUCCESS
} from '../actions/marketdata'
import {marketdataService} from "../../utils";
import {SNACKBAR_ERROR} from "../actions/snackbar";
import {config} from '../../config'

const state = {
    userCoinsMarketData: {},
    allCoinsListData: [],
};

const getters = {
    isUserCoinsMarketDataLoaded: state =>
        Object.keys(state.userCoinsMarketData).length !== 0 && state.userCoinsMarketData.constructor === Object,
    isAllCoinsListDataLoaded: state => (Array.isArray(state.allCoinsListData) && state.allCoinsListData.length > 0),
};

const actions = {
    [MARKETDATA_USERCOINS_SUCCESS]: ({commit, dispatch}, allUserCoinsIds) => {

        let timerId = setTimeout(function tick() {

            console.log('%c MARKETDATA_USERCOINS refresh!', 'color: #4caf50');

            marketdataService.getUserCoinsData(allUserCoinsIds)
                .then(resp => {

                    // parsing of response to have a map like a JSON
                    let userCoinsMarketData = JSON.parse(resp);

                    commit(MARKETDATA_USERCOINS_SUCCESS, userCoinsMarketData)
                })
                .catch(err => {
                    dispatch(SNACKBAR_ERROR, "Error on receiving actual market data!");
                    reject(err)
                });

            timerId = setTimeout(tick, config.marketdata_refresh_timeout);
        }, 0);

    },
    [MARKETDATA_ADDCOIN_TO_USERCOINS]: ({commit, dispatch}, coinId) => {
        return new Promise((resolve, reject) => {

            return marketdataService.getCoinData(coinId)
                .then(resp => {

                    // parsing of response to have a map like a JSON
                    let coinMarketData = JSON.parse(resp);

                    commit(MARKETDATA_ADDCOIN_TO_USERCOINS, coinMarketData);
                    resolve(resp)
                })
                .catch(err => {
                    dispatch(SNACKBAR_ERROR, "Error on receiving actual market data!");
                    reject(err)
                })
        })
    },
    [MARKETDATA_ALLCOINSLIST_SUCCESS]: ({commit, dispatch}) => {
        return new Promise((resolve, reject) => {

            if (Array.isArray(state.allCoinsListData) && state.allCoinsListData.length === 0) {

                return marketdataService.getAllCoinsListData()
                    .then(resp => {

                        // parsing of response to have a map like a JSON
                        let allCoinsListData = JSON.parse(resp);

                        commit(MARKETDATA_ALLCOINSLIST_SUCCESS, allCoinsListData)
                    })
                    .catch(err => {
                        dispatch(SNACKBAR_ERROR, "Error on receiving actual all coins data!");
                        reject(err)
                    })
            }
        })
    },
};

const mutations = {
    [MARKETDATA_USERCOINS_SUCCESS]: (state, userCoinsMarketData) => {

        state.userCoinsMarketData = userCoinsMarketData;

    },
    [MARKETDATA_ADDCOIN_TO_USERCOINS]: (state, coinMarketData) => {

        // add to Object (!!!) userCoinsMarketData new coin
        state.userCoinsMarketData = Object.assign(state.userCoinsMarketData, coinMarketData);
    },
    [MARKETDATA_ALLCOINSLIST_SUCCESS]: (state, allCoinsListData) => {

        state.allCoinsListData = allCoinsListData;
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