// 帖子相关的API服务

// 获取所有帖子
export async function getAllPosts(token) {
  const response = await fetch('http://localhost:8080/api/posts', {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
  })

  const result = await response.json()

  // 将PostController的直接响应转换为前端期望的格式
  if (response.ok) {
    return {
      success: true,
      posts: result  // result是Post对象数组
    }
  } else {
    return {
      success: false,
      posts: [],
      message: '获取帖子失败'
    }
  }
}

// 根据类型获取帖子（seeking/offering）
export async function getPostsByType(type, token) {
  const response = await fetch(`http://localhost:8080/api/posts/role/${type}/open`, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
  })

  const result = await response.json()

  // 将PostController的直接响应转换为前端期望的格式
  if (response.ok) {
    return {
      success: true,
      posts: result  // result是Post对象数组
    }
  } else {
    return {
      success: false,
      posts: [],
      message: '获取帖子失败'
    }
  }
}

// 获取开放状态的帖子（用于首页）
export async function getOpenPosts(type, petType, serviceType, district, token) {
  let url = 'http://localhost:8080/api/posts'

  // 构建查询参数
  const params = new URLSearchParams()
  if (type) params.append('type', type)
  if (petType) params.append('petType', petType)
  if (serviceType) params.append('serviceType', serviceType)
  if (district) params.append('district', district)

  // 如果有任何筛选参数，使用筛选API
  if (params.toString()) {
    url += `/open/search?${params.toString()}`
  } else {
    // 获取所有开放状态的帖子
    url = 'http://localhost:8080/api/posts/open'
  }

  const response = await fetch(url, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
  })

  const result = await response.json()

  // 将PostController的直接响应转换为前端期望的格式
  if (response.ok) {
    return {
      success: true,
      posts: result  // result是Post对象数组
    }
  } else {
    return {
      success: false,
      posts: [],
      message: '获取帖子失败'
    }
  }
}

// 创建新帖子
export async function createPost(postData, token) {
  const response = await fetch('http://localhost:8080/api/posts', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(postData),
  })

  const result = await response.json()

  // 将PostController的直接响应转换为前端期望的格式
  if (response.status === 201) {
    return {
      success: true,
      message: '帖子创建成功',
      post: result
    }
  } else {
    return {
      success: false,
      message: '帖子创建失败'
    }
  }
}

// 获取用户的动物档案
export async function getUserPetProfiles(token, username) {
  const response = await fetch(`http://localhost:8080/api/pet-profiles/user/${username}`, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
  })

  const result = await response.json()

  // 将Controller的直接响应转换为前端期望的格式
  if (response.ok) {
    return {
      success: true,
      profiles: result  // result是PetProfile对象数组
    }
  } else {
    return {
      success: false,
      profiles: [],
      message: '获取动物档案失败'
    }
  }
}

// 获取用户的服务档案
export async function getUserServiceProfiles(token, username) {
  const response = await fetch(`http://localhost:8080/api/service-profiles/user/${username}`, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
  })

  const result = await response.json()

  // 将Controller的直接响应转换为前端期望的格式
  if (response.ok) {
    return {
      success: true,
      profiles: result  // result是ServiceProfile对象数组
    }
  } else {
    return {
      success: false,
      profiles: [],
      message: '获取服务档案失败'
    }
  }
}

// 更新帖子
export async function updatePost(postId, postData, token) {
  const response = await fetch(`http://localhost:8080/api/posts/${postId}`, {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(postData),
  })

  const result = await response.json()

  // 将PostController的直接响应转换为前端期望的格式
  if (response.ok) {
    return {
      success: true,
      message: '帖子更新成功',
      post: result
    }
  } else {
    return {
      success: false,
      message: '帖子更新失败'
    }
  }
}

// 删除帖子
export async function deletePost(postId, token) {
  const response = await fetch(`http://localhost:8080/api/posts/${postId}`, {
    method: 'DELETE',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
  })

  if (response.ok) {
    return {
      success: true,
      message: '帖子删除成功'
    }
  } else {
    return {
      success: false,
      message: '帖子删除失败'
    }
  }
}

// 获取用户的所有帖子（包括关闭状态）- 用于管理页面
export async function getAllUserPosts(type, token) {
  const response = await fetch(`http://localhost:8080/api/posts/role/${type}`, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
  })

  const result = await response.json()

  // 将PostController的直接响应转换为前端期望的格式
  if (response.ok) {
    return {
      success: true,
      posts: result  // result是Post对象数组
    }
  } else {
    return {
      success: false,
      posts: [],
      message: '获取帖子失败'
    }
  }
}

// 更新帖子状态
export async function updatePostStatus(postId, status, token) {
  const response = await fetch(`http://localhost:8080/api/posts/${postId}/status`, {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ status: status }),
  })

  const result = await response.json()

  // 将PostController的直接响应转换为前端期望的格式
  if (response.ok) {
    return {
      success: true,
      message: '状态更新成功',
      post: result
    }
  } else {
    return {
      success: false,
      message: '状态更新失败'
    }
  }
}