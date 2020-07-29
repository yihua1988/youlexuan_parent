//控制层
app.controller('goodsController', function ($scope, $location, $controller, goodsService, uploadService, itemCatService, typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承

    /**
     * 上传图片
     */
    // 业务问题和协议问题
    $scope.uploadFile = function () {
        uploadService.uploadFile().success(function (response) {
            if (response.success) {//如果上传成功，取出url
                $scope.image_entity.url = response.message;//设置文件地址
            } else {
                alert(response.message);
            }
        }).error(function () {
            alert("上传发生错误");
        });
    };

    //定义页面实体结构
    $scope.entity = {goods: {}, goodsDesc: {itemImages: []}};

    //添加图片列表
    $scope.add_image_entity = function () {
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    };

    //列表中移除图片
    $scope.remove_image_entity = function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index, 1);
    };

    //读取一级分类
    $scope.selectItemCat1List = function () {
        itemCatService.findByParentId(0).success(
            function (response) {
                $scope.itemCat1List = response;
            }
        );
    };

    //读取二级分类
    $scope.$watch('entity.goods.category1Id', function (newValue, oldValue) {
        //判断一级分类有选择具体分类值，在去获取二级分类
        if (newValue) {
            //根据选择的值，查询二级分类
            itemCatService.findByParentId(newValue).success(
                function (response) {
                    $scope.itemCat2List = response;
                    $scope.itemCat3List = [];
                    $scope.typeTemplate.brandIds = [];
                }
            );
        }
    });
    //读取三级分类
    $scope.$watch('entity.goods.category2Id', function (newValue, oldValue) {
        //判断一级分类有选择具体分类值，在去获取二级分类
        if (newValue) {
            //根据选择的值，查询二级分类
            itemCatService.findByParentId(newValue).success(
                function (response) {
                    $scope.itemCat3List = response;
                    $scope.typeTemplate.brandIds = [];
                }
            );
        }
    });

    $scope.$watch('entity.goods.category3Id', function (newValue, oldValue) {
        //判断三级分类被选中，在去获取更新模板id
        if (newValue) {
            itemCatService.findOne(newValue).success(
                function (response) {
                    $scope.entity.goods.typeTemplateId = response.typeId; //更新模板ID
                }
            );
        }
    });

    //模板ID选择后  更新品牌列表
    $scope.$watch('entity.goods.typeTemplateId', function (newValue, oldValue) {
        if (newValue) {
            typeTemplateService.findOne(newValue).success(
                function (response) {
                    //获取类型模板
                    $scope.typeTemplate = response;
                    //品牌列表
                    $scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);
                    //扩展属性
                    // $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);
                    //扩展属性
                    if($location.search()['id']==null){
                        $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);
                    }
                }
            );
            //查询规格列表
            typeTemplateService.findSpecList(newValue).success(
                function (response) {
                    $scope.specList = response;
                }
            );
        }
    });

    $scope.entity = {goodsDesc: {itemImages: [], specificationItems: []}};

    $scope.updateSpecAttribute = function ($event, name, value) {
        //搜索规格选项，看指定规格是否存在  name规格名称 value 规格选项
        var object = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems, 'attributeName', name);
        //如果规格存在
        if (object != null) {
            //判断复选框选中状态
            if ($event.target.checked) {
                //复选框选中，把对应的规格选项值插入当前规格对应的规格选项数组
                object.attributeValue.push(value);
            } else {
                //复选框取消勾选 移除选项
                object.attributeValue.splice(object.attributeValue.indexOf(value), 1);
                //如果选项都取消了，将此条记录移除
                if (object.attributeValue.length == 0) {
                    $scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(object), 1);
                }
            }
        } else {
            //首次选中某个规格，添加规格及对应选中的规格值
            $scope.entity.goodsDesc.specificationItems.push({"attributeName": name, "attributeValue": [value]});
        }
    };

    //创建SKU列表
    $scope.createItemList = function () {
        //spec 存储sku对应的规格
        $scope.entity.itemList = [{spec: {}, price: 0, num: 99999, status: '0', isDefault: '0'}];//初始
        //定义变量 items指向 用户选中规格集合
        var items = $scope.entity.goodsDesc.specificationItems;
        //遍历用户选中规格集合
        for (var i = 0; i < items.length; i++) {
            //编写增加sku规格方法addColumn 参数1:sku规格列表  参数2:规格名称  参数3:规格选项
            $scope.entity.itemList = addColumn($scope.entity.itemList, items[i].attributeName, items[i].attributeValue);
        }
    };

    //添加列值
    addColumn = function (list, attributeName, attributeValue) {
        var newList = [];//新的集合
        //遍历sku规格列表
        for (var i = 0; i < list.length; i++) {
            //读取每行sku数据，赋值给遍历oldRow
            var oldRow = list[i];
            //遍历规格选项
            for (var j = 0; j < attributeValue.length; j++) {
                //深克隆当前行sku数据为 newRow
                var newRow = JSON.parse(JSON.stringify(oldRow));//深克隆
                //在新行扩展列（列名是规格名称），给列赋值（规格选项值）
                newRow.spec[attributeName] = attributeValue[j];
                //保存新sku行到sku新集合
                newList.push(newRow);
            }
        }
        return newList;
    };

    //商品状态
    //服务器返回的状态信息分别是 0  1 2 3  对应的也分别是下面数组的内容
    $scope.status = ['未审核', '已审核', '审核未通过', '关闭'];

    //商品分类列表
    $scope.itemCatList = [];

    //查询实体
    $scope.findOne = function () {
        //获取参数值
        var id = $location.search()['id'];

        if (id == null) {
            return;
        }
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
                // 将富文本的内容设置到编辑器当中
                editor.html($scope.entity.goodsDesc.introduction);
                //显示图片列表
                $scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);
                //显示扩展属性
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems);
                //规格
                $scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems);

                //SKU列表规格列转换
                for( var i=0;i<$scope.entity.itemList.length;i++ ){
                    $scope.entity.itemList[i].spec = JSON.parse( $scope.entity.itemList[i].spec);
                }

            }
        );
    };

    //根据规格名称和选项名称返回是否被勾选
    $scope.checkAttributeValue = function(specName,optionName){
        var items= $scope.entity.goodsDesc.specificationItems;
        var object= $scope.searchObjectByKey(items,'attributeName',specName);
        if(object==null){
            return false;
        }else{
            if(object.attributeValue.indexOf(optionName)>=0){
                return true;
            }else{
                return false;
            }
        }
    }



    //加载商品分类列表
    $scope.findItemCatList = function () {
        itemCatService.findAll().success(
            function (response) {
                for (var i = 0; i < response.length; i++) {
                    $scope.itemCatList[response[i].id] = response[i].name;
                }
            }
        );
    };


    //读取列表数据绑定到表单中
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    // $scope.findOne = function (id) {
    //     goodsService.findOne(id).success(
    //         function (response) {
    //             $scope.entity = response;
    //         }
    //     );
    // }

    //保存
    $scope.save = function () {
        //服务层对象
        var serviceObject;
        //如果有ID
        //增加
        $scope.entity.goodsDesc.introduction = editor.html();

        if ($scope.entity.id != null) {
            //修改
            serviceObject = goodsService.update($scope.entity);
        } else {
            //新增
            serviceObject = goodsService.add($scope.entity);
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    alert('保存成功');
                    $scope.entity = {goodsDesc: {itemImages: [], specificationItems: []}};
                    //清空富文本编辑器
                    editor.html('');
                    //跳转到商品列表页
                    location.href="goods.html";
                } else {
                    alert(response.message);
                }
            }
        );
    }


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                //更新总记录数
                $scope.paginationConf.totalItems = response.total;
            }
        );
    }

});