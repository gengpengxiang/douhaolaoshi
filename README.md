该项目为拆分后的新版逗号老师
1. 项目文件名改为：DouhaoTea
2. 包名仍然延续逗号老师的包名：com.bj.eduteacher
    由于包名修改涉及到：友盟推送功能、直播功能、微信分享、微信支付功能，所以决定新版逗号老师仍然延续之前的包名不动，仅修改项目文件名以作区分。


##########################################
Step Product APK
1. 修改版本号
2. HttpUtilService.java -->> 修改正式版域名
3. AndroidManifest.xml  -->> 修改环信 APP_KEY
4. 首页 -->> 开始轮询获取消息
5. tool/Constants -->> 修改腾讯云直播的SDK_APPID 和 ACCOUNT_TYPE

##########################################
start develop 3.3
1. 直播回放
2. 限制发送弹幕
3. 点赞数量累积
4. 逗课搜索

start develop 3.0.1
1. 不需要登录直接进入app
2. 昵称修改
3. 直播分享

start develop 3.0.0
1. 集成腾讯云互动直播

start develop 2.4.0
1. 开放注册
2. 字词朗读王者挑战赛（以lib形式开发）

start develop 2.3.0
1. 集成环信聊天功能

start develop 1.3
start develop 1.2 auto update app.

push source code to server
