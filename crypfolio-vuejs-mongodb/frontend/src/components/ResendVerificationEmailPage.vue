<template>

    <v-container>

        <v-layout align-center column>

            <BigLogo></BigLogo>

            <v-flex xs12 sm8 md4>

                <v-card class="elevation-12" width="400">

                    <v-toolbar dark color="primary">
                        <v-toolbar-title>Resend Verification Email</v-toolbar-title>
                    </v-toolbar>

                    <v-card-title primary-title v-if="verifEmailWasSent">
                        <div>
                            A verification email was sent to you. Please check your email and click the confirmation link.
                        </div>
                    </v-card-title>

                    <v-card-text v-else="!verifEmailWasSent">

                        <v-form @submit.prevent="resendEmail" v-model="formValid">
                            <v-text-field
                                    prepend-icon="person"
                                    v-model="email"
                                    :rules="emailRules"
                                    label="E-mail"
                                    required>
                            </v-text-field>
                        </v-form>

                        <v-card-actions class="justify-center">
                            <v-btn color="primary" @click="resendEmail()">Send</v-btn>
                        </v-card-actions>

                    </v-card-text>

                </v-card>

                <v-layout align-center column>
                    <v-spacer style="height: 20px;"></v-spacer>
                    <v-spacer>Already have an account?</v-spacer>
                    <router-link to="/login">Login</router-link>
                </v-layout>

            </v-flex>

        </v-layout>

    </v-container>

</template>

<script>
    import BigLogo from '@/components/layout/BigLogo'
    import {SNACKBAR_ERROR} from "../store/actions/snackbar";
    import {USER_RESEND_VERIFICATION_EMAIL_REQUEST} from "../store/actions/user";

    export default {
        name: 'resend-verif-email',
        components: {
            BigLogo
        },
        data: () => ({
            verifEmailWasSent: false,
            formValid: false,
            email: '',
            emailRules: [
                v => !!v || 'E-mail is required',
                v => /.+@.+/.test(v) || 'E-mail must be valid'
            ]
        }),
        methods: {
            resendEmail() {
                if (this.formValid) {
                    console.log('Only if Login Form was completed correctly, do API request', this.formValid);

                    this.$store.dispatch(USER_RESEND_VERIFICATION_EMAIL_REQUEST, this.email)
                        .then(() => {
                            this.verifEmailWasSent = true;
                        })
                        .catch(() => {})
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