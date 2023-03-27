//app.js
const QQMapWX = require('./locale/qqmap-wx-jssdk.min.js');
let qqmapsdk;
qqmapsdk = new QQMapWX({
  key: 'LC2BZ-25GCD-ZUP4O-H3U4A-53J5O-IWFF5'
});

App({
  onLaunch: function () {
    this.initPage();
    this.getCapsule();
  },
  initPage() {
    // 用户是否授权地理位置或者定位
    // 获取用户授权信息信息,防止重复出现授权弹框
    wx.getSetting({
      success: res => {
        //已有权限直接获得信息，否则出现授权弹框
        if (res.authSetting['scope.userLocation']) {
          this.getUserLocation()
        } else{
          this.getUserLocation()
        }
      }
    })
  },
  // 获取胶囊信息
  getCapsule() {
    let menuButtonObject = wx.getMenuButtonBoundingClientRect();
    wx.getSystemInfo({
    success: res => {
     //导航高度
     let statusBarHeight = res.statusBarHeight,
       navTop = menuButtonObject.top,
       navObjWid = res.windowWidth - menuButtonObject.right + menuButtonObject.width, // 胶囊按钮与右侧的距离 = windowWidth - right+胶囊宽度
       navHeight = statusBarHeight + menuButtonObject.height + (menuButtonObject.top - statusBarHeight) * 2;
     this.globalData.navHeight = navHeight; //导航栏总体高度
     this.globalData.navTop = navTop; //胶囊距离顶部距离
     this.globalData.navObj = menuButtonObject.height; //胶囊高度
     this.globalData.navObjWid = navObjWid; //胶囊宽度(包括右边距离)
     console.log(navHeight,navTop,menuButtonObject.height,navObjWid)
    },
    fail(err) {
     console.log(err);
    }  
    })
  },
  //获取用户的位置信息
  getUserLocation() {
    wx.getFuzzyLocation({
      //成功授权
      success: (res) => {
        console.log(res);
        const latitude = res.latitude;
        const longitude = res.longitude;
        // 使用腾讯地图接口将位置坐标转出成名称（为什么弹框出出现两次？）
        qqmapsdk.reverseGeocoder({
          location: {   //文档说location默认为当前位置可以省略，但是还是要手动加上，否则弹框会出现两次，手机端则出现问题
            latitude,
            longitude
          },
          success: (res) => {
            const cityFullname = res.result.address_component.city;
            const cityInfo = {
              latitude,
              longitude,
              cityName: cityFullname.substring(0, cityFullname.length - 1),
              status: 1
            }
            this.globalData.userLocation = { ...cityInfo }   //浅拷贝对象
            this.globalData.selectCity = { ...cityInfo } //浅拷贝对象
            // 由于 getUserInfo 是网络请求，可能会在 Page.onLoad 之后才返回，所以此处加入 callback 以防止这种情况
            if (this.userLocationReadyCallback) {
              this.userLocationReadyCallback()
            }
          }
        })
      },
      fail: () => {
        this.globalData.userLocation = { status: 0 }
        //防止当弹框出现后，用户长时间不选择，
        if (this.userLocationReadyCallback) {
          this.userLocationReadyCallback()
        }
      }
    })
  },
  globalData: {
    userInfo: null,//用户信息
    userLocation: null, //用户的位置信息
    selectCity: null, //用户切换的城市
    isRefresh: false,
    url: 'https://liyuxiang.com.cn',
    // url: 'http://127.0.0.1:9999',
    // 胶囊信息
    navHeight:null,
    navTop:null,
    navObj:null,
    navObjWid: null,
  }
})