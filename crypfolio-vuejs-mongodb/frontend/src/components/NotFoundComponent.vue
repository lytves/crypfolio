<template>

    <v-container fluid fill-height>

        <v-layout align-center justify-center column>

            <Header></Header>

            <v-flex xs12 sm8 md4>

                <v-card class="elevation-12" min-width="672">
                    <v-toolbar dark color="primary">
                        <v-toolbar-title>Error: Page Not Found</v-toolbar-title>
                    </v-toolbar>

                    <v-card-title primary-title>
                        <img alt="Crypfolio logo" src="@/assets/error404.png">
                    </v-card-title>

                </v-card>

            </v-flex>
        </v-layout>

    </v-container>

</template>

<script>
    import Header from '@/components/layout/Header'
    export default {
        name: 'NotFoundComponent',
        components: {
            Header
        },
    }
</script>

<style scoped>

</style>