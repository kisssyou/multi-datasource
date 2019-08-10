# multi-datasource
![](https://raw.githubusercontent.com/kisssyou/resource/master/image/sky1.jpg?token=AHU6NF7P5ZM5W2DGA5GGJQS5J3PM4)

---
+ 原理  
springboot多数据源动态切换，主要是通过 AbstractRoutingDataSource 的 determineCurrentLookupKey 方法和线程局部变量 ThreadLocal 来动态切换数据源
+ 配置文件
```
spring:
  datasource:
    master:
      jdbc-url: jdbc:mariadb://localhost:3306/master_db
      type: com.zaxxer.hikari.HikariDataSource
      username: root
      password: root
      driver-class-name: org.mariadb.jdbc.Driver
    slave:
      jdbc-url: jdbc:mariadb://localhost:3306/slave_db
      type: com.zaxxer.hikari.HikariDataSource
      username: root
      password: root
      driver-class-name: org.mariadb.jdbc.Driver
    default: master
```

+ 注解使用
```
@DataSource(routingKey = "master")
public interface UserMapper {

    List<Person> getUsers();

    @DataSource(routingKey = "slave")
    Person getUserById(@Param("id") int id);
}
```
