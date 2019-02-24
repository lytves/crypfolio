import {AUTH_ERROR, AUTH_LOGOUT, AUTH_REQUEST, AUTH_SUCCESS} from '../actions/auth'
import {USER_REQUEST} from '../actions/user'
import {SNACKBAR_ERROR, SNACKBAR_SUCCESS} from '../actions/snackbar'
import {userAuthService} from '../../utils'
import router from '../../router';

const state = {
    token: localStorage.getItem('token') || '',
    // status: ''
};

const getters = {
    isAuthenticated: state => !!state.token,
};

const actions = {
    [AUTH_REQUEST]: ({commit, dispatch}, {email, password}) => {
        // The Promise is used for redirect router to user page
        return new Promise((resolve, reject) => {
            commit(AUTH_REQUEST);

            return userAuthService.authentication(email, password)
                .then(resp => {

                    commit(AUTH_SUCCESS);
                    dispatch(USER_REQUEST);
                    // dispatch(SNACKBAR_SUCCESS, "Login success!");

                    resolve(resp)
                })
                .catch(err => {
                    commit(AUTH_ERROR);
                    dispatch(SNACKBAR_ERROR, "This user doesn't exist or maybe you" +
                        " didn't confirm your email yet!");
                    localStorage.removeItem('token');
                    reject(err)
                })
        })
    },
    [AUTH_LOGOUT]: ({commit, dispatch}) => {
        return new Promise((resolve, reject) => {

            commit(AUTH_LOGOUT);
            localStorage.removeItem('token');
            // remove the axios default header
            // delete AXIOS.defaults.headers.common['Authorization'];
            router.push('/login');

            resolve();
        })
    }
};

const mutations = {
    [AUTH_REQUEST]: (state) => {
        // state.status = 'loading'
    },
    [AUTH_SUCCESS]: (state) => {
        // state.status = 'success';
        state.token = localStorage.getItem("token");
    },
    [AUTH_ERROR]: (state) => {
    },
    [AUTH_LOGOUT]: (state) => {
        state.token = ''
    }
};

export default {
    state,
    getters,
    actions,
    mutations,
}