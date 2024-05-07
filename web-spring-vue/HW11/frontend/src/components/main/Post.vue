<template>
    <ArticleWithComments :post="post"/>
</template>

<script>
import ArticleWithComments from "./article/ArticleWithComments";

export default {
    name: "Post",
    props: ["post"],
    components: {ArticleWithComments},
}
</script>

<style scoped>

</style>