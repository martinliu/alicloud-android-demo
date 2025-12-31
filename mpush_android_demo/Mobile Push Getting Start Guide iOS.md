---
title: "移动研发平台 EMAS 控制台"
source: "https://emas.console.aliyun.com/downloadSdk"
author:
published:
created: 2025-12-31
description:
tags:
  - "clippings"
---
本文档指导开发者完成阿里云移动推送iOS SDK的集成配置，包含SDK初始化、通知权限申请、消息接收处理等核心流程。

## 一、前提条件

- 已完成EMAS应用创建并获取凭证
	- 登录[EMAS控制台](https://emas.console.aliyun.com/)
	- 完成iOS应用创建，获取AppKey和AppSecret
	- 参考[快速入门文档](https://help.aliyun.com/document_detail/436513.html#cafc7340efb0u)
- 苹果推送证书配置
	- 已完成APNs证书制作与上传
	- 参考[iOS 配置推送证书指南](https://help.aliyun.com/document_detail/434701.html#topic-1824039)
- 开发环境要求
	- **Xcode**：12.0 或更高版本
	- **iOS 部署目标**：iOS 12.0 及以上
	- **CocoaPods**：1.11.0 或更高版本
	- **Swift 版本**：支持 Swift 5.x，**不支持 Swift 6**

## 二、集成步骤

### **2.1 添加SDK依赖**

**警告**

**版本升级须知**

- **如果您当前版本低于**`**1.9.9.3**`**，请勿直接升级到**`**2.2.0**`**、**`**2.2.1**`**、**`**3.0.0**`**，否则将导致设备ID重置。**
- **如果您的 AppKey 为早期创建的双端应用（Android和iOS是同一个AppKey），则暂不支持升级至3.0.0 及以上版本。建议您为 Android 和 iOS 平台分别创建独立应用，获取各自的 AppKey，并使用最新版 SDK 重新接入。**

## **方式一：Cocoapods集成（推荐）**

1. 创建/修改Podfile：
	```
	source 'https://github.com/CocoaPods/Specs.git'
	source 'https://github.com/aliyun/aliyun-specs.git'
	platform :ios, '11.0'
	use_frameworks!
	target 'YourTarget' do
	  pod 'AlicloudPush', '~> 3'
	end
	```
	**说明**
	在Xcode项目的根目录中，定位并编辑Podfile文件，以添加AlicloudPush依赖项。如果根目录中没有Podfile文件，可以通过在终端中运行`pod init`命令来创建一个新的Podfile。如果您的计算机上尚未安装CocoaPods，请先参考[CocoaPods官网](https://guides.cocoapods.org/using/getting-started.html)完成安装。
2. 执行安装命令：
	```
	pod repo update AliyunRepo
	pod install
	# 如果您尚未添加阿里云Cocoapods仓库，请先执行以下命令添加仓库
	# pod repo add AliyunRepo https://github.com/aliyun/aliyun-specs.git
	```

## **方式二：手动集成**

1. 参考[快速入门](https://help.aliyun.com/document_detail/436513.html#c8647b35abzpi)文档下载最新SDK包
2. 解压并添加Framework：
	1. 将`CloudPushSDK.xcframework`和`AlicloudELS.xcframework`拖入工程。
	2. 详细操作步骤如下图所示。![image](https://help-static-aliyun-doc.aliyuncs.com/assets/img/zh-CN/3693092471/p918905.png)

**说明**
- **依赖说明**
	- 版本3.0.0及以上：
		- 包含CloudPushSDK 和 AlicloudELS 两个依赖库。
	- 版本2.2.0至3.0.0：
		- 包含CloudPushSDK一个依赖库。
	- 版本2.2.0以下：
		- 包含CloudPushSDK、UTDID 和 AlicloudUtils 三个依赖库。
- **Xcode兼容性**
	使用较低版本的 Xcode 时，可能需要手动添加以下系统库以确保兼容性：
	- libresolv.tbd
	- CoreTelephony.framework
	- SystemConfiguration.framework
	- libsqlite3.tbd
- **链接设置**
	如果在运行时遇到问题，可以尝试在以下位置添加 -ObjC 链接标志：
	1. 打开项目设置。
	2. 导航到 TARGETS。
	3. 选择 Build Settings。
	4. 找到 Linking 部分。
	5. 在 Other Linker Flags 中添加 -ObjC。

### **2.2 SDK初始化**

在`AppDelegate`中完成初始化。示例代码如下：

Swift

```
import CloudPushSDK

@main
class AppDelegate: UIResponder, UIApplicationDelegate {
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        
        CloudPushSDK.setLogLevel(MPLogLevel.info);
        
        // SDK初始化
        CloudPushSDK.start(withAppkey: "您的AppKey", appSecret: "您的AppSecret") { res in
            if res.success {
                print("SDK初始化成功 | DeviceID: \(CloudPushSDK.getDeviceId() ?? "N/A")")
            } else {
                print("初始化失败: \(res.error?.localizedDescription ?? "未知错误")")
            }
        }
        
        return true
    }
}
```

Object C

```
#import "CloudPushSDK/CloudPushSDK.h"

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {

    [CloudPushSDK setLogLevel:MPLogLevelInfo];
    
    // SDK初始化
    [CloudPushSDK startWithAppkey:@"您的AppKey" 
                        appSecret:@"您的AppSecret" 
                        callback:^(CloudPushCallbackResult *result) {
        if (result.success) {
            NSLog(@"SDK初始化成功 | DeviceID: %@", [CloudPushSDK getDeviceId]);
        } else {
            NSLog(@"初始化失败: %@", result.error);
        }
    }];
    
    return YES;
}
@end
```

### **2.3** 配置APNs推送能力

开启推送权限，向苹果服务器申请推送令牌，并上报到阿里云推送服务器。

1. Xcode工程配置：
	1. Target → Signing & Capabilities → 添加 Push Notifications 和 Background Modes 两个Capability
	2. Background Modes → 勾选Remote notifications![image](https://help-static-aliyun-doc.aliyuncs.com/assets/img/zh-CN/3693092471/p926897.png)
2. 请求用户授权，参考代码如下。

Swift

```
// 请求用户授权
func setupAPNs() {
    let center = UNUserNotificationCenter.current()
    center.requestAuthorization(options: [.alert, .sound, .badge]) { granted, error in
        DispatchQueue.main.async {
            if granted {
                UIApplication.shared.registerForRemoteNotifications()
            }
            print("推送权限状态: \(granted ? "已授权" : "被拒绝")")
        }
    }
}

// APNS设备注册成功回调
func application(_ application: UIApplication,
               didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
    CloudPushSDK.registerDevice(deviceToken) { result in
        let status = result.success ? "成功" : "失败"
        print("DeviceToken上报\(status)")
    }
}
```

Object C

```
- (void)setupAPNs {
    UNUserNotificationCenter *center = [UNUserNotificationCenter currentNotificationCenter];
    
    [center requestAuthorizationWithOptions:(UNAuthorizationOptionAlert | UNAuthorizationOptionSound | UNAuthorizationOptionBadge)
                          completionHandler:^(BOOL granted, NSError * _Nullable error) {
        dispatch_async(dispatch_get_main_queue(), ^{
            if (granted) {
                [[UIApplication sharedApplication] registerForRemoteNotifications];
            }
            NSLog(@"推送权限状态: %@", granted ? @"已授权" : @"被拒绝");
        });
    }];
}

// APNS设备注册成功回调
- (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken {
    [CloudPushSDK registerDevice:deviceToken withCallback:^(CloudPushCallbackResult *result) {
        NSString *status = result.success ? @"成功" : @"失败";
        NSLog(@"DeviceToken上报%@", status);
    }];
}
```

### **2.4 消息处理**

消息是通过阿里云自有通道发送的一条包含标题和内容的透传信息。此类消息不会触发手机的铃声或震动，只有在App在线时才能接收。当App收到消息后，开发者可以根据消息内容执行相应的代码逻辑。

Swift

```
// 订阅消息
NotificationCenter.default.addObserver(self, 
                                     selector: #selector(onMessageReceived(_:)), 
                                     name: NSNotification.Name("CCPDidReceiveMessageNotification"), 
                                     object: nil)

@objc func onMessageReceived(_ notification: Notification) {
    guard let data = notification.object as? [String: Any],
          let title = data["title"] as? String,
          let content = data["content"] as? String else {
        return
    }
    print("Receive message title: \(title), content: \(content)")
}
```

Object C

```
// 订阅消息
[[NSNotificationCenter defaultCenter] addObserver:self
                                         selector:@selector(onMessageReceived:)
                                             name:@"CCPDidReceiveMessageNotification"
                                           object:nil];

- (void)onMessageReceived:(NSNotification *)notification {
    NSDictionary *data = [notification object];
    NSString *title = data[@"title"];
    NSString *content = data[@"content"];
    
    NSLog(@"Receive message title: %@, content: %@.", title, content);
}
```

### **2.5 通知处理**

通知是通过苹果服务器推送到用户的苹果手机上，并可以在锁屏状态下展示。接收通知不要求用户的App保持在线，只需手机连接到网络即可完成通知的接收和显示。开发者可以通过以下方式在App内处理通知：

#### **前台通知回调**

当App处于前台时，系统会将通知回调给开发者实现的代理方法。开发者可以在此方法中根据需要执行相应的代码逻辑，并决定是否展示通知。

#### **点击通知回调**

用户点击通知时，系统会打开并跳转到App，同时将点击事件回调给开发者实现的代理方法。

#### **静默通知回调**

当发送静默通知时，系统会在App处于前台或后台时，将通知回调给开发者实现的代理方法。

#### **示例代码**

以下是Swift和Objective-C中实现这些功能的示例代码：

Swift

```
import UIKit
import CloudPushSDK
import UserNotifications

@main
class AppDelegate: UIResponder, UIApplicationDelegate, UNUserNotificationCenterDelegate {

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // 初始化SDK
        // ......
        
        // 设置通知中心代理
        UNUserNotificationCenter.current().delegate = self
        
        return true
    }
    
    // MARK: - UNUserNotificationCenterDelegate
    
    func userNotificationCenter(_ center: UNUserNotificationCenter, 
                              willPresent notification: UNNotification,
                              withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        print("收到前台通知回调")
        handleUserInfo(userInfo: notification.request.content.userInfo)
        
        // 设置通知展示方式
        completionHandler([.alert, .sound])
    }
    
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                              didReceive response: UNNotificationResponse,
                              withCompletionHandler completionHandler: @escaping () -> Void) {
        print("收到点击通知回调")
        handleUserInfo(userInfo: response.notification.request.content.userInfo)
        completionHandler()
    }
    
    func application(_ application: UIApplication,
                   didReceiveRemoteNotification userInfo: [AnyHashable : Any],
                   fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) {
        print("收到静默通知回调")
        handleUserInfo(userInfo: userInfo)
        completionHandler(.newData)
    }
    
    func handleUserInfo(userInfo: [AnyHashable : Any]) {
        // 通过字典获取通知携带的自定义kv，例如：
        // let customValue = userInfo["customKey"] as? String
        
        guard let aps = userInfo["aps"] as? [String: Any],
              let alert = aps["alert"] as? [String: String],
              let title = alert["title"],
              let body = alert["body"] else {
            return
        }
        
        print("通知内容: title=\(title), body=\(body)")
        
        // 通知点击上报
        CloudPushSDK.sendNotificationAck(userInfo)
    }
    
    // ......
}
```

Object C

```
#import "AppDelegate.h"
#import "CloudPushSDK/CloudPushSDK.h"
#import <UserNotifications/UserNotifications.h>

@interface AppDelegate () <UNUserNotificationCenterDelegate>
@end

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    // 初始化SDK
    // ......
    
    // 设置通知中心代理
    [UNUserNotificationCenter currentNotificationCenter].delegate = self;
    
    return YES;
}

#pragma mark - 接收通知
- (void)userNotificationCenter:(UNUserNotificationCenter *)center willPresentNotification:(UNNotification *)notification withCompletionHandler:(void (^)(UNNotificationPresentationOptions))completionHandler {
    NSLog(@"收到前台通知回调");
    [self handleUserInfo:notification.request.content.userInfo];

    // 这里开发者可以根据需要决定是否弹出通知
    completionHandler(UNNotificationPresentationOptionAlert | UNNotificationPresentationOptionSound);
}

- (void)userNotificationCenter:(UNUserNotificationCenter *)center didReceiveNotificationResponse:(UNNotificationResponse *)response withCompletionHandler:(void (^)(void))completionHandler {
    NSLog(@"收到点击通知回调");
    [self handleUserInfo:response.notification.request.content.userInfo];
    completionHandler();
}

- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo fetchCompletionHandler:(void (^)(UIBackgroundFetchResult))completionHandler {
    NSLog(@"收到静默通知回调");
    [self handleUserInfo:userInfo];
    completionHandler(UIBackgroundFetchResultNewData);
}

- (void)handleUserInfo:(NSDictionary *)userInfo {
    // 可以通过字典获取通知携带的自定义kv，例如：
    // NSString *customValue = userInfo[@"customKey"];
    
    NSString *title = userInfo[@"aps"][@"alert"][@"title"];
    NSString *body = userInfo[@"aps"][@"alert"][@"body"];
    NSLog(@"通知内容: title=%@, body=%@", title, body);
    
    // 通知点击上报
    [CloudPushSDK sendNotificationAck:userInfo];
}

// ......
@end
```

### **2.6 成功运行验证**

在真机或模拟器上运行应用后，请检查Xcode控制台输出。确认是否出现类似以下日志信息：

```
2025-03-11 11:26:33.854 INFO [CloudPushSDK] Initialization successful
====================
DeviceId: a1b693a9fced4c22aab3cdfe3fbe5a05
DeviceToken: 76aa3065ca246c81ad1b640715f45f3f8d16fea38ca4fb2917cf34b7249bd803
SdkVersion: 3.0.0
====================
```

此日志表明SDK已成功初始化。接下来，您可以在EMAS推送控制台中查询设备信息。如果能查询到相应的设备信息，表示初始化成功。

- `ACCS`：表示阿里云自有通道注册成功。
- `APNS`：表示苹果推送服务注册成功。

确保上述两个通道信息均显示成功，以验证推送服务已正确配置。![image](https://help-static-aliyun-doc.aliyuncs.com/assets/img/zh-CN/5319150671/p1007702.png)

### **2.7 扩展回执SDK接入（可选）**

由于 iOS 系统本身不提供通知送达或展示的回执能力，为满足部分业务对通知触达状态的追踪需求，您可在通知服务扩展（Notification Service Extension）中接入我们提供的扩展回执 SDK（ext SDK）。该 SDK 可在通知到达设备并即将展示前，主动上报一条类型为 `ext_ack` 的回执日志，用于标识该通知已被设备接收并准备展示。

接入后，您可在回执日志中通过筛选事件类型为 ext\_ack 的记录，来近似判断通知是否成功触达用户设备。具体集成方式、接口说明及注意事项，请参考[iOS Extension SDK集成](https://help.aliyun.com/document_detail/3005985.html)。

**说明**
- 该方案需在通知服务扩展中执行网络请求以完成回执上报，会额外消耗设备电量；
- 由于需等待回执上报完成后再展示通知，可能导致通知展示出现数十秒的延迟；
- 仅建议在对通知触达率有强监控需求且可接受上述副作用的场景下启用此功能。
- 使用 OpenAPI 发送推送时，必须显式启用扩展回执能力。

## 三、扩展资料

1. [iOS SDK集成出错排查步骤](https://help.aliyun.com/document_detail/58624.html)
2. [iOS端推送失败排查步骤](https://help.aliyun.com/document_detail/44608.html)
3. [iOS端获取deviceToken的问题](https://help.aliyun.com/document_detail/130561.html)
4. [移动推送iOS工程样例](https://github.com/aliyun/alicloud-ios-demo/tree/master/mpush_ios_demo)
5. [移动推送产品使用限制](https://help.aliyun.com/document_detail/434629.html?spm=5176.13194971.0.0.788d9482fmfDfw#topic-1993448)