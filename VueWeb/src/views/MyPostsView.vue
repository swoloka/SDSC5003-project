<template>
  <div class="posts-container">
    <header class="header">
      <div class="header-content">
        <h1 class="logo">Pet Service Platform</h1>
        <div class="user-info">
          <span>Welcome, {{ authStore.user?.username }}</span>
          <button @click="$router.go(-1)" class="back-btn">Back</button>
          <button @click="handleLogout" class="logout-btn">Logout</button>
        </div>
      </div>
    </header>

    <main class="main-content">
      <div class="page-header">
        <h2>My Posts</h2>
        <button @click="$router.push('/')" class="create-btn">
          Create New Post
        </button>
      </div>

      <!-- 帖子分类标签 -->
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
          Looking for Service
        </button>
        <button
          :class="{ active: activeTab === 'offering' }"
          @click="activeTab = 'offering'"
        >
          Offering Service
        </button>
      </div>

      <div class="posts-grid">
        <div v-if="loading" class="loading">Loading...</div>
        <div v-else-if="filteredPosts.length === 0" class="no-posts">
          <p>No posts yet</p>
          <p>Click "Create New Post" to get started!</p>
        </div>
        <div v-else v-for="post in filteredPosts" :key="post.postId" class="post-card">
          <div class="post-header">
            <div class="post-badges">
              <span class="post-role" :class="post.role">
                {{ post.role === 'seeking' ? 'Looking for Service' : 'Offering Service' }}
              </span>
              <span class="post-status" :class="post.status">
                {{ post.status === 'open' ? 'Open' : 'Closed' }}
              </span>
            </div>
            <span class="post-time">{{ formatTime(post.createTime) }}</span>
          </div>
          <h3 class="post-title">{{ post.title }}</h3>
          <p class="post-detail">{{ post.description }}</p>

          <!-- Pet Information -->
          <div v-if="post.role === 'seeking' && (post.petName || post.breed)" class="pet-info">
            <span v-if="post.petName" class="pet-name">🐾 Pet: {{ post.petName }}</span>
            <span v-if="post.breed" class="pet-breed">Breed: {{ post.breed }}</span>
            <span v-if="post.serviceType" class="service-type">⚙️ Service: {{ getServiceTypeLabel(post.serviceType) }}</span>
          </div>

          <!-- Service Type for offering posts only -->
          <div v-else-if="post.role === 'offering' && post.serviceType" class="service-info">
            <span class="service-type">⚙️ Service: {{ getServiceTypeLabel(post.serviceType) }}</span>
          </div>

          <!-- Service Time -->
          <div v-if="post.serviceTime" class="service-time">
            🕐 Service Time: {{ formatServiceTime(post.serviceTime) }}
          </div>

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
          <div class="post-actions">
            <button @click="editPost(post)" class="edit-btn">Edit</button>
            <button @click="togglePostStatus(post)" class="status-btn" :class="post.status === 'open' ? 'close-btn' : 'open-btn'">
              {{ post.status === 'open' ? 'Close' : 'Open' }}
            </button>
            <button @click="handleDeletePost(post.postId)" class="delete-btn">Delete</button>
          </div>
        </div>
      </div>
    </main>

    <!-- 编辑模态框 -->
    <div v-if="showEditModal" class="modal-overlay" @click="closeEditModal">
      <div class="modal-content" @click.stop>
        <h2>Edit Post</h2>
        <form @submit.prevent="handleUpdatePost">
          <div class="form-group">
            <label>Post Type</label>
            <select v-model="editForm.role" disabled>
              <option value="seeking">Looking for Service</option>
              <option value="offering">Offering Service</option>
            </select>
          </div>

          <div class="form-group">
            <label>Title</label>
            <input type="text" v-model="editForm.title" required>
          </div>

          <!-- 只在寻求服务时显示宠物相关字段 -->
          <template v-if="editForm.role === 'seeking'">
            <div class="form-group">
              <label>Pet Name</label>
              <input type="text" v-model="editForm.petName">
            </div>
            <div class="form-group">
              <label>Breed</label>
              <input type="text" v-model="editForm.breed">
            </div>
            <div class="form-group">
              <label>Service Type</label>
              <select v-model="editForm.serviceType">
                <option value="">Please Select</option>
                <option value="grooming">Pet Grooming</option>
                <option value="walking">Pet Walking</option>
                <option value="feeding">Pet Feeding</option>
                <option value="other">Other Services</option>
              </select>
            </div>
          </template>

          <!-- 只在提供服务时显示服务类型 -->
          <div v-if="editForm.role === 'offering'" class="form-group">
            <label>Service Type</label>
            <select v-model="editForm.serviceType">
              <option value="">Please Select</option>
              <option value="grooming">Pet Grooming</option>
              <option value="walking">Pet Walking</option>
              <option value="feeding">Pet Feeding</option>
              <option value="other">Other Services</option>
            </select>
          </div>

          <div class="form-group">
            <label>Description</label>
            <textarea v-model="editForm.description" rows="4" required></textarea>
          </div>

          <div class="form-group">
            <label>Price</label>
            <input type="number" v-model="editForm.price" step="0.01">
          </div>

          <div class="form-group">
            <label>Category</label>
            <select v-model="editForm.petType" required>
              <option value="">Please Select</option>
              <option value="cat">🐱 Cat</option>
              <option value="dog">🐶 Dog</option>
              <option value="other">🐾 Other</option>
            </select>
          </div>

          <div class="form-group">
            <label>District</label>
            <select v-model="editForm.district" required>
              <option value="">Please Select District</option>
              <option v-for="district in hongKongDistricts" :key="district" :value="district">
                {{ getDistrictLabel(district) }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label>Contact</label>
            <input type="text" v-model="editForm.contact" required>
          </div>

          <div class="form-actions">
            <button type="button" @click="closeEditModal">Cancel</button>
            <button type="submit" :disabled="updating">
              {{ updating ? 'Updating...' : 'Update' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import {
  getAllUserPosts,
  updatePost,
  updatePostStatus,
  deletePost as deletePostApi
} from '@/services/postService'

const router = useRouter()
const authStore = useAuthStore()

const posts = ref([])
const loading = ref(true)
const activeTab = ref('all')
const showEditModal = ref(false)
const updating = ref(false)
const editingPost = ref(null)

const editForm = ref({
  role: '',
  title: '',
  petName: '',
  breed: '',
  serviceType: '',
  description: '',
  price: '',
  petType: '',
  district: '',
  contact: ''
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

// 根据帖子类型过滤
const filteredPosts = computed(() => {
  if (activeTab.value === 'all') {
    return posts.value
  }
  return posts.value.filter(post => post.role === activeTab.value)
})

// 加载用户的帖子
const loadPosts = async () => {
  loading.value = true
  try {
    // 获取用户的所有帖子（包括关闭状态）
    const [seekingResponse, offeringResponse] = await Promise.all([
      getAllUserPosts('seeking', authStore.token),
      getAllUserPosts('offering', authStore.token)
    ])

    if (seekingResponse.success && offeringResponse.success) {
      const seekingPosts = seekingResponse.posts || []
      const offeringPosts = offeringResponse.posts || []

      // 只保留当前用户的帖子
      const currentUsername = authStore.user?.username
      posts.value = [...seekingPosts, ...offeringPosts].filter(
        post => post.username === currentUsername
      )
    }
  } catch (error) {
    console.error('Error loading posts:', error)
  } finally {
    loading.value = false
  }
}

// 编辑帖子
const editPost = (post) => {
  editingPost.value = post
  editForm.value = {
    role: post.role,
    title: post.title,
    petName: post.petName || '',
    breed: post.breed || '',
    serviceType: post.serviceType || '',
    description: post.description,
    price: post.price ? post.price.toString() : '',
    petType: post.petType || '',
    district: post.district || '',
    contact: post.contact || ''
  }
  showEditModal.value = true
}

// 更新帖子
const handleUpdatePost = async () => {
  updating.value = true
  try {
    const postData = {
      title: editForm.value.title,
      petName: editForm.value.petName || null,
      breed: editForm.value.breed || null,
      serviceType: editForm.value.serviceType || null,
      price: editForm.value.price ? parseFloat(editForm.value.price) : null,
      description: editForm.value.description,
      petType: editForm.value.petType,
      district: getDistrictValue(editForm.value.district),
      contact: editForm.value.contact
    }

    const response = await updatePost(editingPost.value.postId, postData, authStore.token)

    if (response.success) {
      closeEditModal()
      await loadPosts()
    } else {
      alert('Failed to update post: ' + response.message)
    }
  } catch (error) {
    console.error('Error updating post:', error)
    alert('Failed to update post. Please try again.')
  } finally {
    updating.value = false
  }
}

// 切换帖子状态
const togglePostStatus = async (post) => {
  const action = post.status === 'open' ? 'close' : 'open'
  if (!confirm(`Are you sure you want to ${action} this post?`)) {
    return
  }

  try {
    const newStatus = post.status === 'open' ? 'close' : 'open'
    const response = await updatePostStatus(post.postId, newStatus, authStore.token)
    if (response.success) {
      await loadPosts()
    } else {
      alert(`Failed to ${action} post: ` + response.message)
    }
  } catch (error) {
    console.error(`Error ${action}ing post:`, error)
    alert(`Failed to ${action} post. Please try again.`)
  }
}

// 删除帖子
const handleDeletePost = async (postId) => {
  if (!confirm('Are you sure you want to delete this post?')) {
    return
  }

  try {
    const response = await deletePostApi(postId, authStore.token)
    if (response.success) {
      await loadPosts()
    } else {
      alert('Failed to delete post: ' + response.message)
    }
  } catch (error) {
    console.error('Error deleting post:', error)
    alert('Failed to delete post. Please try again.')
  }
}

// 关闭编辑模态框
const closeEditModal = () => {
  showEditModal.value = false
  editingPost.value = null
  editForm.value = {
    role: '',
    title: '',
    petName: '',
    breed: '',
    serviceType: '',
    description: '',
    price: '',
    petType: '',
    district: '',
    contact: ''
  }
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

// 格式化服务时间
const formatServiceTime = (timeString) => {
  if (!timeString) return ''
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

// 获取服务类型的显示标签
const getServiceTypeLabel = (serviceType) => {
  const serviceTypeMap = {
    'grooming': 'Pet Grooming',
    'walking': 'Pet Walking',
    'feeding': 'Pet Feeding',
    'other': 'Other Services'
  }
  return serviceTypeMap[serviceType] || serviceType || 'General Service'
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

// 获取地区显示标签
const getDistrictLabel = (district) => {
  return districtMapping[district] || district || 'Unknown'
}

// 获取地区的英文值
const getDistrictValue = (chineseDistrict) => {
  return districtMapping[chineseDistrict] || chineseDistrict
}

// 退出登录
const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}

// 生命周期
onMounted(() => {
  authStore.initAuth()
  loadPosts()
})
</script>

<style scoped>
.posts-container {
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

.back-btn, .logout-btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
}

.back-btn {
  background-color: #6c757d;
  color: white;
}

.logout-btn {
  background-color: #f56c6c;
  color: white;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.page-header h2 {
  color: #333;
  margin: 0;
}

.create-btn {
  padding: 0.75rem 1.5rem;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
  transition: background-color 0.3s;
}

.create-btn:hover {
  background-color: #369970;
}

.post-tabs {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
  justify-content: center;
}

.post-tabs button {
  padding: 0.75rem 1.5rem;
  border: 1px solid #ddd;
  background-color: white;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.post-tabs button.active {
  background-color: #42b983;
  color: white;
  border-color: #42b983;
}

.posts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 1.5rem;
}

.loading, .no-posts {
  text-align: center;
  padding: 3rem;
  color: #666;
}

.no-posts p {
  margin: 0.5rem 0;
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

.post-badges {
  display: flex;
  gap: 0.5rem;
  align-items: center;
}

.post-status {
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 500;
}

.post-status.open {
  background-color: #f6ffed;
  color: #52c41a;
  border: 1px solid #b7eb8f;
}

.post-status.close {
  background-color: #fff2f0;
  color: #ff4d4f;
  border: 1px solid #ffccc7;
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
  margin-bottom: 1rem;
}

.post-actions {
  display: flex;
  gap: 0.5rem;
  justify-content: flex-end;
}

.edit-btn, .delete-btn, .status-btn {
  padding: 0.25rem 0.75rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.8rem;
}

.edit-btn {
  background-color: #409eff;
  color: white;
}

.status-btn {
  background-color: #909399;
  color: white;
}

.status-btn.open-btn {
  background-color: #52c41a;
  color: white;
}

.status-btn.close-btn {
  background-color: #ff7875;
  color: white;
}

.delete-btn {
  background-color: #f56c6c;
  color: white;
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
  max-width: 600px;
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

.form-group input:disabled {
  background-color: #f5f5f5;
  color: #999;
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

/* 宠物信息样式 */
.pet-info {
  display: flex;
  gap: 1rem;
  margin: 0.5rem 0;
  font-size: 0.9rem;
}

.pet-name, .pet-breed {
  background-color: #f0f8ff;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.8rem;
  color: #333;
}

/* 服务信息样式 */
.service-info {
  display: flex;
  gap: 1rem;
  margin: 0.5rem 0;
  font-size: 0.9rem;
}

.service-type {
  background-color: #e8f5e8;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.8rem;
  color: #2e7d32;
}

/* Service Time styles */
.service-time {
  background-color: #fff9e6;
  padding: 0.5rem;
  border-radius: 4px;
  margin: 0.5rem 0;
  font-size: 0.8rem;
  color: #666;
  border-left: 3px solid #ffc107;
}
</style>