<template>

    <v-container class="ma-0 pa-0">

        <v-card>
            <v-layout row xs12>

                <v-flex xs10 d-flex>

                    <v-card-text style="width: auto;">
                        <span class="grey--text">Bitcon, Ethereum, (??? something more) actual prices</span>
                    </v-card-text>

                </v-flex>

                <v-flex xs2 d-flex>

                    <v-btn
                            color="primary lighten-2"
                            dark
                            @click.stop="showAddWatchCoin">
                        Add New Coin
                    </v-btn>

                </v-flex>

            </v-layout>
        </v-card>

        <v-data-table
                :headers="headers"
                hide-actions
                :items="showWatchlistCoins"
                class="elevation-1">

            <template slot="no-data">

                <v-flex align-center class="font-weight-medium text-sm-center blockquote">
                    Your watchlist is empty. Add any coin to start!
                </v-flex>

            </template>

            <template slot="items" slot-scope="props">
                <td class="pa-2"><img :src="showCoinImage(props.item.coinId.id)"/></td>
                <td class="font-weight-medium">
                    {{ props.item.coinId.name }} {{props.item.coinId.symbol}}
                </td>
                <td>counted</td>
                <td>counted</td>
                <td>counted</td>
                <td>counted</td>
                <td><img src="@/assets/price-graph.png"/></td>
            </template>

        </v-data-table>

        <AddWatchCoin v-model="addWatchCoinDialog"></AddWatchCoin>

    </v-container>

</template>

<script>
    import {mapGetters, mapState} from 'vuex'
    import AddWatchCoin from './layout/AddWatchCoin'

    export default {
        name: "TabWatchlist",
        components: {
            AddWatchCoin,
        },
        data() {
            return {
                headers: [
                    {text: '', value: 'img', sortable: false, width: "1%", class: "pa-0"},
                    {text: 'Coin', value: 'name'},
                    {text: 'Market Price', value: 'calories'},
                    {text: 'Market Cap', value: 'fat'},
                    {text: '24h Changed', value: 'protein'},
                    {text: '7d Changed', value: 'iron'},
                    {text: 'Price Graph', value: 'iron', sortable: false, align: 'center', width: "300",},
                    // {text: 'example',  align: 'left', sortable: false, value: 'name'},
                ],
                counted: 'toCount',
                currencies: ['USD', 'EUR', 'BTC', 'ETH'],
                addWatchCoinDialog: false,
            }
        },
        computed: {
            ...mapGetters(['isUserWatchlistLoaded']),
            ...mapState({
                userWatchlistCoins: state => state.watchlist.userWatchlist
            }),
            showWatchlistCoins() {
                if (this.isUserWatchlistLoaded) {
                    return this.userWatchlistCoins
                }
            }
        },
        methods: {
            showCoinImage(id) {
                if (this.isUserWatchlistLoaded) {
                    return 'https://s2.coinmarketcap.com/static/img/coins/32x32/' + id + '.png'
                }
                return '@/assets/coin-default.png'
            },
            showAddWatchCoin() {
                this.addWatchCoinDialog = true
            },
        }
    }
</script>

<style scoped>

</style>