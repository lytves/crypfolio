import {SNACKBAR_CLEAR, SNACKBAR_ERROR, SNACKBAR_SUCCESS} from '../actions/snackbar'
// is it needed to import it here??? import Vue from 'vue'

const state = {
    snackState: false,
    snackType: '',
    snackMessage: ''
};

const actions = {
    [SNACKBAR_SUCCESS]: ({commit}, message) => {
        // if new snackbar arrived, clear old and shows new one
        commit(SNACKBAR_CLEAR);
        setTimeout(() => {
            commit(SNACKBAR_SUCCESS, message);
        }, 250)
    },
    [SNACKBAR_ERROR]: ({commit}, message) => {
        // if new snackbar arrived, clear old and shows new one
        commit(SNACKBAR_CLEAR);
        setTimeout(() => {
            commit(SNACKBAR_ERROR, message);
            }, 250)
    }
};

const mutations = {
    [SNACKBAR_SUCCESS]: (state, message) => {
        state.snackState = true;
        state.snackType = 'success';
        state.snackMessage = message;
    },
    [SNACKBAR_ERROR]: (state, message) => {
        state.snackState = true;
        state.snackType = 'error';
        state.snackMessage = message;
    },
    [SNACKBAR_CLEAR]: (state, value) => {
        state.snackState = false;
    }
};

export default {
    state,
    actions,
    mutations,
}