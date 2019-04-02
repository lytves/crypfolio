<template>
    <div class="text-xs-center">
        <v-dialog
                lazy
                v-model="show"
                width="500">

            <v-card>
                <v-card-title
                        class="headline grey lighten-2"
                        primary-title>
                    List of portfolios of your friends
                </v-card-title>

                <v-card-text>
                    To implement a list of portfolios of the user's friends
                </v-card-text>

            </v-card>
        </v-dialog>
    </div>
</template>

<script>
    export default {
        name: "Friends",
        props: {
            value: Boolean
        },
        computed: {
            show: {
                get() {
                    return this.value
                },
                set(value) {
                    this.$emit('input', value)
                }
            }
        }
    }
</script>

<style scoped>

</style>