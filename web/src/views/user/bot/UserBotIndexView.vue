<template>
    <div class="container" style="margin-top: 20px;">
        <div class="row">
            <div class="col-3">
                <div class="card">
                    <div class="card-body">
                        <img :src="$store.state.user.photo" alt="头像">
                    </div>
                </div>
            </div>
            <div class="col-9">
                <div class="card">
                    <div class="card-header">
                        <span style="font-size: 130%;">我的Bot</span>
                        <button type="button" class="btn btn-outline-primary btn-sm float-end" data-bs-toggle="modal" data-bs-target="#add-bot-btn">
                            创建Bot
                        </button>
                        <div class="modal fade" id="add-bot-btn" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
                            <div class="modal-dialog modal-xl">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <h1 class="modal-title fs-5" id="add-bot-Label">创建Bot</h1>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                    </div>
                                    <div class="modal-body">
                                        <form>
                                            <div class="mb-3">
                                                <label for="add-bot-title" class="form-label">Bot标题</label>
                                                <input v-model="this_title" type="text" class="form-control" id="add-bot-title" placeholder="请输入Bot名称">
                                            </div>
                                            <div class="mb-3">
                                                <label for="add-bot-description" class="form-label">Bot描述</label>
                                                <textarea v-model="this_description" class="form-control" id="add-bot-description" rows="3" placeholder="请输入Bot描述"></textarea>
                                            </div>
                                            <div class="mb-3">
                                                <label for="add-bot-code" class="form-label">Bot代码</label>
                                                <VAceEditor v-model:value="this_content"  @init="editorInit" lang="c_cpp" theme="textmate" style="height: 300px" 
                                                :options="{
                                                    enableBasicAutocompletion: true, //启用基本自动完成
                                                    enableSnippets: true, // 启用代码段
                                                    enableLiveAutocompletion: true, // 启用实时自动完成
                                                    fontSize: 18, //设置字号
                                                    tabSize: 4, // 标签大小
                                                    showPrintMargin: false, //去除编辑器里的竖线
                                                    highlightActiveLine: true,
                                                }"/>
                                            </div>
                                        </form>
                                    </div>
                                    <div class="modal-footer">
                                        <div class="add_error-message" style="color: red;">{{error_message}}</div>
                                        <button type="button" class="btn btn-primary" @click.prevent="add_bot">创建</button>
                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">关闭</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="card-body">
                        <table class="table table-striped table-hover">
                            <thead>
                                <tr>
                                    <th>名称</th>
                                    <th>创建时间</th>
                                    <th>操作</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="bot in bots" :key="bot.id">
                                    <th>{{ bot.title }}</th>
                                    <th>{{ bot.createtime }}</th>
                                    <th>
                                        <button type="button" class="btn btn-outline-primary btn-sm float-end"  data-bs-toggle="modal" :data-bs-target="'#update-bot-btn-' + bot.id">修改Bot</button>
                                        <button type="button" class="btn btn-outline-danger btn-sm float-end" @click.prevent="remove_bot(bot)">删除Bot</button>

                                        <div class="modal fade" :id="'update-bot-btn-' + bot.id" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
                                            <div class="modal-dialog modal-xl">
                                                <div class="modal-content">
                                                    <div class="modal-header">
                                                        <h1 class="modal-title fs-5">修改Bot</h1>
                                                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                                    </div>
                                                    <div class="modal-body">
                                                        <form>
                                                            <div class="mb-3">
                                                                <label for="add-bot-title" class="form-label">Bot标题</label>
                                                                <input v-model="bot.title" type="text" class="form-control" id="add-bot-title" placeholder="请输入Bot名称">
                                                            </div>
                                                            <div class="mb-3">
                                                                <label for="add-bot-description" class="form-label">Bot描述</label>
                                                                <textarea v-model="bot.description" class="form-control" id="add-bot-description" rows="3" placeholder="请输入Bot描述"></textarea>
                                                            </div>
                                                            <div class="mb-3">
                                                                <label for="add-bot-code" class="form-label">Bot代码</label>
                                                                <VAceEditor v-model:value="bot.content"  @init="editorInit" lang="c_cpp" theme="textmate" style="height: 300px" 
                                                                :options="{
                                                                    enableBasicAutocompletion: true, //启用基本自动完成
                                                                    enableSnippets: true, // 启用代码段
                                                                    enableLiveAutocompletion: true, // 启用实时自动完成
                                                                    fontSize: 18, //设置字号
                                                                    tabSize: 4, // 标签大小
                                                                    showPrintMargin: false, //去除编辑器里的竖线
                                                                    highlightActiveLine: true,
                                                                }"/>
                                                            </div>
                                                        </form>
                                                    </div>
                                                    <div class="modal-footer">
                                                        <div class="add_error-message" style="color: red;">{{ bot.error_message }}</div>
                                                        <button type="button" class="btn btn-primary"  @click.prevent="update_bot(bot)">修改</button>
                                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" @click="refresh_bot()">关闭</button>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </th>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { ref } from 'vue';
