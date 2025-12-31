# Getting Start Guide for Android

## **Preface**

This section introduces how to integrate the Mobile Push Android SDK.

- Gradle-managed Android Studio projects are recommended.
- Android 5.0 and later are supported.
- This document is only applicable to Mobile Push Android SDK V3.0.0 and later.

**Important**

The Mobile Push SDK includes two types of push channels: the EMAS long connection channel and vendor channels. This document mainly introduces the EMAS long connection channel integration.

Integrating vendor channels can improve push delivery while the app is offline. See [Step 3: Integrate vendor channels](https://emas.console.aliyun.com/#4f3bd0e3a1mki).

For the Mobile Push Android SDK demo, see: [Mobile Push Android Demo](https://github.com/aliyun/alicloud-android-demo/tree/master/mpush_android_demo).

The demo is built with Kotlin + MVVM, has a more polished UI and richer features, and can help you quickly understand how to use the Mobile Push Android SDK.

## **Preparation**

- You have created a project and an app. See [Create a project and app](https://help.aliyun.com/document_detail/436513.html#section-8am-xwe-iqh).
- You have read [Android SDK version notes](https://help.aliyun.com/document_detail/434659.html#topic-1996989) and obtained the latest version mapping.

## **Step 1: Add the SDK to your app**

We provide two integration methods: Maven dependencies and local dependencies. Choose one based on your needs.

**Note**

We recommend integrating via Maven dependencies. It is simpler, less error-prone, and easier to update later.

### **1 Maven dependency method**

#### 1.1 Configure Maven repositories

Below are the recommended `dependencyResolutionManagement` configuration for Gradle 7.0+ and the `allprojects` configuration for Gradle below 7.0.

##### 1.1.1 dependencyResolutionManagement method

In your root (project-level) Gradle file (`<project>/settings.gradle`), add the Maven repository URLs under `dependencyResolutionManagement.repositories`.

```
dependencyResolutionManagement {
  repositories {
    maven {
      url 'https://maven.aliyun.com/nexus/content/repositories/releases/'
    }

    // Add the HMS Core SDK Maven repository for Huawei channel integration.
    maven {
      url 'https://developer.huawei.com/repo/'
    }
  }
}
```

##### 1.1.2 allprojects method

In your root (project-level) Gradle file (`<project>/build.gradle`), add the Maven repository URLs under `allprojects.repositories`.

```
allprojects {
  repositories {
    maven {
      url 'https://maven.aliyun.com/nexus/content/repositories/releases/'
    }

    // Add the HMS Core SDK Maven repository for Huawei channel integration.
    maven {
      url 'https://developer.huawei.com/repo/'
    }
  }
}
```

#### 1.2 Add SDK dependencies

In your module (app-level) Gradle file (usually `<project>/<app-module>/build.gradle`), add the SDK dependency under `dependencies`.

```
dependencies {
  implementation 'com.aliyun.ams:alicloud-android-push:{pushVersion}'
}
```

**Important**
- Get `pushVersion` from [Android SDK version notes](https://help.aliyun.com/document_detail/434659.html#topic-1996989).
- Use a fixed version number. Do not use dynamic versions such as `3.+` or `3.2.+`.

### 2 Local dependency method

#### **2.1 Download the SDK**

Refer to [Quick start](https://help.aliyun.com/document_detail/436513.html#aa69447860e8c), select Mobile Push for download, and copy all SDK files into your module (app-level) `<project>/<app-module>/libs` directory.

#### **2.2 Add SDK dependencies**

##### **2.2.1 Configure the local SDK directory**

In your module (app-level) Gradle file (usually `<project>/<app-module>/build.gradle`), add the local SDK directory.

```
repositories {
  flatDir {
    dirs 'libs'
  }
}
```

#### 2.2.2 Add SDK dependencies

In your module (app-level) Gradle file (usually `<project>/<app-module>/build.gradle`), add the SDK dependencies under `dependencies`.

```
dependencies {
  implementation fileTree(include: ['*.jar'], dir: 'libs')
  implementation (name:'alicloud-android-push-3.x.x', ext: 'aar')
  implementation (name:'accs_sdk_taobao-4.x.x', ext: 'aar')
  // Add all AARs
  ...
}
```

**Important**
- Use the SDK version numbers from the downloaded package file names.
- If you see class conflicts, confirm that `implementation fileTree(dir: 'libs', include: ['*.jar'])` is present under dependencies.
- Vendor channel SDKs do not support local dependencies; only Maven dependencies are supported.

## **Step 2: Configure the SDK**

### **1 Configure AppKey and AppSecret**

There are two ways to configure AppKey and AppSecret: via AndroidManifest or via code. Choose one based on your needs.

**Note**
- To avoid leaking `appkey`/`appsecret` in logs or runtime data, disable SDK debug logs in production.
- To prevent reverse engineering from exposing AppKey and AppSecret, use the code configuration method, enable obfuscation, and apply app hardening before release.
- If you are a Baichuan Cloud Push user, you cannot directly use Baichuan AppKey/AppSecret. Log in to the [EMAS Console](https://emas.console.aliyun.com/products) with your Baichuan account and use the AppKey/AppSecret from EMAS.

#### **1.1 AndroidManifest configuration**

Configure AppKey and AppSecret in `AndroidManifest.xml`. Add the following `meta-data` under the `application` node.

**Note**
- `com.alibaba.app.appkey` and `com.alibaba.app.appsecret` are the AppKey/AppSecret for your EMAS app. You can find them in the EMAS Console app management or in the downloaded config file.
- AppKey and AppSecret must be under the `application` tag, otherwise the SDK will report missing AppKey.

```
<application android:name="*****">
    <!-- Please fill in your own appKey -->
    <meta-data android:name="com.alibaba.app.appkey" android:value="*****"/> 
    <!-- Please fill in your own appSecret -->
    <meta-data android:name="com.alibaba.app.appsecret" android:value="****"/> 
</application>
```

#### **1.2 Code configuration**

Besides AndroidManifest configuration, you can also configure AppKey and AppSecret in code.

Kotlin

```
val pushInitConfig = PushInitConfig.Builder()
    .application(application)
    .appKey(appKey)    // Please fill in your own appKey
    .appSecret(appSecret)    // Please fill in your own appSecret
    .build()
```

Java

```
PushInitConfig pushInitConfig = new PushInitConfig.Builder()
        .application(application)
        .appKey(appKey)    // Please fill in your own appKey
        .appSecret(appSecret)    // Please fill in your own appSecret
        .build();
```

For more configuration APIs, see [Basic configuration APIs](https://help.aliyun.com/document_detail/434662.html) and [Advanced configuration APIs](https://help.aliyun.com/document_detail/2834534.html).

### **2 SDK initialization**

To minimize impact on app startup, initialization can be split into stages.

#### **2.1 Logic that must run in Application onCreate**

This stage initializes push parameters without starting the push connection. It must run in Application onCreate.

Based on your AppKey/AppSecret configuration method, there are two ways to initialize.

#### AndroidManifest configuration method

Kotlin

```
PushServiceFactory.init(context)
```

Java

```
PushServiceFactory.init(context);
```

#### Code configuration method

Kotlin

```
PushServiceFactory.init(pushInitConfig)
```

Java

```
PushServiceFactory.init(pushInitConfig);
```

**Note**

If you configured AppKey/AppSecret in AndroidManifest.xml, you can still call `PushServiceFactory.init(pushInitConfig)`. If AppKey/AppSecret are also set in `PushInitConfig`, those values take precedence.

#### **2.2 Logic that can be delayed**

This stage establishes the long connection. You can delay it based on business and compliance needs.

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

**Important**

PushServiceFactory.init must run on the Application main thread. Do not call it in an Activity or initialize asynchronously. Mobile Push will start the background channel process during initialization; both the app process and the channel process must reach PushServiceFactory.init.

### **3 Create notification channels**

Apps on Android 8.0+ must create NotificationChannel on the client side. Example:

Kotlin

```
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    // Notification channel ID.
    val channelId = "vibration_sound" // Provide this ID to backend/ops. It must match AndroidNotificationChannel when pushing.
    // User-visible channel name.
    val name: CharSequence = "My test channel"
    // User-visible channel description.
    val description = "My test channel"
    val importance = NotificationManager.IMPORTANCE_HIGH
    val channel = NotificationChannel(channelId, name, importance)
    
    // Configure channel attributes.
    channel.description = description
    // Enable lights (if supported by the device).
    channel.enableLights(true)
    channel.lightColor = Color.RED
    // Enable vibration (if supported by the device).
    channel.enableVibration(true)
    // Custom sound
    channel.setSound(
        Uri.parse("android.resource://${packageName}/${R.raw.push_hongbao}"),
        Notification.AUDIO_ATTRIBUTES_DEFAULT
    )
    channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
    // Finally, create the channel.
    notificationManager.createNotificationChannel(channel)
}
```

Java

```
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    // Notification channel ID.
    String channelId = "vibration_sound"; // Provide this ID to backend/ops. It must match AndroidNotificationChannel when pushing.
    // User-visible channel name.
    CharSequence name = "My test channel";
    // User-visible channel description.
    String description = "My test channel";
    int importance = NotificationManager.IMPORTANCE_HIGH;
    NotificationChannel mChannel = new NotificationChannel(channelId, name, importance);
    // Configure channel attributes.
    mChannel.setDescription(description);
    // Enable lights (if supported by the device).
    mChannel.enableLights(true);
    mChannel.setLightColor(Color.RED);
    // Enable vibration (if supported by the device).
    mChannel.enableVibration(true);
    // Custom sound
    mChannel.setSound(Uri.parse("android.resource://"
            + this.getPackageName() + "/" + R.raw.push_hongbao), Notification.AUDIO_ATTRIBUTES_DEFAULT);
    mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
    // Finally, create the channel.
    mNotificationManager.createNotificationChannel(mChannel);
}
```

When pushing, the `AndroidNotificationChannel` parameter in [Push - Advanced push](https://help.aliyun.com/document_detail/2249916.html) must match the client `channelId`. Otherwise, notifications will not be displayed.

**Note**

For vivo devices, you must apply for a channel on the vivo open platform before creating a channel on the client. The channel ID must match the client `channelId`. See [Add channel](https://dev.vivo.com.cn/documentCenter/doc/930#s-lo7yeb24).

### 4 **Message receiving configuration**

At this point, the SDK is initialized and the long connection is established. To receive messages, you still need configuration.

We provide two options: `MessageReceiver` and `AliyunMessageIntentService`. They allow you to intercept notifications, receive messages, and get extension fields. You can also handle notification open/delete events.

You can choose either option. See [Message/notification handling APIs](https://help.aliyun.com/document_detail/434669.html).

**Important**
- After completing this step, your app should be able to receive push messages.
- If some devices **cannot receive notifications**, it may be due to system differences. See [Notes](https://emas.console.aliyun.com/#6ea57dcfb9jfv).
- Register your custom default `MessageReceiver` or `AliyunMessageIntentService` to receive default-style notifications. For **custom styles, foreground interception, or transparent messages**, extend the relevant APIs.

### **5 NDK configuration**

In your module (app-level) Gradle file (usually `<project>/<app-module>/build.gradle`), add NDK configuration under `android.defaultConfig`.

```
android {
    defaultConfig {
        ndk {
            // Select the .so types to include. This is an example; push supports all major ABIs.
            abiFilters 'arm64-v8a', 'armeabi-v7a', 'x86', 'x86_64'
        }
    }
}
```

### **6 Custom notification styles (optional)**

If you need custom notification styles, see [Custom notification style APIs](https://help.aliyun.com/document_detail/2834944.html).

### **7 Intercept push notifications (optional)**

To intercept push notifications, extend `showNotificationNow` and `onNotificationReceivedInApp` in `MessageReceiver` or `AliyunMessageIntentService`. Example with `MessageReceiver`:

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
        // Handle incoming push notification here
    }

    override fun showNotificationNow(p0: Context?, p1: MutableMap<String, String>?): Boolean {
        // false = intercept, true = do not intercept; intercepting triggers onNotificationReceivedInApp
        return false
    }
}
```

Java

```
public class MyMessageReceiver extends MessageReceiver {
    @Override
    protected void onNotificationReceivedInApp(Context context, String title, String summary, Map<String, String> map, int openType, String openActivity, String openUrl) {
        // Handle incoming push notification here
    }

    @Override
    public boolean showNotificationNow(Context context, Map<String, String> map) {
        // false = intercept, true = do not intercept; intercepting triggers onNotificationReceivedInApp
        return false;
    }
}
```

For intercepted notifications, you must report click and cancel events yourself. See [Custom notification statistics reporting API](https://help.aliyun.com/document_detail/434667.html).

### **8 Handle push messages (recommended)**

There are two types of push content. Notifications have a default SDK implementation and appear in the notification tray. Push messages require API extensions. Extend `onMessage` in `MessageReceiver` or `AliyunMessageIntentService`. Example with `MessageReceiver`:

Kotlin

```
class Kk: MessageReceiver() {
    override fun onMessage(context: Context?, cPushMessage: CPushMessage?) {
        val title = cPushMessage?.title
        val content = cPushMessage?.content

        if (isForeground) {
            // App in foreground: show as dialog
        } else {
            // App in background: show as notification
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
            // App in foreground: show as dialog
        } else {
            // App in background: show as notification
        }
    }
}
```

For push message click/cancel events, you must report them yourself. See [Custom notification statistics reporting API](https://help.aliyun.com/document_detail/434667.html).

### **9 Push by tags (optional)**

In addition to full push, you can push by tags. Before pushing by tags, bind tags on the device using the SDK. See [Tag management APIs](https://help.aliyun.com/document_detail/434724.html). Example:

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

You can also:

- [Push by account](https://help.aliyun.com/document_detail/434663.html)
- [Push by alias](https://help.aliyun.com/document_detail/434664.html)

### **10 ProGuard configuration**

If your project uses ProGuard or similar, keep the following rules:

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

## **Step 3: Integrate vendor channels**

To improve offline delivery, integrate vendor channels. See [Vendor channel integration](https://help.aliyun.com/document_detail/434677.html).

**Important**

Vendor channel SDK versions must match the push SDK version. See [Vendor channel SDK version mapping](https://help.aliyun.com/document_detail/434659.html#p-bx7-84s-kgx).

## **Step 4: Integration verification**

### **1 Enable SDK logs**

Kotlin

```
val pushService = PushServiceFactory.getCloudPushService()
// Only for Debug builds, not needed for release builds
pushService.setLogLevel(CloudPushService.LOG_DEBUG)
```

Java

```
CloudPushService pushService = PushServiceFactory.getCloudPushService();
// Only for Debug builds, not needed for release builds
pushService.setLogLevel(CloudPushService.LOG_DEBUG);
```

### **2 How to verify normal startup**

- The callback `callback.onSuccess()` is invoked. In logcat, filter by tag: `MPS`:

```
2024-07-03 10:34:48.630 14509-9747  [MPS]                   com.aliyun.emas.pocdemo              I  agoo init success.
2024-07-03 10:34:48.631 14509-9749  [MPS]                   com.aliyun.emas.pocdemo              D  register agoo result 错误码：PUSH_00000, 错误：success
2024-07-03 10:34:48.631 14509-9749  MPS:AppRegister         com.aliyun.emas.pocdemo              I  connState=2;estimatedTime=384;response{msg: success, code: PUSH_00000}
2024-07-03 10:34:48.631 14509-9749  MPS:AppRegister         com.aliyun.emas.pocdemo              D  Looping handleMessage: 1
2024-07-03 10:34:48.631 14509-14509 [MPS]                   com.aliyun.emas.pocdemo              I  errorCode:错误码：PUSH_00000, 错误：success
```

- Confirm cloud channel initialization by filtering logcat with `awcn`:

```
2024-07-03 10:36:57.464  8890-10129 EMASNAccs_NetworkSdk    com.aliyun.emas.pocdemo              I  [awcn.TnetSpdySession]  statusCode:200
2024-07-03 10:36:57.465  8890-10129 EMASNAccs_NetworkSdk    com.aliyun.emas.pocdemo              I  [awcn.TnetSpdySession]  response headers:{date=[Wed, 03 Jul 2024 02:36:57 GMT], content-length=[0], server=[Tengine/Aserver/3.0.413_20221027005707], s-accs-retcode=[SUCCESS], :status=[200], x-workerid=[360290169770599862], x-at=[ZoS4k5ejQckDADGQGBlEBBUB3347866231719988617]}
2024-07-03 10:36:57.467  8890-10129 EMASNAccs_NetworkSdk    com.aliyun.emas.pocdemo              E  [awcn.Session]|[seq:334786623.AWCN1_1] notifyStatus status:AUTH_SUCC
```

- Confirm `deviceId` is available after initialization by calling `cloudPushService.getDeviceId()`.
- If the registration server connection fails, `callback.onFailed()` is invoked and automatic retries will occur until success (retries are triggered by events such as network changes). The error code returned in `onFailed()` can be referenced in [Error handling](https://help.aliyun.com/document_detail/434686.html#topic-1824037).

**Note**

Some devices restrict log display (for example, Huawei hides Debug/Verbose logs). Use Info-level logs during development. See the "Set log level" section in [Basic configuration APIs](https://help.aliyun.com/document_detail/434662.html#topic-1824035).

## **Notes**

### **1 Android 8+ compatibility**

See [Android 8.0+ devices cannot receive push notifications](https://help.aliyun.com/document_detail/67398.html).

### **2 Android 13 compatibility**

Android 13 introduces the POST_NOTIFICATIONS permission. Push SDK 3.8.4 already includes this permission. If your app targetSdk is below 33, upgrade to 3.8.4 and the system will prompt on app start. If targetSdk is 33, you must request POST_NOTIFICATIONS at runtime.

## Non-phone scenario notes

On some devices, usage differs from phones. There are two main types: phone-like apps (only run when in use and may be killed when not in use) and system-level apps (run for long periods).

### **Phone-like app configuration**

In this scenario, you can enable channel process heartbeat to improve channel stability.

Kotlin

```
val pushInitConfig = PushInitConfig.Builder()
    .application(application)
    // Enable channel process
    .disableChannelProcess(false)
    // Enable channel process heartbeat
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
        // Enable channel process
        .disableChannelProcess(false)
        // Enable channel process heartbeat
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

If JobService is not supported (Android API < 21), add WAKE_LOCK permission.

```
<!-- Add WAKE_LOCK permission for devices with Android API < 21 -->
<uses-permission android:name="android.permission.WAKE_LOCK"/>
```

### **Long-running system apps**

These apps run continuously. Depending on system performance requirements, you can avoid using the channel process and rely on the in-app push channel only.

Kotlin

```
val pushInitConfig = PushInitConfig.Builder()
    .application(application)
    // Disable channel process as needed
    .disableChannelProcess(false)
    // Disable channel process heartbeat
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
        // Disable channel process as needed
        .disableChannelProcess(true)
        // Disable channel process heartbeat
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

Long-running apps should monitor connection status. You can register your own listener and add checks.

Kotlin

```
val handler = Handler()
val controlService = PushServiceFactory.getPushControlService()
controlService.setConnectionChangeListener(object : ConnectionChangeListener {
    override fun onConnect() {}
    override fun onDisconnect(code: String, msg: String) {
        val isNetworkIssue = !isNetworkConnected()
        // Check again after some time, e.g., 30s
        handler.postDelayed(Runnable {
            if (isNetworkConnected() && !controlService.isConnected()) {
                // Network is fine but connection not recovered
                // Record code and msg, especially msg
                recordDisconnectEvent(code, msg)
                if (isNetworkIssue) {
                    // No network previously, try reconnect
                    controlService.reconnect()
                } else {
                    // Network OK but still down, reset and re-init
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
// Sample code. Do not use directly; adjust to your business scenario.
final Handler handler = new Handler();
PushControlService controlService = PushServiceFactory.getPushControlService();
controlService.setConnectionChangeListener(new PushControlService.ConnectionChangeListener() {
    @Override
    public void onConnect() {

    }

    @Override
    public void onDisconnect(final String code, final String msg) {
        final boolean isNetworkIssue = !isNetworkConnected();
        // Check again after some time, e.g., 30s
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isNetworkConnected() && !controlService.isConnected()) {
                    // Network is fine but connection not recovered
                    // Record code and msg, especially msg
                    recordDisconnectEvent(code, msg);
                    if (isNetworkIssue) {
                        // No network previously, try reconnect
                        controlService.reconnect();
                    } else {
                        // Network OK but still down, reset and re-init
                        controlService.reset();
                        initCloudChannel(getContext());
                    }

                }
            }
        }, 30 * 1000);
    }
});
```

## **How to manually close and restore the long connection**

After registration, the long connection is established and remains connected unless manually handled. In some scenarios (for example, performance optimization), you may want to close and restore it manually.

### **Close the long connection**

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

### **Restore the long connection**

To restore the long connection, register again:

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

## Common integration issues

1. UTDID conflict: see [Alibaba Cloud SDK UTDID conflict solution](https://help.aliyun.com/document_detail/59152.html?spm=5176.13194971.0.0.362c9482hcuJUH)
2. [NoClassDefFoundError after integrating Android SDK](https://help.aliyun.com/document_detail/56936.html)
3. [Mobile Push Android integration troubleshooting](https://help.aliyun.com/document_detail/58993.html)
4. [Errors during push SDK initialization](https://help.aliyun.com/document_detail/40004.html)
5. [Errors 1105 and 10207 during Android SDK initialization](https://help.aliyun.com/document_detail/43825.html)
6. [Cannot receive push notifications when initializing in Activity](https://help.aliyun.com/document_detail/52815.html)
