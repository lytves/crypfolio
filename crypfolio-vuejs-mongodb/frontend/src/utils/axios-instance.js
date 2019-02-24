// should import axios from 'axios'; always here
import axios from 'axios';
import store from '../store'
import {SNACKBAR_ERROR, SNACKBAR_SUCCESS} from '../store/actions/snackbar'
import {AUTH_LOGOUT} from '../store/actions/auth'

// application shared axios instance
const AXIOS = axios.create({
    baseURL: '/api',
    headers: {
        'Content-Type': 'application/json'
    }
});

// ********     ADD REQUEST INTERCEPTORS     ********
AXIOS.interceptors.request.use(config => {
    // Do something before request is sent:

    console.log('AXIOS.interceptors.request "' + config.baseURL + config.url + '" --- Ok');

    // when user does a refresh the page (app) or any also API request,
    // let's check if it has the authorization token in the localStorage and add it to request header
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.authorization = `Bearer ${token}`;
    }
    return config;

}, error => {
    // Do something with request error:

    console.log('AXIOS.interceptors.request "' + config.baseURL + config.url + '" --- ERROR');
    return Promise.reject(error);
});

// ********     ADD RESPONSE INTERCEPTORS     ********
AXIOS.interceptors.response.use(response => {
    // Do something with response data:

    console.log('AXIOS.interceptors.response "' + response.config.url + '" --- Ok');

    // every successful response from server with authorization token in headers,
    // let's save it to the localStorage
    localStorage.setItem('token', response.headers['authorization']);
    return response;

}, error => {
    // Do something with response error

    // if you ever get an unauthorized, logout the user
    if (error.response.status === 401 && error.config && !error.config.__isRetryRequest) {

        store.dispatch(SNACKBAR_ERROR, "Your authorization token is expired or invalid!");
        store.dispatch(AUTH_LOGOUT);
    }
    console.log('AXIOS.interceptors.response --- ERROR');
    return Promise.reject(error);
});

export default AXIOS;