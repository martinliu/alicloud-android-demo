# Alibaba Cloud Mobile Push Demo App for Android

[中文版](README.md)

Alibaba Cloud Mobile Push is a mobile intelligent push service based on big data. It helps apps quickly integrate mobile push features, achieving efficient, precise, and real-time mobile push while significantly reducing development costs. It enables developers to stay connected with users most effectively, thereby improving user activity and application retention.



## Product Features

-   *Efficient and stable*—Uses the same architecture as Mobile Taobao, based on Alibaba Group's highly available channel. This channel can send up to 3 billion messages per day on average, with 180 million active users currently.
-   *High delivery rate*—Android intelligent channel keep-alive, multi-channel support ensures high delivery rates.
-   *Precise push*—Based on Alibaba big data processing technology to achieve precise push.
-   *In-app message push*—Supports private channels within Android and iOS apps, ensuring high-speed delivery of transparent messages.


## Usage

### 1. Create an App

First, you need to log in to the Mobile Push console and create an App entity corresponding to the Demo App you plan to use. For guidance on creating an App, see:

>[Create Project and App](https://help.aliyun.com/document_detail/436513.html?spm=a2c4g.434660.0.0.255d4289JUbMoM#section-8am-xwe-iqh)

### 2. Download the Demo Project

Clone or download the project locally:

```shell
git clone https://github.com/aliyun/alicloud-android-demo.git
```

`mpush_android_demo` is the Mobile Push Demo App.

`mpush_android_demo` has already completed the Mobile Push SDK integration, but we still recommend that you carefully read the Mobile Push integration documentation.

>[Android SDK Configuration Documentation](https://help.aliyun.com/document_detail/51056.html)

**When you encounter issues integrating Mobile Push into your own app, you can compare the demo app configuration.**

### 3. Configure App Information

#### 3.1 Configure AppKey and AppSecret

To make the Demo App run properly, you also need to configure your appkey/appsecret information.

>[How to get your AppKey / AppSecret](https://help.aliyun.com/document_detail/436513.html?spm=a2c4g.11186623.0.0.613342899Kvoah#aa691d4160wc4)

Replace the `********` placeholders in the following `AndroidManifest.xml` snippet with your appkey/appsecret.

```xml
<meta-data android:name="com.alibaba.app.appkey" android:value="********"/> <!-- Please fill in your appKey -->
<meta-data android:name="com.alibaba.app.appsecret" android:value="********"/> <!-- Please fill in your appSecret -->
```

#### 3.2 Configure the Package Name

Change the `applicationId` parameter in the `build.gradle` file to the package name of the created App:

```gradle
android {
    compileSdkVersion 23
    buildToolsVersion "23.0.1"

    defaultConfig {
        applicationId "********" // Fill in the package name of the created App
        minSdkVersion 11
        targetSdkVersion 23
        versionCode 1
        versionName "1.0"
        
    }
    ......
}
```

### 4. Run the Program

If the program compiles successfully, click `Register and receive push` at runtime, and print logs similar to the following to indicate successful integration:

#### 4.1 The callback method callback.onSuccess() is called. In logcat, enter tag: MPS:

```
2024-07-03 10:34:48.630 14509-9747  [MPS]                   com.aliyun.emas.pocdemo              I  agoo init success.
2024-07-03 10:34:48.631 14509-9749  [MPS]                   com.aliyun.emas.pocdemo              D  register agoo result 错误码：PUSH_00000, 错误：success
2024-07-03 10:34:48.631 14509-9749  MPS:AppRegister         com.aliyun.emas.pocdemo              I  connState=2;estimatedTime=384;response{msg: success, code: PUSH_00000}
2024-07-03 10:34:48.631 14509-9749  MPS:AppRegister         com.aliyun.emas.pocdemo              D  Looping handleMessage: 1
2024-07-03 10:34:48.631 14509-14509 [MPS]                   com.aliyun.emas.pocdemo              I  errorCode:错误码：PUSH_00000, 错误：success
```
#### 4.2 Confirm cloud channel initialization is normal. In logcat, enter the awcn keyword:

```
2024-07-03 10:36:57.464  8890-10129 EMASNAccs_NetworkSdk    com.aliyun.emas.pocdemo              I  [awcn.TnetSpdySession]  statusCode:200
2024-07-03 10:36:57.465  8890-10129 EMASNAccs_NetworkSdk    com.aliyun.emas.pocdemo              I  [awcn.TnetSpdySession]  response headers:{date=[Wed, 03 Jul 2024 02:36:57 GMT], content-length=[0], server=[Tengine/Aserver/3.0.413_20221027005707], s-accs-retcode=[SUCCESS], :status=[200], x-workerid=[360290169770599862], x-at=[ZoS4k5ejQckDADGQGBlEBBUB3347866231719988617]}
2024-07-03 10:36:57.467  8890-10129 EMASNAccs_NetworkSdk    com.aliyun.emas.pocdemo              E  [awcn.Session]|[seq:334786623.AWCN1_1] notifyStatus status:AUTH_SUCC
```
#### 4.3 Confirm that deviceId is retrieved successfully: after initialization succeeds, you can use cloudPushService.getDeviceId() to get deviceId successfully.

#### 4.4 If the registration server connection fails, callback.onFailed() is called, and automatic re-registration occurs until onSuccess (the retry rules are triggered automatically by network switching and other events).

## FAQ

### Integration Issues
- If you cannot receive push notifications, please check system version differences and refer to the precautions for handling
- Ensure AppKey and AppSecret are configured correctly
- Check network connection and firewall settings

### Vendor Channels
Accessing vendor channels can improve push delivery rates when the app is offline. Supported vendor channels include:
- Huawei Push
- Xiaomi Push  
- OPPO Push
- vivo Push
- Meizu Push

## Contact Us

-   Official website: [Mobile Push](https://www.aliyun.com/product/cps)
