//购物车控制层
app.controller('cartController', function ($scope, cartService) {
    //查询购物车列表
    $scope.findCartList = function () {
        alert("ss");
        cartService.findCartList().success(
            function (response) {
                alert(response);
                $scope.cartList = response;
                $scope.totalValue = cartService.sum($scope.cartList);
            }
        );
    };
    //添加商品到购物车
    $scope.addGoodsToCartList = function (itemId, num) {
        cartService.addGoodsToCartList(itemId, num).success(
            function (response) {
                if (response.success) {
                    //刷新列表
                    $scope.findCartList();
                } else {
                    //弹出错误提示
                    alert(response.message);
                }
            }
        );
    };

});
