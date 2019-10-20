import {
    USER_EMAIL_VERIFICATION,
    USER_ERROR,
    USER_REQUEST,
    USER_RESEND_VERIFICATION_EMAIL_REQUEST,
    USER_RESET_PASSWORD_REQUEST,
    USER_SET_NEW_PASSWORD,
    USER_SIGNUP_REQUEST,
    USER_SUCCESS, USER_UPDATE_PASSWORD
} from '../actions/user'
import {AUTH_LOGOUT, AUTH_SUCCESS} from '../actions/auth'
import {SNACKBAR_ERROR, SNACKBAR_SUCCESS} from "../actions/snackbar";
import {PORTFOLIO_SUCCESS} from "../actions/portfolio";
import {WATCHLIST_SUCCESS} from '../actions/watchlist'
import {MARKETDATA_GLOBAL_MARKET_DATA, MARKETDATA_USERCOINS_SUCCESS} from '../actions/marketdata'
import {userAuthService} from "../../utils"

const state = {
    userProfile: {},
    // status: ''
};

const getters = {
    isUserProfileLoaded: state => !!state.userProfile.id,
};

const actions = {
    [USER_REQUEST]: ({commit, dispatch}) => {
        return new Promise((resolve, reject) => {

            commit(USER_REQUEST);

            return userAuthService.getUser()
                .then(resp => {

                    let user = resp;

                    // save object Portfolio to separated 'store portfolio'
                    dispatch(PORTFOLIO_SUCCESS, user.portfolio);

                    // save array userWatchCoins to separated 'store watchlist'
                    dispatch(WATCHLIST_SUCCESS, user.userWatchCoins);

                    // *************   START:   parse  all user's coins IDs   ************************
                    // dispatch to request marketdata user's coins actual marketdata,
                    // but before we have to complete an array with all user's coins Ids,
                    // also from portfolio && watchlist!!
                    let userPortfolioCoinsIds = [],
                        userWatchlistCoinsIds = [],
                        allUserCoinsIds = [];

                    userPortfolioCoinsIds = user.portfolio.items.map(function (e) {
                        return e.coin.id;
                    });
                    userWatchlistCoinsIds = user.userWatchCoins.map(function (e) {
                        return e.coinId.id;
                    });

                    allUserCoinsIds = JSON.stringify(userPortfolioCoinsIds.concat(userWatchlistCoinsIds));

                    dispatch(MARKETDATA_USERCOINS_SUCCESS, allUserCoinsIds);
                    // *************   FINISH:  parse all user's coins IDs   ************************

                    // get global market data to show on watchlist tab
                    dispatch(MARKETDATA_GLOBAL_MARKET_DATA);

                    commit(USER_SUCCESS, user);

                    // and also store new auth token to auth.store.sate
                    commit(AUTH_SUCCESS);

                })
                .catch(err => {
                    commit(USER_ERROR, err);
                    // if resp is unauthorized, logout
                    dispatch(AUTH_LOGOUT);
                })
        })
    },
    [USER_SIGNUP_REQUEST]: ({commit, dispatch}, {email, password, portfolio}) => {
        return new Promise((resolve, reject) => {

            return userAuthService.registration(email, password, portfolio)
                .then(resp => {

                    dispatch(SNACKBAR_SUCCESS, "Success registration!");
                    resolve(resp)

                })
                .catch(err => {
                    commit(USER_ERROR, err);
                    dispatch(SNACKBAR_ERROR, "Invalid User Registration Credentials! Maybe you" +
                        " already have an account");
                    reject(err)
                })
        })
    },
    [USER_RESET_PASSWORD_REQUEST]: ({commit, dispatch}, email) => {
        return new Promise((resolve, reject) => {

            return userAuthService.resetPasswordRequest(email)
                .then(resp => {

                    dispatch(SNACKBAR_SUCCESS, "Success reset password request!");
                    resolve(resp)

                })
                .catch(err => {
                    commit(USER_ERROR, err);
                    dispatch(SNACKBAR_ERROR, "Cannot reset password! Maybe you are not registered yet!");
                    reject(err)
                })
        })
    },
    [USER_SET_NEW_PASSWORD]: ({commit, dispatch}, {code, password}) => {
        return new Promise((resolve, reject) => { // The Promise is used for router redirect to user page

            return userAuthService.setNewPassword(code, password)
                .then(resp => {

                    commit(AUTH_SUCCESS);
                    dispatch(USER_REQUEST);
                    dispatch(SNACKBAR_SUCCESS, "Success setting new password!");
                    resolve(resp)

                })
                .catch(err => {
                    commit(USER_ERROR, err);
                    dispatch(SNACKBAR_ERROR, "Cannot set new password! Maybe your code is incorrect or expired!");
                    reject(err)
                })
        })
    },
    [USER_RESEND_VERIFICATION_EMAIL_REQUEST]: ({commit, dispatch}, email) => {
        return new Promise((resolve, reject) => {

            return userAuthService.resendVerificationEmailRequest(email)
                .then(resp => {

                    dispatch(SNACKBAR_SUCCESS, "A verification email was sent to you");
                    resolve(resp)

                })
                .catch(err => {
                    commit(USER_ERROR, err);
                    dispatch(SNACKBAR_ERROR, "Cannot send verification email!" +
                        " You are not registered yet or your email is already verified!");
                    reject(err)
                })
        })
    },
    [USER_EMAIL_VERIFICATION]: ({commit, dispatch}, code) => {
        return new Promise((resolve, reject) => { // The Promise used for router redirect in user page

            return userAuthService.emailVerification(code)
                .then(resp => {

                    commit(AUTH_SUCCESS);
                    dispatch(USER_REQUEST);
                    dispatch(SNACKBAR_SUCCESS, "Success email verification!");
                    resolve(resp)

                })
                .catch(err => {
                    commit(USER_ERROR, err);
                    dispatch(SNACKBAR_ERROR, "Cannot verify your email!");
                    reject(err)
                })
        })
    },
    [USER_UPDATE_PASSWORD]: async ({commit, dispatch}, {oldPassword, password}) => {

            return await userAuthService.updatePassword(oldPassword, password)
                .then(resp => {

                    dispatch(SNACKBAR_SUCCESS, "Success update password!");
                    return true;

                })
                .catch(err => {
                    dispatch(SNACKBAR_ERROR, "Cannot update password! Maybe you typed the incorrect current password!");
                })
    },
};

const mutations = {
    [USER_REQUEST]: (state) => {
        // state.status = 'loading'
    },
    [USER_SUCCESS]: (state, user) => {
        // state.status = 'success';
        // Vue.set(state, 'userProfile', resp); //other way to set Vuex states

        // store to userProfile only user Object (without portfolio, userWatchCoins, etc.)
        ['portfolio', 'password', 'userWatchCoins', 'emailVerifCode', 'emailVerifCodeRequestDateTime',
            'passwordResetCode', 'passwordResetCodeRequestDateTime'].forEach(function (k) {
            delete user[k];
        });
        // console.log('DELETE user.portfolio, password, userWatchCoins, emailVerifCode, ....');
        state.userProfile = user;
    },
    [USER_ERROR]: (state) => {
        // state.status = 'error'
        state.userProfile = {}
    }
};

export default {
    state,
    getters,
    actions,
    mutations,
}