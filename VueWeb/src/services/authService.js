// 这个文件负责与后端 API 通信

// 用户登录
export async function login(username, password) {
  const response = await fetch('http://localhost:8080/api/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ username, password }),
  })

  return response.json()
}

// 用户注册
export async function register(userData) {
  const response = await fetch('http://localhost:8080/api/users', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(userData),
  })

  const result = await response.json()

  // 将UserController的响应格式转换为前端期望的格式
  if (response.status === 201) {
    return {
      success: true,
      message: 'Registration successful',
      user: result
    }
  } else if (response.status === 409) {
    return {
      success: false,
      message: 'Username already exists'
    }
  } else {
    return {
      success: false,
      message: 'Registration failed, please try again'
    }
  }
}

// 获取用户信息
export async function getUserInfo(token, username) {
  if (!username) {
    return {
      success: false,
      message: 'Username cannot be empty'
    }
  }

  const response = await fetch(`http://localhost:8080/api/users/${username}`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
    },
  })

  const result = await response.json()

  // 将UserController的响应格式转换为前端期望的格式
  if (response.status === 200) {
    return {
      success: true,
      user: result
    }
  } else if (response.status === 404) {
    return {
      success: false,
      message: 'User does not exist'
    }
  } else {
    return {
      success: false,
      message: 'Failed to get user information'
    }
  }
}

