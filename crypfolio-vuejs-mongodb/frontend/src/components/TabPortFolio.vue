<template>

    <v-container class="ma-0 pa-0">

        <v-card>
            <v-layout row xs12>

                <v-flex xs12 d-flex>

                    <v-layout row>
                        <v-card-text style="width: auto;">
                            <span class="grey--text">Market value:</span>
                            <span class="pa-3 font-weight-medium">
                                {{123456789.123456789 | portfolioValues(portfolioShowedCurrency)}} {{portfolioShowedCurrency}}
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
                                {{showPortfolioNetCost | portfolioValues(portfolioShowedCurrency)}} {{portfolioShowedCurrency}}
                            </span>

                            <span class="grey--text">Profit:</span>
                            <span class="pa-3 font-weight-medium green--text">
                                {{123456789.123456789 | portfolioValues(portfolioShowedCurrency) }} {{portfolioShowedCurrency}} (169.19%)
                            </span>
                        </v-card-text>
                    </v-layout>

                </v-flex>

            </v-layout>
        </v-card>

        <v-data-table
                :headers="headers"
                hide-actions
                :items="showPortfolioItems"
                class="elevation-1">

            <template slot="no-data">

                <v-flex align-center class="font-weight-medium text-sm-center blockquote">
                    Your portfolio is empty. Add any item to start!
                </v-flex>

            </template>

            <template slot="items" slot-scope="props">
                <td class="pa-2"><img :src="showItemCoinImage(props.item.coin.id)"/></td>
                <td class="font-weight-medium">{{ props.item.coin.name }}</td>
                <td class="font-weight-medium">{{ props.item.amount}} {{props.item.coin.symbol}}</td>
                <td>counted</td>
                <td>counted</td>
                <td>counted</td>
                <td>counted</td>
                <td>counted</td>
                <td>counted</td>
            </template>

        </v-data-table>

    </v-container>

</template>

<script>
    import {mapGetters, mapState} from 'vuex'
    import {PORTFOLIO_UPDATE_CURRENCY} from '../store/actions/portfolio'


    export default {
        name: "TabPortfolio",
        data() {
            return {
                headers: [
                    {text: '', value: 'img', sortable: false, width: "1%", class: "pa-0"},
                    {text: 'Coin', value: 'name'},
                    {text: 'Amount', value: 'calories'},
                    {text: 'Market Value', value: 'fat'},
                    {text: 'Market Price', value: 'carbs'},
                    {text: '24h Changed', value: 'protein'},
                    {text: 'Profit', value: 'iron'},
                    {text: 'Profit %', value: 'iron'},
                    {text: 'Share %', value: 'iron'},
                    // {text: 'example',  align: 'left', sortable: false, value: 'name'},
                ],
                counted: 'toCount',
                currencies: ['USD', 'EUR', 'BTC', 'ETH'],
            }
        },
        computed: {
            // users: () => this.$store.getters.userSet,

            ...mapGetters(['isUserPortfolioLoaded']),
            ...mapState({
                userPortfolio: state => state.portfolio.userPortfolio,
                userPortfolioItems: state => state.portfolio.userPortfolio.items
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

        },
        methods: {
            showItemCoinImage(id) {
                if (this.isUserPortfolioLoaded) {
                    return 'https://s2.coinmarketcap.com/static/img/coins/32x32/' + id + '.png'
                }
                return '@/assets/coin-default.png'
            }
        }
    }
</script>

<style scoped>
</style>