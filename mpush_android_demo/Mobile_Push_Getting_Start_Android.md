# "移动研发平台 EMAS 控制台"

## **前言**

本章节介绍移动推送Android SDK的接入方法。

- 推荐使用Gradle管理依赖的Android Studio项目。
- 支持Android 5.0及以上版本。
- 本文档仅适合移动推送Android SDK V3.0.0及以上版本的集成操作。

**重要**

移动推送SDK包含EMAS自有长连接通道和厂商通道两种类型的推送通道。本文主要介绍自有长连接通道的接入方式。

接入厂商通道能够提高App离线状态下的推送到达率，请参考[第三步：接入厂商通道](https://emas.console.aliyun.com/#4f3bd0e3a1mki)。

移动推送Android SDK Demo工程请参见：[移动推送Android Demo](https://github.com/aliyun/alicloud-android-demo/tree/master/mpush_android_demo)。

Demo工程使用Kotlin + MVVM开发，界面更美观，功能更丰富，开发者可参考Demo更快速了解移动推送Android SDK如何使用。

## **准备工作**

- 已创建项目和应用。具体操作请参见[创建项目和应用](https://help.aliyun.com/document_detail/436513.html#section-8am-xwe-iqh)。
- 已阅读[Android SDK版本说明](https://help.aliyun.com/document_detail/434659.html#topic-1996989)，获取最新版本对应关系。

## **第一步：将SDK添加到您的应用**

我们提供了Maven依赖和本地依赖两种集成方式，方便您根据需要将SDK添加到您的应用中。

**说明**

建议开发者采用Maven依赖方式进行集成，配置简单，不容易出问题，后续更新方便。

### **1 Maven依赖方式**

#### 1.1 配置Maven仓库

下面分别介绍Gradle 7.0及以上推荐的`dependencyResolutionManagement`配置方式和Gradle 7.0之下推荐的`allprojects`配置方式。

##### 1.1.1 dependencyResolutionManagement方式

在您的根级（项目级）Gradle 文件（`<project>/settings.gradle`）中，在`dependencyResolutionManagement`的`repositories`中添加Maven仓库地址。

```
dependencyResolutionManagement {
  repositories {
    maven {
      url 'https://maven.aliyun.com/nexus/content/repositories/releases/'
    }

    // 配置HMS Core SDK的Maven仓地址，集成华为通道需要。
    maven {
      url 'https://developer.huawei.com/repo/'
    }
  }
}
```

##### 1.1.2 allprojects方式

在您的根级（项目级）Gradle 文件（`<project>/build.gradle`）中，在`allprojects`的`repositories`中添加Maven仓库地址。

```
allprojects {
  repositories {
    maven {
      url 'https://maven.aliyun.com/nexus/content/repositories/releases/'
    }

    // 配置HMS Core SDK的Maven仓地址，集成华为通道需要。
    maven {
      url 'https://developer.huawei.com/repo/'
    }
  }
}
```

#### 1.2 添加SDK依赖

在您的模块（应用级）Gradle 文件（通常是<project>/<app-module>/build.gradle）中，在`dependencies`中添加SDK依赖。

```
dependencies {
  implementation 'com.aliyun.ams:alicloud-android-push:{pushVersion}'
}
```

**重要**
- `pushVersion`请从[Android SDK版本说明](https://help.aliyun.com/document_detail/434659.html#topic-1996989)中获取。
- 请使用固定版本号集成，不要使用动态版本号。错误示例：3.+或者3.2.+。

### 2 本地依赖方式

#### **2.1 下载SDK**

参考[快速入门](https://help.aliyun.com/document_detail/436513.html#aa69447860e8c)，选择移动推送进行下载，将SDK包内所有文件拷贝至您的模块（应用级）的`<project>/<app-module>/libs`目录下。

#### **2.2 添加SDK依赖**

##### **2.2.1 配置本地SDK目录**

在您的模块（应用级）Gradle 文件（通常是<project>/<app-module>/build.gradle）中，添加本地SDK文件目录地址。

```
repositories {
  flatDir {
    dirs 'libs'
  }
}
```

#### 2.2.2 添加SDK依赖

在您的模块（应用级）Gradle 文件（通常是<project>/<app-module>/build.gradle）中，的`dependencies`中添加SDK依赖。

```
dependencies {
  implementation fileTree(include: ['*.jar'], dir: 'libs')
  implementation (name:'alicloud-android-push-3.x.x', ext: 'aar')
  implementation (name:'accs_sdk_taobao-4.x.x', ext: 'aar')
  // 把所有的aar都添加上
  ...
}
```

**重要**
- 示例依赖中的SDK版本号请根据下载产物的文件名中的版本号为准。
- 如果编译报类冲突，请确认dependencies下是否已经有`implementation fileTree(dir: 'libs', include: ['*.jar'])`
- 辅助通道SDK不支持本地依赖，只支持maven依赖。

## **第二步：配置使用SDK**

### **1 配置AppKey和AppSecret**

配置AppKey和AppSecret有两种方式，AndroidManifest文件配置和代码配置两种方式。请根据您的需求选择其中一种方式进行配置即可。

**说明**
- 为避免在日志中泄漏参数`appkey`/`appsecret`或App运行过程中产生的数据，建议线上版本关闭SDK调试日志。
- 为防止恶意反编译获取AppKey和AppSecret参数造成信息泄漏，建议您选择代码配置方式，开启混淆，并进行App加固后再发布上线。
- 如果您是百川云推送用户，不能直接使用百川平台的AppKey和AppSecret，需要登录[EMAS控制台](https://emas.console.aliyun.com/products)，登录账号为您的百川平台账号，并使用阿里EMAS平台的AppKey和AppSecret。

#### **1.1 AndroidManifest文件配置方式**

在`AndroidManifest.xml`文件中配置AppKey、AppSecret。按下面的方式，在`application`节点添加相应的`meta-data`。

**说明**
- `com.alibaba.app.appkey`和`com.alibaba.app.appsecret`为您在EMAS平台上的App对应信息。在EMAS控制台的应用管理中或在下载的配置文件中查看AppKey和AppSecret。
- AppKey和AppSecret请务必写在application标签下，否则SDK会报找不到AppKey的错误。

```
<application android:name="*****">
    <!-- 请填写你自己的appKey -->
    <meta-data android:name="com.alibaba.app.appkey" android:value="*****"/> 
    <!-- 请填写你自己的appSecret -->
    <meta-data android:name="com.alibaba.app.appsecret" android:value="****"/> 
</application>
```

#### **1.2 代码配置方式**

除了在AndroidManifest中配置AppKey和AppSecret的方式，您也可以在代码中进行配置。

Kotlin

```
val pushInitConfig = PushInitConfig.Builder()
    .application(application)
    .appKey(appKey)    //请填写你自己的appKey
    .appSecret(appSecret)    //请填写你自己的appSecret
    .build()
```

Java

```
PushInitConfig pushInitConfig = new PushInitConfig.Builder()
        .application(application)
        .appKey(appKey)    //请填写你自己的appKey
        .appSecret(appSecret)    //请填写你自己的appSecret
        .build();
```

更多配置接口请查看[基础配置接口](https://help.aliyun.com/document_detail/434662.html)和[高阶配置接口](https://help.aliyun.com/document_detail/2834534.html)。

### **2 SDK初始化**

为了尽量降低对App启动速度的影响，初始化可以分阶段进行。

#### **2.1 必须在Application onCreate中执行的逻辑**

此阶段初始化用于初始化一些推送参数，没有启动推送逻辑，必须在Application onCreate中执行。

根据上一节中选择的配置AppKey和AppSecret的方式，这里初始化代码也有两种方式。

#### AndroidManifest文件配置方式

Kotlin

```
PushServiceFactory.init(context)
```

Java

```
PushServiceFactory.init(context);
```

#### 代码配置方式

Kotlin

```
PushServiceFactory.init(pushInitConfig)
```

Java

```
PushServiceFactory.init(pushInitConfig);
```

**说明**

如果您在AndroidManifest.xml中配置了AppKey和AppSecret，也可以使用`PushServiceFactory.init(pushInitConfig)`，如果在`PushInitConfig`也配置了AppKey和AppSecret，会优先使用PushInitConfig中的配置。

#### **2.2 可以延迟执行的逻辑**

此阶段初始化用于建立推送的长连接，可以根据业务需要和合规要求延迟执行。

Kotlin

```
val pushService = PushServiceFactory.getCloudPushService()
pushService.register(this, object : com.alibaba.sdk.android.push.CommonCallback {
    override fun onSuccess(success: String) {}
    override fun onFailed(errorCode: String, errorMessage: String) {}
})
```

Java

```
CloudPushService pushService = PushServiceFactory.getCloudPushService();
pushService.register(this, new com.alibaba.sdk.android.push.CommonCallback() {
    @Override
    public void onSuccess(String success) {

    }

    @Override
    public void onFailed(String errorCode, String errorMessage) {
        
    }
});
```

**重要**

PushServiceFactory.init必须在Application主线程中，不能放到Activity中执行，也不能异步初始化。移动推送在初始化过程中将启动后台进程channel，必须保证应用进程和channel进程都执行到PushServiceFactory.init。

### **3 创建****通知通道**

安卓8.0以上的应用需要在客户端主动创建NotificaitonChannel，代码示例如下。

Kotlin

```
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    // 通知渠道的id。
    val channelId = "vibration_sound" // 这个id值需要给后端开发和运维人员，推送的时候对应 AndroidNotificationChannel 参数。
    // 用户可以看到的通知渠道的名字。
    val name: CharSequence = "我的测试通道"
    // 用户可以看到的通知渠道的描述。
    val description = "我的测试通道"
    val importance = NotificationManager.IMPORTANCE_HIGH
    val channel = NotificationChannel(channelId, name, importance)
    
    // 配置通知渠道的属性。
    channel.description = description
    // 设置通知出现时的闪灯（如果Android设备支持的话）。
    channel.enableLights(true)
    channel.lightColor = Color.RED
    // 设置通知出现时的震动（如果Android设备支持的话）。
    channel.enableVibration(true)
    // 自定义铃声
    channel.setSound(
        Uri.parse("android.resource://${packageName}/${R.raw.push_hongbao}"),
        Notification.AUDIO_ATTRIBUTES_DEFAULT
    )
    channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
    // 最后在notificationmanager中创建该通知渠道。
    notificationManager.createNotificationChannel(channel)
}
```

Java

```
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    // 通知渠道的id。
    String channelId = "vibration_sound"; // 这个id值需要给后端开发和运维人员，推送的时候对应 AndroidNotificationChannel 参数。
    // 用户可以看到的通知渠道的名字。
    CharSequence name = "我的测试通道";
    // 用户可以看到的通知渠道的描述。
    String description = "我的测试通道";
    int importance = NotificationManager.IMPORTANCE_HIGH;
    NotificationChannel mChannel = new NotificationChannel(channelId, name, importance);
    // 配置通知渠道的属性。
    mChannel.setDescription(description);
    // 设置通知出现时的闪灯（如果Android设备支持的话）。
    mChannel.enableLights(true);
    mChannel.setLightColor(Color.RED);
    // 设置通知出现时的震动（如果Android设备支持的话）。
    mChannel.enableVibration(true);
    //  自定义铃声
    mChannel.setSound(Uri.parse("android.resource://"
            + this.getPackageName() + "/" + R.raw.push_hongbao), Notification.AUDIO_ATTRIBUTES_DEFAULT);
    mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
    // 最后在notificationmanager中创建该通知渠道。
    mNotificationManager.createNotificationChannel(mChannel);
}
```

推送时[Push - 高级推送](https://help.aliyun.com/document_detail/2249916.html)接口中的`AndroidNotificationChannel`参数需要与客户端创建的通知通道的`channelId`保持一致。若没有创建通知通道或参数不一致，则无法弹出通知。

**说明**

vivo机型在客户端创建通知通道前需要在vivo开放平台上按照规范填写信息申请渠道，通知渠道id与客户端通知通道的channelId保持一致，请参考[新增渠道](https://dev.vivo.com.cn/documentCenter/doc/930#s-lo7yeb24)。

### 4 **消息接收配置**

到这一步说明您已经完成了SDK的初始化，推送长连接已经成功建立，但是要接收推送消息，还需要进行一些配置。

我们提供了`MessageReceiver`/`AliyunMessageIntentService`两种方式，方便您拦截通知，接收消息，获取推送中的扩展字段。或者在通知打开或删除的时候，切入进行后续处理。

您可以任选一种方式处理推送的消息。具体查看[消息/通知处理接口](https://help.aliyun.com/document_detail/434669.html)。

**重要**
- 完成这一步，理论上您的App已经可以收到推送消息了。
- 如果部分手机上**收不到推送**的通知消息，有可能是系统版本差异性导致，请参考[注意事项](https://emas.console.aliyun.com/#6ea57dcfb9jfv)进行处理。
- 您仅需要注册您自定义的默认实现的`MessageReceiver`/`AliyunMessageIntentService`，即可收到默认样式的通知消息，如果要**自定义样式、前台拦截通知消息、处理透传消息**等，需要扩展对应的API来处理。

### **5 NDK配置**

在您的模块（应用级）Gradle 文件（通常是<project>/<app-module>/build.gradle）中，在`android`的`defaultConfig`节点下添加NDK配置。

```
android {
    defaultConfig {
        ndk {
            //选择要添加的对应cpu类型的.so库。此处仅为示意，推送支持所有主流类型，请根据实际需求选择
            abiFilters 'arm64-v8a', 'armeabi-v7a', 'x86', 'x86_64'
        }
    }
}
```

### **6 自定义推送通知样式（可选）**

如果您需要自定义通知的样式，可以查看[自定义通知样式接口](https://help.aliyun.com/document_detail/2834944.html)。

### **7 拦截推送通知（可选）**

如果您需要拦截处理推送通知，只要扩展`MessageReceiver`/`AliyunMessageIntentService`的`showNotificationNow`和`onNotificationReceivedInApp`即可，下面以`MessageReceiver`的扩展方式来举例。

Kotlin

```
class MyMessageReceiver: MessageReceiver(){

    override fun onNotificationReceivedInApp(
        context: Context?,
        title: String?,
        summary: String?,
        map: MutableMap<String, String>?,
        openType: Int,
        openActivity: String?,
        openUrl: String?
    ) {
        //这里可以处理下发的推送通知
    }

    override fun showNotificationNow(p0: Context?, p1: MutableMap<String, String>?): Boolean {
        //false表示拦截，true表示不拦截，请根据进行拦截，拦截后会执行到 onNotificationReceivedInApp
        return false
    }
}
```

Java

```
public class MyMessageReceiver extends MessageReceiver {
    @Override
    protected void onNotificationReceivedInApp(Context context, String title, String summary, Map<String, String> map, int openType, String openActivity, String openUrl) {
        //这里可以处理下发的推送通知
    }

    @Override
    public boolean showNotificationNow(Context context, Map<String, String> map) {
        //false表示拦截，true表示不拦截，请根据进行拦截，拦截后会执行到 onNotificationReceivedInApp
        return false;
    }
}
```

拦截的推送通知，点击和取消事件，需要您自行上报，上报接口见[自建通知统计上报接口](https://help.aliyun.com/document_detail/434667.html)。

### **8 处理推送消息（建议）**

推送支持的消息有两类，其中推送通知，在SDK有默认实现，消息到达手机后会通过通知栏展示；而推送消息需要您扩展API实现。只要扩展`MessageReceiver`/`AliyunMessageIntentService`的`onMessage`接口即可。下面以`MessageReceiver`的扩展方式来举例。

Kotlin

```
class Kk: MessageReceiver() {
    override fun onMessage(context: Context?, cPushMessage: CPushMessage?) {
        val title = cPushMessage?.title
        val content = cPushMessage?.content

        if (isForeground) {
            //App处于前台，弹窗形式显示
        } else {
            //App处于后台，通知形式显示
        }
    }
}
```

Java

```
public class MyMessageReceiver extends MessageReceiver {
    @Override
    protected void onMessage(Context context, CPushMessage cPushMessage) {
        String title = cPushMessage.getTitle();
        String content = cPushMessage.getContent();

        if (isForeground) {
            //App处于前台，弹窗形式显示
        } else {
            //App处于后台，通知形式显示
        }
    }
}
```

对于推送消息的点击和取消事件，需要您自行上报，上报接口见[自建通知统计上报接口](https://help.aliyun.com/document_detail/434667.html)。

### **9 使用标签进行推送（可选）**

除了全量推送，我们也支持使用标签进行批量推送。在通过标签进行推送前，SDK侧需要将设备绑定上标签，相关接口见[标签管理接口](https://help.aliyun.com/document_detail/434724.html)。代码示例如下：

Kotlin

```
PushServiceFactory.getCloudPushService()
    .bindTag(CloudPushService.DEVICE_TARGET, arrayOf(tag), null, object : CommonCallback {
        override fun onSuccess(s: String) {}
        override fun onFailed(errorCode: String, errorMsg: String) {}
    })
```

Java

```
PushServiceFactory.getCloudPushService().bindTag(CloudPushService.DEVICE_TARGET, new String[]{tag}, null, new CommonCallback() {
    @Override
    public void onSuccess(String s) {

    }

    @Override
    public void onFailed(String errorCode, String errorMsg) {

    }
});
```

除了使用标签进行推送外，我们还支持：

- [使用账号进行推送](https://help.aliyun.com/document_detail/434663.html)
- [使用别名进行推送](https://help.aliyun.com/document_detail/434664.html)

### **10 混淆配置**

如果您的项目中使用Proguard等工具做了代码混淆，请保留以下配置：

```
-keepclasseswithmembernames class ** {
    native <methods>;
}
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }
-keep class com.taobao.** {*;}
-keep class com.alibaba.** {*;}
-keep class com.alipay.** {*;}
-keep class com.ut.** {*;}
-keep class com.ta.** {*;}
-keep class anet.**{*;}
-keep class anetwork.**{*;}
-keep class org.android.spdy.**{*;}
-keep class org.android.agoo.**{*;}
-keep class android.os.**{*;}
-keep class org.json.**{*;}
-dontwarn com.taobao.**
-dontwarn com.alibaba.**
-dontwarn com.alipay.**
-dontwarn anet.**
-dontwarn org.android.spdy.**
-dontwarn org.android.agoo.**
-dontwarn anetwork.**
-dontwarn com.ut.**
-dontwarn com.ta.**
```

## **第三步：接入厂商通道**

为了提高App离线状态下的推送到达率，建议集成厂商通道。请参考[厂商通道集成](https://help.aliyun.com/document_detail/434677.html)进行集成。

**重要**

厂商通道的版本和推送SDK的版本有对应关系，具体信息请查看[厂商通道SDK版本关系说明](https://help.aliyun.com/document_detail/434659.html#p-bx7-84s-kgx)。

## **第四步：接入验证**

### **1 打开SDK日志**

Kotlin

```
val pushService = PushServiceFactory.getCloudPushService()
//仅适用于Debug包，正式包不需要此行
pushService.setLogLevel(CloudPushService.LOG_DEBUG)
```

Java

```
CloudPushService pushService = PushServiceFactory.getCloudPushService();
//仅适用于Debug包，正式包不需要此行
pushService.setLogLevel(CloudPushService.LOG_DEBUG);
```

### **2 启动正常确认方法**

- 回调方法`callback.onSuccess()`被调用。在logcat日志中，输入tag：MPS：

```
2024-07-03 10:34:48.630 14509-9747  [MPS]                   com.aliyun.emas.pocdemo              I  agoo init success.
2024-07-03 10:34:48.631 14509-9749  [MPS]                   com.aliyun.emas.pocdemo              D  register agoo result 错误码：PUSH_00000, 错误：success
2024-07-03 10:34:48.631 14509-9749  MPS:AppRegister         com.aliyun.emas.pocdemo              I  connState=2;estimatedTime=384;response{msg: success, code: PUSH_00000}
2024-07-03 10:34:48.631 14509-9749  MPS:AppRegister         com.aliyun.emas.pocdemo              D  Looping handleMessage: 1
2024-07-03 10:34:48.631 14509-14509 [MPS]                   com.aliyun.emas.pocdemo              I  errorCode:错误码：PUSH_00000, 错误：success
```

- 确认cloud channel初始化正常，在logcat日志中，输入awcn关键字：

```
2024-07-03 10:36:57.464  8890-10129 EMASNAccs_NetworkSdk    com.aliyun.emas.pocdemo              I  [awcn.TnetSpdySession]  statusCode:200
2024-07-03 10:36:57.465  8890-10129 EMASNAccs_NetworkSdk    com.aliyun.emas.pocdemo              I  [awcn.TnetSpdySession]  response headers:{date=[Wed, 03 Jul 2024 02:36:57 GMT], content-length=[0], server=[Tengine/Aserver/3.0.413_20221027005707], s-accs-retcode=[SUCCESS], :status=[200], x-workerid=[360290169770599862], x-at=[ZoS4k5ejQckDADGQGBlEBBUB3347866231719988617]}
2024-07-03 10:36:57.467  8890-10129 EMASNAccs_NetworkSdk    com.aliyun.emas.pocdemo              E  [awcn.Session]|[seq:334786623.AWCN1_1] notifyStatus status:AUTH_SUCC
```

- 确认deviceId获取正常：在初始化成功后使用`cloudPushService.getDeviceId()`可以成功获取deviceId。
- 如果注册服务器连接失败，则调用`callback.onFailed()`方法，并且自动进行重新注册，直到onSuccess为止（重试规则会由网络切换等时机自动触发）。在`onFailed()`方法中，会由相应的错误码返回，可参考[错误处理](https://help.aliyun.com/document_detail/434686.html#topic-1824037)。

**说明**

市场上部分手机，对Log显示做了限制，比如华为手机，屏蔽了Debug和Verbose级别的日志；建议开发时使用Info级别日志。日志级别设置请参考[基础配置接口](https://help.aliyun.com/document_detail/434662.html#topic-1824035)中的“设置日志等级”。

## **注意事项**

### **1 Android 8+适配**

请参考[Android 8.0以上设备接收不到推送通知](https://help.aliyun.com/document_detail/67398.html)。

### **2 Android 13适配**

Android 13新增了POST\_NOTIFICATIONS权限，推送3.8.4 SDK已经添加了该权限，如果业务方的APP的targetSdk低于33，只需要升级至3.8.4版本即可，应用启动后系统会自动弹出授权框；如果targetSdk是33，则需要业务方APP在运行时主动申请POST\_NOTIFICATIONS权限。

## 非手机场景说明

在一些特定设备上，使用场景和手机场景不同，可以进行一些特别的配置，主要分为两类：一类指类似手机应用，当用户使用时应用才会运行，用户不使用时，可能会被系统回收，另一类是系统级别应用，会长时间运行。

### **类手机应用配置**

这种场景，可以开启channel进程心跳，提高通道的稳定性。

Kotlin

```
val pushInitConfig = PushInitConfig.Builder()
    .application(application)
    // 开启channel进程
    .disableChannelProcess(false)
    // 开启channel进程心跳
    .disableChannelProcessHeartbeat(false)
    .build()

PushServiceFactory.init(pushInitConfig)

val pushService = PushServiceFactory.getCloudPushService()
pushService.register(this, object : com.alibaba.sdk.android.push.CommonCallback {
    override fun onSuccess(success: String) {}
    override fun onFailed(errorCode: String, errorMessage: String) {}
})
```

Java

```
PushInitConfig pushInitConfig = new PushInitConfig.Builder()
        .application(application)
        // 开启channel进程
        .disableChannelProcess(false)
        // 开启channel进程心跳
        .disableChannelProcessheartbeat(false)
        .build()

PushServiceFactory.init(pushInitConfig);

CloudPushService pushService = PushServiceFactory.getCloudPushService();
pushService.register(applicationContext, new CommonCallback() {
    @Override
    public void onSuccess(String response) {
        
    }
    @Override
    public void onFailed(String errorCode, String errorMessage) {
        
    }
});
```

如果系统不支持JobService（Android API版本低于21），还需要添加WAKE\_LOCK权限。

```
<!-- 设备 Android API版本低于21 添加WAKE_LOCK权限 -->
<uses-permission android:name="android.permission.WAKE_LOCK"/>
```

### **长时间运行的系统应用**

此类应用因为是一直运行的，根据系统性能要求，可以考虑不使用channel进程推送通道，完全使用应用内推送通道。

Kotlin

```
val pushInitConfig = PushInitConfig.Builder()
    .application(application)
    // 根据情况禁止channel进程
    .disableChannelProcess(false)
    // 禁止channel进程心跳
    .disableChannelProcessHeartbeat(false)
    .build()

PushServiceFactory.init(pushInitConfig)

val pushService = PushServiceFactory.getCloudPushService()
pushService.register(this, object : com.alibaba.sdk.android.push.CommonCallback {
    override fun onSuccess(success: String) {}
    override fun onFailed(errorCode: String, errorMessage: String) {}
})
```

Java

```
PushInitConfig pushsInitConfig = new PushInitConfig.Builder()
        .application(application)
        // 根据情况禁止channel进程
        .disableChannelProcess(true)
        // 禁止channel进程心跳
        .disableChannelProcessheartbeat(true)
        .build();
PushServiceFactory.init(pushsInitConfig);

CloudPushService pushService = PushServiceFactory.getCloudPushService();
pushService.register(applicationContext, new CommonCallback() {
    @Override
    public void onSuccess(String response) {

    }
    @Override
    public void onFailed(String errorCode, String errorMessage) {

    }
});
```

长时间运行的应用，需要关注连接是否一直连接，可以注册自己的监听接口，并做一定的检查措施。

Kotlin

```
val handler = Handler()
val controlService = PushServiceFactory.getPushControlService()
controlService.setConnectionChangeListener(object : ConnectionChangeListener {
    override fun onConnect() {}
    override fun onDisconnect(code: String, msg: String) {
        val isNetworkIssue = !isNetworkConnected()
        // 过一段时间再检查，比如30s
        handler.postDelayed(Runnable {
            if (isNetworkConnected() && !controlService.isConnected()) {
                // 如果网络没有问题，而且连接没有恢复
                // 此时记录埋点 code 和 msg 信息，一定要记录msg信息
                recordDisconnectEvent(code, msg)
                if (isNetworkIssue) {
                    // 刚才没有网络，尝试重连
                    controlService.reconnect()
                } else {
                    // 网络没有问题，或者重连也不行，进行重置，重新进行初始化
                    controlService.reset()
                    initCloudChannel(getContext())
                }
            }
        }, 30 * 1000)
    }
})
```

Java

```
// 示意代码，请不要直接使用，请根据具体的业务情况使用api
final Handler handler = new Handler();
PushControlService controlService = PushServiceFactory.getPushControlService();
controlService.setConnectionChangeListener(new PushControlService.ConnectionChangeListener() {
    @Override
    public void onConnect() {

    }

    @Override
    public void onDisconnect(final String code, final String msg) {
        final boolean isNetworkIssue = !isNetworkConnected();
        // 过一段时间再检查，比如30s
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isNetworkConnected() && !controlService.isConnected()) {
                    // 如果网络没有问题，而且连接没有恢复
                    // 此时记录埋点 code 和 msg 信息，一定要记录msg信息
                    recordDisconnectEvent(code, msg);
                    if (isNetworkIssue) {
                        // 刚才没有网络，尝试重连
                        controlService.reconnect();
                    } else {
                        // 网络没有问题，或者重连也不行，进行重置，重新进行初始化
                        controlService.reset();
                        initCloudChannel(getContext());
                    }

                }
            }
        }, 30 * 1000);
    }
});
```

## **如何实现手动关闭与恢复长连接**

移动推送SDK注册之后长连接建立，长连接建立之后非手动处理会一直保持连接，在某些特定的场景下（例如：性能优化）需要手动控制长连接的断开和恢复，关闭和恢复长连接的实现如下：

### **关闭长连接**

实现长连接关闭代码如下：

Java

```
PushServiceFactory.getPushControlService().disconnect();
PushServiceFactory.getPushControlService().reset();
```

Kotlin

```
PushServiceFactory.getPushControlService().disconnect()
PushServiceFactory.getPushControlService().reset()
```

### **恢复长连接**

实现手动恢复长连接只需要重新注册即可，其实现代码如下：

Java

```
CloudPushService pushService = PushServiceFactory.getCloudPushService();
pushService.register(this, new com.alibaba.sdk.android.push.CommonCallback() {
    @Override
    public void onSuccess(String success) {

    }

    @Override
    public void onFailed(String errorCode, String errorMessage) {
        
    }
});
```

Kotlin

```
PushServiceFactory.getCloudPushService().register(context , object: CommonCallback{
    override fun onSuccess(success: String?) {}

    override fun onFailed(errorCode: String?, errorMessage: String?) {}

})
```

## 常见集成问题

1. UTDID冲突，可参考：[阿里云-云产品SDK UTDID冲突解决方案](https://help.aliyun.com/document_detail/59152.html?spm=5176.13194971.0.0.362c9482hcuJUH)
2. [集成Android SDK后运行App报java.lang.NoClassDefFoundError该如何解决？](https://help.aliyun.com/document_detail/56936.html)
3. [移动推送 Android 端集成失败排查文档](https://help.aliyun.com/document_detail/58993.html)
4. [推送SDK在初始化时报错](https://help.aliyun.com/document_detail/40004.html)
5. [Android SDK初始化时出现1105和10207报错怎么解决？](https://help.aliyun.com/document_detail/43825.html)
6. [在Activity中初始化推送SDK，无法接收到推送通知](https://help.aliyun.com/document_detail/52815.html)