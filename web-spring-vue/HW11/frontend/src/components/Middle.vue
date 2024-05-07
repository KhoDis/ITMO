<template>
    <div class="middle">
        <Sidebar :posts="viewPosts"/>
        <main>
            <Index v-if="page === 'Index'" :posts="posts"/>
            <Enter v-if="page === 'Enter'"/>
            <Register v-if="page === 'Register'"/>
            <Users v-if="page === 'Users'"/>
            <Post v-if="page === 'Post'" :post="post"/>
            <WritePost v-if="page === 'WritePost'"/>
        </main>
    </div>
</template>

<script>
import Sidebar from "./sidebar/Sidebar";
import Index from "./main/Index";
import Enter from "./main/Enter";
import Register from "./main/Register";
import Users from "./main/Users";
import WritePost from "./main/WritePost";
import Post from "./main/Post";

export default {
    name: "Middle",
    data: function () {
        return {
            page: "Index",
            post: null,
        }
    },
    components: {
        WritePost,
        Register,
        Enter,
        Index,
        Sidebar,
        Users,
        Post
    },
    props: ["posts"],
    computed: {
        viewPosts: function () {
            return Object.values(this.posts).sort((a, b) => b.id - a.id).slice(0, 2);
        }
    }, beforeCreate() {
        this.$root.$on("onChangePage", (page) => this.page = page)
        this.$root.$on("onPost", (post) => {
            this.post = post;
            this.page = "Post";
        });

    }
}
</script>

<style scoped>

</style>
