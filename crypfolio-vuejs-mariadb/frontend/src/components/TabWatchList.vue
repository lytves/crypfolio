<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">

    <v-container class="ma-0 pa-0">

        <v-card>
            <v-layout row xs12>

                <v-flex xs10 d-flex>

                    <v-card-text style="width: auto;">
                        <span class="grey--text">
                            Total Market Cap:
                        </span>
                        <span class="pa-3 font-weight-medium">
                            ${{ showTotalMarketCap | marketcapValues }}
                        </span>
                        <span class="grey--text">
                            24h Vol:
                        </span>
                        <span class="pa-3 font-weight-medium">
                            ${{ showTotal24hVolume | marketcapValues }}
                        </span>
                        <span class="grey--text">
                            BTC Dominance:
                        </span>
                        <span class="pa-3 font-weight-medium">
                            {{ showBitcoinPercentageOfMarketCap | percentsValues }}%
                        </span>
                    </v-card-text>

                </v-flex>

                <v-flex xs2 d-flex>

                    <v-btn
                            color="primary lighten-2"
                            dark
                            @click.stop="showAddWatchCoinDialog">
                        <v-icon left>fas fa-plus</v-icon>
                        Add New Coin
                    </v-btn>

                </v-flex>

            </v-layout>
        </v-card>

        <v-data-table
                :headers="headers"
                :items="showWatchlistCoins"
                :expand="expand"
                :custom-sort="customSort"
                disable-initial-sort
                item-key="coinId.id"
                hide-actions
                class="elevation-1">

            <template slot="no-data">

                <v-flex align-center class="font-weight-medium text-sm-center blockquote">
                    Your watchlist is empty. Add any coin to start!
                </v-flex>

            </template>

            <template v-slot:items="props">
                <tr class="cursorPointer" @click="props.expanded = !props.expanded; expandedRowCoinId = props.item.coinId.id;">

                    <td class="pa-2 flex"><img :src="showCoinImage(props.item.coinId.id)"/></td>

                    <td class="font-weight-medium align-center">
                        <span class="font-weight-bold d-block">{{ props.item.coinId.name }}</span>
                        <span class="d-block">{{ props.item.coinId.symbol }}</span>
                    </td>

                    <td>{{ showCoinMarketPrice(props.item.coinId.id, props.item.showedCurrency) |
                        generalValuesByCurrency(props.item.showedCurrency) }}
                        {{ props.item.showedCurrency }}
                    </td>

                    <td>{{ showCoinMarketCap(props.item.coinId.id, props.item.showedCurrency) | marketcapValues }}
                        {{ props.item.showedCurrency }}
                    </td>

                    <td :class="getPercentColor(showCoin24hPriceChange(props.item.coinId.id, props.item.showedCurrency))">
                        {{ showCoin24hPriceChange(props.item.coinId.id, props.item.showedCurrency) | percentsValues
                        }}%
                    </td>

                    <td :class="getPercentColor(showCoin7dPriceChange(props.item.coinId.id, props.item.showedCurrency))">
                        {{ showCoin7dPriceChange(props.item.coinId.id, props.item.showedCurrency) | percentsValues
                        }}%
                    </td>

                    <td><img src="@/assets/price-graph.png"/></td>

                </tr>
            </template>

            <template v-slot:expand="props">
                <v-layout row xs12>

                    <v-flex xs10 d-flex>
                        <div>
                            <span class="grey--text pa-3">Currency:</span>
                            <span
                                    v-for="(currency, index) in currencies"
                                    :key="index">
                                    <v-btn
                                            :class="{'disable-events': showShowedCurrency(currency.name)}"
                                            @click="changeShowedCurrency(currency.name)">
                                        <v-icon color="blue darken-2" left>{{currency.icon}}</v-icon>
                                        {{ currency.name }}
                                    </v-btn>
                            </span>
                        </div>
                    </v-flex>

                    <v-flex xs2>
                        <v-btn @click="deleteCoin">
                            <v-icon color="blue darken-2" left>fas fa-trash</v-icon>
                            Delete Coin
                        </v-btn>
                    </v-flex>

                </v-layout>

                <div>
                    <hr class="ma-2"/>
                </div>
            </template>

        </v-data-table>

        <AddWatchCoin v-model="addWatchCoinDialog"></AddWatchCoin>

    </v-container>

</template>

