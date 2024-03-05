<template>
<PlayGround v-if="$store.state.pk.status === 'playing'">
    
</PlayGround>
<MatchGround v-if="$store.state.pk.status === 'matching'">

</MatchGround>
<ResultBoard v-if="$store.state.pk.loser !== 'none'">
    
</ResultBoard>
</template>

<script>
import PlayGround from '../../components/PlayGround.vue'
import MatchGround from '../../components/MatchGround.vue'
import ResultBoard from '../../components/ResultBoard.vue'
import { onMounted, onUnmounted } from 'vue';
import { useStore } from 'vuex';
export default{
    components:{
        PlayGround,
        MatchGround,
        ResultBoard,
    },

    setup(){

        const store = useStore();
        const socketurl = `ws://localhost:3000/websocket/${store.state.user.token}/`;

        store.commit("updateLoser","none");
        store.commit("updateIsRecord",false);

        let socket = null;
        onMounted(() =>{//当前界面被挂载的时候
            store.commit("updateOpponent", {
                username:"?",
                photo:"https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png"
            })

            socket = new WebSocket(socketurl);
            console.log("这里是PKIndexView界面的socket");
            console.log(socket);

            socket.onopen = () =>{//当链接建立的时候调用此函数
                console.log("前端链接成功信息");
                store.commit("updateSocket", socket);
            }

            socket.onmessage = msg =>{//当接收到信息的时候
                console.log("前端的onmessage函数")
                const data = JSON.parse(msg.data);

                if(data.event === "start-matching"){
                    store.commit("updateOpponent", {
                        username: data.opponent_username,
                        photo: data.opponent_photo,
                    });
                    setTimeout(() => {//此函数是让下面的语句延迟执行 2秒
                        store.commit("updateStatus", "playing");
                    }, 20);
                    store.commit("updateGame", data.game);
                }
                else if(data.event === "move"){
                    console.log(data);
                    const game = store.state.pk.gameObject;
                    const [snake0, snake1] = game.snakes;
                    snake0.set_direction(data.a_direction);
                    snake1.set_direction(data.b_direction);
                }
                else if(data.event === "result"){
                    console.log(data);
                    const game = store.state.pk.gameObject;
                    const [snake0, snake1] = game.snakes;

                    if(data.loser === "All" || data.loser === 'A'){
                        snake0.status = "die";
                    }
                    if(data.loser === "All" || data.loser === 'B'){
                        snake1.status = "die";
                    }

                    store.commit("updateLoser", data.loser);
                }
            }

            socket.onclose = () =>{//当关闭连接的时候执行的函数
                console.log("前端的onclose函数");
            }
        });

        onUnmounted(() =>{//当前页面被卸载的时候
            socket.close();//必须有，不然打开一次页面就会建立一个连接
            store.commit("updateStatus", "matching");
        })

    }

}
</script>

<style scoped>

</style>