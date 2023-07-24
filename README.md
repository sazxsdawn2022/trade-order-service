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

#### 描述
* 请求统一经trade-order-gateway模块转发到trade-order-api
* 从application.yml文件中的action读取两个用于负载均衡的主机
* GatewayService中提供三个简单的负载均衡的算法（随机，hash，轮询）这里使用round轮询

#### 注意
* GatewayController中的不同方法对应不同的需求，根据需求改变形参，loadLalancing的统一在后面要再考量
* loadLalancing可以改成loadBalancing，后面改一下

### 查询订单详情

#### 描述
* 订单详情的组成，返回的信息中data是要从数据库以及第三方接口获取的。
  - id从传进来的参数获取；priceValue从ksc_trade_order表中查询；
  - user,region从第三方接口中获取；
  - configs依赖order_id从ksc_trade_product_config表中获取。
* 从第三方接口中获取的json字符串使用jackson转成对象，方便组成要求的格式
* 订单详情的获取看似简单，但有许多要注意的点。
  - 从region接口查询出的name，编写工具类PinyinUtils转成拼音后再组合
  - 使用jackson把对象和json字符串相互转换
  - 将查询的user,region封装成对象，configs封装成List集合，当作属性设置到TradeResultDTO中
* 在gateway的service中设置upstream到HttpEntity中，转发到api以参数的形式接收

#### 注意
* 返回的configs是数组的形式
* 使用了mybatis-plus，把pom.xml文件中的mybatis-spring-boot-starter注销掉，否则模块启动不起来
* 在生成json字符串中的region时，编写工具类PinyinUtils把获取的regionResponseEntityBody响应体中的data汉字转成拼音，以达到要求的格式
* 从第三方接口查出的region,user，以及从数据库查出的TradeOrder,TradeProductConfig类（pojo），与要求返回的json格式不同（字段上），编写相应的DTO，把需要的值映射到DTO上
* queryOrderInfo携带upstream参数，gateway端execute时设置HttpMethod.POST

### 查机房名称

#### 描述
* 由gateway转发到api，调用三方接口获取responseEntity，然后获取body，映射成RestResult，从body中剥离出data，设置到ResponseEntity中返回

#### 注意
* gateway中loadLalancing，发送HttpMethod.GET请求，与tradeOrderInfo区分开

### 订单优惠券抵扣公摊

#### 描述
* 先根据orderId从数据库从查出订单，将order中的价格减去优惠劵的价格计算后，插入到优惠劵表

### 漏桶限流算法

#### 描述
使用lua脚本加redis实现

### 链路跟踪

#### 描述
* 在gateway发送到api时设置requestId设置到header，然后转发至到api中