# 利用 create-spring-boot-app 快速创建 Spring Boot 应用

如果你尚未安装 Node.js 环境，推荐使用 [nvm](https://github.com/creationix/nvm) 安装 Node.js 基本环境。如果你尚未安装 Java 或者 Gradle，推荐使用 [sdkman](sdkman.io)
为了保证应用具有热加载功能，我们使用 [Spring Loaded](http://docs.spring.io/spring-boot/docs/current/reference/html/howto-hotswapping.html) 扩展：
```groovy
buildscript {
    repositories { jcenter() }
    dependencies {
        classpath "org.springframework.boot:spring-boot-gradle-plugin:1.5.2.RELEASE"
        classpath 'org.springframework:springloaded:1.2.6.RELEASE'
    }
}

apply plugin: 'idea'

idea {
    module {
        inheritOutputDirs = false
        outputDir = file("$buildDir/classes/main/")
    }
}

// ...
```

# 目录结构

## 扩展

# 共享模块

## 实体类

# 数据接口

## 接口风格

## 逻辑与副作用分离

# 数据持久化

# 微服务
 
## HTTPS 与负载均衡

## 服务状态监听

使用了 [actuator-service](https://spring.io/guides/gs/actuator-service/) 监听服务运行状态，需要在依赖中添加 `compile("org.springframework.boot:spring-boot-starter-actuator")`，然后在配置文件中指定监听地址：
```
management.port=8082
management.address=127.0.0.1
```
在应用启动后，访问 `/health` 即可以获取到系统当前信息：
```
{
    status: "UP",
    diskSpace: {
        status: "UP",
        total: 249804890112,
        free: 27797606400,
        threshold: 10485760
        }
}
```

# 测试

## 单元测试

## 集成测试

## 端到端测试