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
##### 主要实现类是RedisRateLimiter，主要用于限制接口的访问速度，防止系统被过度请求压垮。该类使用了Redis来存储漏桶的当前水量，并通过Lua脚本实现了限流的具体逻辑。

* 下面是该类中各个成员变量和方法的详细解释：
  - REDIS_KEY_PREFIX：Redis中存储漏桶当前水量的键值前缀，用于避免不同接口之间使用同一个键值而产生冲突。
  - DEFAULT_LIMIT：默认的QPS限制，即每秒最多处理多少个请求。
  - DEFAULT_CAPACITY：默认的漏桶容量，即漏桶最多能存储多少个请求。
  - SCRIPT：使用Lua脚本实现的限流逻辑，通过Redis的eval命令执行。
  - jedis：Redis连接对象，用于连接Redis服务器。
  - limit：QPS限制，即每秒最多处理多少个请求。
  - capacity：漏桶容量，即漏桶最多能存储多少个请求。
  - key：Redis中存储漏桶当前水量的键值，由REDIS_KEY_PREFIX和API地址组合而成。
  - RedisRateLimiter(Jedis jedis, String apiPath)：构造函数，用于创建RedisRateLimiter对象，并初始化jedis、limit、capacity和key等属性。其中apiPath参数表示当前接口的地址。
  - RedisRateLimiter(Jedis jedis, String apiPath, int limit, int capacity)：构造函数，用于创建RedisRateLimiter对象，并初始化jedis、limit、capacity和key等属性。同时，该构造函数还支持自定义漏桶容量和QPS限制。
  - acquire()：请求限流处理方法，用于判断当前请求是否被限流。该方法通过执行Lua脚本来实现漏桶限流算法，如果当前请求未被限流，则返回true，否则返回false。
* 在listUpstreamController中生成RedisRateLimiter对象，这里使用RedisRateLimiter(Jedis jedis, String apiPath)构造器来初始化对象，并通过调用acquire()方法来进行限流处理。如果当前请求未被限流，则可以继续进行下一步操作；否则，需要等待一段时间后再次尝试请求，以保证系统的稳定性和安全性。
* lua脚本
  - local key = KEYS[1]：将传入的第一个参数(KEYS[1])赋值给变量key，用于表示Redis中存储漏桶当前水量的键值。
  - local limit = tonumber(ARGV[1])：将传入的第二个参数(ARGV[1])转换为数字类型，并赋值给变量limit，表示当前接口的QPS限制。
  - local capacity = tonumber(ARGV[2])：将传入的第三个参数(ARGV[2])转换为数字类型，并赋值给变量capacity，表示漏桶的容量。
  - local current = tonumber(redis.call('get', key) or '0')：从Redis中读取漏桶的当前水量，并将其转换为数字类型赋值给变量current。如果Redis中不存在该键值，则将current赋值为0。
  - if current + 1 > limit then ... else ... end：根据当前水量和QPS限制，判断当前请求是否被限流。如果当前请求未被限流，则将漏桶的水量加1，并设置漏桶的过期时间。如果当前请求被限流，则直接返回0，表示请求被拒绝。
  - redis.call('INCRBY', key, 1)：将漏桶的水量加1。
  - redis.call('expire', key, capacity)：设置漏桶的过期时间，保证漏桶的容量不会超过设定值。
  - return 1：返回1，表示当前请求未被限流。
* 通过Redis的原子性操作保证了多个请求同时到来时漏桶限流算法的正确性。脚本中还设置了漏桶的容量和过期时间，保证了系统的稳定性和安全性。

### 链路跟踪

#### 描述
* 在gateway发送到api时设置requestId设置到header，然后转发至到api中