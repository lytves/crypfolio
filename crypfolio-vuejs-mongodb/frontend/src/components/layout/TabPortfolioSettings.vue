<template>
    <v-container class="ma-0 pa-0">
        <v-card class="fff pa-3"
                outlined>

            <v-form @submit.prevent="save" v-model="formValid" v-if="isUserPortfolioLoaded" ref="form">

                <v-text-field
                        prepend-icon="dashboard"
                        v-model="portfolioName"
                        label="Portfolio Name"
                        :rules="portfolioNameRules">
                </v-text-field>

                <v-switch class="ma-0r pa-0" v-model="isShare" label="Share portfolio"></v-switch>
                <v-switch class="ma-0 pa-0" v-model="isShowAmounts" label="Show holdings amounts"
                          :disabled="!isShare"></v-switch>

                <v-card-actions class="justify-center">
                    <v-btn color="primary" @click="save()" :disabled="!isButtonSaveEnabled">Save</v-btn>
                </v-card-actions>

                <v-card-text class="ma-1 pa-1">
                    <span>Sharing link:</span>
                </v-card-text>

                <v-text-field
                        class="ma-0 pa-0"
                        prepend-icon="link"
                        v-model="shareLink"
                        disabled
                        readonly>
                </v-text-field>

                <v-card-actions class="justify-center">
                    <v-btn v-clipboard:copy="shareLink">
                        <v-icon left>file_copy</v-icon>
                        Copy
                    </v-btn>
                </v-card-actions>

            </v-form>

        </v-card>
    </v-container>
</template>

<script>
    import {mapGetters, mapState} from "vuex";
    import {SNACKBAR_ERROR} from "../../store/actions/snackbar";
    import {PORTFOLIO_UPDATE_VALUES} from "../../store/actions/portfolio";

    export default {
        name: "TabPortfolioSettings",
        data: () => ({
            formValid: false,
            portfolioName: "",
            portfolioNameRules: [
                v => !!v || 'Portfolio Name is required',
                v => v.length >= 1 || 'Portfolio Name must be at least 1 character',
            ],
            isShare: false,
            isShowAmounts: false,
            shareLink: ""
        }),
        computed: {
            ...mapGetters(['isUserPortfolioLoaded']),
            ...mapState({
                userPortfolioName: state => state.portfolio.userPortfolio.name,
                isSharePortfolio: state => state.portfolio.userPortfolio.isShare,
                isShowAmountsPortfolio: state => state.portfolio.userPortfolio.isShowAmounts,
                shareLinkPortfolio: state => state.portfolio.userPortfolio.shareLink
            }),
            isButtonSaveEnabled() {
                return (this.portfolioName !== '' && this.portfolioName !== this.userPortfolioName
                    || this.isShare !== this.isSharePortfolio
                    || this.isShowAmounts !== this.isShowAmountsPortfolio);
            }
        },
        mounted() {
            this.isShare = this.isSharePortfolio;
            this.isShowAmounts = this.isShowAmountsPortfolio;
            this.portfolioName = this.userPortfolioName;
            this.shareLink = window.location.href + "/" + this.shareLinkPortfolio;
        },
        methods: {
            save() {
                if (this.isButtonSaveEnabled) {
                    const {isShare, isShowAmounts, portfolioName} = this;
                    this.$store.dispatch(PORTFOLIO_UPDATE_VALUES, {isShare, isShowAmounts, portfolioName})
                        .then(() => {
                        })
                        .catch(() => {
                        })
                } else {
                    this.$store.dispatch(SNACKBAR_ERROR, "Invalid Action!");
                }
            },
        },
    }
</script>

<style scoped>

</style>