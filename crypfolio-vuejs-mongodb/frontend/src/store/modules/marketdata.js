import {MARKETDATA_ALLCOINSLIST_SUCCESS, MARKETDATA_ERROR, MARKETDATA_USERCOINS_SUCCESS} from '../actions/marketdata'
import {marketdataService} from "../../utils";
import {SNACKBAR_ERROR} from "../actions/snackbar";

const state = {
    userCoinsMarketData: [],
    allCoinsListData: [],
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
        console.log('userCoinsMarketData!!!', state.userCoinsMarketData);
    },
    [MARKETDATA_ALLCOINSLIST_SUCCESS]: (state, allCoinsListData) => {

        state.allCoinsListData = allCoinsListData;
        console.log('allCoinsListData!!!', state.allCoinsListData);
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