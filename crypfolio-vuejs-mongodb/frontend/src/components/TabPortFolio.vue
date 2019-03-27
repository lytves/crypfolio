<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">

    <v-container class="ma-0 pa-0">

        <v-card>
            <v-layout row xs12>

                <v-flex xs10 d-flex>

                    <v-layout row>
                        <v-card-text style="width: auto;">
                            <span class="grey--text">Market value:</span>
                            <span class="pa-3 font-weight-medium">
                                {{ showPortfolioMarketValue | portfolioValues(portfolioShowedCurrency) }} {{ portfolioShowedCurrency }}
                            </span>
                        </v-card-text>

                        <v-flex sm6 d-flex class="selectsFlexBasis">
                            <v-select
                                    v-model="portfolioShowedCurrency"
                                    :items="currencies"
                                    solo hide-details
                                    :disabled="!isUserPortfolioLoaded">

                            </v-select>
                        </v-flex>

                        <v-card-text style="width: auto;">

                            <span class="grey--text">Net cost:</span>
                            <span class="pa-3 font-weight-medium">
                                {{ showPortfolioNetCost | portfolioValues(portfolioShowedCurrency) }} {{ portfolioShowedCurrency }}
                            </span>

                            <span class="grey--text">Profit:</span>
                            <span class="pa-3 font-weight-medium"
                                  :class="getValueColor(showPortfolioProfit)">
                                {{ showPortfolioProfit | portfolioValues(portfolioShowedCurrency) }} {{ portfolioShowedCurrency }}
                                ({{ showPortfolioProfitPercentage | percentsValues }}%)
                            </span>
                        </v-card-text>
                    </v-layout>

                </v-flex>

                <v-flex xs2 d-flex>

                    <v-btn
                            color="primary lighten-2"
                            dark
                            @click.stop="showAddPortfolioItemDialog">
                        <v-icon left>fas fa-plus</v-icon>
                        Add New Item
                    </v-btn>

                </v-flex>

            </v-layout>
        </v-card>

        <v-data-table
                :headers="headers"
                hide-actions
                :items="showPortfolioItems"
                item-key="id"
                :custom-sort="customSort"
                disable-initial-sort
                class="elevation-1">

            <template slot="no-data">

                <v-flex align-center class="font-weight-medium text-sm-center blockquote">
                    Your portfolio is empty. Add any item to start!
                </v-flex>

            </template>

            <template v-slot:items="props">
                <tr class="cursorPointer" @click="selectItem = props.item.id">
                    <td class="pa-2"><img :src="showItemCoinImage(props.item.coin.id)"/></td>

                    <td class="font-weight-medium">
                        {{ props.item.coin.name }}
                    </td>

                    <td>
                        {{ props.item.amount | generalValuesWithGrouping}} {{props.item.coin.symbol}}
                    </td>

                    <td class="font-weight-medium">
                        {{ showItemMarketValue(props.item) |
                        generalValuesByCurrency(props.item.showedCurrency) }} {{ props.item.showedCurrency }}
                    </td>

                    <td>
                        {{ showCoinMarketPrice(props.item) |
                        generalValuesByCurrency(props.item.showedCurrency) }} {{ props.item.showedCurrency }}
                    </td>

                    <td :class="getPercentColor(showCoin24hPriceChange(props.item))">
                        {{ showCoin24hPriceChange(props.item) | percentsValues }}%
                    </td>

                    <td :class="getValueColor(showItemProfit(props.item))">
                        {{ showItemProfit(props.item) |
                        generalValuesByCurrency(props.item.showedCurrency) }} {{ props.item.showedCurrency }}
                    </td>

                    <td :class="getPercentColor(showItemProfitPercentage(props.item))">
                        {{ showItemProfitPercentage(props.item) | percentsValues }}%
                    </td>

                    <td>
                        {{ showItemSharePercentage(props.item) | percentsValues }}%
                    </td>
                </tr>
            </template>

        </v-data-table>

        <AddPortfolioItem v-model="addPortfolioItemDialog"></AddPortfolioItem>
        <ItemDetails v-model="itemDetailsSheet"
                     :selectedItem="selectItem"
                     @clear-selected-item="clearSelectedItem"></ItemDetails>

    </v-container>

