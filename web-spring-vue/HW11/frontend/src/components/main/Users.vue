<template>
    <div class="users datatable">
        <div class="caption">User</div>
        <table>
            <thead>
            <tr>
                <th>Id</th>
                <th>Login</th>
                <th>Name</th>
            </tr>
            </thead>
            <tbody>
            <tr v-if="!Object.keys(users).length">
                <td colspan="3">No data</td>
            </tr>
            <tr v-else v-for="user in users" :key="user.id">
                <td class="id">{{ user.id }}</td>
                <td class="login"> {{ user.login }}</td>
                <td class="roles"> {{ user.name }}</td>
            </tr>
            </tbody>
        </table>
    </div>
</template>

<script>
import axios from "axios";

export default {
    name: "Users",
    data: function () {
        return {
            users: [],
        }
    },
    beforeMount() {
        axios.get("/api/1/users").then(response => {
            this.users = response.data;
        });
    }
}
</script>

<style scoped>

</style>