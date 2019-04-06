# Tail Log By Browser
[中文](https://github.com/huangry999/BrowserTailLog) | [English](https://github.com/huangry999/BrowserTailLog/blob/master/README_EN.md)<br/>
Without login server and any client, you could look over logs like *tail -f* in linux.
## Feature

### Availability
1. Eureka auto register service, simplify configuration for cluster.
2. Build index for log files, speed up opening. 
3. A series of convenient tools for looking. Such as pause, jump to line, auto-pause when scroll and so on.

### Security
1. Authentication by OAuth2, you could set a password if necessary.
2. Reject the read request which beyond log directories defined in configuration.

## Installation

### Environment

#### Production
- Java 8+

#### Development
- Java 8+
- Gradle 5.2+
- Node 10.13+
- Lombok plugin

### Install
1. Build by gradle，or copy the compiled files in *install* directory.
2. Make configuration，and run script of your platform in *bin* directory.

[Online Demo](http://www.94hry.tech:10999)
<br/>(Network maybe slow ...)

### Configuration
**Warning**: The configuration will replace the system default.
#### File service config
filerservice/bin/application.yml
```
#----------The following configuration need to modify based on your deployment ---------#
grpc:
  port: 10903 # grpc port, default 10903
ui-host:
  host: 127.0.0.1 # api server address, default 127.0.0.1
  port: 8080 # api server port, default 8080
log-file:
  path:
    # log root directories
    # path: directory absolute path, enclose with quotation maerk, Required
    # alias：display name in website, default is the directory name
    - { path: 'G:\log'} # just demo
  suffix:
    - '' # filter with suffix, default none
  recursive: true # is include/exclude child directories, default include

#--------------The following configuration is about system function------------------#
file-monitor:
  interval-ms: 3000 # check file event time interval, unit: million second, default 3000
file-reader:
  sampling-interval: 100 # skip interval when build index, smaller will faster but means more Ram take, default 100
log-host:
  name: SERVICE ${grpc.port} # this file service name, the name should be unique, default: SERVICE + grpc.port
  desc: # Log this file service info, will showed in website, default none
logging:
  path: log # system log path, default: runDir/log
  level:
    root: info # system log level, default: info
spring:
  profiles:
    active: prod
```
#### Api service config
uiapi/bin/application.yml
```
#----------The following configuration need to modify based on your deployment ---------#
server:
  port: 8080 # web port, default 8080
  address: 127.0.0.1 # web address, default 127.0.0.1
grpc:
  port: 10902 # grpc port, default 10902

#--------------The following configuration is about system function------------------#
uiapi-properties:
  netty:
    port: 10901 # websocket port，need to be same as wsPort in ui/config/index.js, default 10901
  security:
    auth: # password to access logs, none means allow anonymous, default none
    expire-minutes: 30 # session expire time, default 30 mins
logging:
  path: log # system log path, default: runDir/log
  level:
    root: info # system log level, default: info
    subscribe: info # subscribe stats log, if level is warning or error, will not print stats, default: info
eureka:
  dashboard:
    path: /eurekadashboard #Eureka dashboard page path, default /eurekadashboard
    enabled: true # enabled/disabled Eureka dashboard page, default enabled
spring:
  profiles:
    active: prod
```
