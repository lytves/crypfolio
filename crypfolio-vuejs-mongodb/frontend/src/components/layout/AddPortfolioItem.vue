<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <v-dialog
            lazy
            v-model="show"
            @keydown.esc="show = false"
            width="400"
            class="text-xs-center"
            scrollable>

        <v-card>
            <v-card-title v-if="!hideAutocompleteForm"
                          class="headline pa-3 ma-0"
                          primary-title>
                Add New Item
            </v-card-title>

            <!--// header when user have chose a coin to add new item-->
            <v-card-title class="headline pa-2 ma-0"
                          v-if="hideAutocompleteForm && (typeof selectedCoin.id === 'number')">

                <v-layout align-center>

                    <img class="pa-2" :src="showCoinImage(selectedCoin.id)"/>
                    <span v-text="selectedCoin.name"></span>
                    <span class="pa-2 grey--text" v-text="selectedCoin.symbol"></span>

                    <v-spacer></v-spacer>

                </v-layout>

            </v-card-title>

            <!--// autocomplete from to search a coin and then to add an item-->
            <v-card-text v-if="!hideAutocompleteForm">
                <v-autocomplete
                        v-model="selectedCoin"
                        return-object
                        dense
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

                    <!--// a message that appear when there is no coins matched to search string-->
                    <template v-slot:no-data>

                        <v-list-tile>
                            <v-list-tile-title>
                                Search a <strong>Cryptocoin</strong> to add
                            </v-list-tile-title>
                        </v-list-tile>

                    </template>

                    <!--// selected coin-->
                    <template v-slot:selection="{ item, selected }">

                        <img class="pa-2" :src="showCoinImage(item.id)"/>
                        <span v-text="item.name"></span>
                        <span class="pa-2 grey--text" v-text="item.symbol"></span>

                    </template>

                    <!--// the list of matched coins by search string-->
                    <template v-slot:item="{ item }">

                        <img class="pa-2" :src="showCoinImage(item.id)"/>

                        <v-list-tile-title v-text="item.name"></v-list-tile-title>

                        <v-list-tile-action>
                            <v-list-tile-sub-title v-text="item.symbol"></v-list-tile-sub-title>
                        </v-list-tile-action>

                    </template>

                </v-autocomplete>
            </v-card-text>

            <!--// user's exist items in the portfolio to choose-->
            <v-card-text class="pa-0" v-if="isUserPortfolioLoaded && userPortfolioItems.length > 0 && !selectedCoin">
                <v-list>
                    <v-list-tile
                            v-for="item in userPortfolioItems"
                            :key="item.id"
                            @click="chooseCoinFromPortfolio(item)">

                        <v-list-tile-action>
                            <img :src="showCoinImage(item.coin.id)"/>
                        </v-list-tile-action>

                        <v-list-tile-content>
                            <v-list-tile-title v-text="item.coin.name"></v-list-tile-title>
                        </v-list-tile-content>

                        <v-list-tile-avatar>
                            <v-list-tile-sub-title v-text="item.coin.symbol"></v-list-tile-sub-title>
                        </v-list-tile-avatar>

                    </v-list-tile>
                </v-list>
            </v-card-text>

            <!--// a form to add transaction's details-->
            <v-card-text v-if="selectedCoin" style="padding: 0 16px;">

                <!--// TRANSACTION TYPE "BUY/SELL"-->
                <div class="text-xs-center">
                    <v-layout>
                        <v-spacer></v-spacer>
                        <v-btn small color="success" class="btn-type"
                               :class="{'disable-events active': transBtnType('BUY')}"
                               @click="toggleTransType('BUY')">
                            BUY
                        </v-btn>
                        <v-btn small color="error" class="btn-type"
                               :class="{'disable-events active': transBtnType('SELL')}"
                               @click="toggleTransType('SELL')">
                            SELL
                        </v-btn>
                    </v-layout>
                </div>

                <!--// TRANSACTION AMOUNT-->
                <v-form @submit.prevent="addTransaction" v-model="transFormValid">
                    <v-text-field
                            v-model.number="transAmount"
                            label="Amount"
                            placeholder="0"
                            class="inputNumbersWithoutSpin"
                            type="number"
                            :suffix="selectedCoin.symbol"
                            :rules="numberMoreThanZeroRules"
                            required>
                    </v-text-field>

                    <!--// TRANSACTION SET Market Price-->
                    <div class="text-xs-center">
                        <v-layout>
                            <v-btn
                                    small color="primary"
                                    @click="setTransMarketPrice">
                                Market Price
                            </v-btn>
                            <v-spacer></v-spacer>
                        </v-layout>
                    </div>

                    <!--// TRANSACTION Price-->
                    <v-layout row>
                        <v-flex xs12>
                            <v-text-field
                                    v-model.number="transPrice"
                                    label="Price"
                                    placeholder="0"
                                    class="inputNumbersWithoutSpin"
                                    type="number"
                                    :rules="numberOrZeroRules"
                                    required>
                            </v-text-field>
                        </v-flex>
                        <!--// TRANSACTION SELECT CURRENCY-->
                        <v-flex sm6 d-flex class="selectsFlexBasis">
                            <v-select
                                    v-model="transChooseCurrency"
                                    :items="currencies"
                                    solo hide-details>
                            </v-select>
                        </v-flex>

                    </v-layout>

                    <!--// TRANSACTION TOTAL-->
                    <v-text-field
                            v-model.number="transTotal"
                            label="Total"
                            placeholder="0"
                            :suffix="transCurrency"
                            class="inputNumbersWithoutSpin"
                            type="number"
                            :rules="numberOrZeroRules"
                            required>
                    </v-text-field>

                    <!--// TRANSACTION DATE-->
                    <v-menu
                            v-model="datePickerWindow"
                            :close-on-content-click="false"
                            :nudge-right="40"
                            lazy
                            transition="scale-transition"
                            offset-y
                            full-width
                            min-width="290px">

                        <template v-slot:activator="{ on }">
                            <v-text-field
                                    :value="computedDateFormattedDatefns"
                                    label="Date"
                                    prepend-icon="event"
                                    readonly
                                    v-on="on"
                            ></v-text-field>
                        </template>

                        <v-date-picker v-model="transDate" :max="dateToday"
                                       @input="datePickerWindow = false"></v-date-picker>
                    </v-menu>

                    <!--// TRANSACTION COMMENT-->
                    <v-expansion-panel focusable>
                        <v-expansion-panel-content>
                            <template v-slot:header>
                                <div>Comment</div>
                            </template>
                            <v-card>
                                <v-card-text class="pa-2">
                                    <v-textarea class="pa-0 ma-0"
                                                v-model="transComment"
                                                :rules="commentRules"
                                                :counter=true>
                                    </v-textarea>
                                </v-card-text>
                            </v-card>
                        </v-expansion-panel-content>
                    </v-expansion-panel>

                </v-form>
            </v-card-text>

            <!--// "RESET" and "ADD" buttons-->
            <div class="pa-1 ma-0 text-xs-center" v-if="selectedCoin">

                <v-spacer></v-spacer>
                <v-btn
                        small
                        color="primary"
                        @click="clearForm">
                    Reset
                </v-btn>
                <v-btn
                        small id="addTransaction"
                        :class="{'disable-events': !transFormValid}"
                        color="primary"
                        @click="addTransaction">
                    Add
                </v-btn>
            </div>

        </v-card>
    </v-dialog>
