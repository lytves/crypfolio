<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">

<v-container class="ma-0 pa-0">

        <v-data-table
                :headers="headers"
                hide-actions
                :items="showPortfolioArchiveItems"
                item-key="id"
                :custom-sort="customSort"
                disable-initial-sort
                class="elevation-1">

            <template slot="no-data">

                <v-flex align-center class="font-weight-medium text-sm-center blockquote">
                    You have not archive items yet!
                </v-flex>

            </template>

            <template v-slot:items="props">
                <td class="pa-2"><img :src="showItemCoinImage(props.item.coin.id)"/></td>

                <td class="font-weight-medium">
                    {{ props.item.coin.name }}
                </td>

                <td>
                    {{ props.item.amount | generalValuesWithGrouping}} {{props.item.coin.symbol}}
                </td>

                <td>
                    {{ showCoinMarketPrice(props.item) |
                    generalValuesByCurrency(props.item.showedCurrency) }} {{ props.item.showedCurrency }}
                </td>

                <td :class="getPercentColor(showCoin24hPriceChange(props.item))">
                    {{ showCoin24hPriceChange(props.item) | percentsValues }}%
                </td>

                <td :class="getPercentColor(showCoin7dPriceChange(props.item))">
                    {{ showCoin7dPriceChange(props.item) | percentsValues }}%
                </td>

                <td><img src="@/assets/price-graph.png"/></td>

            </template>

        </v-data-table>

    </v-container>

</template>

<script>
    import {mapGetters, mapState} from "vuex";

    export default {
        name: "TabArchive",
        data() {
            return {
                headers: [
                    {text: '', sortable: false, width: "1%", class: "pa-0"},
                    {text: 'Coin', value: 'coin-name'},
                    {text: 'Amount', value: 'amount'},
                    {text: 'Market Price', value: 'market-price'},
                    {text: '24h Changed', value: '24h-changed'},
                    {text: '7d Changed', value: '7d-changed'},
                    {text: 'Price Graph', sortable: false, align: 'center', width: "300",},
                ],
            }
        },
        computed: {
            ...mapGetters(['isUserPortfolioLoaded', 'isUserCoinsMarketDataLoaded']),
            ...mapState({
                // userPortfolio: state => state.portfolio.userPortfolio,
                userPortfolioItems: state => state.portfolio.userPortfolio.items,
                userCoinsMarketData: state => state.marketdata.userCoinsMarketData,
            }),
            showPortfolioArchiveItems() {

                if (this.isUserPortfolioLoaded) {

                    // show only "NotArchive" items, that is to say they have amount > 0
                    return this.userPortfolioItems.filter(item => (item.amount === 0));
                }
            },
        },
        methods: {
            showItemCoinImage(id) {

                if (this.isUserPortfolioLoaded) {
                    return 'https://s2.coinmarketcap.com/static/img/coins/32x32/' + id + '.png'
                }
                return '@/assets/coin-default.png'
            },
            showCoinMarketPrice(item) {

                if (this.isUserCoinsMarketDataLoaded) {

                    switch (item.showedCurrency) {
                        case 'USD':
                            return this.userCoinsMarketData[item.coin.id]['USD']['price'];
                        case 'EUR':
                            return this.userCoinsMarketData[item.coin.id]['EUR']['price'];
                        case 'BTC':
                            return this.userCoinsMarketData[item.coin.id]['BTC']['price'];
                        case 'ETH':
                            return this.userCoinsMarketData[item.coin.id]['ETH']['price'];
                        default:
                            return 0;
                    }
                }
            },
            showCoin24hPriceChange(item) {

                if (this.isUserCoinsMarketDataLoaded) {

                    switch (item.showedCurrency) {
                        case 'USD':
                            return this.userCoinsMarketData[item.coin.id]['USD']['percent_change_24h'];
                        case 'EUR':
                            return this.userCoinsMarketData[item.coin.id]['EUR']['percent_change_24h'];
                        case 'BTC':
                            return this.userCoinsMarketData[item.coin.id]['BTC']['percent_change_24h'];
                        case 'ETH':
                            return this.userCoinsMarketData[item.coin.id]['ETH']['percent_change_24h'];
                        default:
                            return 0;
                    }
                }
            },
            showCoin7dPriceChange(item) {

                switch (item.showedCurrency) {
                    case 'USD':
                        return this.userCoinsMarketData[item.coin.id]['USD']['percent_change_7d'];
                    case 'EUR':
                        return this.userCoinsMarketData[item.coin.id]['EUR']['percent_change_7d'];
                    case 'BTC':
                        return this.userCoinsMarketData[item.coin.id]['BTC']['percent_change_7d'];
                    case 'ETH':
                        return this.userCoinsMarketData[item.coin.id]['ETH']['percent_change_7d'];
                    default:
                        return 0;
                }
            },
            getValueColor(value) {

                if (value >= 0) {
                    return 'green--text'
                } else if (value < 0) {
                    return 'red--text'
                }
            },
            getPercentColor(value) {

                value = this.$options.filters.percentsValues(value);

                if (value >= 0) {
                    return 'green--text'
                } else if (value < 0) {
                    return 'red--text'
                }
            },
            customSort(items, index, isDesc) {

                items.sort((a, b) => {

                    if (index === 'coin-name') {
                        if (isDesc) {
                            return a.coin.name.toLowerCase() < b.coin.name.toLowerCase() ? -1 : 1;
                        } else {
                            return a.coin.name.toLowerCase() > b.coin.name.toLowerCase() ? -1 : 1;
                        }
                    } else if (index === 'amount') {
                        if (isDesc) {
                            return a.amount < b.amount ? -1 : 1;
                        } else {
                            return a.amount > b.amount ? -1 : 1;
                        }
                    } else if (index === 'market-price') {
                        if (isDesc) {
                            return this.showCoinMarketPrice(a)
                            < this.showCoinMarketPrice(b) ? -1 : 1;
                        } else {
                            return this.showCoinMarketPrice(a)
                            > this.showCoinMarketPrice(b) ? -1 : 1;
                        }
                    } else if (index === '24h-changed') {
                        if (isDesc) {
                            return this.showCoin24hPriceChange(a)
                            < this.showCoin24hPriceChange(b) ? -1 : 1;
                        } else {
                            return this.showCoin24hPriceChange(a)
                            > this.showCoin24hPriceChange(b) ? -1 : 1;
                        }
                    } else if (index === '7d-changed') {
                        if (isDesc) {
                            return this.showCoin7dPriceChange(a)
                            < this.showCoin7dPriceChange(b) ? -1 : 1;
                        } else {
                            return this.showCoin7dPriceChange(a)
                            > this.showCoin7dPriceChange(b) ? -1 : 1;
                        }
                    }
                });

                return items;
            },
        },
    }
</script>

<style scoped>

</style>