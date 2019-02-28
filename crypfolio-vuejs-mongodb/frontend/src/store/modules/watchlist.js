import {WATCHLIST_SUCCESS, WATCHLIST_ERROR} from '../actions/watchlist'
import {SNACKBAR_ERROR, SNACKBAR_SUCCESS} from "../actions/snackbar";
import {userWatchlistService} from "../../utils";

const state = {
    userWatchlist: [],
};

const getters = {
    isUserWatchlistLoaded: state => !!state.userWatchlist,
};

const actions = {
    [WATCHLIST_SUCCESS]: ({commit}, watchlist) => {
        commit(WATCHLIST_SUCCESS, watchlist)
    },
};

const mutations = {
    [WATCHLIST_SUCCESS]: (state, watchlist) => {
        state.userWatchlist = watchlist;
        console.log('watchlist!!!', state.userWatchlist);
        if (!!state.userWatchlist)
            console.log('true');
        else
            console.log('false');
    },
    [WATCHLIST_ERROR]: (state) => {
        state.userWatchlist = {};
    }
};
export default {
    state,
    getters,
    actions,
    mutations,
}