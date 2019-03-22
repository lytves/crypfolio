import {
    PORTFOLIO_ACTUALIZE_ITEM,
    PORTFOLIO_ACTUALIZE_NETCOSTS,
    PORTFOLIO_ADD_TRANSACTION,
    PORTFOLIO_ERROR,
    PORTFOLIO_SUCCESS,
    PORTFOLIO_UPDATE_CURRENCY,
    PORTFOLIO_UPDATE_ITEM_CURRENCY
} from "../actions/portfolio";
import {userPortfolioService} from "../../utils";
import {SNACKBAR_ERROR, SNACKBAR_SUCCESS} from "../actions/snackbar";
import Vue from 'vue'
import {MARKETDATA_ADDCOIN_TO_USERCOINS} from "../actions/marketdata";

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
                dispatch(SNACKBAR_ERROR, "Invalid portfolio currency change request!");
            });
    },
    [PORTFOLIO_ADD_TRANSACTION]: async ({commit, dispatch}, payload) => {

        return await userPortfolioService.addTransaction(payload.payload)
            .then(resp => {

                // parsing response status to check if the coin was added successfully
                // or it already is in the watchlist
                let responseStatus = JSON.parse(resp.status);
                // should parse twice, coz in the backend do twice JSON mapping
                let responseData = JSON.parse(JSON.parse(resp.data));

                if (responseStatus.error_code === 400) {
                    dispatch(SNACKBAR_ERROR, responseStatus.error_message);
                } else {
                    commit(PORTFOLIO_ACTUALIZE_ITEM, JSON.parse(responseData.actualizedItem));

                    // if it's new item, the adds his marketdata to Vuex store userCoinsMarketData
                    commit(MARKETDATA_ADDCOIN_TO_USERCOINS, payload.selectedCoinMarketData);

                    commit(PORTFOLIO_ACTUALIZE_NETCOSTS, JSON.parse(responseData.portfolioNetCosts));

                    dispatch(SNACKBAR_SUCCESS, "The transaction has been processed successfully.");

                    // return TRUE to close modal window
                    return true;
                }
            })
            .catch(err => {
                dispatch(SNACKBAR_ERROR, "Error on adding the transaction to portfolio!");
            });
    },
    [PORTFOLIO_UPDATE_ITEM_CURRENCY]: async ({commit, dispatch}, {coinId, currency}) => {

        return await userPortfolioService.setItemShowedCurrency(coinId, currency)
            .then(resp => {

                commit(PORTFOLIO_UPDATE_ITEM_CURRENCY, {coinId, currency});

            })
            .catch(err => {
                dispatch(SNACKBAR_ERROR, "Error on changing item's currency!");
            })
    },
};

const mutations = {
    [PORTFOLIO_SUCCESS]: (state, portfolio) => {
        state.userPortfolio = portfolio;
        // console.log('portfolio', state.userPortfolio);
    },
    [PORTFOLIO_UPDATE_CURRENCY]: (state, currency) => {
        state.userPortfolio.showedCurrency = currency;
        // console.log('portfolio', state.userPortfolio);
    },
    [PORTFOLIO_ERROR]: (state) => {
        state.userPortfolio = {};
    },
    // to actualize an item with inserting a new transaction or insert new item
    [PORTFOLIO_ACTUALIZE_ITEM]: (state, actualizedItem) => {
        // look up to item's index in the items array to replace
        let itemIndex = state.userPortfolio.items.findIndex(item => item.id === actualizedItem.id);

        // if the item is already in the portfolio, then actualizes them WITH DEEP Vue.set(...) method
        if (itemIndex >= 0) {
            Vue.set(state.userPortfolio.items, itemIndex, actualizedItem); //other way to set Vuex states
            // inserts new item
        } else {
            state.userPortfolio.items.push(actualizedItem);
        }
    },
    [PORTFOLIO_ACTUALIZE_NETCOSTS]: (state, netcosts) => {
        state.userPortfolio.netCostUsd = netcosts.netCostUsd;
        state.userPortfolio.netCostEur = netcosts.netCostEur;
        state.userPortfolio.netCostBtc = netcosts.netCostBtc;
        state.userPortfolio.netCostEth = netcosts.netCostEth;
    },
    [PORTFOLIO_UPDATE_ITEM_CURRENCY]: (state, {coinId, currency}) => {
        // set new currency to the item in store after receive a successfully response from backend
        state.userPortfolio.items.find(i => i.coin.id === coinId).showedCurrency = currency;
    }
};

export default {
    state,
    getters,
    actions,
    mutations,
}