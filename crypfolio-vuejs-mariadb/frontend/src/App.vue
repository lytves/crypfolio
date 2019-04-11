<template>

    <v-app>
        <v-container class="pa-0" fill-height>
            <v-layout row wrap>

                <v-content class="content-container">

                    <!-- content -->
                    <router-view  class="main-content"/>

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
            document.title = "Crypfolio";
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
    .content-container .v-content__wrap {
        display: flex;
        min-height: 100vh;
        flex-direction: column;
    }
    .main-content {
        flex: 1;
        max-width: 1300px;
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
    .cursorPointer {
        cursor: pointer;
    }
    .v-dialog.v-bottom-sheet {
        height: 90% !important;
    }
    .background-grey, .v-expansion-panel__header {
        background-color: #e8e8e8;
    }
    .table-transactions table.v-table thead tr {
        height: 30px;
    }
    .trans-menu a.v-list__tile {
        height: 30px !important;
    }
</style>