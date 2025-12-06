<template>
  <main>
    <div class="auth-container">
      <div class="auth-tabs">
        <button
          :class="{ active: !isRegistering }"
          @click="isRegistering = false"
        >
          登录
        </button>
        <button
          :class="{ active: isRegistering }"
          @click="isRegistering = true"
        >
          注册
        </button>
      </div>

      <!-- 登录表单 -->
      <form v-if="!isRegistering" @submit.prevent="handleLogin" class="auth-form">
        <h1>用户登录</h1>
        <div class="input-group">
          <label for="username">用户名</label>
          <input type="text" id="username" v-model="username" required>
        </div>
        <div class="input-group">
          <label for="password">密码</label>
          <input type="password" id="password" v-model="password" required>
        </div>
        <button type="submit">登录</button>
        <p v-if="error" class="error-message">{{ error }}</p>
      </form>

      <!-- 注册表单 -->
      <form v-else @submit.prevent="handleRegister" class="auth-form">
        <h1>用户注册</h1>
        <div class="input-group">
          <label for="reg-username">用户名</label>
          <input type="text" id="reg-username" v-model="regForm.username" required>
        </div>
        <div class="input-group">
          <label for="reg-password">密码</label>
          <input type="password" id="reg-password" v-model="regForm.password" required>
        </div>
        <div class="input-group">
          <label for="reg-confirm-password">确认密码</label>
          <input type="password" id="reg-confirm-password" v-model="regForm.confirmPassword" required>
        </div>
        <div class="input-group">
          <label for="reg-contact">联系方式</label>
          <input type="text" id="reg-contact" v-model="regForm.contact" required>
        </div>
        <div class="input-group">
          <label for="reg-district">地区</label>
          <select id="reg-district" v-model="regForm.district" required>
            <option value="">请选择地区</option>
            <option v-for="district in hongKongDistricts" :key="district" :value="district">
              {{ district }}
            </option>
          </select>
        </div>
        <button type="submit">注册</button>
        <p v-if="error" class="error-message">{{ error }}</p>
        <p v-if="success" class="success-message">{{ success }}</p>
      </form>
    </div>
  </main>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
// 导入 authService 中的 login 函数
import { login, register, getUserInfo } from '@/services/authService'

// 登录相关数据
const username = ref('')
const password = ref('')
const error = ref('')
const success = ref('')

// 注册相关数据
const isRegistering = ref(false)
const regForm = ref({
  username: '',
  password: '',
  confirmPassword: '',
  contact: '',
  district: ''
})

// 香港18个区
const hongKongDistricts = [
  '中西区',
  '湾仔区',
  '东区',
  '南区',
  '油尖旺区',
  '深水埗区',
  '九龙城区',
  '黄大仙区',
  '观塘区',
  '荃湾区',
  '屯门区',
  '元朗区',
  '北区',
  '大埔区',
  '西贡区',
  '沙田区',
  '葵青区',
  '离岛区'
]

const router = useRouter()
const authStore = useAuthStore()

// 处理登录
const handleLogin = async () => {
  error.value = ''
  success.value = ''
  try {
    const response = await login(username.value, password.value)

    if (response.success) {
      // 登录成功后，获取完整的用户信息
      try {
        const userResponse = await getUserInfo(response.token, username.value)
        if (userResponse.success) {
          authStore.setLoginStatus(userResponse.user, response.token)
          // 保存用户信息到localStorage作为备用
          localStorage.setItem('current_username', username.value)
          if (userResponse.user?.district) {
            localStorage.setItem('current_district', userResponse.user.district)
          }
          if (userResponse.user?.contact) {
            localStorage.setItem('current_contact', userResponse.user.contact)
          }
        } else {
          // 如果获取用户信息失败，至少保存username
          authStore.setLoginStatus({ username: username.value }, response.token)
          localStorage.setItem('current_username', username.value)
        }
      } catch (err) {
        // 如果获取用户信息失败，尝试从注册信息中获取
        const pendingDistrict = localStorage.getItem('pending_district')
        const pendingContact = localStorage.getItem('pending_contact')

        const userData = {
          username: username.value,
          district: pendingDistrict || '',
          contact: pendingContact || ''
        }

        authStore.setLoginStatus(userData, response.token)
        localStorage.setItem('current_username', username.value)
        if (pendingDistrict) {
          localStorage.setItem('current_district', pendingDistrict)
        }
        if (pendingContact) {
          localStorage.setItem('current_contact', pendingContact)
        }
        // 清除pending数据
        localStorage.removeItem('pending_username')
        localStorage.removeItem('pending_district')
        localStorage.removeItem('pending_contact')
      }
      router.push('/')
    } else {
      error.value = response.message
    }
  } catch (err) {
    error.value = '登录失败，请检查网络或服务器。'
    console.error(err)
  }
}

// 处理注册
const handleRegister = async () => {
  error.value = ''
  success.value = ''

  // 验证密码
  if (regForm.value.password !== regForm.value.confirmPassword) {
    error.value = '两次输入的密码不一致'
    return
  }

  try {
    const response = await register({
      username: regForm.value.username,
      password: regForm.value.password,
      contact: regForm.value.contact,
      district: regForm.value.district
    })

    if (response.success) {
      success.value = '注册成功！请登录'

      // 保存注册信息到localStorage，以便登录时使用
      localStorage.setItem('pending_username', regForm.value.username)
      localStorage.setItem('pending_district', regForm.value.district)
      localStorage.setItem('pending_contact', regForm.value.contact)

      // ���空注册表单
      regForm.value = {
        username: '',
        password: '',
        confirmPassword: '',
        contact: '',
        district: ''
      }
      // 2秒后切换到登录页面
      setTimeout(() => {
        isRegistering.value = false
        success.value = ''
      }, 2000)
    } else {
      error.value = response.message
    }
  } catch (err) {
    error.value = '注册失败，请检查网络或服务器。'
    console.error(err)
  }
}
</script>

<style scoped>
main {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 20px;
  background-color: #f5f5f5;
}

.auth-container {
  width: 100%;
  max-width: 400px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.auth-tabs {
  display: flex;
  background-color: #f8f9fa;
}

.auth-tabs button {
  flex: 1;
  padding: 15px;
  border: none;
  background-color: transparent;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.3s;
}

.auth-tabs button.active {
  background-color: #42b983;
  color: white;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 15px;
  padding: 30px;
}

.auth-form h1 {
  text-align: center;
  margin: 0 0 20px 0;
  color: #333;
}

.input-group {
  display: flex;
  flex-direction: column;
  text-align: left;
}

.input-group label {
  margin-bottom: 5px;
  font-weight: 500;
  color: #555;
}

.input-group input,
.input-group select {
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.input-group input:focus,
.input-group select:focus {
  outline: none;
  border-color: #42b983;
}

.auth-form button {
  padding: 12px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 500;
  transition: background-color 0.3s;
}

.auth-form button:hover {
  background-color: #369970;
}

.error-message {
  color: #e74c3c;
  margin: 10px 0;
  text-align: center;
  font-size: 14px;
}

.success-message {
  color: #27ae60;
  margin: 10px 0;
  text-align: center;
  font-size: 14px;
}
</style>

