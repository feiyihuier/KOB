import { createStore } from 'vuex';
import ModulUser from './user';
import ModulePK  from './pk';

export default createStore({
  state: {
  },
  getters: {
  },
  mutations: {
  },
  actions: {
  },
  modules: {
    user:ModulUser,
    pk:ModulePK
  }
})
