export default ({
    state: {
        is_record:false,
        a_step:"",
        b_step:"",
        record_loser:"",
    },
    getters: {
       
    },
    mutations: {
        updateIsRecord(state, is_record){
            state.is_record=is_record;
        },
        updateStep(state, data){
            state.a_step = data.a_step;
            state.b_step = data.b_step;
        },
        updateRecordLoser(state, loser){
            console.log("这里是record.js区域",loser);
            state.record_loser = loser;
        }
    },
    actions: {
    },
    modules: {
    }
  })