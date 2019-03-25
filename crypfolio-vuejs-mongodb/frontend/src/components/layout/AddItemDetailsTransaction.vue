<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">

    <v-dialog
            lazy
            v-model="show"
            @keydown.esc="show = false"
            width="400"
            class="text-xs-center"
            scrollable>

        <v-card>
            <!--// header with item's coin image and name-->
            <v-card-title class="headline pa-2 ma-0" v-if="selectedItem">

                <v-layout align-center>

                    <img class="pa-2" :src="showCoinImage(selectedItem.coin.id)"/>
                    <span v-text="selectedItem.coin.name"></span>
                    <span class="pa-2 grey--text" v-text="selectedItem.coin.symbol"></span>

                    <v-spacer></v-spacer>

                </v-layout>

            </v-card-title>

            <!--// a form to add transaction's details-->
            <v-card-text v-if="selectedItem" style="padding: 0 16px;">

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
                            :suffix="selectedItem.coin.symbol"
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
                                    v-on="on">
                            </v-text-field>
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
            <v-card-actions class="justify-center">
                <div class="pa-1 ma-0 text-xs-center">

                    <v-spacer></v-spacer>

                    <v-btn
                            small id="addTransaction"
                            :class="{'disable-events': !transFormValid}"
                            color="primary"
                            @click="addTransaction">
                        Add
                    </v-btn>
                </div>
            </v-card-actions>

        </v-card>

    </v-dialog>
</template>

<script>
    import {mapGetters, mapState} from "vuex";
    import format from 'date-fns/format'
    import {PORTFOLIO_ADD_TRANSACTION} from "../../store/actions/portfolio";

    export default {
        name: "AddItemDetailsTransaction",
        props: {
            value: Boolean,
            selectedItem: null,
        },
        data: () => ({
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
                    return this.value;
                },
                set(value) {
                    this.$emit('input', value)
                }
            },
            ...mapGetters(['isUserCoinsMarketDataLoaded']),
            ...mapState({
                userCoinsMarketData: state => state.marketdata.userCoinsMarketData,
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
            show(val) {
                // on close modal window -> run clearForm() to reset all component's variables
                !val && this.clearForm();
                // on open modal window -> set initial transPrice
                val && this.setTransMarketPrice();
            },
        },
        methods: {
            clearForm() {
                // reset all component's variables
                Object.assign(this.$data, this.$options.data())
            },
            // *** autocomplete and choose coin methods: ***
            showCoinImage(id) {

                if (this.selectedItem) {
                    return 'https://s2.coinmarketcap.com/static/img/coins/32x32/' + id + '.png'
                }
                return '@/assets/coin-default.png'
            },
            // *** transaction's form methods: ***
            transBtnType(type) {
                return this.transType === type;
            },
            toggleTransType(type) {
                this.transType = (type === "SELL" ? "SELL" : "BUY");
            },
            setTransMarketPrice() {

                if (this.selectedItem && this.isUserCoinsMarketDataLoaded) {
                    let tempTransPrice = 0;

                    switch (this.transCurrency) {
                        case 'USD':
                            tempTransPrice = this.userCoinsMarketData[this.selectedItem.coin.id]["USD"]["price"];
                            break;
                        case 'EUR':
                            tempTransPrice = this.userCoinsMarketData[this.selectedItem.coin.id]["EUR"]["price"];
                            break;
                        case 'BTC':
                            tempTransPrice = this.userCoinsMarketData[this.selectedItem.coin.id]["BTC"]["price"];
                            break;
                        case 'ETH':
                            tempTransPrice = this.userCoinsMarketData[this.selectedItem.coin.id]["ETH"]["price"];
                            break;
                    }
                    this.transPrice = this.$options.filters.generalValuesByCurrency(tempTransPrice, this.transCurrency);
                }
            },
            // button "DONE" handler
            addTransaction() {
                // payload - new transaction data to pass to backend,
                const payload = {
                    'payload': {
                        'transCoinId': this.selectedItem.coin.id, 'transCurrency': this.transCurrency,
                        'transType': this.transType, 'transAmount': this.transAmount, 'transPrice': this.transPrice,
                        'transDate': this.transDate, 'transComment': this.transComment
                    },
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
        }
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