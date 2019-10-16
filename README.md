# wufan-chat
一个类似微信的前后端分离的简易聊天工具


## 介绍
采用前后端分离开发模式实现的一个简易B/S模式的聊天系统。
前端基于vue实现。

后端服务实现基于以下技术：

- 使用 Java + Spring Boot + Mybatis开发，采用 Zookeeper + Dubbo RPC 微服务架构，基于gradle构建
- 基于 Spring Security 实现 SSO 登录方案
- 前后端联合使用WebSocket 和Http协议进行通信，接口设计遵循 Restful 规范
- 使用 MySQL 持久化存储数据，利用Redis 实现缓存控制
- 集成Swagger接口文档，log4j日志记录和dubbo-admin服务监控。
- 实现功能包括：

```
用户服务：注册、单点登录、用户名校验、注销、修改密码；
好友服务：查找用户、用户详情、添加好友、查看好友列表、接受/拒绝好友请求、未读好友申请提醒、删除好友；
聊天服务:  未读好友消息提醒、发起聊天、查看聊天记录、分页查看聊天记录、最近联系人、图片发送；
群聊服务：搜索群聊、创建群聊、加入群聊、群成员退出群聊、管理员解散群聊、即时群聊天、未读群聊消息提醒
```

## 功能演示

![pic1](https://s2.ax1x.com/2019/10/16/KFcSd1.gif)

![pic2](https://s2.ax1x.com/2019/10/16/KFc6m9.gif)

![pic3](https://s2.ax1x.com/2019/10/16/KFcH0A.gif)

![pic4](https://s2.ax1x.com/2019/10/16/KFcLkt.gif)

![pic5](https://s2.ax1x.com/2019/10/16/KFg9mj.gif)

![pic6](https://s2.ax1x.com/2019/10/16/KFgC0s.gif)

![pic7](https://s2.ax1x.com/2019/10/16/KFgP7n.gif)

## 接口定义

### 用户相关接口：
```
/account/login	POST	登录请求（匿名）
/account/register	POST	注册请求（匿名）
/account/varifyname 	POST	名称验证（匿名）
/account/findbyname	POST	获取用户信息
/account/changepwd	POST	修改密码
```

### 好友相关接口：
```
/friend/friendlist	websocket send	获取好友列表
/friend/reveivereqlist	websocket send	获取收到的好友请求列表
/friend/replyrequest 	websocket send	处理好友申请
/friend/sendrequest	websocket send	发送好友申请
/friend/deletefriend	websocket send	删除好友
```

### 聊天相关接口：
```
/chat/sendchat	websocket send	发送消息
/chat/gethistorybypage	websocket send	分页获取历史记录
/chat/beginchat	websocket send	开始用户聊天
/chat/recentchat	websocket send	获取最近消息列表
/chat/readmessages	websocket send	读消息确认
```

### 群聊相关接口：
```
/group/grouplist	websocket send	获取群组列表
/group/newgroup	websocket send	新建群组
/group/deletegroup	websocket send	解散群组
/group/joingroup	websocket send	加入群组
/group/exitgroup	websocket send	退出群组
```

### 客户端订阅接口：
```
/app/topic/friendlist  好友列表
/app/topic/sendreqlist  发送的好友请求列表
/app/topic/receivereqlist  收到的好友请求列表
/app/topic/recentchat  最近联系人列表
/app/topic/gethistorybypage  分页查询历史 
/app/topic/grouplist  群组列表
/app/topic/chathistory 聊天框历史记录列表
```