</template>

<script>
    import {mapGetters, mapState} from 'vuex'
    import {PORTFOLIO_UPDATE_CURRENCY} from '../store/actions/portfolio'
    import AddPortfolioItem from './layout/AddPortfolioItem'
    import {MARKETDATA_ALLCOINSLIST_SUCCESS} from "../store/actions/marketdata";
    import ItemDetails from "./layout/ItemDetails";

    export default {
        name: "TabPortfolio",
        components: {
            AddPortfolioItem,
            ItemDetails
        },
        data() {
            return {
                headers: [
                    {text: '', sortable: false, width: "1%", class: "pa-0"},
                    {text: 'Coin', value: 'coin-name'},
                    {text: 'Amount', value: 'amount'},
                    {text: 'Market Value', value: 'market-value'},
                    {text: 'Market Price', value: 'market-price'},
                    {text: '24h Changed', value: '24h-changed'},
                    {text: 'Profit', value: 'profit'},
                    {text: 'Profit %', value: 'profit-percentage'},
                    {text: 'Share %', value: 'share-percentage'},
                ],
                currencies: ['USD', 'EUR', 'BTC', 'ETH'],
                addPortfolioItemDialog: false,
                itemDetailsSheet: false,
                selectItemId: null,
            }
        },
        computed: {
            ...mapGetters(['isUserPortfolioLoaded', 'isUserCoinsMarketDataLoaded']),
            ...mapState({
                userPortfolio: state => state.portfolio.userPortfolio,
                userPortfolioItems: state => state.portfolio.userPortfolio.items,
                userCoinsMarketData: state => state.marketdata.userCoinsMarketData,
            }),
            showPortfolioItems() {

                if (this.isUserPortfolioLoaded) {

                    // show only "NotArchive" items, that is to say they have amount > 0
                    return this.userPortfolioItems.filter(item => (item.amount > 0));
                }
            },
            portfolioShowedCurrency: {
                get() {
                    if (this.isUserPortfolioLoaded) {
                        return this.userPortfolio.showedCurrency
                    }
                },
                set(mainCurrency) {
                    this.$store.dispatch(PORTFOLIO_UPDATE_CURRENCY, mainCurrency)
                }
            },
            showPortfolioNetCost() {

                switch (this.userPortfolio.showedCurrency) {
                    case 'USD':
                        return this.userPortfolio.netCostUsd;
                    case 'EUR':
                        return this.userPortfolio.netCostEur;
                    case 'BTC':
                        return this.userPortfolio.netCostBtc;
                    case 'ETH':
                        return this.userPortfolio.netCostEth;
                    default:
                        return 0;
                }
            },
            showPortfolioMarketValue() {

                if (this.isUserPortfolioLoaded && this.isUserCoinsMarketDataLoaded) {

                    let marketValue = 0;
                    this.userPortfolioItems.forEach((item) => {
                        marketValue += this.showItemMarketValueByCurrency(item, this.userPortfolio.showedCurrency);
                    });
                    return marketValue;
                }
            },
            showPortfolioProfit() {

                if (this.isUserPortfolioLoaded && this.isUserCoinsMarketDataLoaded) {
                    let profit = 0;
                    profit = this.showPortfolioMarketValue - this.showPortfolioNetCost;
                    return profit;
                }
            },
            showPortfolioProfitPercentage() {

                if (this.isUserPortfolioLoaded && this.isUserCoinsMarketDataLoaded) {
                    if (this.showPortfolioNetCost) {
                        return this.showPortfolioProfit * 100 / this.showPortfolioNetCost;
                    }
                    return 0;
                }
            },
            selectItem: {
                get() {
                    if (this.isUserPortfolioLoaded) {
                        return this.userPortfolioItems.find(item => (item.id === this.selectItemId));
                    }
                },
                set(id) {
                    this.itemDetailsSheet = true;
                    this.selectItemId = id;
                }
            },
        },
        methods: {
            // it calls from ItemDetails child's method for clear selectedItem on close ItemDetailsSheet
            clearSelectedItem() {
                this.selectItemId = null;
                // this.selectItem = null;
            },
            showItemCoinImage(id) {

                if (this.isUserPortfolioLoaded) {
                    return 'https://s2.coinmarketcap.com/static/img/coins/32x32/' + id + '.png'
                }
                return '@/assets/coin-default.png'
            },
            showAddPortfolioItemDialog() {

                this.addPortfolioItemDialog = true;
                this.$store.dispatch(MARKETDATA_ALLCOINSLIST_SUCCESS)
            },
            showItemMarketValue(item) {

                if (this.isUserCoinsMarketDataLoaded) {

                    return item.amount * this.showCoinMarketPrice(item);
                }
            },
            showItemMarketValueByCurrency(item, currency) {

                if (this.isUserCoinsMarketDataLoaded) {

                    return item.amount * this.showCoinMarketPriceByCurrency(item, currency);
                }
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
            showCoinMarketPriceByCurrency(item, currency) {

                if (this.isUserCoinsMarketDataLoaded) {

                    switch (currency) {
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
            showItemProfit(item) {

                if (this.isUserCoinsMarketDataLoaded) {

                    let profit = 0;

                    profit = (item.amount * this.showCoinMarketPrice(item)) - this.getItemNetCostsByCurrency(item);

                    return profit;
                }
            },
            showItemProfitPercentage(item) {

                let profitPercentage = 0;

                if (this.isUserCoinsMarketDataLoaded && item.amount > 0) {

                    switch (item.showedCurrency) {
                        case 'USD':
                            profitPercentage = this.showItemProfit(item) * 100 / item.netCostUsd;
                            break;
                        case 'EUR':
                            profitPercentage = this.showItemProfit(item) * 100 / item.netCostEur;
                            break;
                        case 'BTC':
                            profitPercentage = this.showItemProfit(item) * 100 / item.netCostBtc;
                            break;
                        case 'ETH':
                            profitPercentage = this.showItemProfit(item) * 100 / item.netCostEth;
                            break;
                    }
                }
                return profitPercentage;
            },
            showItemSharePercentage(item) {

                let sharePercentage = 0;

                if (this.isUserCoinsMarketDataLoaded && item.amount > 0) {

                    sharePercentage = this.showItemMarketValueByCurrency(item, this.userPortfolio.showedCurrency)
                        * 100 / this.showPortfolioMarketValue;
                }
                return sharePercentage;
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
            getItemNetCostsByCurrency(item) {

                if (this.isUserCoinsMarketDataLoaded) {

                    switch (item.showedCurrency) {
                        case 'USD':
                            return item.netCostUsd;
                        case 'EUR':
                            return item.netCostEur;
                        case 'BTC':
                            return item.netCostBtc;
                        case 'ETH':
                            return item.netCostEth;
                        default:
                            return 0;
                    }
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
                    } else if (index === 'market-value') {
                        if (isDesc) {
                            return this.showItemMarketValue(a)
                            < this.showItemMarketValue(b) ? -1 : 1;
                        } else {
                            return this.showItemMarketValue(a)
                            > this.showItemMarketValue(b) ? -1 : 1;
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
                    } else if (index === 'profit') {
                        if (isDesc) {
                            return this.showItemProfit(a)
                            < this.showItemProfit(b) ? -1 : 1;
                        } else {
                            return this.showItemProfit(a)
                            > this.showItemProfit(b) ? -1 : 1;
                        }
                    } else if (index === 'profit-percentage') {
                        if (isDesc) {
                            return this.showItemProfitPercentage(a)
                            < this.showItemProfitPercentage(b) ? -1 : 1;
                        } else {
                            return this.showItemProfitPercentage(a)
                            > this.showItemProfitPercentage(b) ? -1 : 1;
                        }
                    } else if (index === 'share-percentage') {
                        if (isDesc) {
                            return this.showItemSharePercentage(a)
                            < this.showItemSharePercentage(b) ? -1 : 1;
                        } else {
                            return this.showItemSharePercentage(a)
                            > this.showItemSharePercentage(b) ? -1 : 1;
                        }
                    }
                });

                return items;
            },
        }
    }
</script>

<style scoped>
</style>