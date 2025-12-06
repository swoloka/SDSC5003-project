<template>
  <div class="home-container">
    <!-- 顶部导航栏 -->
    <header class="header">
      <div class="header-content">
        <h1 class="logo">Pet Service Platform</h1>
        <div class="user-info">
          <span>Welcome, {{ authStore.user?.username }}</span>
          <span class="district" v-if="authStore.user?.district">District: {{ getDistrictLabel(authStore.user?.district) }}</span>
          <button @click="handleLogout" class="logout-btn">Logout</button>
        </div>
      </div>
    </header>

    <!-- 主要内容区域 -->
    <main class="main-content">
      <!-- 快捷操作区域 -->
      <div class="quick-actions">
        <button @click="showCreatePostModal = true" class="create-post-btn">
          Create New Post
        </button>
        <button @click="$router.push('/my-posts')" class="profile-btn my-posts-btn">
          Manage My Posts
        </button>
        <button @click="$router.push('/pet-profiles')" class="profile-btn pet-profile-btn">
          Manage Pet Profiles
        </button>
        <button @click="$router.push('/service-profiles')" class="profile-btn service-profile-btn">
          Manage Service Profiles
        </button>
      </div>

      <!-- 筛选器区域 -->
      <div class="filter-section">
        <!-- 帖子类型筛选 -->
        <div class="filter-group">
          <h3>Post Type</h3>
          <div class="post-tabs">
            <button
              :class="{ active: activeTab === 'all' }"
              @click="activeTab = 'all'"
            >
              All Posts
            </button>
            <button
              :class="{ active: activeTab === 'seeking' }"
              @click="activeTab = 'seeking'"
            >
              Seeking Service
            </button>
            <button
              :class="{ active: activeTab === 'offering' }"
              @click="activeTab = 'offering'"
            >
              Offering Service
            </button>
          </div>
        </div>

        <!-- 宠物类型筛选 -->
        <div class="filter-group">
          <h3>Pet Type</h3>
          <div class="pet-type-filters">
            <button
              :class="{ active: selectedPetType === 'all' }"
              @click="selectedPetType = 'all'"
            >
              All Pets
            </button>
            <button
              :class="{ active: selectedPetType === 'cat' }"
              @click="selectedPetType = 'cat'"
            >
              🐱 Cats
            </button>
            <button
              :class="{ active: selectedPetType === 'dog' }"
              @click="selectedPetType = 'dog'"
            >
              🐶 Dogs
            </button>
            <button
              :class="{ active: selectedPetType === 'other' }"
              @click="selectedPetType = 'other'"
            >
              🐾 Others
            </button>
          </div>
        </div>
      </div>

      <!-- 帖子列表 -->
      <div class="posts-container">
        <div v-if="loading" class="loading">Loading...</div>
        <div v-else-if="filteredPosts.length === 0" class="no-posts">No posts yet</div>
        <div v-else class="posts-grid">
          <div
            v-for="post in filteredPosts"
            :key="post.postId"
            class="post-card"
          >
            <div class="post-header">
              <span class="post-role" :class="post.role">
                {{ post.role === 'seeking' ? '寻求服务' : '提供服务' }}
              </span>
              <span class="post-time">{{ formatTime(post.createTime) }}</span>
            </div>
            <h3 class="post-title">{{ post.title }}</h3>
            <p class="post-detail">{{ post.description }}</p>
            <div class="post-meta">
              <span class="post-category">{{ getCategoryLabel(post.petType) }}</span>
              <span class="post-price" v-if="post.price">
                ¥{{ post.price }}
              </span>
            </div>
            <div class="post-footer">
              <span class="post-district">District: {{ getDistrictLabel(post.district) }}</span>
              <span class="post-contact">Contact: {{ post.contact }}</span>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- 发帖模态框 -->
    <div v-if="showCreatePostModal" class="modal-overlay" @click="closeCreateModal">
      <div class="modal-content" @click.stop>
        <h2>Create New Post</h2>
        <form @submit.prevent="handleCreatePost">
          <div class="form-group">
            <label>Post Type</label>
            <select v-model="newPost.role" required @change="onPostTypeChange">
              <option value="">Please Select</option>
              <option value="seeking">Seeking Service</option>
              <option value="offering">Offering Service</option>
            </select>
          </div>

          <!-- 档案选择部分 -->
          <div v-if="newPost.role === 'seeking' && petProfiles.length > 0" class="form-group">
            <label>Select Pet Profile (Optional)</label>
            <select v-model="selectedPetProfile" @change="onPetProfileChange">
              <option value="">Create custom post</option>
              <option v-for="profile in petProfiles" :key="profile.petId" :value="profile">
                {{ profile.petName }} ({{ profile.petType }})
              </option>
            </select>
          </div>

          <div v-if="newPost.role === 'offering' && serviceProfiles.length > 0" class="form-group">
            <label>Select Service Profile (Optional)</label>
            <select v-model="selectedServiceProfile" @change="onServiceProfileChange">
              <option value="">Create custom post</option>
              <option v-for="profile in serviceProfiles" :key="profile.serviceId" :value="profile">
                {{ profile.serviceType }} ({{ profile.petType || 'All pets' }})
              </option>
            </select>
          </div>

          <div class="form-group">
            <label>Title</label>
            <input type="text" v-model="newPost.title" required>
          </div>

          <!-- 只在寻求服务时显示宠物相关字段 -->
          <template v-if="newPost.role === 'seeking'">
            <div class="form-group">
              <label>Pet Name (Optional)</label>
              <input type="text" v-model="newPost.petName" placeholder="Enter pet name">
            </div>
            <div class="form-group">
              <label>Breed (Optional)</label>
              <input type="text" v-model="newPost.breed" placeholder="Enter breed">
            </div>
          </template>

          <!-- 只在提供服务时显示服务类型 -->
          <div v-if="newPost.role === 'offering'" class="form-group">
            <label>Service Type</label>
            <select v-model="newPost.serviceType">
              <option value="">Please Select</option>
              <option value="grooming">Pet Grooming</option>
              <option value="walking">Pet Walking</option>
              <option value="sitting">Pet Sitting</option>
              <option value="training">Pet Training</option>
              <option value="boarding">Pet Boarding</option>
              <option value="medical">Pet Medical Care</option>
              <option value="other">Other Services</option>
            </select>
          </div>
          <div class="form-group">
            <label>Description</label>
            <textarea v-model="newPost.detail" rows="4" required></textarea>
          </div>
          <div class="form-group">
            <label>Price (Optional)</label>
            <input type="number" v-model="newPost.price" step="0.01">
          </div>

          <!-- 只在寻求服务时显示Category选择，提供服务时从service profile获取 -->
          <div v-if="newPost.role === 'seeking'" class="form-group">
            <label>Category</label>
            <select v-model="newPost.category" required>
              <option value="">Please Select</option>
              <option value="cat">🐱 Cat</option>
              <option value="dog">🐶 Dog</option>
              <option value="other">🐾 Other</option>
            </select>
          </div>

          <!-- 服务类型时显示pet_type选择 -->
          <div v-if="newPost.role === 'offering'" class="form-group">
            <label>Pet Type</label>
            <select v-model="newPost.category" required>
              <option value="">Please Select</option>
              <option value="cat">🐱 Cat</option>
              <option value="dog">🐶 Dog</option>
              <option value="other">🐾 Other</option>
            </select>
          </div>
          <div class="form-group">
            <label>District</label>
            <select v-model="newPost.district" required>
              <option value="">Please Select District</option>
              <option v-for="district in hongKongDistricts" :key="district" :value="district">
                {{ district }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label>Contact</label>
            <input type="text" v-model="newPost.contact" placeholder="Please enter phone number or other contact info" required>
          </div>
          <div class="form-actions">
            <button type="button" @click="closeCreateModal">Cancel</button>
            <button type="submit" :disabled="submitting">
              {{ submitting ? 'Publishing...' : 'Publish' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watchEffect } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import {
  getAllPosts,
  getPostsByType,
  createPost
} from '@/services/postService'
import {
  getUserPetProfiles,
  getUserServiceProfiles
} from '@/services/profileService'

const router = useRouter()
const authStore = useAuthStore()

// 响应式数据
const posts = ref([])
const loading = ref(true)
const activeTab = ref('all')
const selectedPetType = ref('all')
const showCreatePostModal = ref(false)
const submitting = ref(false)

// 新帖子表单
const newPost = ref({
  role: '',
  title: '',
  petName: '',
  breed: '',
  serviceType: '',
  price: '',
  detail: '',
  category: '',
  district: '',
  contact: ''
})

// 用户档案数据
const petProfiles = ref([])
const serviceProfiles = ref([])
const selectedPetProfile = ref(null)
const selectedServiceProfile = ref(null)

// 根据帖子��型和宠物类型过滤
const filteredPosts = computed(() => {
  let filtered = posts.value

  // 根据帖子类型过滤
  if (activeTab.value !== 'all') {
    filtered = filtered.filter(post => post.role === activeTab.value)
  }

  // 根据宠物类型过滤
  if (selectedPetType.value !== 'all') {
    filtered = filtered.filter(post => post.petType === selectedPetType.value)
  }

  return filtered
})

// 固定的分类选项
const petCategories = [
  { value: 'cat', label: '🐱 猫' },
  { value: 'dog', label: '🐶 狗' },
  { value: 'other', label: '🐾 其他' }
]

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

// 加载帖子数据
const loadPosts = async () => {
  loading.value = true
  try {
    const response = await getAllPosts(authStore.token)
    console.log('后端返回的帖子数据:', response)

    if (response.success) {
      posts.value = response.posts || []
      console.log('设置的帖子数组:', posts.value)
      console.log('帖子数量:', posts.value.length)
    } else {
      console.error('Failed to load posts:', response.message)
    }
  } catch (error) {
    console.error('Error loading posts:', error)
  } finally {
    loading.value = false
  }
}

// 加载用户档案
const loadUserProfiles = async () => {
  try {
    const [petResponse, serviceResponse] = await Promise.all([
      getUserPetProfiles(authStore.token),
      getUserServiceProfiles(authStore.token)
    ])

    if (petResponse.success) {
      petProfiles.value = petResponse.profiles || []
    }
    if (serviceResponse.success) {
      serviceProfiles.value = serviceResponse.profiles || []
    }
  } catch (error) {
    console.error('Error loading profiles:', error)
  }
}

// 创建帖子
const handleCreatePost = async () => {
  submitting.value = true
  try {
    // 确保username一定有值，从localStorage获取作为备用
    const currentUsername = authStore.user?.username || localStorage.getItem('current_username')

    const postData = {
      username: currentUsername,
      role: newPost.value.role,
      title: newPost.value.title,
      petName: newPost.value.petName || null,
      breed: newPost.value.breed || null,
      serviceType: newPost.value.serviceType || null,
      price: newPost.value.price ? parseFloat(newPost.value.price) : null,
      description: newPost.value.detail,
      petType: newPost.value.category,
      district: getDistrictValue(newPost.value.district),
      contact: newPost.value.contact
    }

    // 调试信息：检查发送的数据
    console.log('发送到后端的帖子数据:', postData)
    console.log('当前用户信息:', authStore.user)
    console.log('用户名:', currentUsername)
    console.log('localStorage数据:', {
      username: localStorage.getItem('current_username'),
      district: localStorage.getItem('current_district'),
      contact: localStorage.getItem('current_contact')
    })

    // 验证必需字段
    if (!postData.username) {
      alert('用户信息不完整，请重新登录')
      submitting.value = false
      return
    }

    // 调试信息：检查发送的数据
    console.log('发送到后端的帖子数据:', postData)
    console.log('当前用户信息:', authStore.user)
    console.log('用户名:', currentUsername)

    const response = await createPost(postData, authStore.token)

    if (response.success) {
      // 重新加载帖子列表
      await loadPosts()
      // 重置表单并关闭模态框
      resetNewPost()
      showCreatePostModal.value = false
    } else {
      alert('发布失败：' + response.message)
    }
  } catch (error) {
    console.error('Error creating post:', error)
    alert('发布失败，请检查网络连接')
  } finally {
    submitting.value = false
  }
}

// 处理帖子类型变化
const onPostTypeChange = () => {
  selectedPetProfile.value = null
  selectedServiceProfile.value = null
  // 重置表单数据
  newPost.value.title = ''
  newPost.value.petName = ''
  newPost.value.breed = ''
  newPost.value.serviceType = ''
  newPost.value.detail = ''
  newPost.value.price = ''

  // 根据帖子类型重置特定字段
  if (newPost.value.role === 'seeking') {
    newPost.value.serviceType = ''
  } else if (newPost.value.role === 'offering') {
    newPost.value.petName = ''
    newPost.value.breed = ''
    newPost.value.category = ''
  }
}

// 处理宠物档案选择
const onPetProfileChange = () => {
  if (selectedPetProfile.value) {
    newPost.value.title = `Looking for ${selectedPetProfile.value.petType} services - ${selectedPetProfile.value.petName}`
    newPost.value.detail = selectedPetProfile.value.description || ''
    newPost.value.petName = selectedPetProfile.value.petName
    newPost.value.breed = selectedPetProfile.value.breed || ''
    newPost.value.category = selectedPetProfile.value.petType
  }
}

// 处理服务档案选择
const onServiceProfileChange = () => {
  if (selectedServiceProfile.value) {
    newPost.value.title = `${selectedServiceProfile.value.serviceType} Services Available`
    newPost.value.detail = selectedServiceProfile.value.description || ''
    // 设置服务类型，但允许用户修改pet_type
    newPost.value.serviceType = selectedServiceProfile.value.serviceType
    newPost.value.price = selectedServiceProfile.value.price
    // 如果当前category为空，则建议使用service profile的petType
    if (!newPost.value.category && selectedServiceProfile.value.petType) {
      newPost.value.category = selectedServiceProfile.value.petType
    }
  } else {
    // 如果没有选择服务档案，只清空服务相关字段，保留用户选择的pet_type
    newPost.value.serviceType = ''
    newPost.value.price = ''
  }
}

// 格式化价格范围
const formatPriceRange = (minPrice, maxPrice) => {
  if (!minPrice && !maxPrice) return 'Not specified'
  if (!minPrice) return `Up to ¥${maxPrice}`
  if (!maxPrice) return `From ¥${minPrice}`
  return `¥${minPrice} - ¥${maxPrice}`
}

// 重置新帖子表单
const resetNewPost = () => {
  newPost.value = {
    role: '',
    title: '',
    petName: '',
    breed: '',
    serviceType: '',
    price: '',
    detail: '',
    category: '',
    district: '',
    contact: ''
  }
  selectedPetProfile.value = null
  selectedServiceProfile.value = null
}

// 关闭创建模态框
const closeCreateModal = () => {
  showCreatePostModal.value = false
  resetNewPost()
}

// 格式化时间
const formatTime = (timeString) => {
  const date = new Date(timeString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 获取宠物类型的显示标签
const getCategoryLabel = (category) => {
  const categoryMap = {
    'cat': '🐱 Cat',
    'dog': '🐶 Dog',
    'other': '🐾 Other'
  }
  return categoryMap[category] || category || 'Uncategorized'
}

// 地区中英文映射
const districtMapping = {
  '中西区': 'Central and Western',
  '湾仔区': 'Wan Chai',
  '东区': 'Eastern',
  '南区': 'Southern',
  '油尖旺区': 'Yau Tsim Mong',
  '深水埗区': 'Sham Shui Po',
  '九龙城区': 'Kowloon City',
  '黄大仙区': 'Wong Tai Sin',
  '观塘区': 'Kwun Tong',
  '荃湾区': 'Tsuen Wan',
  '屯门区': 'Tuen Mun',
  '元朗区': 'Yuen Long',
  '北区': 'North',
  '大埔区': 'Tai Po',
  '西贡区': 'Sai Kung',
  '沙田区': 'Sha Tin',
  '葵青区': 'Kwai Tsing',
  '离岛区': 'Islands'
}

// 获取地区显示标签（中英文对照）
const getDistrictLabel = (district) => {
  // 如果是英文，显示为中文
  const englishToChinese = Object.fromEntries(
    Object.entries(districtMapping).map(([chinese, english]) => [english, chinese])
  )
  return districtMapping[district] || englishToChinese[district] || district || 'Unknown'
}

// 获取地区的英文值用于存储
const getDistrictValue = (chineseDistrict) => {
  return districtMapping[chineseDistrict] || chineseDistrict
}

// 退出登录
const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}

// 生命周期
onMounted(async () => {
  // 初始化认证状���
  authStore.initAuth()

  // 加载数据
  await Promise.all([
    loadPosts(),
    loadUserProfiles()
  ])
})

</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.header {
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 1rem 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  color: #42b983;
  margin: 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.district {
  color: #666;
  font-size: 0.9rem;
}

.logout-btn {
  padding: 0.5rem 1rem;
  background-color: #f56c6c;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
}

.quick-actions {
  margin-bottom: 2rem;
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
}

.create-post-btn, .profile-btn {
  padding: 1rem 2rem;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s;
}

.create-post-btn {
  background-color: #42b983;
}

.create-post-btn:hover {
  background-color: #369970;
}

.pet-profile-btn {
  background-color: #e6a23c;
}

.pet-profile-btn:hover {
  background-color: #cf9236;
}

.my-posts-btn {
  background-color: #909399;
}

.my-posts-btn:hover {
  background-color: #73767a;
}

.service-profile-btn {
  background-color: #409eff;
}

.service-profile-btn:hover {
  background-color: #337ecc;
}

/* 筛选器区域样式 */
.filter-section {
  display: flex;
  flex-direction: column;
  gap: 2rem;
  margin: 2rem 0;
  padding: 1.5rem;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.filter-group h3 {
  margin: 0 0 1rem 0;
  color: #333;
  font-size: 1.1rem;
  font-weight: 600;
}

.post-tabs {
  display: flex;
  gap: 1rem;
  justify-content: center;
}

.pet-type-filters {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
}

.post-tabs button,
.pet-type-filters button {
  padding: 0.75rem 1.5rem;
  border: 1px solid #ddd;
  background-color: white;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 0.9rem;
}

.post-tabs button:hover,
.pet-type-filters button:hover {
  border-color: #42b983;
  background-color: #f6ffed;
}

.post-tabs button.active,
.pet-type-filters button.active {
  background-color: #42b983;
  color: white;
  border-color: #42b983;
}

.posts-container {
  margin-top: 2rem;
}

.loading, .no-posts {
  text-align: center;
  padding: 3rem;
  color: #666;
}

.posts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 1.5rem;
}

.post-card {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s;
}

.post-card:hover {
  transform: translateY(-2px);
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.post-role {
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 500;
}

.post-role.seeking {
  background-color: #e6f7ff;
  color: #1890ff;
}

.post-role.offering {
  background-color: #f6ffed;
  color: #52c41a;
}

.post-time {
  color: #999;
  font-size: 0.8rem;
}

.post-title {
  margin: 0 0 0.5rem 0;
  color: #333;
  font-size: 1.1rem;
}

.post-detail {
  color: #666;
  margin: 0 0 1rem 0;
  line-height: 1.5;
}

.post-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 1rem;
}

.post-category {
  background-color: #f0f2f5;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.8rem;
}

.post-price {
  color: #f56c6c;
  font-weight: 500;
}

.post-footer {
  display: flex;
  justify-content: space-between;
  padding-top: 1rem;
  border-top: 1px solid #eee;
  font-size: 0.8rem;
  color: #666;
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  max-height: 80vh;
  overflow-y: auto;
}

.modal-content h2 {
  margin-top: 0;
  color: #333;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #333;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.form-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  margin-top: 1.5rem;
}

.form-actions button {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

.form-actions button[type="button"] {
  background-color: #f0f2f5;
  color: #333;
}

.form-actions button[type="submit"] {
  background-color: #42b983;
  color: white;
}

.form-actions button[type="submit"]:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.readonly-input {
  background-color: #f5f5f5;
  border-color: #ddd;
  color: #666;
  cursor: not-allowed;
}
</style>
