<template>

    <v-container>

        <v-layout align-center column>

            <BigLogo></BigLogo>

            <v-flex xs12 sm8 md4>

                <v-card class="elevation-12" width="400">

                    <v-toolbar dark color="primary">
                        <v-toolbar-title>Email Confirmation</v-toolbar-title>
                    </v-toolbar>

                    <v-card-title primary-title v-if="confirmationEmailInvalid">
                        <div>
                            Email address verification error. The code is invalid or expired,
                            or your email has been already confirmed. Try to request new email address confirmation link by email again.
                        </div>
                    </v-card-title>

                    <v-card-text align-center>
                        <p class="text-sm-center">
                            <router-link to="/resend-verif-email">Resend confirm email</router-link>
                        </p>
                    </v-card-text>

                </v-card>

            </v-flex>

        </v-layout>

    </v-container>

</template>

<script>
    import BigLogo from '@/components/layout/BigLogo'
    import {USER_EMAIL_VERIFICATION} from "../store/actions/user";

    export default {
        name: 'verify-email-confirm-link',
        components: {
            BigLogo
        },
        data: () => ({
            confirmationEmailInvalid: false,
            code: '',
        }),
        mounted() {
            this.code = new URL(window.location.href).searchParams.get("code");

            this.$store.dispatch(USER_EMAIL_VERIFICATION, this.code)
                .then(() => {
                    this.$router.push('/user')
                })
                .catch(() => {
                    this.confirmationEmailInvalid = true;
                });
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