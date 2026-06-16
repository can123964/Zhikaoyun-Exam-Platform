import axios from 'axios'
import { router } from '@/router'
import { Message, Loading } from 'element-ui'

// 创建 axios 实例
const service = axios.create({
  timeout: 30000
})

// 请求拦截器：自动附加 JWT Token
service.interceptors.request.use(config => {
  const token = localStorage.getItem('exam_token')
  if (token) {
    config.headers['Authorization'] = 'Bearer ' + token
  }
  config.headers['request-ajax'] = true
  return config
}, error => {
  return Promise.reject(error)
})

// 响应拦截器：统一处理响应和错误
service.interceptors.response.use(response => {
  const res = response.data
  if (res.code === 200) {
    return res
  } else if (res.code === 401) {
    localStorage.removeItem('exam_token')
    localStorage.removeItem('exam_username')
    localStorage.removeItem('exam_realname')
    localStorage.removeItem('exam_role')
    router.push({ path: '/login' }).catch(() => {})
    return Promise.reject(res)
  } else if (res.code === 403) {
    Message.error('无权限访问')
    return Promise.reject(res)
  } else {
    Message.error(res.msg || '请求失败')
    return Promise.reject(res)
  }
}, error => {
  if (error.response) {
    const status = error.response.status
    if (status === 401) {
      localStorage.removeItem('exam_token')
      localStorage.removeItem('exam_username')
      localStorage.removeItem('exam_realname')
      localStorage.removeItem('exam_role')
      router.push({ path: '/login' }).catch(() => {})
      return Promise.reject(error)
    } else if (status === 403) {
      Message.error('无权限访问')
      return Promise.reject(error)
    }
  }
  Message.error(error.message || '网络错误')
  return Promise.reject(error)
})

const request = function (loadtip, config) {
  let loading
  if (loadtip) {
    loading = Loading.service({
      lock: false,
      text: '正在加载中…',
      spinner: 'el-icon-loading',
      background: 'rgba(0, 0, 0, 0.5)'
    })
  }
  return service(config).then(res => {
    if (loadtip && loading) loading.close()
    return res
  }).catch(e => {
    if (loadtip && loading) loading.close()
    return Promise.reject(e)
  })
}

const post = function (url, params) {
  return request(false, {
    url: url,
    method: 'post',
    data: params,
    headers: { 'Content-Type': 'application/json' }
  })
}

const postWithLoadTip = function (url, params) {
  return request(true, {
    url: url,
    method: 'post',
    data: params,
    headers: { 'Content-Type': 'application/json' }
  })
}

const get = function (url, params) {
  return request(false, {
    url: url,
    method: 'get',
    params: params
  })
}

const put = function (url, params) {
  return request(false, {
    url: url,
    method: 'put',
    data: params,
    headers: { 'Content-Type': 'application/json' }
  })
}

const del = function (url, params) {
  return request(false, {
    url: url,
    method: 'delete',
    params: params
  })
}

export {
  post,
  postWithLoadTip,
  get,
  put,
  del
}
