import { login as loginApi, register as registerApi } from '@/api/auth'
import { getProfile } from '@/api/user'

const state = {
  token: null,
  userId: null,
  username: null,
  role: null,
  avatar: null,
  realName: null,
  permissions: []
}

const getters = {
  isLoggedIn: state => !!state.token,
  isAdmin: state => state.role === 'admin',
  isEntrepreneur: state => state.role === 'entrepreneur',
  isInvestor: state => state.role === 'investor',
  isProvider: state => state.role === 'provider'
}

const mutations = {
  SET_TOKEN(state, token) {
    state.token = token
    if (token) localStorage.setItem('token', token)
    else localStorage.removeItem('token')
  },
  SET_USER(state, payload) {
    if (payload) {
      state.userId = payload.userId
      state.username = payload.username
      state.role = payload.role
      state.avatar = payload.avatar
      state.realName = payload.realName
      state.permissions = payload.permissions || []
    }
  },
  LOGOUT(state) {
    state.token = null
    state.userId = null
    state.username = null
    state.role = null
    state.avatar = null
    state.realName = null
    state.permissions = []
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }
}

const actions = {
  async login({ commit }, { account, password, captcha, remember }) {
    const data = await loginApi({ account, password, captcha, remember })
    commit('SET_TOKEN', data.token)
    commit('SET_USER', {
      userId: data.userId,
      username: data.username,
      role: data.role,
      avatar: data.avatar,
      realName: data.realName,
      permissions: data.permissions || []
    })
    localStorage.setItem('user', JSON.stringify(data))
    return data
  },
  async register({ dispatch }, form) {
    await registerApi(form)
  },
  logout({ commit }) {
    commit('LOGOUT')
  },
  async fetchProfile({ commit }) {
    const data = await getProfile()
    commit('SET_USER', {
      userId: data.id,
      username: data.username,
      role: data.role,
      avatar: data.avatar,
      realName: data.realName
    })
    return data
  },
  updateUser({ commit, state }, userData) {
    const updatedUser = {
      ...state,
      ...userData
    }
    commit('SET_USER', updatedUser)
    localStorage.setItem('user', JSON.stringify(updatedUser))
  }
}

export default {
  namespaced: true,
  state,
  getters,
  mutations,
  actions
}
