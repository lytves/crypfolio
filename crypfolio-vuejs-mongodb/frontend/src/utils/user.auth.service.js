import AXIOS from './axios-instance';

export const userAuthService = {
    authentication,
    getUser,
    registration,
    resetPasswordRequest,
    setNewPassword,
    resendVerificationEmailRequest,
    emailVerification
};

async function authentication(email, password) {
    return AXIOS.post('/authentication', {
        "email": email,
        "password": password
    })
        .then(
            response => {
                // console.log(email, password);

                // we don't need to pass any data,
                // coz it's a request only for check login credentials and for receive auth token
                // and set response.headers['authorization'] to interceptors
            })
        .catch(error => {
            return Promise.reject(error.response);
        });
}

async function getUser() {
    return AXIOS.get('/user')
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
    return AXIOS.post('/registration', {
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
    return AXIOS.post('/reset-password/request', {
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
    return AXIOS.post('/reset-password', {
        "code": code,
        "password": password
    })
        .then(
            response => {
                // console.log(code, password);

                // we don't need to pass any data,
                // coz it's a request for pass new password and for receive auth token
                // and set response.headers['authorization'] to interceptors
            })
        .catch(error => {
            return Promise.reject(error.response);
        });
}

async function resendVerificationEmailRequest(email) {
    return AXIOS.post('/verify-email/request', {
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
    return AXIOS.post('/verify-email', {
        "code": code,
    })
        .then(
            response => {
                // console.log(code);

                // we don't need to pass any data,
                // coz it's a request for check emailVerification code and for receive auth token
                // and set response.headers['authorization'] to interceptors
            })
        .catch(error => {
            return Promise.reject(error.response);
        });
}