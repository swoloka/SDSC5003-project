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
        <h2>My Pet Profiles</h2>
        <button @click="showCreateModal = true" class="create-btn">
          Add New Pet
        </button>
      </div>

      <div class="profiles-grid">
        <div v-if="loading" class="loading">Loading...</div>
        <div v-else-if="petProfiles.length === 0" class="no-profiles">
          <p>No pet profiles yet</p>
          <p>Create your first pet profile to get started!</p>
        </div>
        <div v-else v-for="profile in petProfiles" :key="profile.petId" class="profile-card">
          <div class="profile-header">
            <h3>{{ profile.petName }}</h3>
            <div class="profile-actions">
              <button @click="editProfile(profile)" class="edit-btn">Edit</button>
              <button @click="deleteProfile(profile.petId)" class="delete-btn">Delete</button>
            </div>
          </div>
          <div class="profile-info">
            <p><strong>Type:</strong> {{ profile.petType }}</p>
            <p><strong>Breed:</strong> {{ profile.breed || 'Not specified' }}</p>
            <p v-if="profile.description"><strong>Description:</strong> {{ profile.description }}</p>
          </div>
        </div>
      </div>
    </main>

    <!-- 创建/编辑模态框 -->
    <div v-if="showCreateModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <h2>{{ editingProfile ? 'Edit Pet Profile' : 'Create Pet Profile' }}</h2>
        <form @submit.prevent="handleSubmit">
          <div class="form-group">
            <label>Pet Name *</label>
            <input type="text" v-model="formData.petName" required>
          </div>
          <div class="form-group">
            <label>Pet Type *</label>
            <select v-model="formData.petType" required>
              <option value="">Please Select</option>
              <option value="cat">Cat</option>
              <option value="dog">Dog</option>
              <option value="other">Other</option>
            </select>
          </div>
          <div class="form-group">
            <label>Breed</label>
            <input type="text" v-model="formData.breed">
          </div>
          <div class="form-group">
            <label>Description</label>
            <textarea v-model="formData.description" rows="3"></textarea>
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
  getUserPetProfiles,
  createPetProfile,
  updatePetProfile,
  deletePetProfile
} from '@/services/profileService'

const router = useRouter()
const authStore = useAuthStore()

const petProfiles = ref([])
const loading = ref(true)
const showCreateModal = ref(false)
const submitting = ref(false)
const editingProfile = ref(null)

const formData = ref({
  petName: '',
  petType: '',
  breed: '',
  age: '',
  weight: '',
  description: ''
})

const loadProfiles = async () => {
  loading.value = true
  try {
    const response = await getUserPetProfiles(authStore.token, authStore.user?.username)
    if (response.success) {
      petProfiles.value = response.profiles || []
    }
  } catch (error) {
    console.error('Error loading pet profiles:', error)
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  submitting.value = true
  try {
    // 确保username一定有值，从localStorage获取作为备用
    const currentUsername = authStore.user?.username || localStorage.getItem('current_username')

    const data = {
      username: currentUsername,
      ...formData.value,
      age: formData.value.age ? parseInt(formData.value.age) : null,
      weight: formData.value.weight ? parseFloat(formData.value.weight) : null
    }

    let response
    if (editingProfile.value) {
      response = await updatePetProfile(editingProfile.value.petId, data, authStore.token)
    } else {
      response = await createPetProfile(data, authStore.token)
    }

    if (response.success) {
      closeModal()
      await loadProfiles()
    } else {
      alert('Failed to save pet profile: ' + response.message)
    }
  } catch (error) {
    console.error('Error saving pet profile:', error)
    alert('Failed to save pet profile. Please try again.')
  } finally {
    submitting.value = false
  }
}

const editProfile = (profile) => {
  editingProfile.value = profile
  formData.value = {
    petName: profile.petName,
    petType: profile.petType,
    breed: profile.breed || '',
    age: profile.age?.toString() || '',
    weight: profile.weight?.toString() || '',
    description: profile.description || ''
  }
  showCreateModal.value = true
}

const deleteProfile = async (profileId) => {
  if (!confirm('Are you sure you want to delete this pet profile?')) {
    return
  }

  try {
    const response = await deletePetProfile(profileId, authStore.token)
    if (response.success) {
      await loadProfiles()
    } else {
      alert('Failed to delete pet profile: ' + response.message)
    }
  } catch (error) {
    console.error('Error deleting pet profile:', error)
    alert('Failed to delete pet profile. Please try again.')
  }
}

const closeModal = () => {
  showCreateModal.value = false
  editingProfile.value = null
  formData.value = {
    petName: '',
    petType: '',
    breed: '',
    age: '',
    weight: '',
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
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
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
</style>