import { useStore } from 'vuex'
import $ from "jquery"
import { Modal } from 'bootstrap/dist/js/bootstrap';

//引入代码区
import { VAceEditor } from 'vue3-ace-editor';
import ace from 'ace-builds';

//以下导入是解决代码区域代码高亮问题
import 'ace-builds/src-noconflict/mode-c_cpp';
import 'ace-builds/src-noconflict/mode-json';
import 'ace-builds/src-noconflict/theme-chrome';
import 'ace-builds/src-noconflict/ext-language_tools';

export default{
    components:{
        VAceEditor,
    },
    
   
    setup(){
        ace.config.set(
            "basePath",
            "https://cdn.jsdelivr.net/npm/ace-builds@" + require("ace-builds").version + "/src-noconflict/")

        const store = useStore();
        let bots = ref([]);
        let this_title = ref('');
        let this_description = ref('');
        let this_content = ref('');
        let error_message = ref('');


        const refresh_bot = () => {
            $.ajax({
                url:"http://localhost:3000/user/bot/getlist/",
                type:"GET",
                headers:{
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp){
                    bots.value = resp;
                },
            });
        };
        refresh_bot();

        const add_bot = () =>{
            $.ajax({
                url:"http://localhost:3000/user/bot/add/",
                type:"POST",
                data:{
                    title:this_title.value,
                    description:this_description.value,
                    content:this_content.value,
                },
                headers:{
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp){
                    if(resp.error_message === "success"){
                        this_title.value = "";
                        this_description.value = "";
                        this_content.value = "";
                        error_message.value = "";
                        Modal.getInstance("#add-bot-btn").hide();
                        refresh_bot();
                    }else{
                        error_message.value = resp.error_message;
                    }
                },
                error(resp){
                    error_message.value = resp.error_message;
                }
            });
        };

        const update_bot = (bot) =>{
            $.ajax({
                url:"http://localhost:3000/user/bot/update/",
                type:"POST",
                data:{
                    bot_id: bot.id,
                    title:bot.title,
                    description:bot.description,
                    content:bot.content,
                },
                headers:{
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp){
                    if(resp.error_message === "success"){
                       Modal.getInstance("#update-bot-btn-"+ bot.id).hide();
                        refresh_bot();
                    }else{
                        error_message.value = resp.error_message;
                    }
                },
                error(resp){
                    error_message.value = resp.error_message;
                }
            });
        };

        const remove_bot = (bot) =>{
             $.ajax({
                url:"http://localhost:3000/user/bot/remove/",
                type:"POST",
                data:{
                    bot_id:bot.id,
                },
                headers:{
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp){
                    if(resp.error_message === "success"){
                        refresh_bot();
                    }else{
                        window.alert(resp.error_message);
                    }
                },
                error(resp){
                    window.alert(resp.error_message);
                }
        });
        };


        return {
            bots,
            this_title,
            this_description,
            this_content,
            error_message,
            add_bot,
            remove_bot,
            update_bot,
            refresh_bot,
        }


    }
}
</script>

<style scoped>
button{
    margin: 5px 5px 5px 5px;
}
td, th {
  text-align: center; /* 设置单元格内容居中对齐 */
  padding: 8px; /* 设置单元格内边距 */
}
</style>