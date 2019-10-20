import AXIOS from './axios-instance';

export const userAuthService = {
    authentication,
    getUser,
    registration,
    resetPasswordRequest,
    setNewPassword,
    resendVerificationEmailRequest,
    emailVerification,
    updatePassword
};

async function authentication(email, password) {
    return await AXIOS.post('/authentication', {
        "email": email,
        "password": password
    })
        .then(
            response => {
                // we don't need to pass any data,
                // coz it's a request only for check login credentials and for receive auth token
                // and set response.headers['authorization'] to interceptors
            })
        .catch(error => {
            return Promise.reject(error.response);
        });
}

async function getUser() {
    return await AXIOS.get('/user')
        .then(
            response => {
                // returns only JSON object User
                // and set response.headers['authorization'] to interceptors
                return response.data.data;
            })
        .catch(error => {
            return Promise.reject(error.response);
        });
}

async function registration(email, password, portfolio) {
    return await AXIOS.post('/registration', {
        "email": email,
        "password": password,
        "portfolioName": portfolio,
    })
        // we don't need to pass any data,
        // coz if response was success, so registration has been done successfully
        .then()
        .catch((error) => {
            return Promise.reject(error.response);
        });
}

async function resetPasswordRequest(email) {
    return await AXIOS.post('/reset-password/request', {
        "email": email,
    })
        // we don't need to pass any data,
        // coz if response was success, so resetPasswordRequest has been done successfully
        .then()
        .catch(error => {
            return Promise.reject(error.response);
        });
}

async function setNewPassword(code, password) {
    return await AXIOS.post('/reset-password', {
        "code": code,
        "password": password
    })
        .then(
            response => {
                // we don't need to pass any data,
                // coz it's a request for pass new password and for receive auth token
                // and set response.headers['authorization'] to interceptors
            })
        .catch(error => {
            return Promise.reject(error.response);
        });
}

async function resendVerificationEmailRequest(email) {
    return await AXIOS.post('/verify-email/request', {
        "email": email,
    })
        // we don't need to pass any data
        // coz if response was success, so resendVerificationEmail has been done successfully
        .then()
        .catch(error => {
            return Promise.reject(error.response);
        });
}

async function emailVerification(code) {
    return await AXIOS.post('/verify-email', {
        "code": code,
    })
        .then(
            response => {
                // we don't need to pass any data,
                // coz it's a request for check emailVerification code and for receive auth token
                // and set response.headers['authorization'] to interceptors
            })
        .catch(error => {
            return Promise.reject(error.response);
        });
}

async function updatePassword(oldPassword, newPassword) {
    return await AXIOS.post('/update-password', {
        "oldPassword": oldPassword,
        "newPassword": newPassword
    })
        .then(
            response => {
                // returns only JSON object User
                // and set response.headers['authorization'] to interceptors
                return response.data.data;
            })
        .catch(error => {
            return Promise.reject(error.response);
        });
}