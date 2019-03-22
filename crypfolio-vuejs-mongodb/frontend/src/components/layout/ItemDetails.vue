<template>

    <v-bottom-sheet
            v-model="show"
            @keydown.esc="closeItemDetailsSheet"
            full-width
            lazy>

        <v-card full-width class="fill-height pa-2" v-if="selectedItem">

            <v-layout row xs12>

                <!-- left column: position & coin details, item currency choose, delete item -->
                <v-flex xs4 style="background-color:#e8e8e8">

                    <v-card-text class="text-sm-center pa-2">
                        <img style="vertical-align: text-bottom;" :src="showItemCoinImage(selectedItem.coin.id)"/>
                        <span class="display-1 pa-2">
                            {{ selectedItem.coin.name }}
                        </span>
                    </v-card-text>

                    <v-layout row xs12 class="text-uppercase font-weight-medium pa-2">
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

                    <v-layout row xs12 class="text-uppercase font-weight-medium pa-2">
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

                    <v-layout row xs12 class="text-uppercase font-weight-medium pa-2">
                        Item Currency:
                    </v-layout>

                    <v-layout row xs12 class="text-uppercase font-weight-medium pa-2">
                        Delete Item:
                    </v-layout>

                </v-flex>

                <!-- right big column: TradingView widget, add transaction, transactions list -->
                <v-flex xs8 d-flex style="background-color:blue">
                    ddfdf
                </v-flex>

            </v-layout>

        </v-card>

    </v-bottom-sheet>

</template>

<script>
    import {mapGetters, mapState} from "vuex";

    export default {
        name: "ItemDetails",
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
            ]
        }),
        computed: {
            ...mapGetters(['isUserCoinsMarketDataLoaded']),
            ...mapState({
                userCoinsMarketData: state => state.marketdata.userCoinsMarketData,
            }),
            show: {
                get() {
                    return this.value
                },
                set(value) {
                    this.$emit('input', value)
                }
            },
        },
        methods: {
            closeItemDetailsSheet() {
                // call TabPortfolio method to clear selectedItem
                this.$parent.clearSelectedItem();
                this.show = false;
            },
            showItemCoinImage(id) {

                if (this.selectedItem) {
                    return 'https://s2.coinmarketcap.com/static/img/coins/32x32/' + id + '.png'
                }
                return '@/assets/coin-default.png'
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
            }
        }
    }
</script>

<style scoped>
</style>