<script>
    import {mapGetters, mapState} from 'vuex'
    import AddWatchCoin from './layout/AddWatchCoin'
    import {WATCHLIST_CHANGE_COIN_CURRENCY, WATCHLIST_DELETE_COIN} from '../store/actions/watchlist'
    import {MARKETDATA_ALLCOINSLIST_SUCCESS} from "../store/actions/marketdata";

    export default {
        name: "TabWatchlist",
        components: {
            AddWatchCoin,
        },
        data() {
            return {
                headers: [
                    {text: '', sortable: false, width: "1%", class: "pa-0"},
                    {text: 'Coin', value: 'coin-name'},
                    {text: 'Market Price', value: 'market-price'},
                    {text: 'Market Cap', value: 'market-cap'},
                    {text: '24h Changed', value: '24h-changed'},
                    {text: '7d Changed', value: '7d-changed'},
                    {text: 'Price Graph', sortable: false, align: 'center', width: "300",},
                ],
                expand: false,
                expandedRowCoinId: '',
                addWatchCoinDialog: false,
                currencies: [
                    {name: 'USD', icon: 'fas fa-dollar-sign'},
                    {name: 'EUR', icon: 'fas fa-euro-sign'},
                    {name: 'BTC', icon: 'fab fa-btc'},
                    {name: 'ETH', icon: 'fab fa-ethereum'},
                ],
            }
        },
        computed: {
            ...mapGetters(['isUserWatchlistLoaded', 'isUserCoinsMarketDataLoaded', 'isGlobalMarketDataLoaded']),
            ...mapState({
                userWatchlistCoins: state => state.watchlist.userWatchlist,
                userCoinsMarketData: state => state.marketdata.userCoinsMarketData,
                globalMarketData: state => state.marketdata.globalMarketData,
            }),
            showWatchlistCoins() {
                if (this.isUserWatchlistLoaded) {
                    return this.userWatchlistCoins
                }
            },
            showTotalMarketCap() {

                if (this.isGlobalMarketDataLoaded) {
                    return this.globalMarketData["total_market_cap_usd"];
                }
            },
            showTotal24hVolume() {

                if (this.isGlobalMarketDataLoaded) {
                    return this.globalMarketData["total_24h_volume_usd"];
                }
            },
            showBitcoinPercentageOfMarketCap() {

                if (this.isGlobalMarketDataLoaded) {
                    return this.globalMarketData["bitcoin_percentage_of_market_cap"];
                }
            },
        },
        methods: {
            showAddWatchCoinDialog() {
                this.addWatchCoinDialog = true;
                this.$store.dispatch(MARKETDATA_ALLCOINSLIST_SUCCESS)
            },
            showCoinImage(id) {

                if (this.isUserWatchlistLoaded) {
                    return 'https://s2.coinmarketcap.com/static/img/coins/32x32/' + id + '.png'
                }
                return '/img/coin-default.png'
            },
            showCoinMarketPrice(coinId, showedCurrency) {

                if (this.isUserCoinsMarketDataLoaded) {

                    switch (showedCurrency) {
                        case 'USD':
                            return this.userCoinsMarketData[coinId]['USD']['price'];
                        case 'EUR':
                            return this.userCoinsMarketData[coinId]['EUR']['price'];
                        case 'BTC':
                            return this.userCoinsMarketData[coinId]['BTC']['price'];
                        case 'ETH':
                            return this.userCoinsMarketData[coinId]['ETH']['price'];
                        default:
                            return 0;
                    }
                }
            },
            showCoinMarketCap(coinId, showedCurrency) {

                if (this.isUserCoinsMarketDataLoaded) {

                    switch (showedCurrency) {
                        case 'USD':
                            return this.userCoinsMarketData[coinId]['USD']['market_cap'];
                        case 'EUR':
                            return this.userCoinsMarketData[coinId]['EUR']['market_cap'];
                        case 'BTC':
                            return this.userCoinsMarketData[coinId]['BTC']['market_cap'];
                        case 'ETH':
                            return this.userCoinsMarketData[coinId]['ETH']['market_cap'];
                        default:
                            return 0;
                    }
                }
            },
            showCoin24hPriceChange(coinId, showedCurrency) {

                if (this.isUserCoinsMarketDataLoaded) {

                    switch (showedCurrency) {
                        case 'USD':
                            return this.userCoinsMarketData[coinId]['USD']['percent_change_24h'];
                        case 'EUR':
                            return this.userCoinsMarketData[coinId]['EUR']['percent_change_24h'];
                        case 'BTC':
                            return this.userCoinsMarketData[coinId]['BTC']['percent_change_24h'];
                        case 'ETH':
                            return this.userCoinsMarketData[coinId]['ETH']['percent_change_24h'];
                        default:
                            return 0;
                    }
                }
            },
            showCoin7dPriceChange(coinId, showedCurrency) {

                if (this.isUserCoinsMarketDataLoaded) {

                    switch (showedCurrency) {
                        case 'USD':
                            return this.userCoinsMarketData[coinId]['USD']['percent_change_7d'];
                        case 'EUR':
                            return this.userCoinsMarketData[coinId]['EUR']['percent_change_7d'];
                        case 'BTC':
                            return this.userCoinsMarketData[coinId]['BTC']['percent_change_7d'];
                        case 'ETH':
                            return this.userCoinsMarketData[coinId]['ETH']['percent_change_7d'];
                        default:
                            return 0;
                    }
                }
            },
            showShowedCurrency(currency) {
                let expandCoin = this.userWatchlistCoins.find(coin => {
                    return coin.coinId.id === this.expandedRowCoinId
                });

                return expandCoin.showedCurrency === currency;
            },
            changeShowedCurrency(currency) {
                const payload = {'coinId': this.expandedRowCoinId, 'currency': currency};
                this.$store.dispatch(WATCHLIST_CHANGE_COIN_CURRENCY, payload);
            },
            getPercentColor(value) {
                if (value > 0) {
                    return 'green--text'
                } else if (value < 0) {
                    return 'red--text'
                }
            },
            customSort(items, index, isDesc) {

                items.sort((a, b) => {

                    if (index === 'coin-name') {
                        if (isDesc) {
                            return a.coinId.name.toLowerCase() < b.coinId.name.toLowerCase() ? -1 : 1;
                        } else {
                            return a.coinId.name.toLowerCase() > b.coinId.name.toLowerCase() ? -1 : 1;
                        }
                    } else if (index === 'market-price') {
                        if (isDesc) {
                            return this.showCoinMarketPrice(a.coinId.id, a.showedCurrency)
                            < this.showCoinMarketPrice(b.coinId.id, b.showedCurrency) ? -1 : 1;
                        } else {
                            return this.showCoinMarketPrice(a.coinId.id, a.showedCurrency)
                            > this.showCoinMarketPrice(b.coinId.id, b.showedCurrency) ? -1 : 1;
                        }
                    } else if (index === 'market-cap') {
                        if (isDesc) {
                            return this.showCoinMarketCap(a.coinId.id, a.showedCurrency)
                            < this.showCoinMarketCap(b.coinId.id, b.showedCurrency) ? -1 : 1;
                        } else {
                            return this.showCoinMarketCap(a.coinId.id, a.showedCurrency)
                            > this.showCoinMarketCap(b.coinId.id, b.showedCurrency) ? -1 : 1;
                        }
                    } else if (index === '24h-changed') {
                        if (isDesc) {
                            return this.showCoin24hPriceChange(a.coinId.id, a.showedCurrency)
                            < this.showCoin24hPriceChange(b.coinId.id, b.showedCurrency) ? -1 : 1;
                        } else {
                            return this.showCoin24hPriceChange(a.coinId.id, a.showedCurrency)
                            > this.showCoin24hPriceChange(b.coinId.id, b.showedCurrency) ? -1 : 1;
                        }
                    } else if (index === '7d-changed') {
                        if (isDesc) {
                            return this.showCoin7dPriceChange(a.coinId.id, a.showedCurrency)
                            < this.showCoin7dPriceChange(b.coinId.id, b.showedCurrency) ? -1 : 1;
                        } else {
                            return this.showCoin7dPriceChange(a.coinId.id, a.showedCurrency)
                            > this.showCoin7dPriceChange(b.coinId.id, b.showedCurrency) ? -1 : 1;
                        }
                    }
                });

                return items;
            },
            deleteCoin() {
                this.$store.dispatch(WATCHLIST_DELETE_COIN, this.expandedRowCoinId);
            }
        }
    }
</script>

<style scoped>
    .v-datatable.v-table tr {
        cursor: pointer;
    }
</style>