<template>
    <v-container class="ma-0 pa-0">
        <v-card class="fff pa-3"
                outlined>

            <v-text-field
                    prepend-icon="person"
                    v-model="userEmail"
                    label="E-mail"
                    disabled
                    readonly>
            </v-text-field>

            <v-form @submit.prevent="save" v-model="formValid" v-if="isUserProfileLoaded" ref="form">

                <v-text-field
                        prepend-icon="lock"
                        id="oldPassword"
                        v-model="oldPassword"
                        :rules="passwordRules"
                        counter
                        name="oldPassword"
                        label="Current Password"
                        browser-autocomplete="off"
                        :append-icon="visible ? 'visibility_off' : 'visibility'"
                        @click:append="visible = !visible"
                        :type="visible ? 'text' : 'password'"
                        hint="At least 6 characters">
                </v-text-field>

                <v-text-field
                        prepend-icon="lock"
                        id="password"
                        v-model="password"
                        :rules="passwordRules"
                        counter
                        name="password"
                        label="New Password"
                        browser-autocomplete="off"
                        :append-icon="visible ? 'visibility_off' : 'visibility'"
                        @click:append="visible = !visible"
                        :type="visible ? 'text' : 'password'"
                        hint="At least 6 characters">
                </v-text-field>

                <v-text-field
                        :error-messages="passwordMatchError()"
                        prepend-icon="lock"
                        id="password2"
                        v-model="password2"
                        counter
                        name="password2"
                        label="Repeat New Password"
                        browser-autocomplete="off"
                        :append-icon="visible ? 'visibility_off' : 'visibility'"
                        @click:append="visible = !visible"
                        :type="visible ? 'text' : 'password'"
                        hint="At least 6 characters">
                </v-text-field>

            </v-form>

            <v-card-actions class="justify-center">
                <v-btn color="primary" @click="save()">Save</v-btn>
            </v-card-actions>

        </v-card>
    </v-container>
</template>

<script>
    import {mapGetters, mapState} from 'vuex'
    import {SNACKBAR_ERROR} from "../../store/actions/snackbar";
    import {USER_UPDATE_PASSWORD} from "../../store/actions/user";

    export default {
        name: "TabUserSettings",
        data: () => ({
            formValid: false,
            visible: false,
            oldPassword: '',
            password: '',
            password2: '',
            passwordRules: [
                v => !!v || 'Password is required',
                v => v.length >= 6 || 'Password must be at least 6 characters',
            ],
        }),
        computed: {
            ...mapGetters(['isUserProfileLoaded']),
            ...mapState({
                userEmail: state => state.user.userProfile.email
            })
        },
        methods: {
            save() {
                if (this.formValid) {

                    const {oldPassword, password} = this;
                    this.$store.dispatch(USER_UPDATE_PASSWORD, {oldPassword, password})
                        .then(() => {
                            // provoked console errors
                            // this.$refs.form.reset();
                            // or
                            Object.assign(this.$data, this.$options.data());
                            this.$refs.form.resetValidation();
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

</style>