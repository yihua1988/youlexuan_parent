//用户表控制层 
app.controller('userController', function ($scope, $controller, userService) {

    $controller('baseController', {$scope: $scope});//继承

    //发送验证码
    /*
        验证->验证一下是机器操作还是人工操作
        验证码发送 需要进行时间控制  60秒
     */
    $scope.sendCode = function () {
        //判断手机号码是否为空
        if ($scope.entity.phone == null || $scope.entity.phone == "") {
            alert("请输入手机号码");
            return;
        }
        userService.sendCode($scope.entity.phone).success(
            function (response) {
                alert(response.message);
            }
        );
    };


    $scope.entity = {};

    $scope.reg = function () {
        if ($scope.entity.username == '' || $scope.entity.username == null) {
            alert("请输入要注册的用户名");
            return;
        }
        if ($scope.entity.password == '' || $scope.entity.password == null) {
            alert("请输入要注册的密码");
            return;
        }
        if ($scope.entity.password != $scope.password) {
            alert("对不起两次输入的密码不一致");
            return;
        }
        if ($scope.entity.phone == '' || $scope.entity.phone == null) {
            alert("请输入手机号码");
            return;
        }
        if ($scope.entity.phone.length != 11) {
            alert("请输入合法的手机号码");
            return;
        }
        if ($scope.smscode == '' || $scope.smscode == null || $scope.smscode.length !=6){
            alert("验证码不合法");
            return;
        }
        userService.add($scope.entity, $scope.smscode).success(
            function (response) {
                if (response.success) {
                    alert("注册成功");

                } else {
                    alert(response.message);
                }
            }
        );
    };

});	