const app = getApp();
Page({
    data: {
        avatarUrl: '/image/user/avatar.png',
        username: '点击登录',
        hasUserInfo: false,
        // 想看的
        wishMovie: [],
        wishMovieTotal: 0,
        // 看过的
        watched: 0,
        wait: 0,
        avatar:''
    },
    // 分享
    onShareAppMessage(options) {
        var that = this;
        var shareObj = {
            title: "印象电影院", 
            imageUrl: '',     //自定义图片路径，可以是本地文件路径、代码包文件路径或者网络图片路径，支持PNG及JPG，不传入 imageUrl 则使用默认截图。显示图片长宽比是 5:4
            success: function(res){
            　　// 转发成功之后的回调
            　　if(res.errMsg == 'shareAppMessage:ok'){
                    
            　　}
            },
            fail: function(){
            　　// 转发失败之后的回调
            　　if(res.errMsg == 'shareAppMessage:fail cancel'){
            　　　　// 用户取消转发
            　　}else if(res.errMsg == 'shareAppMessage:fail'){
            　　    // 转发失败，其中 detail message 为详细失败信息
            　　}
            },
            complete: function(){
                // 转发结束之后的回调（转发成不成功都会执行）
            }
        };
        return shareObj;
    },
    onLoad: function () {
        this.userAuthorized();
    },
    onShow: function () {
        this.userAuthorized();
    },
    // 事件
    toWishMovie(event){
        let that = this;
        if (this.data.hasUserInfo) {
            
        }
    },
    toMovieOrder(event) {
        if (this.data.hasUserInfo) {
            wx.navigateTo({
                url: `../subPages/movie-order/movie-order`
            })
        }
    },
    // 获取待评价的电影
    getNotCommentMovie() {
        var _this = this;
        const userInfo = wx.getStorageSync('userInfo')
        wx.request({
            url: app.globalData.url + '/user/getNotCommentMovie',
            method: 'GET',
            header: {
                'token': userInfo.data.token
            },
            success(res) {
                console.log(res);
                if (res.data.state === 202) {
                    _this.setData({
                        hasUserInfo: false
                    })
                } else {
                    _this.setData({
                        hasUserInfo: true,
                        wait: res.data.data.notCommentMovie,
                        watched: res.data.data.orderTotal
                    })
                }
            }
        })
    },
    // 获取想看的电影 此接口有bug
    getWishMovie() {
        var _this = this;
        const userInfo = wx.getStorageSync('userInfo')
        wx.request({
            url: app.globalData.url + '/user/wishMovie',
            method: 'GET',
            header: {
                'token': userInfo.data.token
            },
            success(res) {
                if (res.data.state === 202) {
                    _this.setData({
                        hasUserInfo: false
                    })
                } else {
                    _this.setData({
                        hasUserInfo: true,
                        wishMovie: res.data.data.beanList,
                        wishMovieTotal: res.data.data.tr
                    })
                }
            }
        })
    },
    // 获取用户的权限
    userAuthorized() {
        var _this = this;
        const userInfo = wx.getStorageSync('userInfo')
        if (userInfo) {
            //存在则判断服务端session是否过期
            wx.request({
                url: app.globalData.url + '/user/isAuth',
                method: 'GET',
                header: {
                    'token': userInfo.data.token
                },
                success(res) {
                    console.log(res);
                    if (res.data.state == 202) {
                        _this.setData({
                            hasUserInfo: false
                        });
                    } else {
                        _this.setData({
                            hasUserInfo: true,
                        });
                        _this.getWishMovie()
                        _this.getNotCommentMovie()
                    }
                }
            })
        } else {
            _this.setData({
                hasUserInfo: false
            });
        }
    },
    // 用户登录
    onGetUserInfo(event) {
        var that = this;
        const userInfo = event.detail.userInfo
        if (userInfo) {
            wx.login({
                success: function (res_1) {
                    if (res_1.code) {
                        wx.getSetting({
                            success(res_2) {
                                if (res_2.authSetting['scope.userInfo']) {
                                    wx.getUserInfo({
                                        success: function (res_3) {
                                            wx.request({
                                                url: app.globalData.url + '/user/wxLogin',
                                                method: 'POST',
                                                header: {
                                                    'content-type': 'application/x-www-form-urlencoded'
                                                },
                                                data: {
                                                    code: res_1.code,
                                                    nickName: res_3.userInfo.nickName,
                                                    avatarUrl: res_3.userInfo.avatarUrl,
                                                    gender: res_3.userInfo.gender
                                                },
                                                success: function (res) {
                                                    const userInfo = res.data
                                                    wx.setStorage({
                                                        key: 'userInfo',
                                                        data: userInfo
                                                    })
                                                    that.setData({
                                                        hasUserInfo: true
                                                    })
                                                    app.globalData.userInfo = res_3.userInfo
                                                    that.getWishMovie()
                                                    that.getNotCommentMovie()
                                                }
                                            })
                                        }
                                    })
                                }
                            }
                        })
                    }
                }
            })
        }
    },
    // chooseavatar(e){
    //     wx.request({
    //       url: 'url',
    //     })
    //     this.setData({
    //         avatarUrl: e.detail.avatarUrl
    //     })
    // }
})