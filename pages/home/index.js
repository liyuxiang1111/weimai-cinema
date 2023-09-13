//index.js
//获取应用实例
const app = getApp();
const util = require('../../utils/util.js')
Page( {
  data: {
    itemsShow: [], //热映
    pageNumHost: 0,
    limit: 6,
    hasMoreHost: true,
    hasMoreWait: true,
    pageNumWait: 0,
    itemsWait: [], //待映
    itemsPop: [],
    bannerList: [],
    swiperViewHeight: 0,
    currentTab: 0,
    city: '正在定位...',
    inputShowed: false,
    indicatorDots: true,
    vertical: false,
    autoplay: true,
    interval: 3000,
    duration: 1200,
    scrollTop: 0,
    colors:[],
    color: 0,
    fixColor: "#7d995e",
  },
  swiperchange: function(e) {
    let that = this;
    this.setData({
        color: that.data.colors[e.detail.current]
    })
  },
  onLoad: function() {
    var that = this
    //调用应用实例的方法获取全局数据
    //bannerList
    wx.request({
      url: app.globalData.url +'/home/getBannerList',
      method: 'GET',
      data: {},
      success: function (res) {
        console.log(res);
        res.data.data.forEach(element => {
            that.data.colors.push(element.color)
        });
        that.setData({ bannerList: res.data.data,color: that.data.colors[0] })
      }
    })
    that.cityInit();
    wx.request({
      url: app.globalData.url +'/home/getMoviePop',
      method: 'GET',
      data: {},
      header: {
        'Accept': 'application/json'
      },
      success: function (res) {
        // console.log(res)
        var list = res.data.data;
        for (var i in list) {
          list[i].rt = util.formatDate(new Date(list[i].rt));
        }
        that.setData({ itemsPop: list })
      }
    })
    that.getMovieWait();
    that.getMovieList();
  },
  //监听页面滚动
  onPageScroll:function(e){
    this.setData({
        scrollTop: e.scrollTop
    })
  },
  getMovieList: function() {
    var that = this;
    var pageNumHost = that.data.pageNumHost; // 当前页
    var limit = that.data.limit;
    //playingList
    wx.request({
      url: app.globalData.url +'/home/getMovieList',
      method: 'GET',
      data: {
        pageNum: ++pageNumHost,
        limit: limit
      },
      header: {
        'Accept': 'application/json'
      },
      success: function (res) {
        const itemsShow = that.data.itemsShow.concat(res.data.data.beanList);
        that.setData({
          hasMoreHost: pageNumHost * limit < res.data.data.tr, 
          itemsShow: itemsShow,
          pageNumHost: pageNumHost
        })
        if (that.data.currentTab == 0)
          that.setSwiperHeight();
      }
    })
  },
  getMovieWait: function() {
    var that = this;
    var pageNumWait = that.data.pageNumWait;
    var limit = that.data.limit;
    wx.request({
      url: app.globalData.url +'/home/getMovieWait',
      method: 'GET',
      data: {
        pageNum: ++pageNumWait,
        limit: limit
      },
      success: function (res) {
        const itemsWait = that.data.itemsWait.concat(res.data.data.beanList);
        that.setData({
          hasMoreWait: pageNumWait < res.data.data.tr,
          itemsWait: itemsWait,
          pageNumWait: pageNumWait
        })
        if (that.data.currentTab == 1)
          that.setSwiperHeight();
      }
    })
  },
  onReachBottom: function(){
    if (this.data.currentTab == 0 && this.data.hasMoreHost)
      this.getMovieList();
    else if (this.data.currentTab == 1 && this.data.hasMoreWait)
      this.getMovieWait();
  },
  cityInit:function() {
    if (app.globalData.userLocation) {
      this.setData({
        city: app.globalData.selectCity ? app.globalData.selectCity.cityName : '定位失败'
      })
    } else {
      app.userLocationReadyCallback = () => {
        this.setData({
          city: app.globalData.selectCity ? app.globalData.selectCity.cityName : '定位失败'
        })
      }
    }
  },
  setSwiperHeight: function(){
    var query = wx.createSelectorQuery();
    //选择id getSystemInfoSync 获取系统信息和屏幕长度 根据屏幕比例划分
    var that = this;
    var px1 = 208 / 750 * wx.getSystemInfoSync().windowWidth;
    var px2 = 376 / 750 * wx.getSystemInfoSync().windowWidth;
    query.selectAll('.swiperH').boundingClientRect(function (rect) {
      var itemsLength = 0;
      if (that.data.currentTab == 0){
        // console.log(that.data.itemsShow);
        itemsLength = rect[0].height*that.data.itemsShow.length + px1;
      }else if(that.data.currentTab == 1){
        itemsLength = rect[0].height*that.data.itemsWait.length + px2;
      }
      that.setData({
        swiperViewHeight: itemsLength
      })
    }).exec();
  },
  //滑动切换
  swiperTab: function(e){
    var that = this;
    that.setSwiperHeight();
    wx.pageScrollTo({
      scrollTop: 0
    })
    that.setData({
      currentTab: e.detail.current
    });
  },
  //点击切换
  clickTab: function (e) {
    var that = this;
    if (this.data.currentTab === e.target.dataset.current) {
      return false;
    } else {
      that.setSwiperHeight();
      that.setData({
        currentTab: e.target.dataset.current
      })
    }
  },
  onShow: function () {
    if (app.globalData.selectCity) {
      this.setData({
        city: app.globalData.selectCity.cityName
      })
    }
  }
})
