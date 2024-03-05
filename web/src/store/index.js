import { createStore } from 'vuex';
import ModulUser from './user';
import ModulePK  from './pk';
import ModuleRecord from '../store/record'

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
    pk:ModulePK,
    record:ModuleRecord,
  }
})
