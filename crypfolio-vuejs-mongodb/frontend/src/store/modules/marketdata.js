import {
    MARKETDATA_ADDCOIN_TO_USERCOINS,
    MARKETDATA_ALLCOINSLIST_SUCCESS,
    MARKETDATA_ERROR,
    MARKETDATA_USERCOINS_SUCCESS
} from '../actions/marketdata'
import {marketdataService} from "../../utils";
import {SNACKBAR_ERROR} from "../actions/snackbar";

const state = {
    userCoinsMarketData: {},
    allCoinsListData: [],
};

const getters = {
    isUserCoinsMarketDataLoaded: state => !!state.userCoinsMarketData,
    isAllCoinsListDataLoaded: state => (Array.isArray(state.allCoinsListData) && state.allCoinsListData.length > 0),
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

/*    [WATCHLIST_REQUESSSSSSSSSSST]: ({commit}, watchlist) => {
        commit(WATCHLIST_SUCCESS, watchlist);

        let timerId = setTimeout(function tick() {

            console.log('tick');

            userPortfolioService.getApplicationContainer()
                .then(resp => {

                    console.log(resp);
/!*                    // parsing of response to have User entity as JSON
                    let user = JSON.parse(resp);

                    // save object Portfolio to separated 'store portfolio'
                    dispatch(PORTFOLIO_SUCCESS, user.portfolio);

                    // save array userWatchCoins to separated 'store watchlist'
                    dispatch(WATCHLIST_SUCCESS, user.userWatchCoins);

                    commit(USER_SUCCESS, user);

                    // and also store new auth token to auth.store.sate
                    commit(AUTH_SUCCESS);*!/

                })
                .catch(err => {
                    // commit(USER_ERROR, err);
                    // if resp is unauthorized, logout
                    // dispatch(AUTH_LOGOUT)
                });

            timerId = setTimeout(tick, 60000);
        }, 0);



        /!*        setInterval(() => {
            console.log('intervaaaaaaaaaaaaaaaaal');
        }, 5000)*!/
    },*/