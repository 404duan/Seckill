# 秒杀项目

## 具体内容

1. 项目框架搭建
   - SpringBoot环境搭建
   - 集成Thymeleaf, MyBatis Plus
2. 分布式会话
   - 用户登录
      - 数据库设计
      - 密码二次MD5加密（前端+后端）
      - 参数校验（前端+后端）+全局异常处理
   - 共享Session
      - Spring Session -> Redis
3. 功能开发
   - 商品列表
   - 商品详情
   - 秒杀
   - 订单详情
4. 系统压测
   - 自定义变量模拟多用户
   - 压测
      - 商品列表
      - 秒杀接口
5. 页面优化
   - 页面缓存 -> URL缓存 -> 对象缓存
   - 页面静态化，前后端分离
   - 静态资源优化
6. 接口优化
   - Redis预减库存减少数据库的访问
   - 使用内存标记减少Redis的访问
   - RabbitMQ异步下单
7. 安全优化
   - 秒杀接口地址隐藏
   - 算术验证码
   - 接口防刷

## 软件架构

|                         技术                          | 版本  |      说明      |
| :---------------------------------------------------: | :---: | :------------: |
|                      Spring Boot                      | 2.7.0 |                |
|                         MySQL                         |  5.7  |                |
| [MyBatis Plus](https://github.com/baomidou/generator) | 3.4.0 |                |
|              [Redis](https://redis.io/)               | 5.0.5 | 配置在Docker上 |
|         [RabbitMQ](https://www.rabbitmq.com/)         | 3.8.5 | 配置在Docker上 |



## 使用说明

登录页面：http://localhost:8080/login/toLogin
