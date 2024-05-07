<template>
    <div class="articles">
        <Article v-for="post in posts" :post="post" :key="post.id"/>
    </div>
</template>

<script>
import Article from "./article/Article";
export default {
    name: "Index",
    props: ["posts"],
    components: {
        Article
    },
}
</script>

<style scoped>

</style>
