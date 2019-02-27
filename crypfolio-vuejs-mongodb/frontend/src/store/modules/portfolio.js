import {PORTFOLIO_ERROR, PORTFOLIO_SUCCESS, PORTFOLIO_UPDATE_CURRENCY} from "../actions/portfolio";
import {userPortfolioService} from "../../utils";
import {SNACKBAR_ERROR} from "../actions/snackbar";

const state = {
    userPortfolio: {},
};

const getters = {
    isUserPortfolioLoaded: state => !!state.userPortfolio.id,
};

const actions = {
    [PORTFOLIO_SUCCESS]: ({commit}, portfolio) => {
        commit(PORTFOLIO_SUCCESS, portfolio)
    },
    [PORTFOLIO_UPDATE_CURRENCY]: ({commit, dispatch}, currency) => {

        userPortfolioService.setPortfolioShowedCurrency(currency)
            .then(resp => {

                commit(PORTFOLIO_UPDATE_CURRENCY, currency);
            })
            .catch(err => {
                console.log('err', err);
                dispatch(SNACKBAR_ERROR, "Invalid portfolio currency change request!");
            });
    },
};

const mutations = {
    [PORTFOLIO_SUCCESS]: (state, portfolio) => {
        state.userPortfolio = portfolio;
        console.log('portfolio', state.portfolio);
    },
    [PORTFOLIO_UPDATE_CURRENCY]: (state, currency) => {
        state.userPortfolio.showedCurrency = currency;
        console.log('portfolio', state.userPortfolio);
    },
    [PORTFOLIO_ERROR]: (state) => {
        state.userPortfolio = {};
    }
};

export default {
    state,
    getters,
    actions,
    mutations,
}