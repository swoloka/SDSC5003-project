<template>
  <div class="profiles-container">
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
        <h2>My Service Profiles</h2>
        <button @click="showCreateModal = true" class="create-btn">
          Add New Service
        </button>
      </div>

      <div class="profiles-grid">
        <div v-if="loading" class="loading">Loading...</div>
        <div v-else-if="serviceProfiles.length === 0" class="no-profiles">
          <p>No service profiles yet</p>
          <p>Create your first service profile to offer your services!</p>
        </div>
        <div v-else v-for="profile in serviceProfiles" :key="profile.serviceId" class="profile-card">
          <div class="profile-header">
            <h3>{{ profile.serviceType }} Services</h3>
            <div class="profile-actions">
              <button @click="editProfile(profile)" class="edit-btn">Edit</button>
              <button @click="deleteProfile(profile.serviceId)" class="delete-btn">Delete</button>
            </div>
          </div>
          <div class="profile-info">
            <p><strong>Pet Type:</strong> {{ profile.petType || 'Not specified' }}</p>
            <p><strong>Service Type:</strong> {{ profile.serviceType }}</p>
            <p><strong>Price:</strong> {{ profile.price ? `¥${profile.price}` : 'Not specified' }}</p>
            <p v-if="profile.description"><strong>Description:</strong> {{ profile.description }}</p>
          </div>
        </div>
      </div>
    </main>

    <!-- 创建/编辑模态框 -->
    <div v-if="showCreateModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <h2>{{ editingProfile ? 'Edit Service Profile' : 'Create Service Profile' }}</h2>
        <form @submit.prevent="handleSubmit">
          <div class="form-group">
            <label>Pet Type</label>
            <select v-model="formData.petType">
              <option value="">Please Select</option>
              <option value="cat">Cat</option>
              <option value="dog">Dog</option>
              <option value="other">Other</option>
            </select>
          </div>
          <div class="form-group">
            <label>Service Type *</label>
            <select v-model="formData.serviceType" required>
              <option value="">Please Select</option>
              <option value="grooming">Pet Grooming</option>
              <option value="walking">Pet Walking</option>
              <option value="feeding">Pet Feeding</option>
              <option value="other">Other Services</option>
            </select>
          </div>
          <div class="form-group">
            <label>Price (¥)</label>
            <input type="number" v-model="formData.price" min="0" step="0.01">
          </div>
          <div class="form-group">
            <label>Description</label>
            <textarea v-model="formData.description" rows="4"></textarea>
          </div>
          <div class="form-actions">
            <button type="button" @click="closeModal">Cancel</button>
            <button type="submit" :disabled="submitting">
              {{ submitting ? 'Saving...' : 'Save' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import {
  getUserServiceProfiles,
  createServiceProfile,
  updateServiceProfile,
  deleteServiceProfile
} from '@/services/profileService'

const router = useRouter()
const authStore = useAuthStore()

const serviceProfiles = ref([])
const loading = ref(true)
const showCreateModal = ref(false)
const submitting = ref(false)
const editingProfile = ref(null)

const formData = ref({
  petType: '',
  serviceType: '',
  price: '',
  description: ''
})

const loadProfiles = async () => {
  loading.value = true
  try {
    const response = await getUserServiceProfiles(authStore.token, authStore.user?.username)
    if (response.success) {
      serviceProfiles.value = response.profiles || []
    }
  } catch (error) {
    console.error('Error loading service profiles:', error)
  } finally {
    loading.value = false
  }
}

const formatPrice = (price) => {
  return price ? `¥${price}` : 'Not specified'
}

const handleSubmit = async () => {
  submitting.value = true
  try {
    // 确保username一定有值，从localStorage获取作为备用
    const currentUsername = authStore.user?.username || localStorage.getItem('current_username')

    const data = {
      username: currentUsername,
      ...formData.value,
      price: formData.value.price ? parseFloat(formData.value.price) : null
    }

    let response
    if (editingProfile.value) {
      response = await updateServiceProfile(editingProfile.value.serviceId, data, authStore.token)
    } else {
      response = await createServiceProfile(data, authStore.token)
    }

    if (response.success) {
      closeModal()
      await loadProfiles()
    } else {
      alert('Failed to save service profile: ' + response.message)
    }
  } catch (error) {
    console.error('Error saving service profile:', error)
    alert('Failed to save service profile. Please try again.')
  } finally {
    submitting.value = false
  }
}

const editProfile = (profile) => {
  editingProfile.value = profile
  formData.value = {
    petType: profile.petType || '',
    serviceType: profile.serviceType,
    price: profile.price?.toString() || '',
    description: profile.description || ''
  }
  showCreateModal.value = true
}

const deleteProfile = async (profileId) => {
  if (!confirm('Are you sure you want to delete this service profile?')) {
    return
  }

  try {
    const response = await deleteServiceProfile(profileId, authStore.token)
    if (response.success) {
      await loadProfiles()
    } else {
      alert('Failed to delete service profile: ' + response.message)
    }
  } catch (error) {
    console.error('Error deleting service profile:', error)
    alert('Failed to delete service profile. Please try again.')
  }
}

const closeModal = () => {
  showCreateModal.value = false
  editingProfile.value = null
  formData.value = {
    petType: '',
    serviceType: '',
    price: '',
    description: ''
  }
}

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}

onMounted(() => {
  authStore.initAuth()
  loadProfiles()
})
</script>

<style scoped>
.profiles-container {
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

.profiles-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 1.5rem;
}

.loading, .no-profiles {
  text-align: center;
  padding: 3rem;
  color: #666;
}

.no-profiles p {
  margin: 0.5rem 0;
}

.profile-card {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s;
}

.profile-card:hover {
  transform: translateY(-2px);
}

.profile-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.profile-header h3 {
  margin: 0;
  color: #333;
  font-size: 1.2rem;
}

.profile-actions {
  display: flex;
  gap: 0.5rem;
}

.edit-btn, .delete-btn {
  padding: 0.25rem 0.5rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.8rem;
}

.edit-btn {
  background-color: #409eff;
  color: white;
}

.delete-btn {
  background-color: #f56c6c;
  color: white;
}

.profile-info p {
  margin: 0.5rem 0;
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

.form-row {
  display: flex;
  gap: 1rem;
}

.form-row .form-group {
  flex: 1;
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

.form-group select[multiple] {
  min-height: 120px;
}

.form-group small {
  display: block;
  margin-top: 0.25rem;
  color: #666;
  font-size: 0.8rem;
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
</style>