<template>
    <ContentField>
        <table class="table table-striped table-hover">
            <thead>
                <tr>
                    <th>A</th>
                    <th>B</th>
                    <th>对战结果</th>
                    <th>对战时间</th>
                    <th>操作</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="item in records" :key="item.record.id">
                    <td>
                        <img :src="item.a_photo" alt="头像" class="record-user-photo">
                        &nbsp;
                        <span class="record-user-username">{{ item.a_username }}</span>
                    </td>
                    <td>
                        <img :src="item.b_photo" alt="头像" class="record-user-photo">
                        &nbsp;
                        <span class="record-user-username">{{ item.b_username }}</span>
                    </td>
                    <td><span class="record-result">{{ item.result }}</span></td>
                    <td><span class="record-time">{{ item.record.createtime }}</span></td>
                    <td>
                        <button @click="open_record_content(item.record.id)" type="submit" class="btn btn-outline-primary">重演</button>
                    </td>
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
import router from '@/router/index';
import { useStore } from 'vuex'
import { ref } from 'vue';
import $ from 'jquery'

export default{
    components:{
        ContentField,
    },
    setup(){
        const store = useStore();
        let records = ref([]);
        let current_page = 1;
        let total_records = 0;
        let pages = ref([]);

        const update__pages = () =>{
            let max_pages = parseInt(Math.ceil(total_records / 10));
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
            let max_pages = parseInt(Math.ceil(total_records / 10));
            if(current_page >= 1 && current_page <= max_pages){
                pull_page(current_page);
            }
        }

        const pull_page = page =>{
            $.ajax({
                url:"http://localhost:3000/record/getlist/",
                type:"GET",
                data:{
                    page,
                 },
                headers:{
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp){
                   console.log(resp);
                   records.value = resp.records;
                   total_records = resp.records_count;
                   update__pages();
                },
                error(resp){
                    console.log(resp);
                }
            })
        }
        pull_page(current_page);

        const stringto2D = (map) =>{
            let g = [];
            for(let i = 0, k = 0 ; i < 13 ; i++){
                let line = [];
                for(let j = 0 ; j < 14 ; j++, k++){
                    if(map[k] === '0')line.push(0);
                    else line.push(1);
                }
                g.push(line);
            }
            return g;
        }

        const open_record_content = recordId =>{
            for(const record of records.value){
                if(record.record.id === recordId){
                    console.log(record);
                    store.commit("updateIsRecord",true);
                    store.commit("updateGame",{
                        map:stringto2D(record.record.map),
                        a_id:record.record.aid,
                        a_sx:record.record.asx,
                        a_sy:record.record.asy,
                        b_id:record.record.bid,
                        b_sx:record.record.bsx,
                        b_sy:record.record.bsy,
                    });
                    store.commit("updateStep",{
                        a_step:record.record.asteps,
                        b_step:record.record.bsteps,
                    });
                    console.log("这里是recordcontentview区域的log,输家是", record.record.loser);
                    store.commit("updateRecordLoser", record.record.loser);
                    router.push({
                        name:"record_content",
                        params:{
                            recordId:recordId,
                        }
                    })
                    break;
                
                }
            }
        }

       
        
        return {
            records,
            current_page,
            total_records,
            open_record_content,
            pages,
            click_page,
        }
        
    }
}
</script>

<style scoped>
img.record-user-photo{
    height: 4vh;
    border-radius: 50%;
}
td, th {
  text-align: center; /* 设置单元格内容居中对齐 */
  padding: 8px; /* 设置单元格内边距 */
}
</style>