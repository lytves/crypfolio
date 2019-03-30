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
                <v-form @submit.prevent="addTransaction" v-model="transFormValid" ref="form">
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
                    <v-expansion-panel v-model="expandComment">
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
                            v-if="!editableTrans"
                            small id="addTransaction"
                            :class="{'disable-events': !transFormValid}"
                            color="primary"
                            @click="addTransaction">
                        Add Transaction
                    </v-btn>

                    <v-btn
                            v-if="editableTrans"
                            small id="editTransaction"
                            :class="{'disable-events': !transFormValid}"
                            color="primary"
                            @click="editTransaction">
                        Edit Transaction
                    </v-btn>
                </div>
            </v-card-actions>

        </v-card>

    </v-dialog>
</template>

<script>
    import {mapGetters, mapState} from "vuex";
    import format from 'date-fns/format'
    import {PORTFOLIO_ADD_TRANSACTION, PORTFOLIO_EDIT_TRANSACTION} from "../../store/actions/portfolio";

    export default {
        name: "AddItemDetailsTransaction",
        props: {
            value: Boolean,
            selectedItem: null,
            editableTrans: null,
        },
        data: () => ({
            transId: null,
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
            expandComment: null,
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

                if (val) {
                    // if it's editing of the existing transaction
                    if (this.editableTrans) {
                        this.transId = this.editableTrans.id;
                        this.transType = this.editableTrans.type;
                        this.transAmount = this.editableTrans.amount;
                        this.transPrice = this.showTransPriceByCurrency();
                        this.transTotal = this.showTransTotalByCurrency();
                        this.transCurrency = this.editableTrans.boughtCurrency;
                        // https://stackoverflow.com/questions/10830357/javascript-toisostring-ignores-timezone-offset
                        // const tempDate = this.editableTrans.boughtDate;
                        // const dateToFormat = new Date(tempDate.year + " " + tempDate.monthValue + " " + tempDate.dayOfMonth);
                        // const tzoffset = (new Date()).getTimezoneOffset() * 60000; //offset in milliseconds
                        // this.transDate = (new Date(dateToFormat - tzoffset)).toISOString().substr(0, 10);
                        this.transComment = this.editableTrans.comment;
                    } else {
                        // on open modal window -> set initial transPrice
                        this.setTransMarketPrice();
                    }
                } else {
                    // on close modal window -> run clearForm() to reset all component's variables
                    // reset all component's variables
                    Object.assign(this.$data, this.$options.data());
                    this.$refs.form.resetValidation();
                    this.$emit('clear-editable-trans', null);
                }
            },
        },
        methods: {
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
            showTransPriceByCurrency() {

                if (this.editableTrans) {

                    switch (this.editableTrans.boughtCurrency) {
                        case 'USD':
                            return this.editableTrans.boughtPriceUsd;
                        case 'EUR':
                            return this.editableTrans.boughtPriceEur;
                        case 'BTC':
                            return this.editableTrans.boughtPriceBtc;
                        case 'ETH':
                            return this.editableTrans.boughtPriceEth;
                        default:
                            return 0;
                    }
                }
            },
            showTransTotalByCurrency() {

                if (this.editableTrans) {

                    switch (this.editableTrans.boughtCurrency) {
                        case 'USD':
                            return this.editableTrans.amount * this.editableTrans.boughtPriceUsd;
                        case 'EUR':
                            return this.editableTrans.amount * this.editableTrans.boughtPriceEur;
                        case 'BTC':
                            return this.editableTrans.amount * this.editableTrans.boughtPriceBtc;
                        case 'ETH':
                            return this.editableTrans.amount * this.editableTrans.boughtPriceEth;
                        default:
                            return 0;
                    }
                }
            },
            // button "ADD" handler
            addTransaction() {
                // payload - new transaction data to pass to backend
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
            // button "EDIT" handler
            editTransaction() {
                // payload - edited transaction data to pass to backend
                const payload = {
                    'payload': {
                        'transId': this.transId, 'transCurrency': this.transCurrency,
                        'transType': this.transType, 'transAmount': this.transAmount, 'transPrice': this.transPrice,
                        'transDate': this.transDate, 'transComment': this.transComment, 'itemId': this.selectedItem.id
                    },
                };
                console.log('payload', payload);

                this.$store.dispatch(PORTFOLIO_EDIT_TRANSACTION, payload)
                    .then(resp => {
                        if (resp) {
                            this.show = false;
                        }
                    })
                    .catch(() => {
                    })
            }
        }
    }
</script>

<style scoped>
    .btn-type:not(.active) {
        opacity: 0.4;
    }

    #addTransaction.disable-events, #editTransaction.disable-events {
        opacity: 0.4;
    }
</style>