import Vue from 'vue'
import Router from 'vue-router'
import Banker from '../components/Banker.vue'


Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'Banker',
      component: Banker
    }
  ]
})
