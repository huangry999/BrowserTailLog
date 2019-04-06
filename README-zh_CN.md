# 在线查看日志工具
[中文](https://github.com/huangry999/BrowserTailLog) | [English](https://github.com/huangry999/BrowserTailLog/blob/master/README_EN.md)<br/>
无需登陆服务器，打开浏览器即可实现Linux *tail -f* 的功能
## 特性

### 可用性
1. Eureka 自动注册服务，适配集群，简化配置。
2. 自建日志内容索引，加速文件读取。
3. 界面提供多种工具——暂停、定位到行、滚轮上划暂停、滑倒底部实时显示最新内容等，方便日志的查看。

### 可靠性
1. 基于OAuth2，可选择开启密码验证。
2. 校验文件读取请求，拒绝不在配置文件范围内的请求。

## 安装使用

### 环境

#### 使用
- java 8+

#### 开发
- java 8+
- gradle 5.2+
- node 10.13+
- lombok 插件

### 安装
1. gradle 自行打包，或者使用位于*install*下已经打包好的版本
2. 完成相关配置，运行*bin*目录下的启动脚本。

[Online Demo](http://www.94hry.tech:10999)
<br/>(服务器网速可能比较慢)

### 配置说明
**注意**：以下配置会覆盖系统内默认的application.yml。
#### file service 配置
filerservice/bin/application.yml
```
#-----------------------请注意以下配置可能需要根据实际部署更改---------------------------#
grpc:
  port: 10903 # grpc 端口，默认 10903
ui-host:
  host: 127.0.0.1 # api服务器web地址，默认 127.0.0.1
  port: 8080 # api服务器web端口，默认 8080
log-file:
  path:
    # 日志根目录路径
    # path：文件夹的绝对路径，需要用''括起来，必填
    # alias：在ui显示的别名，默认为path按文件夹分割符分割后的最后一个文件名
    - { path: 'G:\log'}
  suffix:
    - '' # 过滤日志文件后缀名，默认不过滤
  recursive: true # 包含子文件夹，默认包含

#-------------------------以下为系统功能性配置，按默认配置或自定义皆可-------------------------#
file-monitor:
  interval-ms: 3000 # 轮询日志变化间隔，单位：毫秒，默认3000
file-reader:
  sampling-interval: 100 # 建立索引的行数间隔，间隔越大占内存越小但读取操作约慢，默认100
log-host:
  name: SERVICE ${grpc.port} # Log服务器名称，多个File Service实例间不能相同，默认SERVICE + grpc.port
  desc: # Log 服务的信息，会显示在ui上，默认为空
logging:
  path: log # 系统日志路径，默认为 runDir/log
  level:
    root: info # 系统日志等级，默认为info
spring:
  profiles:
    active: prod
```
#### api service 配置
uiapi/bin/application.yml
```
#-----------------------请注意以下配置可能需要根据实际部署更改---------------------------#
server:
  port: 8080 # web端口，默认 8080
  address: 127.0.0.1 # web监听地址，默认 127.0.0.1
grpc:
  port: 10902 # grpc 端口，10902

#-------------------------以下为系统功能性配置，按默认配置或自定义皆可-------------------------#
uiapi-properties:
  netty:
    port: 10901 # websocket服务端口，如果修改需要修改ui/config/index.js中的配置，默认10901
  security:
    auth: # 需要验证密码，可选，默认不需要
    expire-minutes: 30 # session过期时间，默认30分钟
logging:
  path: log # 系统日志路径，默认为 runDir/log
  level:
    root: info # 系统日志等级，默认为info
    subscribe: info #统计订阅者的日志，info时打印到日志文件，默认info
eureka:
  dashboard:
    path: /eurekadashboard #Eureka监控页面访问路径，默认/eurekadashboard
    enabled: true #是否开启Eureka监控页面，默认开启
spring:
  profiles:
    active: prod
```
