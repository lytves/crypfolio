<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">

    <v-bottom-sheet
            v-model="show"
            @keydown.esc="closeItemDetailsSheet"
            full-width
            lazy>

        <v-card full-width class="fill-height pa-2 scroll-y" v-if="selectedItem">

            <v-layout row xs12>

                <!-- left column: position & coin details, item currency choose, delete item -->
                <v-flex xs4 style="min-width: 350px;">

                    <v-card-text class="text-sm-center pa-2">
                        <img style="vertical-align: text-bottom;" :src="showItemCoinImage(selectedItem.coin.id)"/>
                        <span class="display-1 pa-2">
                            {{ selectedItem.coin.name }}
                        </span>
                    </v-card-text>

                    <v-layout row xs12 class="text-uppercase font-weight-medium pa-2 background-grey">
                        Position Details:
                    </v-layout>

                    <v-list class="pa-0 ma-0">
                        <span
                                class="pa-0 ma-0"
                                v-for="(cell, index) in positionDetails"
                                :key="index">

                                <v-layout row xs12 class="pa-1">
                                    <v-flex xs6 class="grey--text" style="padding-left: 10px !important;">
                                        {{ cell.title }}:
                                    </v-flex>
                                    <v-flex xs6 style="padding-right: 10px !important;"
                                            :class="classRedGreen(cell.property)">
                                        {{ showPositionDetails(cell.property) }}
                                    </v-flex>
                                </v-layout>
                        </span>
                    </v-list>

                    <v-layout row xs12 class="text-uppercase font-weight-medium pa-2 background-grey">
                        Coin Details:
                    </v-layout>

                    <v-list class="pa-0 ma-0">
                        <span
                                class="pa-0 ma-0"
                                v-for="(cell, index) in coinDetails"
                                :key="index">

                                <v-layout row xs12 class="pa-1">
                                    <v-flex xs6 class="grey--text" style="padding-left: 10px !important;">
                                        {{ cell.title }}:
                                    </v-flex>
                                    <v-flex xs6 style="padding-right: 10px !important;"
                                            :class="classRedGreen(cell.property)">
                                        {{ showCoinDetails(cell.property) }}
                                    </v-flex>
                                </v-layout>
                        </span>
                    </v-list>

                    <v-layout row xs12 class="text-uppercase font-weight-medium pa-2 background-grey">
                        Item Currency:
                    </v-layout>

                    <v-layout row xs12 style="background-color: white">
                        <v-flex d-flex style="padding: 0 10px !important;">
                            <v-list class="pa-0 ma-0">
                                <span
                                        class="pa-0 ma-0"
                                        v-for="(currency, index) in currencies"
                                        :key="index">
                                    <v-btn
                                            style="min-width: unset;"
                                            :class="{'disable-events': showShowedCurrency(currency.name)}"
                                            class="ma-2"
                                            @click="changeItemShowedCurrency(currency.name)">
                                        <v-icon
                                                style="margin-right: 6px;"
                                                color="blue darken-2">{{currency.icon}}</v-icon>
                                        {{ currency.name }}
                                    </v-btn>
                                </span>
                            </v-list>
                        </v-flex>
                    </v-layout>

                    <v-layout row xs12 class="text-uppercase font-weight-medium pa-2 background-grey">
                        Delete Item:
                    </v-layout>

                    <v-layout row xs12 style="background-color: white">
                        <v-flex d-flex style="padding: 0 10px !important; max-width: 200px;">
                            <v-btn @click="deleteItem">
                                <v-icon color="blue darken-2" left>fas fa-trash</v-icon>
                                Delete Item
                            </v-btn>
                        </v-flex>
                    </v-layout>

                </v-flex>

                <!-- right big column: TradingView widget, add transaction, transactions list -->
                <v-flex xs8>

                    <v-card-text class="text-sm-center pa-0">
                        <img alt="Crypfolio logo" src="@/assets/trading-view-widget-example.png"
                             style="display: block; margin: auto; height: 320px;">
                    </v-card-text>

                    <v-layout d-flex row xs12
                              class="align-center text-uppercase font-weight-medium pa-2 background-grey">
                        Transactions:
                        <v-spacer></v-spacer>
                        <v-btn
                                class="pa-0 ma-0"
                                style="max-width: 200px;"
                                color="primary lighten-2"
                                dark small
                                @click.stop="addItemDetailsTransactionDialog = true">
                            <v-icon left>fas fa-plus</v-icon>
                            Add Transaction
                        </v-btn>
                    </v-layout>

                    <v-data-table
                            :headers="headers"
                            hide-actions
                            :expand="expand"
                            :items="showItemTransactions"
                            item-key="id"
                            class="elevation-1 table-transactions">

                        <template v-slot:items="props">
                            <tr class="cursorPointer" @click="props.expanded = !props.expanded">
                                <td class="pa-2 font-weight-medium">
                                    <v-icon
                                            style="vertical-align: middle; display: inline-flex; padding-right: 6px;"
                                            :color="transTypeIconColor(props.item.type)">
                                        {{ showTransTypeIcon(props.item.type) }}
                                    </v-icon>
                                    {{ props.item.type }}
                                </td>

                                <td class="pa-2">
                                    {{ props.item.amount }}
                                </td>

                                <td class="pa-2">
                                    {{ showTransPriceByCurrency(props.item) }} {{ props.item.boughtCurrency }}
                                </td>

                                <td class="pa-2">
                                    {{ showTransTotalByCurrency(props.item) }} {{ props.item.boughtCurrency }}
                                </td>
                                <!-- the table will be sorting by the "boughtDate" -->
                                <td class="pa-2">
                                    {{ showTransDate(props.item.boughtDate) }}
                                </td>
                                <!-- Transaction Dropdown Menu -->
                                <td class="pa-2" @click.stop>
                                    <v-menu bottom offset-y>

                                        <v-btn slot="activator" small class="pa-0 ma-0">
                                            <v-icon color="blue darken-2">fas fa-ellipsis-h</v-icon>
                                        </v-btn>

                                        <v-list class="pa-0 ma-0">
                                            <v-list-tile class="trans-menu"
                                                         v-for="(item, index) in transMenu"
                                                         :key="index"
                                                         @click.stop="chooseMenuAction(item.link, props.item.id)">

                                                <v-list-tile-title>
                                                    {{ item.title }}
                                                </v-list-tile-title>

                                            </v-list-tile>
                                        </v-list>

                                    </v-menu>
                                </td>
                            </tr>
                        </template>

                        <template v-slot:expand="props">
                            <v-card flat>
                                <v-card-text>{{ props.item.comment }}</v-card-text>
                            </v-card>
                        </template>

                    </v-data-table>

                </v-flex>

            </v-layout>

        </v-card>

        <AddItemDetailsTransaction :selectedItem="this.selectedItem" :editableTrans="this.editableTrans"
                                   @clear-editable-trans="resetEditableTrans"
                                   v-model="addItemDetailsTransactionDialog"></AddItemDetailsTransaction>

    </v-bottom-sheet>

