import {WATCHLIST_CHANGE_COIN_CURRENCY, WATCHLIST_ERROR, WATCHLIST_SUCCESS} from '../actions/watchlist'
import {SNACKBAR_ERROR} from "../actions/snackbar";
import {userWatchlistService} from "../../utils";

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
};

const mutations = {
    [WATCHLIST_SUCCESS]: (state, watchlist) => {
        state.userWatchlist = watchlist;
        console.log('watchlist!!!', state.userWatchlist);
    },
    [WATCHLIST_CHANGE_COIN_CURRENCY]: (state, {coinId, currency}) => {

        state.userWatchlist.find(v => v.coinId.id === coinId).showedCurrency = currency;

        /* let ku = state.userWatchlist.find(function(e) {
            return e.coinId.id === coinId;
        });
        ku.showedCurrency = currency;*/

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
            console.log('fuck intervaaaaaaaaaaaaaaaaal');
        }, 5000)*!/
    },*/