import Vue from 'vue'
import Vuex from 'vuex'
import user from './modules/user'
import auth from './modules/auth'
import snackbar from './modules/snackbar'
import portfolio from './modules/portfolio'
import watchlist from './modules/watchlist'
import marketdata from './modules/marketdata'
import createLogger from 'vuex/dist/logger';

Vue.use(Vuex);

const debug = process.env.NODE_ENV !== 'production';

export default new Vuex.Store({
    modules: {
        user,
        auth,
        snackbar,
        portfolio,
        watchlist,
        marketdata
    },
    strict: debug,
    plugins: debug ? [createLogger()] : []
})