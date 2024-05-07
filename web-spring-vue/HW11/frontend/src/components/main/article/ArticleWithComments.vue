<template>
    <div>
        <Article :post="post"/>
        <h3>Comments:</h3>
        <div v-if="!post.comments.length">
            No comments.
        </div>
        <article v-else v-for="comment in post.comments" :key="comment.id">
            <div class="information">By {{ comment.user.login }}, {{ comment.creationTime }}</div>
            <div class="body">{{ comment.text }}</div>
        </article>
        <div class="form">
            <div class="header">Write Comment</div>
            <div class="body">
                <form @submit.prevent="onWriteComment">
                    <div class="field">
                        <div class="name">
                            <label for="text">Text</label>
                        </div>
                        <div class="value">
                            <textarea id="text" name="text" v-model="text"></textarea>
                        </div>
                    </div>
                    <div class="error">{{ error }}</div>
                    <div class="button-field">
                        <input type="submit" value="Write">
                    </div>
                </form>
            </div>
        </div>
    </div>
</template>

<script>
import Article from "./Article";

export default {
    name: "ArticleWithComments",
    data() {
        return {
            text: "",
            error: "",
        }
    }, beforeCreate() {
        this.$root.$on("onWritePostValidationError", (error) => {
            return this.error = error;
        });
    },
    methods: {
        onWriteComment: function () {
            this.error = "";
            this.$root.$emit("onWriteComment", this.text, this.post.id);
        }
    },
    components: {Article},
    props: ['post'],
}
</script>

<style scoped>

</style>