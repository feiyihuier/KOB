<template>
    <ContentField>
        <div class="row justify-content-md-center">
            <div class="col-3">
                <form @submit.prevent="register">
                    <div class="mb-3">
                        <label for="username" class="form-label">用户名</label>
                        <input v-model="thisusername" type="text" class="form-control" id="username" placeholder="请输入用户名">
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">密码</label>
                        <input v-model="thispassword" type="password" class="form-control" id="password" placeholder="请输入密码">
                    </div>
                    <div class="mb-3">
                        <label for="confirmedpassword" class="form-label">确认密码</label>
                        <input v-model="thisconfirmedpassword" type="password" class="form-control" id="confirmedpassword" placeholder="请再次输入密码">
                    </div>
                    <div class="error-message">{{ error_message }}</div>
                    <div class="div-btn">
                        <button type="submit" class="btn btn-outline-primary">注册</button>
                        <button type="reset"  class="btn btn-outline-primary">取消</button>
                    </div>
                </form>
            </div>
        </div>
    </ContentField>
</template>

<script>
import ContentField from '../../../components/ContentField.vue'
import { ref } from 'vue';
import router from '@/router/index';
import $ from 'jquery'

export default{
    components:{
        ContentField,
    },
    setup() {
        let thisusername = ref('');
        let thispassword = ref('');
        let thisconfirmedpassword = ref('');
        let error_message = ref('');

        const register = () => {
            $.ajax({
                url:"http://localhost:3000/user/account/register/",
                type:"POST",
                data:{
                    username:thisusername.value,
                    password:thispassword.value,
                    confirmedPassword:thisconfirmedpassword.value,
                },
                success(resp){
                    if(resp.error_message === "success"){
                        router.push({name:"user_account_login"});
                    }
                    else{
                        error_message.value = resp.error_message;
                    }
                },
                error(){
                    error_message.value = "系统错误！";
                },
            })
        }
        return {
            thisusername,
            thispassword,
            thisconfirmedpassword,
            error_message,
            register,
        }
    }
}
</script>

<style scoped>
.div-btn{
    display: flex;
    flex-direction: row;
    justify-content: space-around;
}
.error-message{
    color: red;
}
</style>