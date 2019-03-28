# 在线查看日志工具
无需登陆服务器，打开浏览器即可实现不至于 *tail -f* 的功能
## 使用步骤
### 环境
- java 8+
### 安装
用gradle自行编译或下载位于*install*下已经编译好的版本，解压后做好相关配置，运行启动脚本即可。

### 配置说明
#### file service 配置
Yaml风格，没说明可选且无默认值的都为必填项
```
ui-host:
  host: 127.0.0.1 # api服务器地址，默认 127.0.0.1
  port: 23330 # api服务器grpc端口，默认23330
file-monitor:
  interval-ms: 3000 # 轮询日志变化间隔，单位：毫秒，默认3000
file-reader:
  sampling-interval: 100 # 索引间隔，默认100
grpc:
  port: 23331 # grpc 端口
log-file:
  path:
    - "G:\\log" # 日志文件根路径
  suffix:
    - ".log" # 日志文件后缀名，可选，默认空
  recursive: true # 包含子文件夹，默认包含
log-host:
  name: 'Main Host' # 服务器名称，需要和api服务器统一
```
#### api service 配置
Yaml风格，没说明可选且无默认值的都为必填项
```
grpc:
  port: 23330 # grpc端口，默认23330
uiapi-properties:
  netty:
    port: 8081 # websocket服务端口，默认8081
    address: 0.0.0.0 # websocket服务监听地址，默认0.0.0.0
  security:
    auth: # 需要验证密码，可选，默认不需要
    expire-minutes: 30 # session过期时间，默认30分钟
# file service 服务，name需要与host配置文件相同
  hosts:
    - {ip: 192.168.1.101, name: Main Host, desc: 'test machine', rpc-port: 23331} 
```
