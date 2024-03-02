<template>
    <div class="matchground">
        <div class="row">
            <div class="col-4">
                <div class="user-photo">
                    <img :src="$store.state.user.photo" alt="头像">
                </div>
                <div class="user-username">
                    {{ $store.state.user.username }}
                </div>
            </div>
            <div class="col-4">
                <div class="user-select-bot">
                    <select v-model="select_bot" class="form-select" aria-label="Default select example">
                        <option value="-1" selected>亲自出马</option>
                        <option v-for="bot in bots" :key="bot.id" :value="bot.id">
                        {{bot.title}}
                        </option>
                    </select>
                </div>
            </div>
            <div class="col-4">
                <div class="user-photo">
                    <img :src="$store.state.pk.opponent_photo" alt="头像">
                </div>
                <div class="user-username">
                    {{ $store.state.pk.opponent_usernmae }}
                </div>
            </div>
            <div class="col-12" style="text-align: center;padding-top: 15vh;">
                <button type="button" class="btn btn-warning btn-lg" @click="click_match_btn">{{ match_btn_info }}</button>
            </div>
        </div>
    </div>
</template>

<script>
import { ref } from 'vue';
import { useStore } from 'vuex';
import $ from 'jquery'

export default {
    setup() {
        const store = useStore();
        let match_btn_info = ref("开始匹配");
        let bots = ref([]);
        let select_bot = ref("-1");

        const click_match_btn = () => {
            if (match_btn_info.value === "开始匹配") {
                
                console.log("matchview 开始匹配");
                console.log(select_bot.value);

                match_btn_info.value = "取消";
                store.state.pk.socket.send(JSON.stringify({
                    event: "start-matching",
                    bot_id:select_bot.value,
                }));
            } else {
                console.log("matchview 取消");
                match_btn_info.value = "开始匹配";
                store.state.pk.socket.send(JSON.stringify({
                    event: "stop-matching",
                }));
                
            }
        }

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
        refresh_bot();//从后端动态的获取bot


        return {
            match_btn_info,
            click_match_btn,
            bots,
            select_bot,
        }
    }
}
</script>   


<style scoped>
div.matchground {
    width: 60vw;
    height: 70vh;
    margin: 40px auto;
    background-color: rgba(50, 50, 50, 0.5);
}

div.user-photo {
    text-align: center;
    padding-top: 10vh;
}

div.user-photo>img {
    border-radius: 50%;
    width: 20vh;
}

div.user-username {
    text-align: center;
    font-size: 24px;
    font-weight: 600;
    color: white;
    padding-top: 2vh;
}
div.user-select-bot{
    padding-top: 20vh;
}
div.user-select-bot > select{
    width: 80%;
    margin: 0 auto;
}


</style>