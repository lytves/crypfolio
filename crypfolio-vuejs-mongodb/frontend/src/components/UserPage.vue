<template>

    <v-container class="pa-0">

        <v-layout align-center column>

            <!-- Main Toolbar -->
            <v-toolbar>

                <!-- Logo -->
                <div>
                    <a href="/">
                        <img class="small-logo" src="../assets/logo_small_vuejs_mongodb.png"
                             alt="Crypfolio :: a new crypto portfolio"/>
                    </a>
                </div>

                <v-spacer></v-spacer>

                <!-- Tabs choosing -->
                <v-toolbar-items style="height: unset;">

                    <v-tabs fixed-tabs
                            color="transparent">

                        <!-- Tab title -->
                        <v-tab
                                v-for="item in items"
                                @click.stop="chooseTab(item.slug)">

                            {{item.title}}

                        </v-tab>

                    </v-tabs>

                </v-toolbar-items>

                <v-spacer></v-spacer>

                <!-- User Dropdown Menu -->
                <UserMenu></UserMenu>

            </v-toolbar>

            <!-- Component changes when currentTabComponent changes -->
            <!-- https://vuejs.org/v2/guide/components.html#Dynamic-Components -->
            <component
                    :is="currentTabComponent">
            </component>

        </v-layout>

    </v-container>

</template>

<script>
    import UserMenu from '@/components/layout/UserMenu'
    import TabPortFolio from './TabPortFolio'
    import TabArchive from './TabArchive'
    import TabWatchList from './TabWatchList'

    export default {
        name: 'user',
        components: {
            UserMenu,
            TabPortFolio,
            TabArchive,
            TabWatchList
        },
        data() {
            return {
                currentTabComponent: 'TabPortFolio',
                items: [
                    {title: 'Portfolio', slug: 'TabPortFolio'},
                    {title: 'Archive', slug: 'TabArchive'},
                    {title: 'Watchlist', slug: 'TabWatchList'}
                ],
            }
        },
        methods: {
            chooseTab(tabSlug) {
                this.currentTabComponent = tabSlug;
            }
        },
    }
</script>

<style scoped>
    .small-logo {
        width: 192px;
        height: 43px;
        display: flex;
    }
</style>