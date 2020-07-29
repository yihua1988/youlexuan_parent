app.service("searchService", function ($http) {
    this.search = function (searchMap) {
        // http中get和post的区别是什么?
        // get和post 提交数据的形式和体积存在一定差异
        // 本质上get和post没什么区别，如果你使用post请求，也一样能够达成get请求的效果
        // searchMap 存放在请求体里面
        return $http.post('itemsearch/search.do', searchMap);
    }
});
