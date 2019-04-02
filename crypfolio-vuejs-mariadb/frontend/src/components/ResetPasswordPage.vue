<template>

    <v-container>

        <v-layout align-center column>

            <BigLogo></BigLogo>

            <v-flex xs12 sm8 md4>

                <v-card class="elevation-12" width="400">

                    <v-toolbar dark color="primary">
                        <v-toolbar-title>Reset Password</v-toolbar-title>
                    </v-toolbar>

                    <v-card-title primary-title v-if="resetPasswordFormWasSent">
                        <div>
                            Please check the email we have just sent to you and follow
                            the link to reset your password.
                        </div>
                    </v-card-title>

                    <v-card-text v-else="!resetPasswordFormWasSent">

                        <v-form @submit.prevent="resetPassword" v-model="formValid">
                            <v-text-field
                                    prepend-icon="person"
                                    v-model="email"
                                    :rules="emailRules"
                                    label="E-mail"
                                    required>
                            </v-text-field>
                        </v-form>

                        <v-card-actions class="justify-center">
                            <v-btn color="primary" @click="resetPassword()">Send</v-btn>
                        </v-card-actions>

                    </v-card-text>

                </v-card>

                <v-layout align-center column>
                    <v-spacer style="height: 20px;"></v-spacer>
                    <v-spacer>Remember your password?</v-spacer>
                    <router-link to="/login">Login</router-link>
                </v-layout>

            </v-flex>

        </v-layout>

    </v-container>

</template>

<script>
    import BigLogo from '@/components/layout/BigLogo'
    import {SNACKBAR_ERROR} from "../store/actions/snackbar";
    import {USER_RESET_PASSWORD_REQUEST} from "../store/actions/user";

    export default {
        name: 'reset-password',
        components: {
            BigLogo
        },
        data: () => ({
            resetPasswordFormWasSent: false,
            formValid: false,
            email: '',
            emailRules: [
                v => !!v || 'E-mail is required',
                v => /.+@.+/.test(v) || 'E-mail must be valid'
            ]
        }),
        methods: {
            resetPassword() {
                if (this.formValid) {

                    this.$store.dispatch(USER_RESET_PASSWORD_REQUEST, this.email)
                        .then(() => {
                            this.resetPasswordFormWasSent = true;
                        })
                        .catch(() => {
                        })
                } else {
                    this.$store.dispatch(SNACKBAR_ERROR, "Invalid Form Credentials!");
                }
            }
        }
    }
</script>

<style scoped>
    a {
        text-decoration: none;
        text-transform: uppercase;
        font-weight: bold;
    }
</style>