<template>
    <div id="app">
        <Header :user="user"/>
        <Middle :posts="posts" :user="user"/>
        <Footer/>
    </div>
</template>

<script>
import Header from "./components/Header";
import Middle from "./components/Middle";
import Footer from "./components/Footer";
import axios from "axios"

export default {
    name: 'App',
    components: {
        Footer,
        Middle,
        Header
    },
    data: function () {
        return {
            user: null,
            posts: [],
        }
    },
    beforeMount() {
        if (localStorage.getItem("jwt") && !this.user) {
            this.$root.$emit("onJwt", localStorage.getItem("jwt"));
        }

        axios.get("/api/1/posts").then(response => {
            this.posts = response.data;
        });
    },
    beforeCreate() {
        this.$root.$on("onEnter", (login, password) => {
            if (password === "") {
                this.$root.$emit("onEnterValidationError", "Password is required");
                return;
            }

            axios.post("/api/1/jwt", {
                    login, password
            }).then(response => {
                localStorage.setItem("jwt", response.data);
                this.$root.$emit("onJwt", response.data);
            }).catch(error => {
                this.$root.$emit("onEnterValidationError", error.response.data);
            });
        });

        this.$root.$on("onJwt", (jwt) => {
            localStorage.setItem("jwt", jwt);

            axios.get("/api/1/users/auth", {
                params: {
                    jwt
                }
            }).then(response => {
                this.user = response.data;
                this.$root.$emit("onChangePage", "Index");
            }).catch(() => this.$root.$emit("onLogout"))
        });

        this.$root.$on("onLogout", () => {
            localStorage.removeItem("jwt");
            this.user = null;
        });

        this.$root.$on("onRegister", (login, password, name) => {
            axios.post('api/1/users', {
                login: login,
                password: password,
                name: name
            }).then(() => {
                this.$root.$emit("onEnter", login, password)
            }).catch(error => {
                this.$root.$emit("onRegisterValidationError", error.response.data)
            })
        });

        this.$root.$on("onWritePost", (title, text) => {
            axios.post('api/1/posts', {
                jwt: localStorage.getItem("jwt"),
                title: title,
                text: text,
            }).then(() => {
                axios.get("/api/1/posts").then(response => {
                    this.posts = response.data;
                });
                this.$root.$emit("onChangePage", "Index")
            }).catch(error => {
                this.$root.$emit("onWritePostValidationError", error.response.data)
            })
        });

        this.$root.$on("onWriteComment", (text, postId) => {
            axios.post('api/1/comments', {
                jwt: localStorage.getItem("jwt"),
                text: text,
                postId: postId,
            }).then(() => {
                axios.get("/api/1/posts").then(response => {
                    this.posts = response.data;
                });
            }).catch(error => {
                this.$root.$emit("onWritePostValidationError", error.response.data)
            })
        });
    }
}
</script>

<style>
#app {

}
</style>
