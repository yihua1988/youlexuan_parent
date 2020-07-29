app.controller('searchController', function ($scope,$location, searchService) {

    //加载查询字符串
    $scope.loadkeywords=function(){
        $scope.searchMap.keywords=  $location.search()['keywords'];
        $scope.search();
    }
    //搜索
    $scope.search = function () {
        console.log($scope.searchMap);
        $scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);
        searchService.search($scope.searchMap).success(
            function (response) {
                //搜索返回的结果
                //宗旨只有一条 就是别携带冗余数据就行
                $scope.resultMap = response;
                //调用
                buildPageLabel();
            }
        );
    };

    //搜索对象 双向绑定
    //keywords 关键字搜索，需要用户手动输入
    //category 选中的分类信息
    //brand 选中的品牌信息
    //spec 选中的规格信息
    $scope.searchMap = {
        'keywords': '', 'category': '', 'brand': '', 'spec': {}, 'price': '',
        'pageNo': 1, 'pageSize': 10, 'sortField': '', 'sort': ''
    };
    //设置排序规则
    $scope.sortSearch=function(sortField,sort){
        $scope.searchMap.sortField=sortField;
        $scope.searchMap.sort=sort;
        $scope.search();
    };
    //添加搜索项
    $scope.addSearchItem = function (key, value) {
        //如果点击的是分类或者是品牌
        if (key == 'category' || key == 'brand' || key == 'price') {
            $scope.searchMap[key] = value;
        } else {
            $scope.searchMap.spec[key] = value;
        }
        //执行搜索
        $scope.search();
    };

    //移除复合搜索条件
    $scope.removeSearchItem = function (key) {
        //如果是分类或品牌
        if (key == "category" || key == "brand" || key == 'price') {
            $scope.searchMap[key] = "";
        } else {
            //否则是规格
            //移除此属性
            delete $scope.searchMap.spec[key];
        }
        //执行搜索
        $scope.search();
    };

    //构建分页标签(totalPages为总页数)
    buildPageLabel = function () {
        //新增分页栏属性 用于循环生成分页插件中分页的页码按钮 有效范围从1 - 最大页码数
        $scope.pageLabel = [];
        //得到总页码
        var maxPageNo = $scope.resultMap.totalPages;
        //开始页码
        var firstPage = 1;
        //截止页码
        var lastPage = maxPageNo;
        //前面有点
        $scope.firstDot = true;
        //后边有点
        $scope.lastDot = true;
        //如果总页数大于5页,显示部分页码
        if (maxPageNo > 5) {
            if ($scope.searchMap.pageNo <= 3) {
                lastPage = 5;
                //前面没点
                $scope.firstDot = false;
            } else if ($scope.searchMap.pageNo >= lastPage - 2) {
                firstPage = maxPageNo - 4;
                $scope.lastDot = false;//后边没点
            } else {
                firstPage = $scope.searchMap.pageNo - 2;
                lastPage = $scope.searchMap.pageNo + 2;
            }
        } else {
            //前面无点
            $scope.firstDot = false;
            //后边无点
            $scope.lastDot = false;
        }

        //循环产生页码标签 合法1 - 37  实际是1-37里面的某五个页码
        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageLabel.push(i);
        }
    };

    //根据页码查询
    $scope.queryByPage = function (pageNo) {
        //页码验证，页码为负数或者页码大于总页码，验证不通过
        if (pageNo < 1 || pageNo > $scope.resultMap.totalPages) {
            return;
        }
        $scope.searchMap.pageNo = pageNo;
        $scope.search();
    };

    //判断当前页为第一页
    $scope.isTopPage = function () {
        if ($scope.searchMap.pageNo == 1) {
            return true;
        } else {
            return false;
        }
    };

    //判断当前页是否未最后一页 该方法会出现totalPage未定义异常,需要定义totalPages
    $scope.resultMap = {"totalPages": 1};
    $scope.isEndPage = function () {
        if ($scope.searchMap.pageNo == $scope.resultMap.totalPages) {
            return true;
        } else {
            return false;
        }
    };

    //判断指定页码是否是当前页
    $scope.isPage = function (p) {
        if (parseInt(p) == parseInt($scope.searchMap.pageNo)) {
            return true;
        } else {
            return false;
        }
    }

    //判断关键字是不是品牌
    $scope.keywordsIsBrand=function(){
        for(var i=0;i<$scope.resultMap.brandList.length;i++){
            if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){//如果包含
                return true;
            }
        }
        return false;
    }
});
