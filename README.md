# android_permission
Android 权限管理
aop技术封装Android权限申请框架
简书：[aop技术封装Android权限申请框架](https://www.jianshu.com/p/b41b556ec5dc)



# 使用
在root build.gradle引入

    classpath 'com.hujiang.aspectjx:gradle-android-plugin-aspectjx:2.0.4'
在app build.gradle引入
             
    apply plugin: 'android-aspectjx'
    
    
在需要使用android_permission module  中引入：

    implementation 'com.github:android_permission_aop:release'