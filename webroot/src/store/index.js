/**
 * Created by kurro on 3/14/17.
 */
import Vue from 'vue'
import Vuex from 'vuex'
import createLogger from 'vuex/dist/logger'
import Banker from './modules/Banker'

Vue.use(Vuex)
const debug = process.env.NODE_ENV !== 'production'

let plugins = debug ? [createLogger()] : []

export default new Vuex.Store({
  modules: {
   Banker
  },
  strict: debug,
  plugins: plugins
})
