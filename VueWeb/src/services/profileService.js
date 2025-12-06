// 档案相关的API服务

// 宠物档案相关
export async function getUserPetProfiles(token) {
  const response = await fetch('http://localhost:8080/api/pet-profiles', {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
  })

  const result = await response.json()

  if (response.ok) {
    return {
      success: true,
      profiles: result
    }
  } else {
    return {
      success: false,
      profiles: [],
      message: 'Failed to get pet profiles'
    }
  }
}

export async function createPetProfile(profileData, token) {
  const response = await fetch('http://localhost:8080/api/pet-profiles', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(profileData),
  })

  const result = await response.json()

  if (response.status === 201) {
    return {
      success: true,
      profile: result
    }
  } else {
    return {
      success: false,
      message: 'Failed to create pet profile'
    }
  }
}

export async function updatePetProfile(profileId, profileData, token) {
  const response = await fetch(`http://localhost:8080/api/pet-profiles/${profileId}`, {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(profileData),
  })

  const result = await response.json()

  if (response.ok) {
    return {
      success: true,
      profile: result
    }
  } else {
    return {
      success: false,
      message: 'Failed to update pet profile'
    }
  }
}

export async function deletePetProfile(profileId, token) {
  const response = await fetch(`http://localhost:8080/api/pet-profiles/${profileId}`, {
    method: 'DELETE',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
  })

  if (response.ok) {
    return {
      success: true
    }
  } else {
    return {
      success: false,
      message: 'Failed to delete pet profile'
    }
  }
}

// 服务档案相关
export async function getUserServiceProfiles(token) {
  const response = await fetch('http://localhost:8080/api/service-profiles', {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
  })

  const result = await response.json()

  if (response.ok) {
    return {
      success: true,
      profiles: result
    }
  } else {
    return {
      success: false,
      profiles: [],
      message: 'Failed to get service profiles'
    }
  }
}

export async function createServiceProfile(profileData, token) {
  const response = await fetch('http://localhost:8080/api/service-profiles', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(profileData),
  })

  const result = await response.json()

  if (response.status === 201) {
    return {
      success: true,
      profile: result
    }
  } else {
    return {
      success: false,
      message: 'Failed to create service profile'
    }
  }
}

export async function updateServiceProfile(profileId, profileData, token) {
  const response = await fetch(`http://localhost:8080/api/service-profiles/${profileId}`, {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(profileData),
  })

  const result = await response.json()

  if (response.ok) {
    return {
      success: true,
      profile: result
    }
  } else {
    return {
      success: false,
      message: 'Failed to update service profile'
    }
  }
}

export async function deleteServiceProfile(profileId, token) {
  const response = await fetch(`http://localhost:8080/api/service-profiles/${profileId}`, {
    method: 'DELETE',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
  })

  if (response.ok) {
    return {
      success: true
    }
  } else {
    return {
      success: false,
      message: 'Failed to delete service profile'
    }
  }
}