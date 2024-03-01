


export default {
    state: {
        status:"matching", // matching表示匹配界面，playing表示对战界面
        socket:null,
        opponent_usernmae:"",
        opponent_photo:"",
        gamemap:null,
        a_id: 0,//左下角蛇的id
        a_sx: 0,
        a_sy: 0,
        b_id: 0,//右上角蛇的id
        b_sx: 0,
        b_sy: 0,
        gameObject:null,//在GameMap.vue中的js里调用updateGameObject对此进行赋值，即是把整个地图存下来
        loser:"none",//All表示全输，A表示左下角的输，B表示右上角的输，none表示游戏进行中，还没有人输
    },
    getters: {
    },
    mutations: {
        updateSocket(state, socket){
            state.socket = socket;
        },
        updateOpponent(state, opponent){
            state.opponent_usernmae = opponent.username;
            state.opponent_photo = opponent.photo;
        },
        updateStatus(state, status){
            state.status = status;
        },
        updateGame(state, game){
            state.gamemap = game.map;
            state.a_id = game.a_id;
            state.a_sx = game.a_sx;
            state.a_sy = game.a_sy;
            state.b_id = game.b_id;
            state.b_sx = game.b_sx;
            state.b_sy = game.b_sy;
        },
        updateGameObject(state, gameObject){
            state.gameObject = gameObject;
        },
        updateLoser(state, loser){
            state.loser = loser;
        }

    },
    actions: {
    },
    modules: {

    }
  }
  