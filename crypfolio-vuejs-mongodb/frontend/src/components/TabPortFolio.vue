<template>

    <v-container class="ma-0 pa-0">

        <v-card>
            <v-layout row xs12>

                <v-flex xs12 d-flex>

                    <v-layout row>
                        <v-card-text style="width: auto;">
                            <span class="grey--text">Market value:</span>
                            <span class="pa-3 font-weight-medium">555,333,315,639.6 {{portfolioShowedCurrency}}</span>
                        </v-card-text>

                        <v-flex xs12 sm6 d-flex selectClass>
                            <v-select
                                    v-model="portfolioShowedCurrency"
                                    :items="currencies"
                                    label="Solo field"
                                    solo hide-details
                                    :disabled="!isUserPortfolioLoaded">

                            </v-select>
                        </v-flex>

                        <v-card-text style="width: auto;">

                            <span class="grey--text">Net cost:</span>
                            <span class="pa-3 font-weight-medium">5,889.77 {{portfolioShowedCurrency}}</span>

                            <span class="grey--text">Profit:</span>
                            <span class="pa-3 font-weight-medium green--text">9,289.83 {{portfolioShowedCurrency}} (169.19%)</span>
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
                <td>{{ props.item.coin.name }}</td>
                <td>{{ props.item.amount}} {{props.item.coin.symbol}}</td>
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
                    return this.userPortfolioItems
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
            }
        },
        mounted() {
        }
    }
</script>

<style scoped>
    .selectClass {
        flex-basis: 0;
    }
</style>