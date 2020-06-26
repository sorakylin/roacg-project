# Dubbo、Feign 等RPC的API


### 基本命名

* Dubbo 的接口, 应该以 RService 结尾命名(Remote Service), 如 **UserRService** 
* Feign 提供的接口, 应该以 Feign 结尾命名, 如 UserFeign
* 在远程调用之间通信的对象, 如果是实体类则命名以 RDTO 结尾命名