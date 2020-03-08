# roacg-project
一个社区应用
暂定域名为 roacg.com \ roacg.co

---

### 大事记:
2020-01-22 正式决定立项，进入研发阶段

<br/>
<br/>
<br/>

### 架构设计:

service-gateway: Api gateway, 鉴权、认证需要在此进行  
service-system: 综合OAuth2认证服务、用户/权限/角色等相关操作


所有服务均提供相应的 Dubbo Service 供系统内部进行远程服务调用  
除了service-system服务外, 其他服务均需要提供 HTTP 访问接口供网关层路由。