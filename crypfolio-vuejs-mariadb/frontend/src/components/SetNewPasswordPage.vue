<template>

    <v-container>

        <v-layout align-center column>

            <BigLogo></BigLogo>

            <v-flex xs12 sm8 md4>

                <v-card class="elevation-12" width="400">

                    <v-toolbar dark color="primary">
                        <v-toolbar-title>Set New Password</v-toolbar-title>
                    </v-toolbar>

                    <v-card-text>

                        <v-form @submit.prevent="setNewPassword" v-model="formValid">

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
                                    browser-autocomplete="off"
                                    :append-icon="visible ? 'visibility_off' : 'visibility'"
                                    @click:append="() => (visible = !visible)"
                                    :type="visible ? 'text' : 'password'">
                            </v-text-field>

                        </v-form>

                        <v-card-actions class="justify-center">
                            <v-btn color="primary" @click="setNewPassword()">Submit</v-btn>
                        </v-card-actions>

                    </v-card-text>

                </v-card>

            </v-flex>

        </v-layout>

    </v-container>

</template>

<script>
    import BigLogo from '@/components/layout/BigLogo'
    import {SNACKBAR_ERROR} from "../store/actions/snackbar";
    import {USER_SET_NEW_PASSWORD} from "../store/actions/user";

    export default {
        name: 'reset-password-reset-link',
        components: {
            BigLogo
        },
        data: () => ({
            code: '',
            formValid: false,
            visible: false,
            password: '',
            password2: '',
            passwordRules: [
                v => !!v || 'Password is required',
                v => v.length >= 6 || 'Password must be at least 6 characters',
            ],
        }),
        mounted() {
            this.code = new URL(window.location.href).searchParams.get("code");
        },
        methods: {
            setNewPassword() {
                if (this.formValid) {

                    const {code, password} = this;

                    this.$store.dispatch(USER_SET_NEW_PASSWORD, {code, password})
                        .then(() => {
                            this.$router.push('/user')
                        })
                        .catch(() => {
                        })
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