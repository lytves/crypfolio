<template>

    <v-container>

        <v-layout align-center column>

            <BigLogo></BigLogo>

            <v-flex xs12 sm8 md4>
                <v-card class="elevation-12" width="400" color="orange darken-1">
                    <v-card-text>Demo account credentials:<br>
                            E-mail: demo@crypfolio.tk<br>
                            Password: demo123
                        </v-card-text>
                </v-card>
            </v-flex>

            <v-flex xs12 sm8 md4>

                <v-card class="elevation-12" width="400">

                    <v-toolbar dark color="primary">
                        <v-toolbar-title>Login to Crypfolio</v-toolbar-title>
                    </v-toolbar>

                    <v-card-text>

                        <v-form @submit.prevent="login" v-model="formValid">
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
                                    browser-autocomplete="off"
                                    :append-icon="visible ? 'visibility_off' : 'visibility'"
                                    @click:append="() => (visible = !visible)"
                                    :type="visible ? 'text' : 'password'">
                            </v-text-field>
                        </v-form>

                        <p class="text-sm-left">
                            <router-link to="/reset-password">Reset password</router-link>
                        </p>
                        <p class="text-sm-left" v-if="showResendLink">
                            <router-link to="/resend-verif-email">Resend confirm email</router-link>
                        </p>

                    </v-card-text>

                    <v-card-actions class="justify-center">
                        <v-btn color="primary" type="submit" @click="login()">Login</v-btn>
                    </v-card-actions>
                </v-card>

                <v-layout align-center column>
                    <v-spacer style="height: 20px;"></v-spacer>
                    <router-link to="/signup">Create an account</router-link>
                </v-layout>

            </v-flex>

        </v-layout>

    </v-container>

</template>

<script>
    import BigLogo from '@/components/layout/BigLogo'
    import {AUTH_REQUEST} from '../store/actions/auth'
    import {SNACKBAR_ERROR} from '../store/actions/snackbar'

    export default {
        name: 'login',
        components: {
            BigLogo
        },
        data: () => ({
            showResendLink: false,
            visible: false,
            formValid: false,
            email: '',
            password: '',
            passwordRules: [
                v => !!v || 'Password is required',
                v => v.length >= 6 || 'Password must be at least 6 characters'
            ],
            emailRules: [
                v => !!v || 'E-mail is required',
                v => /.+@.+/.test(v) || 'E-mail must be valid'
            ]
        }),
        methods: {
            login() {

                if (this.formValid) {

                    const {email, password} = this;
                    this.$store.dispatch(AUTH_REQUEST, {email, password})
                        .then(() => {
                            this.$router.push('/user')
                        })
                        .catch(() => {
                            this.showResendLink = true
                        });
                } else {
                    this.$store.dispatch(SNACKBAR_ERROR, "Invalid Login Credentials!");
                }
            }
        },
    }
</script>

<style scoped>
    a {
        text-decoration: none;
        text-transform: uppercase;
        font-weight: bold;
    }

    p {
        margin-top: 16px;
    }
</style>