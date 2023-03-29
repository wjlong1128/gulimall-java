1. nacos mysql版本过低 将mysql对应jar包放入nacos目录下面的`plugin/mysql`
2. 跨域要在网关配置 妈的  网关和被代理者要在同一名称空间下
3. 校验注解 如果被校验@Validated有分组,那么要加上分组 ，不然是不起作用的
   1. 没有分组的被修饰字段默认在`javax.validation.groups.Default`分组 可以让自定义校验分组接口继承该接口 