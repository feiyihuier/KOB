<template>
<nav class="navbar navbar-expand-lg bg-body-tertiary" data-bs-theme="dark">
  <div class="container">
    <router-link class="navbar-brand" :to="{name: 'home'}" >King Of Bots</router-link>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarText" aria-controls="navbarText" aria-expanded="false" aria-label="Toggle navigation">
      <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarText">
      <ul class="navbar-nav me-auto mb-2 mb-lg-0">
        <li class="nav-item">
            <router-link :class="route_name == 'pk_index' ?'nav-link active' : 'nav-link' " :to="{name: 'pk_index'}" >对战</router-link>
        </li>
        <li class="nav-item">
            <router-link :class="route_name == 'record_index' ?'nav-link active' : 'nav-link' " :to="{name: 'record_index'}" >对局列表</router-link>
        </li>
        <li class="nav-item">
            <router-link :class="route_name == 'ranklist_index' ?'nav-link active' : 'nav-link' " :to="{name: 'ranklist_index'}" >排行榜</router-link>
        </li>
      </ul>

      <ul class="navbar-nav" v-if="$store.state.user.is_login">
        <li class="nav-item dropdown">
          <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
            {{ $store.state.user.username }}
          </a>
          <ul class="dropdown-menu">
            <router-link class="nav-link" :to="{name: 'user_bot_index'}" >我的Bot</router-link>
            <router-link class="nav-link" :to="{name: ''}" @click="navbarlogout">退出</router-link>
          </ul>
        </li>
      </ul>

      <ul class="navbar-nav" v-else-if="!$store.state.user.pulling_info">
        <li class="nav-item">
          <router-link class="nav-link" :to="{name: 'user_account_login' }" role="button">
            登录
          </router-link>
        </li>
        <li class="nav-item">
          <router-link class="nav-link" :to="{name: 'user_account_register'}" role="button">
            注册
          </router-link>
        </li>

      </ul>

    </div>
  </div>
</nav>
</template>

<script>
import { useRoute } from 'vue-router';
import { computed} from 'vue';
import { useStore } from 'vuex';

export default{
  setup(){
    const route = useRoute();
    const store = useStore();
    let route_name = computed(() => route.name);

    const navbarlogout = () => {
        store.dispatch("userjs_actios_logout");
    }

    return {
      route_name,
      navbarlogout,
      store,
    };
  }
}
</script>

<style scoped>

</style>
