<template>

    <v-container fluid fill-height>

        <v-layout align-center justify-center column>

            <Header></Header>

            <v-flex xs12 sm8 md4>

                <v-card class="elevation-12" width="400">

                    <v-toolbar dark color="primary">
                        <v-toolbar-title>Create an account</v-toolbar-title>
                    </v-toolbar>

                    <v-card-title primary-title v-if="signupFormWasSent">
                        <div>
                            You are registered user now! In order to complete your registration, please click the
                            confirmation link in the email that we just have sent to you.
                        </div>
                    </v-card-title>

                    <v-card-text v-else="!signupFormWasSent">

                        <v-form @submit.prevent="signup" v-model="formValid">

                            <v-text-field
                                    prepend-icon="dashboard"
                                    v-model="portfolio"
                                    :rules="portfolioRules"
                                    label="Portfolio Name"
                                    required>
                            </v-text-field>

                            <v-text-field
                                    prepend-icon="person"
                                    v-model="email"
                                    :rules="emailRules"
                                    label="E-mail"
                                    required>
                            </v-text-field>

                            <v-text-field
                                    prepend-icon="lock"
                                    id="password"
                                    v-model="password"
                                    :rules="passwordRules"
                                    :counter=true
                                    name="password"
                                    label="Password"
                                    :append-icon="visible ? 'visibility_off' : 'visibility'"
                                    @click:append="() => (visible = !visible)"
                                    :type="visible ? 'text' : 'password'">
                            </v-text-field>

                            <v-text-field
                                    :error-messages="passwordMatchError()"
                                    prepend-icon="lock"
                                    id="password2"
                                    v-model="password2"
                                    :counter="true"
                                    name="password2"
                                    label="Repeat Password"
                                    :append-icon="visible ? 'visibility_off' : 'visibility'"
                                    @click:append="() => (visible = !visible)"
                                    :type="visible ? 'text' : 'password'">
                            </v-text-field>
                        </v-form>

                        <v-card-actions class="justify-center">
                            <v-btn color="primary" @click="signup()">Sign Up</v-btn>
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
    import Header from '@/components/layout/Header'
    import {SNACKBAR_ERROR} from '../store/actions/snackbar'
    import {USER_SIGNUP_REQUEST} from '../store/actions/user'

    export default {
        name: 'login',
        components: {
            Header
        },
        data: () => ({
            signupFormWasSent: false,
            visible: false,
            formValid: false,
            portfolio: '',
            email: '',
            password: '',
            password2: '',
            passwordRules: [
                v => !!v || 'Password is required',
                v => v.length >= 6 || 'Password must be at least 6 characters',
            ],
            emailRules: [
                v => !!v || 'E-mail is required',
                v => /.+@.+/.test(v) || 'E-mail must be valid'
            ],
            portfolioRules: [
                v => !!v || 'Portfolio Name is required',
            ]
        }),
        methods: {
            signup() {

                if (this.formValid) {
                    console.log('Only if Login Form was completed correctly, do API request', this.formValid);

                    const {email, password, portfolio} = this;
                    this.$store.dispatch(USER_SIGNUP_REQUEST, {email, password, portfolio})
                        .then(() => {
                            this.signupFormWasSent = true;
                        })
                        .catch(() => {})
                } else {
                    this.$store.dispatch(SNACKBAR_ERROR, "Invalid Form Credentials!");
                }
            },
            passwordMatchError() {
                return (this.password === this.password2) ? '' : 'Password must match'
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