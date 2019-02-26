<template>

    <v-menu bottom offset-y>

        <v-btn slot="activator">
            <v-icon left color="blue darken-2">fas fa-user</v-icon>
            {{userEmail}}
            <v-icon right color="blue darken-2">expand_more</v-icon>
        </v-btn>

        <v-list>
            <v-list-tile
                    v-for="(item, index) in items"
                    :key="index"
                    @click.stop="chooseMenuAction(item.link)">

                <v-list-tile-avatar>
                    <v-icon color="blue darken-2">{{ item.icon }}</v-icon>
                </v-list-tile-avatar>

                <v-list-tile-title>
                    {{ item.title }}
                </v-list-tile-title>

            </v-list-tile>
        </v-list>

        <UserSettings v-model="showUserSettings"></UserSettings>
        <Friends v-model="showFriends"></Friends>

    </v-menu>

</template>

<script>
    import {mapState} from 'vuex'
    import {AUTH_LOGOUT} from "../../store/actions/auth";
    import UserSettings from "@/components/layout/UserSettings"
    import Friends from "@/components/layout/Friends"

    export default {
        name: "UserMenu",
        components: {
            UserSettings,
            Friends
        },
        data() {
            return {
                items: [
                    {title: 'Settings', icon: 'far fa-address-card', link: 'settings'},
                    {title: 'Friends', icon: 'fas fa-star', link: 'friends'},
                    {title: 'Logout', icon: 'fas fa-sign-out-alt', link: 'logout'},
                ],
                showUserSettings: false,
                showFriends: false,
            }
        },
        computed: {
            // ...mapGetters(['getUserProfile', 'isAuthenticated', 'isUserProfileLoaded']),
            ...mapState({
                // authLoading: state => state.auth.status === 'loading',
                userEmail: state => state.user.userProfile.email
            }),
        },
        methods: {
            chooseMenuAction(link) {
                this[link]()
            },
            logout() {
                this.$store.dispatch(AUTH_LOGOUT)
                    .then(() => {
                        this.$router.push('/login')
                    })
                    .catch(() => {
                    })
            },
            settings() {
                this.showUserSettings = true
            },
            friends() {
                this.showFriends = true
            }
        }
    }
</script>

<style scoped>

</style>