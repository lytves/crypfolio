import {
    WATCHLIST_ADD_NEW_COIN,
    WATCHLIST_CHANGE_COIN_CURRENCY,
    WATCHLIST_ERROR,
    WATCHLIST_SUCCESS
} from '../actions/watchlist'
import {SNACKBAR_ERROR, SNACKBAR_SUCCESS} from "../actions/snackbar";
import {userWatchlistService} from "../../utils";
import {MARKETDATA_ADDCOIN_TO_USERCOINS} from "../actions/marketdata";

const state = {
    userWatchlist: [],
};

const getters = {
    isUserWatchlistLoaded: state => !!state.userWatchlist,
};

const actions = {
    [WATCHLIST_SUCCESS]: ({commit}, watchlist) => {
        commit(WATCHLIST_SUCCESS, watchlist);
    },
    [WATCHLIST_CHANGE_COIN_CURRENCY]: ({commit, dispatch}, {coinId, currency}) => {
        return new Promise((resolve, reject) => {

            return userWatchlistService.setCoinShowedCurrency(coinId, currency)
                .then(resp => {

                    commit(WATCHLIST_CHANGE_COIN_CURRENCY, {coinId, currency});

                })
                .catch(err => {
                    dispatch(SNACKBAR_ERROR, "Error on changing coin's currency!");
                    reject(err)
                })
        })
    },
    [WATCHLIST_ADD_NEW_COIN]: ({commit, dispatch, rootState}, {coinId, currency}) => {
        return new Promise((resolve, reject) => {

            return userWatchlistService.addNewWatchlistCoin(coinId, currency)
                .then(resp => {

                    // parsing response status to check if the coin was added successfully
                    // or it is already in the watchlist
                    let responseStatus = JSON.parse(resp);

                    if (responseStatus.error_code === 400) {
                        dispatch(SNACKBAR_ERROR, "The coin is already in your watchlist!");

                    } else {

                        dispatch(MARKETDATA_ADDCOIN_TO_USERCOINS, coinId)
                            .then(() => {
                                // get new coin's data to add to the user's watchlist store in the mutation
                                let coinIdData = rootState.marketdata.allCoinsListData.find(coin => coin.id === coinId);

                                commit(WATCHLIST_ADD_NEW_COIN, {coinIdData, currency});

                                dispatch(SNACKBAR_SUCCESS, "The coin has been successfully added to your watchlist!");
                                resolve(resp)
                            })
                            .catch(() => {
                                dispatch(SNACKBAR_ERROR, "Error on adding a coin to watchlist!");
                            });
                    }

                    resolve(resp);
                })
                .catch(err => {
                    dispatch(SNACKBAR_ERROR, "Error on adding a coin to watchlist!");
                    reject(err)
                })
        })
    },

};

const mutations = {
    [WATCHLIST_SUCCESS]: (state, watchlist) => {
        state.userWatchlist = watchlist;
    },
    [WATCHLIST_CHANGE_COIN_CURRENCY]: (state, {coinId, currency}) => {

        // set new currency to watchlist coin in store after set successfully response from backend
        state.userWatchlist.find(v => v.coinId.id === coinId).showedCurrency = currency;

        /* let ku = state.userWatchlist.find(function(e) {
            return e.coinId.id === coinId;
        });
        ku.showedCurrency = currency;*/

    },
    [WATCHLIST_ADD_NEW_COIN]: (state, {coinIdData, currency}) => {

        // add to Vuex store "userWatchlist" a new element with data from allCoinsList and showed currency
        state.userWatchlist.push({coinId: coinIdData, showedCurrency: currency});

    },
    [WATCHLIST_ERROR]: (state, message) => {
        // state.userWatchlist = {};
    }
};

export default {
    state,
    getters,
    actions,
    mutations,
}