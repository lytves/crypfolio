import {
    MARKETDATA_ADDCOIN_TO_USERCOINS,
    MARKETDATA_ALLCOINSLIST_SUCCESS,
    MARKETDATA_ERROR,
    MARKETDATA_GLOBAL_MARKET_DATA,
    MARKETDATA_USERCOINS_SUCCESS
} from '../actions/marketdata'
import {marketdataService} from "../../utils";
import {SNACKBAR_ERROR} from "../actions/snackbar";
import {config} from '../../config'

const state = {
    userCoinsMarketData: {},
    allCoinsListData: [],
    globalMarketData: {},
};

const getters = {
    isUserCoinsMarketDataLoaded: state =>
        Object.keys(state.userCoinsMarketData).length !== 0 && state.userCoinsMarketData.constructor === Object,
    isAllCoinsListDataLoaded: state => (Array.isArray(state.allCoinsListData) && state.allCoinsListData.length > 0),
    isGlobalMarketDataLoaded: state =>
        Object.keys(state.globalMarketData).length !== 0 && state.globalMarketData.constructor === Object,
};

const actions = {
    [MARKETDATA_USERCOINS_SUCCESS]: ({commit, dispatch}, allUserCoinsIds) => {

        let timerId = setTimeout(function tick() {

            console.log('%c MARKETDATA_USERCOINS refresh!', 'color: #4caf50');

            marketdataService.getUserCoinsData(allUserCoinsIds)
                .then(resp => {

                    commit(MARKETDATA_USERCOINS_SUCCESS, resp)
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

                    commit(MARKETDATA_ADDCOIN_TO_USERCOINS, resp);
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

                        commit(MARKETDATA_ALLCOINSLIST_SUCCESS, resp)
                    })
                    .catch(err => {
                        dispatch(SNACKBAR_ERROR, "Error on receiving actual all coins data!");
                        reject(err)
                    })
            }
        })
    },
    [MARKETDATA_GLOBAL_MARKET_DATA]: async ({commit}) => {

        return await marketdataService.getGlobalMarketData()
            .then(resp => {
                commit(MARKETDATA_GLOBAL_MARKET_DATA, resp)
            })
    }
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
    [MARKETDATA_GLOBAL_MARKET_DATA]: (state, globalMarketData) => {

        state.globalMarketData = globalMarketData;
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