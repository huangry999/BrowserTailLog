# 在线查看日志工具
无需登陆服务器，打开浏览器即可实现Linux *tail -f* 的功能
<br/>
[github](https://github.com/huangry999/logger)
## 特性

### 可用性
1. 适配集群，能够通过api服务器读取内网服务器日志文件。
2. 过滤文件类型。
3. 自建日志内容索引，加速文件读取。
4. 界面提供多种工具——暂停、定位到行、滚轮上划暂停、滑倒底部实时显示最新内容等，方便日志的查看。

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
<br/>Yaml风格，没说明可选且无默认值的都为必填项
```
ui-host:
  host: 127.0.0.1 # api服务器地址，默认 127.0.0.1
  port: 10902 # api服务器grpc端口，默认 10902
file-monitor:
  interval-ms: 3000 # 轮询日志变化间隔，单位：毫秒，默认3000
file-reader:
  sampling-interval: 100 # 建立索引的行数间隔，间隔越大占内存越小但读取操作约慢，默认100
grpc:
  port: 10903 # grpc 端口，多个filer service 服务需要配置不用端口，默认 10903
log-file:
  path:
    - { path: "G:\\log", alias: "log" }# 日志根目录路径，path 为绝对路径【必填】，可以取别名（在ui显示，默认为path最后一个文件路径名）
  suffix:
    - ".log" # 过滤日志文件后缀名，默认留空不过滤
  recursive: true # 包含子文件夹，默认包含
log-host:
  name: 'Demo' # 服务器名称，需要和api服务器统一
logging:
  path: systemlog # 系统日志路径，默认为 runDir/systemlog
  level:
    root: info # 日志等级，默认为root
```
#### api service 配置
uiapi/bin/application.yml
<br/>Yaml风格，没说明可选且无默认值的都为必填项
```
server:
  port: 8080 # tomcat 端口，默认 8080
  address: 127.0.0.1 # tomcat 监听地址，默认 127.0.0.1
grpc:
  port: 10902 # grpc 端口，10902
uiapi-properties:
  netty:
    port: 8081 # websocket服务端口，如果修改需要修改ui/config/index.js中的配置，默认8081
  security:
    auth: # 需要验证密码，可选，默认不需要
    expire-minutes: 30 # session过期时间，默认30分钟
    # file service 服务，name需要与host配置文件相同
    # ip: file service ip
    # name: file service配置中的log-host.name
    # desc: 在ui中显示
    # rpc-port ile service配置中的grpc.port
    hosts:
      - {ip: 127.0.0.1, name: Demo, desc: 'Demo Host', rpc-port: 23331}
logging:
  path: systemlog # 系统日志路径，默认为 runDir/systemlog
  level:
    root: info # 系统日志等级，默认为info
    subscribe: info #统计订阅者的日志，info时打印到日志文件，默认info
```
