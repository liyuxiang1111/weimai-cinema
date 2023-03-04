import axios from 'axios'
import cookies from 'vue-cookies'
axios.defaults.withCredentials = true
axios.defaults.baseURL = 'http://127.0.0.1:9999'
// axios.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded'
axios.defaults.headers.post['Content-Type'] = 'application/json;charset=utf-8'
axios.defaults.headers.post['Access-Control-Allow-Credentials'] = true
axios.defaults.headers.post['token'] = localStorage.getItem('token')
axios.defaults.headers.get['token'] = localStorage.getItem('token')
console.log('ok')
console.log(cookies.get('JSESSIONID'))

//封装ajax
export default function ajax(url = '', params = {}, type = 'GET') {
  let promise
  return new Promise((resolve, reject) => {
    //1.判断请求方式
    if ('GET' === type) {
      //1.1拼接字符串
      let str = ''
      Object.keys(params).forEach((value, index) => {
        if (index + 1 === Object.keys(params).length) {
          str += value + '=' + params[value]
        } else {
          str += value + '=' + params[value] + '&'
        }
      })
      //1.2完整路径
      url += '?' + str
      //1.3发送get请求
      promise = axios.get(url)
    } else if ('POST' === type) {
      //1.3发送post请求
      promise = axios.post(url, params)
    }
    //2.返回请求结果
    promise
      .then(response => {
        resolve(response.data)
      })
      .catch(error => {
        reject(error)
      })
  })
}
