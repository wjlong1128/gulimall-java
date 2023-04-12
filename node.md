1. nacos mysql版本过低 将mysql对应jar包放入nacos目录下面的`plugin/mysql`
2. 跨域要在网关配置 妈的  网关和被代理者要在同一名称空间下
3. 校验注解 如果被校验@Validated有分组,那么要加上分组 ，不然是不起作用的
   1. 没有分组的被修饰字段默认在`javax.validation.groups.Default`分组 可以让自定义校验分组接口继承该接口 
4. 启动参数 -Xmx100m

## elsticSerarch 部署
* docker network create es-net
 ```sh
docker run -d \
	--name es \
    -e "ES_JAVA_OPTS=-Xms512m -Xmx512m" \
    -e "discovery.type=single-node" \
    -v es-data:/usr/share/elasticsearch/data \
    -v es-plugins:/usr/share/elasticsearch/plugins \
    --privileged \
    --network es-net \
    -p 9200:9200 \
    -p 9300:9300 \
elasticsearch
```
docker run -d --name es  -e "ES_JAVA_OPTS=-Xms512m -Xmx512m" -e "discovery.type=single-node" -v C:/Users/wangj/Desktop/gulimall_code/es/data:/usr/share/elasticsearch/data -v C:/Users/wangj/Desktop/gulimall_code/es/data/plugins:/usr/share/elasticsearch/plugins --privileged --network es-net -p 9200:9200 -p 9300:9300 elasticsearch:7.4.2
docker run -d --name kibana -e ELASTICSEARCH_HOSTS=http://es:9200 --network=es-net -p 5601:5601  kibana:7.4.2

es 
* 索引-> 数据库
* 类型-> 表
* 文档-> 记录
* 查看所有索引 GET http://localhost:9200/_cat/indices
* 安装分词器 https://github.com/medcl/elasticsearch-analysis-ik
* docker container cp nginx:/etc/nginx .
  docker run --name nginx -p 81:80 -v C:/Users/wangj/Desktop/gulimall_code/nginx/conf:/etc/nginx -v C:/Users/wangj/Desktop/gulimall_code/nginx/logs:/var/log/nginx -v C:/Users/wangj/Desktop/gulimall_code/nginx/html:/usr/share/nginx/html --network es-net   -d nginx

## 域名
1. host文件欺骗dns
2. 代理到虚拟机中的nginx
```nginx
upstream wjl {
    server 192.168.56.103:88;
}
```
3. nginx代理到本地网关
```nginx
 location / {
          proxy_pass http://wjl;
          # 默认丢掉host头
          proxy_set_header Host $host;
 }
```
4. 网关代理到具体服务
```yaml
- id: wjl-cn
  uri: lb://gulimall-product
  predicates:
    - Host=wjl.cn
```


## 性能优化
1. 开缓存 
2. 升级日志
3. 加索引 优化数据库
4. 优化业务逻辑

![cache](https://raw.githubusercontent.com/wjlong1128/images/main/wjl202304101752265.png)

![cache2](https://raw.githubusercontent.com/wjlong1128/images/main/wjl202304102035706.png)

![](https://raw.githubusercontent.com/wjlong1128/images/main/wjl202304111710509.png)

![](https://raw.githubusercontent.com/wjlong1128/images/main/wjl202304111724408.png)

![](https://raw.githubusercontent.com/wjlong1128/images/main/gulimall/202304111726817.png)