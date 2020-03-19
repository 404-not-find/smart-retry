#### 这里是个创建订单的例子，创建订单后，需要支付，支付完成之后，再更新订单的状态。由于支付是请求外部服务，有会网络异常导致支付失败的情况，所以需要重试

#### 如何运行
* 需要有一个mysql数据库，执行retry-samples/resources/sql/mysql.sql创建表
* 配置mysql数据源
* 打包mvn clean package
* java -jar xxx.jar 运行
* 请求/order/createOrderWithRetryFunction 或 /order/createOrderWithRetryHandler 创建订单