// components/topbar.js
var app = getApp();
Component({
    /**
     * 组件的属性列表
     */
    properties: {
        color: {
            type: String,
            value: "#fff"
        },
        city: {
            type: String,
            value: "正在定位..."
        },
        placeholder: {
            type: String,
            value: "查询电影"
        }
    },

    options: {
        addGlobalClass: true
    },
    /**
     * 组件的初始数据
     */
    data: {
        navHeight: app.globalData.navHeight,
        navTop: app.globalData.navTop,
        navObj: app.globalData.navObj,
        navObjWid: app.globalData.navObjWid
    },
    /**
     * 组件的方法列表
     */
    methods: {},
})