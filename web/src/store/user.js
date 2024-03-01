import $ from 'jquery'
import router from '@/router/index';

export default{
    state: {
        id:"",
        username:"",
        photo:"",
        token:"",
        is_login:false,
        pulling_info:true,//表示当前是否在拉取信息中
    },
    getters: {
    },
    mutations: {
        updateUser(state,user){
            state.id = user.id;
            state.username = user.username;
            state.photo = user.photo;
            state.is_login = user.is_login;
        },
        updateToken(state,token){
            state.token = token;
        },
        userjs_mutations_logout(state){
            state.id = "";
            state.username = "";
            state.photo = "";
            state.token = "";
            state.is_login = false;
        },
        updatePullingInfo(state, pulling_info){
            state.pulling_info = pulling_info;
        }
    },
    actions: {
        login(context, data){
            $.ajax({
                url:"http://localhost:3000/user/account/token/",
                type:"POST",
                data:{
                  username:data.username,
                  password:data.password,
                },
                success(resp){
                    if(resp.error_message === "success"){
                        localStorage.setItem("jwt_token", resp.token);//将token存储在本地空间
                        context.commit("updateToken", resp.token);
                        data.success(resp);
                    }else{
                        data.error(resp);
                    }
                },
                error(resp){
                  data.error(resp);
                },
              });
        },
        getinfo(context,data){
            $.ajax({
                url:"http://localhost:3000/user/account/info/",
                type:"GET",
                headers:{
                    Authorization: "Bearer " + context.state.token,
                },
                success(resp){
                    if(resp.error_message === "success"){
                        context.commit("updateUser", {
                                ...resp,
                                is_login: true,
                        });
                        data.success(resp);
                    }else{
                        data.error(resp);
                    }
                  
                },
                error(resp){
                    data.error(resp);
                },
              });
        },
        userjs_actios_logout(context){
            localStorage.removeItem("jwt_token");
            context.commit("userjs_mutations_logout");
            router.push({name:"user_account_login"});
        }
    },
    modules: {
    }
}