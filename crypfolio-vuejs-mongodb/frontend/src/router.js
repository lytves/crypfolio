import Vue from 'vue'
import Router from 'vue-router'
import store from './store'
import LoginPage from '@/components/LoginPage'
import SignupPage from '@/components/SignupPage'
import UserPage from '@/components/UserPage'
import ResetPasswordPage from '@/components/ResetPasswordPage'
import SetNewPasswordPage from '@/components/SetNewPasswordPage'
import ResendVerificationEmailPage from "./components/ResendVerificationEmailPage";
import VefiticationEmailPage from "./components/VefiticationEmailPage";
import NotFoundComponent from "./components/NotFoundComponent";

Vue.use(Router);

const ifNotAuthenticated = (to, from, next) => {
    if (!store.getters.isAuthenticated) {
        next();
        return
    }
    next('/user')
};

const ifAuthenticated = (to, from, next) => {
    if (store.getters.isAuthenticated) {
        next();
        return
    }
    next('/login')
};

export default new Router({
    // https://router.vuejs.org/guide/essentials/history-mode.html#html5-history-mode
    mode: 'history',
    routes: [
        {
            path: '/login',
            name: 'login',
            component: LoginPage,
            beforeEnter: ifNotAuthenticated
        },
        {
            path: '/signup',
            name: 'signup',
            component: SignupPage,
            beforeEnter: ifNotAuthenticated
        },
        {
            path: '/reset-password',
            name: 'reset-password',
            component: ResetPasswordPage,
            beforeEnter: ifNotAuthenticated
        },
        {
            path: '/reset-password/reset-link',
            name: 'reset-password-reset-link',
            component: SetNewPasswordPage,
            beforeEnter: ifNotAuthenticated
        },
        {
            path: '/resend-verif-email',
            name: 'resend-verif-email',
            component: ResendVerificationEmailPage,
            beforeEnter: ifNotAuthenticated
        },
        {
            path: '/verify-email/confirm-link',
            name: 'verify-email-confirm-link',
            component: VefiticationEmailPage,
            beforeEnter: ifNotAuthenticated
        },
        {
            path: '/user',
            name: 'user',
            component: UserPage,
            beforeEnter: ifAuthenticated
        },
        {path: '/', redirect: '/login'},
        // otherwise redirect to the default error page
        {path: '*', component: NotFoundComponent}
    ]
});