import Vue from 'vue'
import App from './App'
import router from './router/index'
import store from './store/store'
import axios from 'axios'
// 画图图表组件
import echarts from 'echarts'
// 异步请求
import vueResource from 'vue-resource'

Vue.use(vueResource)
Vue.config.debug = true
Vue.config.productionTip = false
Vue.prototype.$axios = axios
Vue.prototype.$echarts = echarts

new Vue({
  el: '#app',
  router: router,
  store: store,
  template: '<App/>',
  components: { App }
})