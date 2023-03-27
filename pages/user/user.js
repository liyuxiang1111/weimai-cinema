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
        wait: 0
    },
    onShareAppMessage(res) {
        return {
            title: '微票电影',
            path: 'pages/home/home'
        }
    },
    onLoad: function () {
        this.userAuthorized();
    },
    onShow: function () {
        this.userAuthorized();
        this.getWishMovie();
        this.getNotCommentMovie();
    },
    // 事件
    toWishMovie(event){
        if (this.data.hasUserInfo) {
            wx.navigateTo({
                url: `../subPages/movie-wishmovie/wishmovie?itemsShow=${JSON.stringify(this.data.wishMovie)}`
            })
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
    // 获取想看的电影
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
    userAuthorized() {
        var _this = this;
        const userInfo = wx.getStorageSync('userInfo')
        if (userInfo) {
            //存在则判断服务端session是否过期
            console.log("判断服务端session是否过期")
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
                    }
                }
            })
        } else {
            _this.setData({
                hasUserInfo: false
            });
        }
    },
    onGetUserInfo(event) {
        var that = this;
        const userInfo = event.detail.userInfo
        if (userInfo) {
            wx.login({
                success: function (res_1) {
                    wx.getUserInfo({
                        success: function (res_2) {
                            console.log(res_2)
                            wx.request({
                                url: app.globalData.url + '/user/wxLogin',
                                method: 'POST',
                                header: {
                                    'content-type': 'application/x-www-form-urlencoded'
                                },
                                data: {
                                    code: res_1.code,
                                    nickName: res_2.userInfo.nickName,
                                    avatarUrl: res_2.userInfo.avatarUrl,
                                    gender: res_2.userInfo.gender
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
                                    app.globalData.userInfo = res_2.userInfo
                                    that.getWishMovie()
                                    that.getNotCommentMovie()
                                }
                            })
                        }
                    })
                }
            })
        }
    }
})