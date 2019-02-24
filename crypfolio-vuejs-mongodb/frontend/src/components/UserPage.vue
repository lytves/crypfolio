<template>

    <v-toolbar>

        <v-toolbar-title>

            <a href="/">
                <img class="small-logo" src="../assets/logo_small_vuejs_mongodb.png"
                     alt="Crypfolio :: a new crypto portfolio"/>
            </a>

        </v-toolbar-title>

        <v-spacer></v-spacer>

        <div>
            <UserMenu></UserMenu>
        </div>

    </v-toolbar>

</template>

<script>
    import UserMenu from '@/components/layout/UserMenu'
    // import {mapGetters, mapState} from 'vuex'
    import {AUTH_LOGOUT} from '../store/actions/auth'

    export default {
        name: 'user',
        components: {
            UserMenu
        },
        methods: {
            logout: function () {
                this.$store.dispatch(AUTH_LOGOUT)
                    .then(() => {
                        this.$router.push('/login')
                    })
                    .catch(() => {})
            },
        },
        computed: {
            // will use on the future functionality
            // ...mapGetters(['getUserProfile', 'isAuthenticated', 'isUserProfileLoaded']),
            // ...mapState({
                // "authLoading" is not using actually
                // authLoading: state => state.auth.status === 'loading',
                // userInfo: state => `id: ${state.user.userProfile.id}, email: ${state.user.userProfile.email}`,
            // }),
        }
    }
</script>

<style scoped>
    .small-logo {
        width: 192px;
        height: 43px;
    }
</style>