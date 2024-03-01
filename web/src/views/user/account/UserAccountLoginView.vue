<template>
    <ContentField v-if="!$store.state.user.pulling_info">
        <div class="row justify-content-md-center">
            <div class="col-3">
                <form @submit.prevent="login">
                    <div class="mb-3">
                        <label for="username" class="form-label">用户名</label>
                        <input v-model="thisusername" type="text" class="form-control" id="username" placeholder="请输入用户名">
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">密码</label>
                        <input v-model="thispassword" type="password" class="form-control" id="password" placeholder="请输入密码">
                    </div>
                    <div class="error-message">{{ error_message }}</div>
                    <div class="div-btn">
                        <button type="submit" class="btn btn-outline-primary">登录</button>
                        <button type="reset"  class="btn btn-outline-primary">取消</button>
                    </div>
                </form>
            </div>
        </div>
    </ContentField>
</template>

<script>
import ContentField from '../../../components/ContentField.vue'
import { useStore } from 'vuex';
import { ref } from 'vue';
import router from '@/router/index';

export default{
    components:{
        ContentField,
    },
    setup(){
        const store = useStore();
        let thisusername = ref('');
        let thispassword = ref('');
        let error_message = ref('');

        const jwt_token = localStorage.getItem("jwt_token");
        if(jwt_token){
            store.commit("updateToken", jwt_token);
            store.dispatch("getinfo",{
                success(){
                    router.push({name:"home"});
                },
                error(){
                    store.commit("updatePullingInfo", false);
                }
            })
        }else{
            store.commit("updatePullingInfo", false);
        }

        const login = () =>{
            store.dispatch("login",{
                username:thisusername.value,
                password:thispassword.value,
                success(){
                    store.dispatch("getinfo",{
                        success(){
                            router.push({name:"home"});
                        }
                    })
                },
                error(){
                    error_message.value = "用户名或密码错误";
                }
            })
        }

        return{
            thisusername,
            thispassword,
            error_message,
            login,
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