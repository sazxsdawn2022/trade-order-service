#  trade-order



## 说明

* 项目编码：UTF-8
* JDK:1.8
* IDE：不限
* 项目开发方式  
  - 分支开发，发布时合并主干
  - 环境隔离（profile：local、dev、uat、prod）

## 模块

* biz：公共业务模块（供其它模块调用）<br/>
* biz-base:基础业务 <br/>
* biz-req-trace:请求链路跟踪<br/>
* biz-glue:<br/>
* trade-order-api：业务代码 <br/>
* trade-order-gateway：路由服务 <br/>


## 功能点

### 请求转发

#### 简述
* 请求统一经trade-order-gateway模块转发到trade-order-api
* 从application.yml文件中的action读取两个用于负载均衡的主机
* GatewayService中提供三个简单的负载均衡的算法（随机，hash，轮询）这里使用random随机

#### 注意
* GatewayController中的不同方法对应不同的需求，根据需求改变形参，loadLalancing的统一在后面要再考量



