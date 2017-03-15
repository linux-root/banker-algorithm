/**
 * Created by kurro on 3/14/17.
 */

import {START_SEND_DATA, CALCULATE_SUCCESS, CALCULATE_FAILURE} from '../mutation-types'
import Vue from 'vue'

// init state
const state = {
  result: [],
  loading: false,
  error: null,
  success: false
}

const getters = {
  result: state => state.result
}

const actions = {
  async calculate ({commit}, Data) {
    commit(START_SEND_DATA)
    try {
      let result = await Vue.http.post('/banker', {data: Data}).then(respone => respone.body.data)
      commit(CALCULATE_SUCCESS, result)
    } catch (error) {
      commit(CALCULATE_FAILURE, error)
    }
  }
}

const mutations = {
  [START_SEND_DATA] (state) {
    state.loading = true
  },
  [CALCULATE_SUCCESS] (state, result) {
    state.loading = false
    state.success = true
    state.result = result
  },
  [CALCULATE_FAILURE] (state, error) {
    state.loading = false
    state.success = false
    if (error.status === 401 || error.status === 500) {
      state.error = error.body
    } else {
      state.error = error.body.error.message
    }
  }
}

export default {
  state,
  getters,
  actions,
  mutations
}
