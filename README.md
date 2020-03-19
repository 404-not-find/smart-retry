## Smart Retry
Smart Retry主要是用来进行方法重试的。和Guava Retry、Spring Retry相比，Smart Retry最大的特点是异步重试，支持持久化，系统重启之后可以继续重试。

## 背景
在微服务环境下，会根据不同的业务将拆分成不同的服务，比如会员服务、订单服务、商品服务等等，每个服务都会和其他服务进行交互，根据交互的结果来更新自己服务的数据。在交互的过程中，往往会因为网络原因、对方服务的中断或更新重启导致交互失败，在这种情况下，要保证各个服务数据的一致性就必须要进行重试。市面上开源的重试框架有很多，如Guava Retry、Spring Retry等都支持重试，但是他们都不支持任务的持久化，系统更新重启，重试任务就会丢失，这无法保证数据的一致性。传统的做法是写一个定时任务，定时补偿。但这样会增加工作量，增加很多冗余的代码。Smart Retry在这种背景下应运而生，支持持久化的任务重试，保证数据的一致性

## 架构图
![架构图](https://s1.ax1x.com/2020/03/15/88RvNj.png)

## 实现原理
- 系统启动后，把所有com.github.smartretry.core.RetryHandler和带有@RetryFunction注解的方法注册为Quartz Job。
- 所有com.github.smartretry.core.RetryHandler和带有@RetryFunction注解的方法都会被Spring进行代理，执行的时候，会先把参数序列化，然后把执行任务插入到数据库。最后根据任务执行的成功与否，更新任务的相应状态。
- Quartz Job定时从表里面获取未成功的任务，进行重试

## 项目特点
* 方法重试持久化，系统重启之后可以继续重试
* 异步重试（不支持同步重试）
* 支持接口实现和声明式方式
* 大量的扩展点
* 提供重试Job可视化管理

## 适用场景
* 方法重试需要持久化，系统重启、宕机恢复之后继续重试，直到重试成功
* 分布式事务最终一致性

## 不适用场景
* 延时低、实时性很高的重试
* 允许任务丢失（直接用Guava Retry就行了）
* 需要同步重试的

## Requirements:
* Java 8 or Above
* Apache Maven 3.x

## Maven dependency
* Spring 4.x
* Quartz 2.3.2
* aspectj 1.9.5
* lombok 1.18.12
* metainf-services 1.8
* apache commons-lang3 3.9

## Maven dependency provided
* MySQL
* PostgreSQL
* MSSQL Server
* Jackson 2.x
* Gson
* Fastjson

## 模块说明
* [retry-cpre](https://github.com/hadoop002/smart-retry/tree/master/retry-core)：重试模块的核心，定义了一系列的接口和扩展点
* [retry-spring4](https://github.com/hadoop002/smart-retry/tree/master/retry-spring4)：基于spring4实现的重试模块
* [retry-serializer-jackson2](https://github.com/hadoop002/smart-retry/tree/master/retry-serializer/retry-serializer-jackson2)：使用jackson2来实现参数的序列化和反序列化
* [retry-serializer-gson](https://github.com/hadoop002/smart-retry/tree/master/retry-serializer/retry-serializer-gson)：使用gson来实现参数的序列化和反序列化
* [retry-serializer-fastjson](https://github.com/hadoop002/smart-retry/tree/master/retry-serializer/retry-serializer-fastjson)：使用fastjson来实现参数的序列化和反序列化
* [retry-samples](https://github.com/hadoop002/smart-retry/tree/master/retry-samples)：配套的示例demo，可直接使用

## 如何使用
* 本工具只适合在Spring项目中使用，Spring依赖至少包含（spring-context-support、spring-aop、spring-jdbc）
* 在项目中引入maven依赖、对应的数据库驱动

        <dependency>
            <groupId>com.github.hadoop002.smartretry</groupId>
            <artifactId>retry-spring4</artifactId>
            <version>使用最新版本</version>
        </dependency>

* 创建数据库（或者直接使用已有的数据库），支持Microsoft SQL Server、PostgreSQL、MySQL数据库，其他数据库请自行扩展
* 初始化系统表，根据具体的数据库，执行retry-spring4/resources/sql/sqlserver.sql、retry-spring4/resources/sql/postgresql.sql、retry-spring4/resources/sql/mysql.sql对应的建表SQL
* 配置数据源
* 编写业务逻辑

        @RetryFunction(identity = "order.payment")
        public void payOrderAndUpdateStatus(Order order) {
            boolean success = paymentBusiness.doPayment(order);
            if (success) {
                orderBusiness.updateOrderPayStatus(order);
            } else {
                orderBusiness.updateOrderPayFail(order);
            }
        }
   或者
   
        @Slf4j
        @Service("orderPaymentBusiness")
        public class OrderPaymentBusiness implements RetryHandler<Order, Void> {
        
            @Autowired
            private PaymentBusiness paymentBusiness;
        
            @Autowired
            private OrderBusiness orderBusiness;
        
            @Override
            public String identity() {
                return "order.payment";
            }
        
            @Override
            public Void handle(Order order) {
                boolean success = paymentBusiness.doPayment(order);
                if (success) {
                    orderBusiness.updateOrderPayStatus(order);
                } else {
                    orderBusiness.updateOrderPayFail(order);
                }
                return null;
            }
        }

* 最后在启动入口加上 @EnableRetrying 注解

## Job管理页面
系统内置了一个简易的Job管理页面（页面地址: /job/dashboard.html），通过这个管理页面可以查看当前系统所有已注册的重试任务，对重试任务进行执行、停止、启动等操作
![Job管理页面](https://s1.ax1x.com/2020/03/18/8DckEn.png)

## 其他文档
* [重试方法参数说明](https://github.com/hadoop002/smart-retry/tree/master/doc/TASK_PARAM.md)
* [系统参数说明](https://github.com/hadoop002/smart-retry/tree/master/doc/SYS_ARGS.md)
* [监听器](https://github.com/hadoop002/smart-retry/tree/master/doc/LISTENER.md)

## 打包
mvn clean package