</template>

<script>
    import {mapGetters, mapState} from 'vuex'
    import {marketdataService} from "../../utils/marketdata.service";
    import {SNACKBAR_ERROR} from "../../store/actions/snackbar";
    import format from 'date-fns/format'
    import {PORTFOLIO_ADD_TRANSACTION} from "../../store/actions/portfolio";

    export default {
        name: "AddPortfolioItem",
        props: {
            value: Boolean,
        },
        data: () => ({
            items: [],
            search: null,
            isReadonly: false,
            selectedCoin: null,
            selectedCoinMarketData: null,
            hideAutocompleteForm: false,
            transType: "BUY",
            transAmount: Number,
            transPrice: Number,
            transTotal: Number,
            transCurrency: "USD",
            transDate: new Date().toISOString().substr(0, 10),
            transComment: "",
            dateToday: new Date().toISOString().substr(0, 10),
            datePickerWindow: false,
            currencies: ['USD', 'EUR', 'BTC', 'ETH'],
            numberMoreThanZeroRules: [
                v => !!v || "number is required!",
                v =>
                    v < 999999999999.99999999 ||
                    'number must be less than 999999999999.99999999 numbers',
                v =>
                    v > 0 ||
                    'number must be greater than 0'
            ],
            numberOrZeroRules: [
                v => (v !== "" && Number(v) >= 0) || "number is required!",
                v =>
                    v < 999999999999.99999999 ||
                    'number must be less than 999999999999.99999999 numbers',
            ],
            commentRules: [
                v => v.length <= 200 || 'Comment must be less than 200 characters'
            ],
            transFormValid: false,
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
            ...mapGetters(['isAllCoinsListDataLoaded', 'isUserPortfolioLoaded']),
            ...mapState({
                allCoinsListData: state => state.marketdata.allCoinsListData,
                userCoinsMarketData: state => state.marketdata.userCoinsMarketData,
                userPortfolioItems: state => state.portfolio.userPortfolio.items
            }),
            transChooseCurrency: {
                get() {
                    return this.transCurrency
                },
                set(mainCurrency) {
                    this.transCurrency = mainCurrency;
                    this.setTransMarketPrice();
                }
            },
            computedDateFormattedDatefns() {
                return this.transDate ? format(this.transDate, 'dddd, Do MMMM YYYY') : ''
            }
        },
        watch: {
            search(val) {
                // search coins in allCoinsListData which matched with "search input string"
                if (val && val.trim() !== '') {
                    this.items = this.allCoinsListData;
                }
            },
            show(val) {
                // on close modal window -> run clearForm() to reset all component's variables
                !val && this.clearForm()
            },
        },
        methods: {
            clearForm() {
                // reset all component's variables
                Object.assign(this.$data, this.$options.data())
            },
            // *** autocomplete and choose coin methods: ***
            showCoinImage(id) {
                if (this.isAllCoinsListDataLoaded) {
                    return 'https://s2.coinmarketcap.com/static/img/coins/32x32/' + id + '.png'
                }
                return '@/assets/coin-default.png'
            },
            // when the coin for transaction was chosen by autocomplete searching
            changeSelected() {
                if (this.selectedCoin) {

                    this.hideAutocompleteForm = true;
                    this.isReadonly = true;

                    // case when chosen coin IS in user's coins list
                    if (this.selectedCoin.id in this.userCoinsMarketData) {

                        this.selectedCoinMarketData = this.userCoinsMarketData[this.selectedCoin.id];

                        this.setTransMarketPrice();

                        // case when chosen coin isn't in user's coins list
                    } else {
                        // API REST call to receive actual coin's market data
                        marketdataService.getCoinData(this.selectedCoin.id)
                            .then(resp => {
                                // parsing of response to have a map like a JSON
                                let coinMarketData = JSON.parse(resp);
                                this.selectedCoinMarketData = coinMarketData[this.selectedCoin.id];

                                this.setTransMarketPrice();
                            })
                            .catch(err => {
                                this.$store.dispatch(SNACKBAR_ERROR, "Error on receive actual market data of the coin!");
                            })
                    }
                }
            },
            parseMatchedItem(matchedItem) {
                // !!! here is defined that item-text need to match on "searching by coin name && coin symbol"
                return [matchedItem.name, matchedItem.symbol];
            },
            chooseCoinFromPortfolio(item) {
                this.selectedCoin = item.coin;
                // call "general autocomplete" method
                this.changeSelected();
            },
            // *** transaction's form methods: ***
            transBtnType(type) {
                return this.transType === type;
            },
            toggleTransType(type) {
                this.transType = (type === "SELL" ? "SELL" : "BUY");
            },
            setTransMarketPrice() {
                let tempTransPrice = 0;
                switch (this.transCurrency) {
                    case 'USD':
                        tempTransPrice = this.selectedCoinMarketData["USD"]["price"];
                        break;
                    case 'EUR':
                        tempTransPrice = this.selectedCoinMarketData["EUR"]["price"];
                        break;
                    case 'BTC':
                        tempTransPrice = this.selectedCoinMarketData["BTC"]["price"];
                        break;
                    case 'ETH':
                        tempTransPrice = this.selectedCoinMarketData["ETH"]["price"];
                        break;
                }
                this.transPrice = this.$options.filters.generalValuesByCurrency(tempTransPrice, this.transCurrency);
            },
            // button "DONE" handler
            addTransaction() {
                const payload = {
                    'transCoinId': this.selectedCoin.id, 'transCurrency': this.transCurrency,
                    'transType': this.transType, 'transAmount': this.transAmount, 'transPrice': this.transPrice,
                    'transDate': this.transDate, 'transComment': this.transComment
                };
                this.$store.dispatch(PORTFOLIO_ADD_TRANSACTION, payload)
                    .then(resp => {
                        if (resp) {
                            this.show = false;
                        }
                    })
                    .catch(() => {
                    })
            },
        },
    }
</script>

<style scoped>
    .btn-type:not(.active) {
        opacity: 0.4;
    }
    #addTransaction.disable-events {
        opacity: 0.4;
    }
</style>