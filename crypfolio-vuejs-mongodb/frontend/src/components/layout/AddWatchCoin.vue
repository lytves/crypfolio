<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <div class="text-xs-center">
        <v-dialog
                lazy
                v-model="show"
                @keydown.esc="show = false"
                width="500">

            <v-card>
                <v-card-title
                        class="headline"
                        primary-title>
                    Add New Coin
                </v-card-title>

                <v-card-text>
                    <v-autocomplete
                            v-model="model"
                            :items="items"
                            :search-input.sync="search"
                            clearable
                            hide-details
                            hide-selected
                            solo
                            label="Search a Coin by Name or Ticker..."
                            :item-text="parseMatchedItem"
                            item-value="id"
                            :readonly="isReadonly"
                            autofocus
                            single-line
                            @change="changeSelected"
                            @click:clear="clearForm">
                        <template v-slot:no-data>

                            <v-list-tile>
                                <v-list-tile-title>
                                    Search for your favorite
                                    <strong>Cryptocurrency</strong>
                                </v-list-tile-title>
                            </v-list-tile>

                        </template>

                        <template v-slot:selection="{ item, selected }">

                            <img class="pa-2" :src="showCoinImage(item.id)"/>
                            <span v-text="item.name"></span>
                            <span class="pa-2 grey--text" v-text="item.symbol"></span>

                        </template>

                        <template v-slot:item="{ item }">

                            <img class="pa-2" :src="showCoinImage(item.id)"/>

                            <v-list-tile-title v-text="item.name"></v-list-tile-title>

                            <v-list-tile-action>
                                <v-list-tile-sub-title v-text="item.symbol"></v-list-tile-sub-title>
                            </v-list-tile-action>

                        </template>

                    </v-autocomplete>
                </v-card-text>

                <div class="text-xs-center" v-show="model">
                    <div class="text-sm-left ma-3">
                        <span class="grey--text">Currency *</span>
                    </div>
                    <v-btn :class="{'disable-events': showShowedCurrency('USD')}" @click="changeShowedCurrency('USD')">
                        USD
                    </v-btn>
                    <v-btn :class="{'disable-events': showShowedCurrency('EUR')}" @click="changeShowedCurrency('EUR')">
                        EUR
                    </v-btn>
                    <v-btn :class="{'disable-events': showShowedCurrency('BTC')}" @click="changeShowedCurrency('BTC')">
                        BTC
                    </v-btn>
                    <v-btn :class="{'disable-events': showShowedCurrency('ETH')}" @click="changeShowedCurrency('ETH')">
                        ETH
                    </v-btn>

                    <v-divider class="ma-3"></v-divider>

                    <v-spacer></v-spacer>
                    <v-btn
                            color="primary"
                            @click="clearForm">
                        Reset
                    </v-btn>
                    <v-btn
                            color="primary"
                            @click="addNewWatchCoin">
                        Done
                    </v-btn>
                </div>

            </v-card>
        </v-dialog>
    </div>
</template>

<script>
    import {mapGetters, mapState} from 'vuex'
    import {WATCHLIST_ADD_NEW_COIN} from "../../store/actions/watchlist";

    export default {
        name: "AddWatchCoin",
        props: {
            value: Boolean
        },
        data: () => ({
            items: [],
            search: null,
            isReadonly: false,
            model: null,
            currency: 'USD',
        }),
        computed: {
            show: {
                get() {
                    return this.value
                },
                set(value) {
                    this.$emit('input', value)
                }
            },
            ...mapGetters(['isAllCoinsListDataLoaded']),
            ...mapState({
                allCoinsListData: state => state.marketdata.allCoinsListData
            }),

        },
        watch: {
            search(val) {
                // search coins in allCoinsListData which matched with "search input string"
                if (val && val.trim() !== '') {

                    this.items = this.allCoinsListData.filter(item => (item.name.toLowerCase().includes(val.toLowerCase())
                        || item.symbol.toLowerCase().includes(val.toLowerCase())));
                }
            }
            ,
            show(val) {
                // on close modal window -> run clearForm() to reset all component's variables
                !val && this.clearForm()
            }
        },
        methods: {
            showCoinImage(id) {
                if (this.isAllCoinsListDataLoaded) {
                    return 'https://s2.coinmarketcap.com/static/img/coins/32x32/' + id + '.png'
                }
                return '@/assets/coin-default.png'
            },
            changeSelected() {
                if (this.model) {

                    this.isReadonly = true;
                }
            },
            clearForm() {
                // reset all component's variables
                Object.assign(this.$data, this.$options.data())
            },
            parseMatchedItem(matchedItem) {
                // !!! here is defined that item-text need matched on "searching by coin name && coin symbol"
                return [matchedItem.name, matchedItem.symbol];
            },
            showShowedCurrency(currency) {
                return this.currency === currency;
            },
            changeShowedCurrency(currency) {
                switch (currency) {
                    case 'USD':
                        this.currency = 'USD';
                        break;
                    case 'EUR':
                        this.currency = 'EUR';
                        break;
                    case 'BTC':
                        this.currency = 'BTC';
                        break;
                    case 'ETH':
                        this.currency = 'ETH';
                        break;
                }
            },
            addNewWatchCoin() {
                const payload = {'coinId': this.model, 'currency': this.currency};
                this.$store.dispatch(WATCHLIST_ADD_NEW_COIN, payload)
                    .then(() => {
                        this.show = false;
                    })
                    .catch(() => {})
            }
        }
    }
</script>

<style scoped>

</style>