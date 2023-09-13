// pages/subPages/movie-wishmovie/wishmovie.js
Page({
    /**
     * 页面的初始数据
     */
    data: {
        itemsShow: []
    },
    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
        const list = JSON.parse(options.itemsShow)
        this.initData(list)
    },
    initData(list) {
        for (let i = 0; i < list.length; i++) {
            list[i].version = list[i].version && list[i].version.split(' ').map(item => {
                return item.toUpperCase().replace('V', '')
            })
        }
        console.log(list[0].version);
        this.setData({
            itemsShow: list
        })
    },
    toMovieDetail(event) {
        wx.navigateTo({
          url: `../../subPages/movie-detail/movie-detail?movieId=${event.currentTarget.dataset.movieid}&showst=${event.currentTarget.dataset.showst}`
        })
    }
})