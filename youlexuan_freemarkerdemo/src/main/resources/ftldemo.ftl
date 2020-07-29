<html>
<head>
    <meta charset="utf-8">
    <title>FtlDemo</title>
</head>
<body>
<#assign username="有就业校友">
欢迎用户:${username}登录系统
<#assign info={"mobile":"1891882881",'address':'北京市朝阳区五方桥'} >
电话：${info.mobile}  地址：${info.address}
<#include "head.ftl">
<#assign success=true>
<#if success=true>
  你已通过实名认证
<#else>
  你未通过实名认证
</#if>
<#list goodsList as goods>
    ${goods_index+1} 商品名称： ${goods.name} 价格：${goods.price}<br>
</#list>
</body>
</html>