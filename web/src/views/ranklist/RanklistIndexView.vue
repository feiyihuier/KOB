<template>
    <ContentField>
        <table class="table table-striped table-hover">
            <thead>
                <tr>
                    <th>用户</th>
                    <th>分数</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="user in users" :key="user.id">
                    <td>
                        <img :src="user.photo" alt="头像" class="user-photo">
                        &nbsp;
                        <span class="user-username">{{ user.username }}</span>
                    </td>
                    <td><span class="-result">{{ user.rating }}</span></td>
                </tr>
            </tbody>
        </table>

        <nav aria-label="...">
            <ul class="pagination" style="float: right;">
                <li class="page-item disabled" @click="click_page(-2)">
                    <a class="page-link">前一页</a>
                </li>
                <li :class="'page-item ' + page.is_active" v-for="page in pages" :key="page.number" @click="click_page(page.number)">
                    <a class="page-link" href="#">{{ page.number }}</a>
                </li>
                
                <li class="page-item" @click="click_page(-1)">
                    <a class="page-link" href="#">后一页</a>
                </li>
            </ul>
        </nav>
    </ContentField>
</template>

<script>
import ContentField from '../../components/ContentField.vue'
import { useStore } from 'vuex'
import { ref } from 'vue';
import $ from 'jquery'

export default{
    components:{
        ContentField,
    },
    setup(){
        const store = useStore();
        let users = ref([]);
        let current_page = 1;
        let total_users = 0;
        let pages = ref([]);

        const update__pages = () =>{
            let max_pages = parseInt(Math.ceil(total_users / 3));
            let new_pages = [];
            for(let i = current_page -2;i<=current_page + 2;i++){
                if(i >=1 && i <= max_pages){
                    new_pages.push({
                        number : i,
                        is_active: i===current_page?"active":"",
                    });
                }
            }
            pages.value = new_pages;
        }

        const click_page = p =>{
            if(p === -2)current_page = current_page - 1;
            else if(p === -1)current_page = current_page +1;
            else current_page = p;
            let max_pages = parseInt(Math.ceil(total_users / 3));
            if(current_page >= 1 && current_page <= max_pages){
                pull_page(current_page);
            }
        }

        const pull_page = page =>{
            $.ajax({
                url:"http://localhost:3000/rank/getlist/",
                type:"GET",
                data:{
                    page,
                 },
                headers:{
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp){
                   console.log(resp);
                   users.value = resp.users;
                   total_users = resp.users_count;
                   update__pages();
                },
                error(resp){
                    console.log(resp);
                }
            })
        }
        pull_page(current_page);

        
        return {
            users,
            current_page,
            pages,
            click_page,
        }
        
    }
}
</script>

<style scoped>
img.user-photo{
    height: 4vh;
    border-radius: 50%;
}
td, th {
  text-align: center; /* 设置单元格内容居中对齐 */
  padding: 8px; /* 设置单元格内边距 */
}
</style>