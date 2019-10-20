<template>
    <v-dialog
            lazy
            v-model="show"
            width="400">

        <v-card>
            <v-card-title
                    class="headline grey lighten-2"
                    primary-title>
                Settings
            </v-card-title>

            <v-card-text class="ma-0 pa-0">
                <!-- Tabs choosing -->
                <v-toolbar-items style="height: unset;">

                    <v-tabs
                            fixed-tabs
                            v-model="active_tab">

                        <!-- Tab title -->
                        <v-tab
                                v-for="item in items"
                                @click.stop="chooseTab(item.slug)">

                            {{item.title}}

                        </v-tab>

                    </v-tabs>

                </v-toolbar-items>
            </v-card-text>

            <!-- Component changes when currentTabComponent changes -->
            <!-- https://vuejs.org/v2/guide/components.html#Dynamic-Components -->
            <component
                    :is="currentTabComponent">
            </component>

        </v-card>

    </v-dialog>

</template>

<script>
    import TabUserSettings from "./TabUserSettings";
    import TabPortfolioSettings from "./TabPortfolioSettings";

    export default {
        name: "UserSettings",
        components: {
            TabUserSettings,
            TabPortfolioSettings,
        },
        props: {
            value: Boolean
        },
        data() {
            return {
                currentTabComponent: 'TabUserSettings',
                active_tab: 0,
                items: [
                    {title: 'User', slug: 'TabUserSettings'},
                    {title: 'Portfolio', slug: 'TabPortfolioSettings'},
                ],
            }
        },
        computed: {
            show: {
                get() {
                    return this.value
                },
                set(value) {
                    this.$emit('input', value)
                },
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

</style>