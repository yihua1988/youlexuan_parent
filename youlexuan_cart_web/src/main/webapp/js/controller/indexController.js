//首页控制器
app.controller('indexController',function($scope,loginService){
    // 触发service中获取用户名的函数，拿当前登录到服务器的用户名
    $scope.showName=function(){
        loginService.showName().success(
            function(response){
                $scope.loginName = response.loginName;
            }
        );
    }
});
