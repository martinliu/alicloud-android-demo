# Mobile Push iOS Quick Integration Guide

This document guides developers to complete the integration configuration of the Alibaba Cloud Mobile Push iOS SDK, including SDK initialization, notification permission requests, and message receiving handling.

## I. Prerequisites

- EMAS app creation completed and credentials obtained
	- Log in to the [EMAS Console](https://emas.console.aliyun.com/)
	- Complete iOS app creation and obtain AppKey and AppSecret
	- Refer to the [Quick Start Guide](https://help.aliyun.com/document_detail/436513.html#cafc7340efb0u)
- Apple Push certificate configuration
	- APNs certificate creation and upload completed
	- Refer to [iOS Push Certificate Configuration Guide](https://help.aliyun.com/document_detail/434701.html#topic-1824039)
- Development environment requirements
	- **Xcode**: 12.0 or later
	- **iOS deployment target**: iOS 12.0 or later
	- **CocoaPods**: 1.11.0 or later
	- **Swift version**: Supports Swift 5.x, **does not support Swift 6**

## II. Integration Steps

### **2.1 Add SDK dependency**

**Warning**

**Version upgrade notes**

- **If your current version is below** `**1.9.9.3**`, **do not upgrade directly to** `**2.2.0**`, `**2.2.1**`, or `**3.0.0**`, **otherwise the device ID will be reset.**
- **If your AppKey is an early dual-platform app (Android and iOS share the same AppKey), upgrading to 3.0.0 and above is not supported. It is recommended to create separate apps for Android and iOS, obtain their respective AppKeys, and re-integrate with the latest SDK.**

## **Method 1: CocoaPods integration (recommended)**

1. Create/modify Podfile:
	```
	source 'https://github.com/CocoaPods/Specs.git'
	source 'https://github.com/aliyun/aliyun-specs.git'
	platform :ios, '11.0'
	use_frameworks!
	target 'YourTarget' do
	  pod 'AlicloudPush', '~> 3'
	end
	```
	**Note**
	In the root directory of the Xcode project, locate and edit the Podfile to add the AlicloudPush dependency. If there is no Podfile in the root directory, create one by running `pod init` in Terminal. If CocoaPods is not installed on your machine, install it first by following the [CocoaPods official guide](https://guides.cocoapods.org/using/getting-started.html).
2. Run installation commands:
	```
	pod repo update AliyunRepo
	pod install
	# If you have not added the Aliyun CocoaPods repo, add it with the following command first
	# pod repo add AliyunRepo https://github.com/aliyun/aliyun-specs.git
	```

## **Method 2: Manual integration**

1. Download the latest SDK package according to the [Quick Start](https://help.aliyun.com/document_detail/436513.html#c8647b35abzpi) document
2. Unzip and add frameworks:
	1. Drag `CloudPushSDK.xcframework` and `AlicloudELS.xcframework` into the project.
	2. The detailed steps are shown in the figure below. ![image](https://help-static-aliyun-doc.aliyuncs.com/assets/img/zh-CN/3693092471/p918905.png)

**Note**
- **Dependency notes**
	- Version 3.0.0 and above:
		- Includes CloudPushSDK and AlicloudELS.
	- Version 2.2.0 to 3.0.0:
		- Includes CloudPushSDK only.
	- Below 2.2.0:
		- Includes CloudPushSDK, UTDID, and AlicloudUtils.
- **Xcode compatibility**
	When using older Xcode versions, you may need to manually add the following system libraries to ensure compatibility:
	- libresolv.tbd
	- CoreTelephony.framework
	- SystemConfiguration.framework
	- libsqlite3.tbd
- **Linking settings**
	If runtime issues occur, try adding the -ObjC linker flag in the following location:
	1. Open project settings.
	2. Navigate to TARGETS.
	3. Select Build Settings.
	4. Find the Linking section.
	5. Add -ObjC under Other Linker Flags.

### **2.2 SDK initialization**

Complete initialization in `AppDelegate`. Example code:

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

### **2.3** Configure APNs push capability

Enable push permission, request the push token from Apple servers, and report it to the Alibaba Cloud push server.

1. Xcode project configuration:
	1. Target → Signing & Capabilities → add Push Notifications and Background Modes capabilities
	2. Background Modes → check Remote notifications ![image](https://help-static-aliyun-doc.aliyuncs.com/assets/img/zh-CN/3693092471/p926897.png)
2. Request user authorization. Example code:

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

### **2.4 Message handling**

Messages are transparent data sent through Alibaba Cloud's private channel with a title and content. These messages do not trigger ringtone or vibration and can only be received when the app is online. After receiving a message, developers can execute logic based on the content.

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

### **2.5 Notification handling**

Notifications are delivered to the user's iPhone by Apple servers and can be displayed on the lock screen. Receiving notifications does not require the app to be online; the phone only needs a network connection. Developers can handle notifications in the app as follows:

#### **Foreground notification callback**

When the app is in the foreground, the system calls the delegate method implemented by the developer. The developer can handle logic and decide whether to display the notification.

#### **Notification tap callback**

When the user taps a notification, the system opens and navigates to the app and calls the delegate method implemented by the developer.

#### **Silent notification callback**

When a silent notification is sent, the system calls the delegate method implemented by the developer when the app is in the foreground or background.

#### **Example code**

Below are Swift and Objective-C examples for these functions:

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

### **2.6 Successful run verification**

After running the app on a real device or simulator, check the Xcode console output. Confirm whether logs similar to the following appear:

```
2025-03-11 11:26:33.854 INFO [CloudPushSDK] Initialization successful
====================
DeviceId: a1b693a9fced4c22aab3cdfe3fbe5a05
DeviceToken: 76aa3065ca246c81ad1b640715f45f3f8d16fea38ca4fb2917cf34b7249bd803
SdkVersion: 3.0.0
====================
```

This log indicates the SDK has been initialized successfully. Next, you can query device information in the EMAS push console. If the corresponding device information is found, initialization is successful.

- `ACCS`: indicates the Alibaba Cloud private channel registration succeeded.
- `APNS`: indicates Apple Push Service registration succeeded.

Ensure both channel statuses show success to verify that push service is correctly configured. ![image](https://help-static-aliyun-doc.aliyuncs.com/assets/img/zh-CN/5319150671/p1007702.png)

### **2.7 Extension receipt SDK integration (optional)**

Because iOS itself does not provide delivery or display receipts for notifications, to meet tracking requirements for notification delivery status in some scenarios, you can integrate the extension receipt SDK (ext SDK) in the Notification Service Extension. This SDK can proactively report an `ext_ack` receipt log when a notification arrives and is about to be displayed, indicating the notification has been received and is ready to display.

After integration, you can filter receipt logs by event type `ext_ack` to approximate whether notifications have successfully reached user devices. For integration steps, APIs, and precautions, see [iOS Extension SDK Integration](https://help.aliyun.com/document_detail/3005985.html).

**Note**
- This solution performs network requests in the Notification Service Extension to report receipts, which consumes extra battery.
- Because it waits for receipt reporting to complete before displaying notifications, notification display may be delayed by tens of seconds.
- Only enable this feature when you have a strong need to monitor notification delivery and can accept the above side effects.
- When sending push via OpenAPI, you must explicitly enable the extension receipt feature.

## III. Additional Resources

1. [Troubleshooting steps for iOS SDK integration errors](https://help.aliyun.com/document_detail/58624.html)
2. [Troubleshooting iOS push failures](https://help.aliyun.com/document_detail/44608.html)
3. [DeviceToken retrieval issues on iOS](https://help.aliyun.com/document_detail/130561.html)
4. [Mobile Push iOS sample project](https://github.com/aliyun/alicloud-ios-demo/tree/master/mpush_ios_demo)
5. [Mobile Push product usage limits](https://help.aliyun.com/document_detail/434629.html?spm=5176.13194971.0.0.788d9482fmfDfw#topic-1993448)
