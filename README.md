# ycXposeInjectEvent
基于xpose和安卓5.1.1，解决普通应用没有inject event权限，不能发送给其他进程的问题
## 使用方法
1. 安装xpose目录下的xpose.zip, 方法是在安卓设备上建立一个目录，复制进去，然后运行里面的.sh命令
重启就好了，需要等挺久的。（root后才能操作，有变砖风险自己解决）
2. 安装Android Stuido，打开项目编译，然后部署到机器上。
3. 在xpose框架里面勾选上这个apk。然后别的应用就可以支持发送inject event了。