</template>

<script>
    import {mapGetters, mapState} from "vuex";
    import {
        PORTFOLIO_DELETE_ITEM,
        PORTFOLIO_DELETE_TRANSACTION,
        PORTFOLIO_UPDATE_ITEM_CURRENCY
    } from "../../store/actions/portfolio";
    import compareAsc from 'date-fns/compare_asc'
    import format from 'date-fns/format'
    import AddItemDetailsTransaction from "./AddItemDetailsTransaction";

    export default {
        name: "ItemDetails",
        components: {
            AddItemDetailsTransaction,
        },
        props: {
            value: Boolean,
            selectedItem: null,
        },
        data: () => ({
            positionDetails: [
                {title: 'Amount', property: 'amount'},
                {title: 'Average Buy Price', property: 'averageBoughtPrice'},
                {title: 'Net Cost', property: 'netCost'},
                {title: 'Market Value', property: 'market-value'},
                {title: 'Profit', property: 'profit'},
                {title: 'Portfolio Share', property: 'portfolio-share'},
            ],
            coinDetails: [
                {title: 'Market Price', property: 'price'},
                {title: '24h Change', property: 'percent_change_24h'},
                {title: 'Circulating Supply', property: 'circulating_supply'},
                {title: 'Total Supply', property: 'total_supply'},
                {title: 'CoinMarketCap Rank', property: 'cmc_rank'},
            ],
            currencies: [
                {name: 'USD', icon: 'fas fa-dollar-sign'},
                {name: 'EUR', icon: 'fas fa-euro-sign'},
                {name: 'BTC', icon: 'fab fa-btc'},
                {name: 'ETH', icon: 'fab fa-ethereum'},
            ],
            headers: [
                {text: 'Type', sortable: false},
                {text: 'Amount', sortable: false,},
                {text: 'Price', sortable: false,},
                {text: 'Total', sortable: false,},
                {text: 'Date', sortable: false,},
                {text: '', sortable: false,},
            ],
            expand: true,
            addItemDetailsTransactionDialog: false,
            transMenu: [
                {title: 'Edit', link: 'editTransaction'},
                {title: 'Delete', link: 'deleteTransaction'},
            ],
            editableTrans: null,
        }),
        computed: {
            ...mapGetters(['isUserCoinsMarketDataLoaded']),
            ...mapState({
                userCoinsMarketData: state => state.marketdata.userCoinsMarketData,
                userPortfolioItems: state => state.portfolio.userPortfolio.items,
            }),
            show: {
                get() {
                    return this.value
                },
                set(value) {
                    // this.$parent.clearSelectedItem();
                    this.$emit('clear-selected-item');
                    this.$emit('input', value)
                }
            },
            showItemTransactions() {

                if (this.selectedItem) {
                    // for sorting should be used only copy of the array,
                    // because .sort change the source array, but we can't modify Vuex data here
                    const transactionsClonedArray = [...this.selectedItem.transactions];
                    return transactionsClonedArray.sort((a, b) => {
                        return compareAsc(
                            this.dateConverter(a.boughtDate),
                            this.dateConverter(b.boughtDate)
                        );
                    });
                }
            },
        },
        methods: {
            closeItemDetailsSheet() {
                // call TabPortfolio method to clear selectedItem
                // this.$parent.clearSelectedItem();
                this.$emit('clear-selected-item');
                this.show = false;
            },
            resetEditableTrans() {
                this.editableTrans = null;
            },
            showItemCoinImage(id) {

                if (this.selectedItem) {
                    return 'https://s2.coinmarketcap.com/static/img/coins/32x32/' + id + '.png'
                }
                return '/img/coin-default.png'
            },
            showPositionDetails(property) {

                if (this.selectedItem) {

                    switch (property) {
                        case "amount":
                            return this.$options.filters.generalValuesWithGrouping(this.selectedItem.amount)
                                + " " + this.selectedItem.coin.symbol;

                        case "averageBoughtPrice":
                            return this.$options.filters.generalValuesByCurrency(
                                this.showItemPropertyByCurrency(property), this.selectedItem.showedCurrency)
                                + " " + this.selectedItem.showedCurrency;

                        case "netCost":
                            return this.$options.filters.generalValuesByCurrency(
                                this.showItemPropertyByCurrency(property), this.selectedItem.showedCurrency)
                                + " " + this.selectedItem.showedCurrency;

                        case "market-value":
                            // return this.$options.filters.generalValuesByCurrency(
                            //     this.selectedItem.amount * this.showCoinPropertyByCurrency("price"), this.selectedItem.showedCurrency)
                            //     + " " + this.selectedItem.showedCurrency;

                            return this.$options.filters.generalValuesByCurrency(
                                this.$parent.showItemMarketValue(this.selectedItem))
                                + " " + this.selectedItem.showedCurrency;

                        case "profit":
                            return this.$options.filters.generalValuesByCurrency(
                                this.$parent.showItemProfit(this.selectedItem)) + " " + this.selectedItem.showedCurrency
                                + " (" + this.$options.filters.percentsValues(
                                    this.$parent.showItemProfitPercentage(this.selectedItem))
                                + "%)";

                        case "portfolio-share":
                            return this.$options.filters.percentsValues(
                                this.$parent.showItemSharePercentage(this.selectedItem)) + "%";
                    }
                }
            },
            showCoinDetails(property) {

                if (this.selectedItem && this.isUserCoinsMarketDataLoaded) {

                    switch (property) {
                        case "price":
                            return this.$options.filters.generalValuesByCurrency(
                                this.$parent.showCoinMarketPrice(this.selectedItem), this.selectedItem.showedCurrency)
                                + " " + this.selectedItem.showedCurrency;

                        case "percent_change_24h":
                            return this.$options.filters.percentsValues(
                                this.$parent.showCoin24hPriceChange(this.selectedItem)) + "%";

                        case "circulating_supply":
                        case "total_supply":
                            return this.$options.filters.generalValuesWithGrouping(
                                this.userCoinsMarketData[this.selectedItem.coin.id]['additionalData'][property])
                                + " " + this.selectedItem.coin.symbol;
                        case "cmc_rank":
                            return this.userCoinsMarketData[this.selectedItem.coin.id]['additionalData'][property];

                    }
                }
            },
            classRedGreen(property) {

                if (this.selectedItem) {

                    switch (property) {

                        case "profit":
                            if (this.$parent.showItemProfit(this.selectedItem) >= 0)
                                return 'green--text';
                            else
                                return 'red--text';

                        case "percent_change_24h":
                            if (this.$parent.showCoin24hPriceChange(this.selectedItem) >= 0)
                                return 'green--text';
                            else
                                return 'red--text';
                    }
                }
            },
            showItemPropertyByCurrency(property) {

                if (this.selectedItem) {

                    switch (this.selectedItem.showedCurrency) {
                        case 'USD':
                            return this.selectedItem[property + "Usd"];
                        case 'EUR':
                            return this.selectedItem[property + "Eur"];
                        case 'BTC':
                            return this.selectedItem[property + "Btc"];
                        case 'ETH':
                            return this.selectedItem[property + "Eth"];
                        default:
                            return 0;
                    }
                }
            },
            showCoinPropertyByCurrency(property) {

                if (this.selectedItem && this.isUserCoinsMarketDataLoaded) {

                    switch (this.selectedItem.showedCurrency) {
                        case 'USD':
                            return this.userCoinsMarketData[this.selectedItem.coin.id]['USD'][property];
                        case 'EUR':
                            return this.userCoinsMarketData[this.selectedItem.coin.id]['EUR'][property];
                        case 'BTC':
                            return this.userCoinsMarketData[this.selectedItem.coin.id]['BTC'][property];
                        case 'ETH':
                            return this.userCoinsMarketData[this.selectedItem.coin.id]['ETH'][property];
                        default:
                            return 0;
                    }
                }
            },
            showShowedCurrency(currency) {
                if (this.selectedItem) {
                    return this.selectedItem.showedCurrency === currency;
                }
            },
            changeItemShowedCurrency(currency) {
                const payload = {'coinId': this.selectedItem.coin.id, 'currency': currency};
                this.$store.dispatch(PORTFOLIO_UPDATE_ITEM_CURRENCY, payload);
            },
            deleteItem() {
                this.$store.dispatch(PORTFOLIO_DELETE_ITEM, this.selectedItem.id)
                    .then(resp => {
                        if (resp) {
                            this.closeItemDetailsSheet();
                        }
                    })
                    .catch(() => {
                    })
            },
            dateConverter(date) {
                // compose a normal Date format from Object
                // old version was used before GSON and backend LocalDateAdapter
                // return new Date(date.year + "-" + date.monthValue + "-" + date.dayOfMonth);
                return date;
            },
            showTransDate(date) {
                return format(this.dateConverter(date), 'DD MMM, YYYY')
            },
            showTransTypeIcon(type) {

                if (this.selectedItem) {
                    if (type.toUpperCase() === "BUY") {
                        return "fas fa-arrow-circle-down"
                    } else if (type.toUpperCase() === "SELL") {
                        return "fas fa-arrow-circle-up"
                    }
                }
            },
            transTypeIconColor(type) {

                if (this.selectedItem) {
                    if (type.toUpperCase() === "BUY") {
                        return "green"
                    } else if (type.toUpperCase() === "SELL") {
                        return "red"
                    }
                }
            },
            showTransPriceByCurrency(trans) {

                if (this.selectedItem) {

                    switch (trans.boughtCurrency) {
                        case 'USD':
                            return trans.boughtPriceUsd;
                        case 'EUR':
                            return trans.boughtPriceEur;
                        case 'BTC':
                            return trans.boughtPriceBtc;
                        case 'ETH':
                            return trans.boughtPriceEth;
                        default:
                            return 0;
                    }
                }
            },
            showTransTotalByCurrency(trans) {

                if (this.selectedItem) {

                    switch (trans.boughtCurrency) {
                        case 'USD':
                            return trans.amount * trans.boughtPriceUsd;
                        case 'EUR':
                            return trans.amount * trans.boughtPriceEur;
                        case 'BTC':
                            return trans.amount * trans.boughtPriceBtc;
                        case 'ETH':
                            return trans.amount * trans.boughtPriceEth;
                        default:
                            return 0;
                    }
                }
            },
            chooseMenuAction(link, transId) {
                this[link](transId)
            },
            editTransaction(transId) {
                this.editableTrans = this.selectedItem.transactions.find(trans => trans.id === transId);
                this.addItemDetailsTransactionDialog = true;
                // then request to edit transaction will be made from AddItemDetailsTransaction component
            },
            deleteTransaction(transId) {
                const payload = {'itemId': this.selectedItem.id, 'transId': transId};
                this.$store.dispatch(PORTFOLIO_DELETE_TRANSACTION, payload);
            }
        }
    }
</script>

<style scoped>
</style>