import Vue from 'vue'
import Vuex from 'vuex'
import user from './modules/user'
import auth from './modules/auth'
import snackbar from './modules/snackbar'
import portfolio from './modules/portfolio'

Vue.use(Vuex);

const debug = process.env.NODE_ENV !== 'production';

export default new Vuex.Store({
    modules: {
        user,
        auth,
        snackbar,
        portfolio
    },
    strict: debug,
})