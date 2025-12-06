import { ref } from 'vue'
import { defineStore } from 'pinia'

// 这个文件负责管理前端的登录状态
export const useAuthStore = defineStore('auth', () => {
  const isLoggedIn = ref(false)
  const user = ref(null)
  const token = ref(null)

  // 登录成功后，调用此函数更新状态
  function setLoginStatus(userData, authToken) {
    isLoggedIn.value = true
    user.value = userData
    token.value = authToken

    // 将token存储到localStorage
    if (authToken) {
      localStorage.setItem('auth_token', authToken)
    }
    if (userData) {
      localStorage.setItem('user_data', JSON.stringify(userData))
    }
  }

  // 退出登录时，调用此函数重置状态
  function logout() {
    isLoggedIn.value = false
    user.value = null
    token.value = null

    // 清除localStorage
    localStorage.removeItem('auth_token')
    localStorage.removeItem('user_data')
    localStorage.removeItem('current_username')
  }

  // 初始化时检查localStorage中的登录状态
  function initAuth() {
    const savedToken = localStorage.getItem('auth_token')
    const savedUser = localStorage.getItem('user_data')
    const savedUsername = localStorage.getItem('current_username')

    if (savedToken && savedUser) {
      try {
        token.value = savedToken
        user.value = JSON.parse(savedUser)
        isLoggedIn.value = true
      } catch (error) {
        console.error('Failed to parse saved user data:', error)
        // 如果用户数据解析失败，至少保存username
        if (savedUsername) {
          user.value = { username: savedUsername }
          isLoggedIn.value = true
        } else {
          logout()
        }
      }
    } else if (savedToken && savedUsername) {
      // 只有token和username的情况
      token.value = savedToken
      user.value = { username: savedUsername }
      isLoggedIn.value = true
    }
  }

  // 更新用户信息
  function updateUser(userData) {
    if (userData && isLoggedIn.value) {
      user.value = { ...user.value, ...userData }
      localStorage.setItem('user_data', JSON.stringify(user.value))
    }
  }

  // 获取认证头
  function getAuthHeaders() {
    return token.value ? {
      'Authorization': `Bearer ${token.value}`,
      'Content-Type': 'application/json'
    } : {
      'Content-Type': 'application/json'
    }
  }

  return {
    isLoggedIn,
    user,
    token,
    setLoginStatus,
    logout,
    initAuth,
    updateUser,
    getAuthHeaders
  }
})

