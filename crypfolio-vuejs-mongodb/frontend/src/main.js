// Packages
import Vue from 'vue'
import Vuetify from 'vuetify'
import 'vuetify/dist/vuetify.min.css' // Ensure you are using css-loader
import VueClipboard from 'vue-clipboard2'
import router from './router'
import store from './store'
import './filters'

// Application
import App from './App.vue'

Vue.config.productionTip = false;

Vue.use(Vuetify);
Vue.use(VueClipboard);

new Vue({
    router,
    store,
    render: h => h(App)
}).$mount('#app');