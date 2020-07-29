app.controller('itemController', function ($scope) {

    //添加商品到购物车
    $scope.addToCart=function(){
        alert('skuid:'+$scope.sku.id);
    };

    //匹配两个对象
    matchObject = function (map1, map2) {
        for (var k in map1) {
            if (map1[k] != map2[k]) {
                return false;
            }
        }
        for (var k in map2) {
            if (map2[k] != map1[k]) {
                return false;
            }
        }
        return true;
    };

    //查询SKU
    searchSku = function () {
        for (var i = 0; i < skuList.length; i++) {
            if (matchObject(skuList[i].spec, $scope.specificationItems)) {
                $scope.sku = skuList[i];
                return;
            }
        }
        $scope.sku = {id: 0, title: '--------', price: 0};//如果没有匹配的
        console.log("sku:"+$scope.sku);
    };

    //加载默认SKU
    // 苹果手机 红色  绿色  黑色  64G 128G 256G
    // 1.苹果手机 红色 64G   5400
    // 2.苹果手机 红色 128G   5800
    // 3.苹果手机 红色 256G   6400
    // 4.苹果手机 绿色 64G   5600
    // 5.苹果手机 绿色 128G  6000
    // 6.苹果手机 绿色 256G  6600
    // 7.苹果手机 黑色 64G   5100
    // 8.苹果手机 黑色 128G  5500
    // 9.苹果手机 黑色 256G  6100
    $scope.loadSku = function () {
        $scope.sku = skuList[0];
        $scope.specificationItems = JSON.parse(JSON.stringify($scope.sku.spec));
        console.log("skuList:"+skuList);
    };

    //数量操作
    $scope.addNum = function (x) {
        $scope.num = $scope.num + x;
        if ($scope.num < 1) {
            $scope.num = 1;
        }
    };

    $scope.specificationItems = [];

    //用户选择规格
    $scope.selectSpecification = function (name, value) {
        $scope.specificationItems[name] = value;
        //读取sku
        searchSku();
    };

    //判断某规格选项是否被用户选中
    $scope.isSelected = function (name, value) {
        if ($scope.specificationItems[name] == value) {
            return true;
        } else {
            return false;
        }
    };
});
