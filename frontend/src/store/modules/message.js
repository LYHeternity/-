import { unreadCount as apiUnreadCount } from '@/api/message'

const state = {
  unreadCount: 0,
  loading: false
}

const getters = {
  hasUnreadMessages: state => state.unreadCount > 0
}

const mutations = {
  SET_UNREAD_COUNT(state, count) {
    state.unreadCount = count
  },
  SET_LOADING(state, loading) {
    state.loading = loading
  }
}

const actions = {
  async fetchUnreadCount({ commit }) {
    commit('SET_LOADING', true)
    try {
      const data = await apiUnreadCount()
      // 后端返回的 data 直接是数字，而不是 { count: number }
      const count = typeof data === 'number' ? data : (data.count || 0)
      commit('SET_UNREAD_COUNT', count)
      return data
    } catch (error) {
      console.error('获取未读消息数量失败:', error)
      commit('SET_UNREAD_COUNT', 0)
    } finally {
      commit('SET_LOADING', false)
    }
  },
  updateUnreadCount({ commit }, count) {
    commit('SET_UNREAD_COUNT', count)
  }
}

export default {
  namespaced: true,
  state,
  getters,
  mutations,
  actions
}
