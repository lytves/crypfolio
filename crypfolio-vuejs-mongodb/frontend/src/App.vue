<template>

    <v-app>
        <v-container class="pa-0" fill-height>
            <v-layout row wrap>

                <v-content>

                    <!-- content -->
                    <router-view/>

                    <!-- footer -->
                    <Footer></Footer>

                    <!-- snackbar -->
                    <v-snackbar
                            v-model="snackbar"
                            :color="snackType"
                            top right
                            absolute
                            multi-line>
                        {{ snackMessage }}
                    </v-snackbar>

                </v-content>

            </v-layout>
        </v-container>
    </v-app>

</template>

<script>
    import {mapState} from 'vuex'
    import Footer from '@/components/layout/Footer'
    import {USER_REQUEST} from './store/actions/user'
    import {SNACKBAR_CLEAR} from './store/actions/snackbar'

    export default {
        name: 'app',
        components: {
            Footer
        },
        computed: {
            ...mapState({
                snackType: state => state.snackbar.snackType,
                snackMessage: state => state.snackbar.snackMessage,
            }),
            // https://vuex.vuejs.org/guide/forms.html#two-way-computed-property
            // Two-way Computed Property
            snackbar: {
                get() {
                    return this.$store.state.snackbar.snackState
                },
                set(value) {
                    // the better way is to call $store.dispatch, but I left it here in this way
                    // just to know that it possible to do a commit directly to the vuex store
                    this.$store.commit(SNACKBAR_CLEAR, value)
                }
            }
        },
        created: function () {
            // each time when the authenticated user makes a refresh of the page - calling API request
            // to retrieve new user's data
            if (this.$store.getters.isAuthenticated) {
                this.$store.dispatch(USER_REQUEST)
            }
        }
    }
</script>

<style>
    @import url('https://fonts.googleapis.com/css?family=Material+Icons');
    @import url('https://use.fontawesome.com/releases/v5.7.2/css/all.css');

    body {
        background-color: #ffffff;
        /*font-size: 13px;*/
        margin: auto;
    }
    ul {
        margin: 0;
        padding: 0;
        list-style-type: none;
    }
    #app {
        min-height: 100vh;
    }
    footer {
        position: absolute;
        bottom: 0;
        width: 100%;
    }
    .selectsFlexBasis {
        flex-basis: 0 !important;
    }
    .v-dialog {
        top: 70px;
        position: absolute;
    }
    .disable-events {
        pointer-events: none;
        background-color: #82B1FF !important;
        color: white !important;
    }
    .inputNumbersWithoutSpin input[type='number'] {
        -moz-appearance: textfield;
    }
    .inputNumbersWithoutSpin input::-webkit-outer-spin-button,
    .inputNumbersWithoutSpin input::-webkit-inner-spin-button {
        -webkit-appearance: none;
    }
    .v-text-field__suffix {
        margin: 0 10px;
    }
    input[type='number'] {
        margin: 0 10px;
    }
</style>