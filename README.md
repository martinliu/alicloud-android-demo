# EMAS Android Demo

[Chinese Version](README.zh.md)

Alibaba Cloud Enterprise Mobile Application Studio (EMAS) is a one-stop application R&D platform for full-scenario endpoints (mobile apps, H5 apps, mini programs, web apps, PC apps, etc.). Based on widely adopted cloud-native technologies (Backend as a Service, Serverless, DevOps, low-code, etc.), EMAS provides enterprises and developers with one-stop application development, operations, and management services across the full application lifecycle, covering development, testing, operations, and user growth.

## Quick Start

1. **Clone the project**

```bash
  git clone https://github.com/martinliu/alicloud-android-demo.git
```

2. **Choose a product demo**

- Each subdirectory corresponds to an Android demo for an EMAS product.
- For detailed integration guidance, see the README.md under each product directory.

3. **Configure credentials**

- HTTPDNS does not require creating an app; configure it by following the [README](httpdns_android_demo/README.md)
- Log in to the [EMAS Console](https://emas.console.aliyun.com/) to create an app
- Obtain the corresponding AppKey, AppSecret, and other configuration information
- Configure according to each demo's README instructions

EMAS management entry: [EMAS Console](https://emas.console.aliyun.com/)

SDK download: see EMAS Quick Start -> Download SDK [Link](https://help.aliyun.com/document_detail/436513.html)

> Note: The account information in the demo is configured only for running the demo. For real products, we recommend using a secure black box or other methods to protect your credentials.

### I. HTTPDNS

------

HTTPDNS is a DNS resolution service for multi-end applications (mobile apps, PC client apps). It uses HTTP(S)-based resolution requests to effectively solve problems of traditional DNS such as hijacking, inaccurate resolution, slow updates, and unstable service.

- **Android Demo** : [httpdns_android_demo](https://github.com/martinliu/alicloud-android-demo/tree/master/httpdns_android_demo)

- **Flutter Demo** : [alicloud-flutter-demo](https://github.com/aliyun/alicloud-flutter-demo)

- **Product website** : [Link](https://www.aliyun.com/product/httpdns)

- **Configuration** : Read `httpdns_android_demo/README.md`. The demo does not require creating an app in the console or obtaining AppKey/AppSecret to try it.

### II. Mobile Push

------

Mobile Push provides a mobile messaging service for app developers. By integrating push into an app, it enables efficient, precise, and real-time push messaging to reach users in time and improve user engagement.

- **Android Demo** : [mpush_android_demo](https://github.com/martinliu/alicloud-android-demo/tree/master/mpush_android_demo)

- **React Native Plugin** : [alibabacloud-push-reactnative-plugin](https://github.com/aliyun/alibabacloud-push-reactnative-plugin)

- **Flutter Plugin** : [alibabacloud-push-flutter-plugin](https://github.com/aliyun/alibabacloud-push-flutter-plugin)

- **Product website** : [Link](https://www.aliyun.com/product/cps)

### III. Application Performance Monitoring

------

EMAS App Monitoring is a comprehensive client-side monitoring platform covering mobile and web/H5. Backed by Alibaba's technical strengths, it provides stable and efficient monitoring to help developers understand app performance and stability in real time, build an "Observe > Diagnose > Fix" operations loop, ensure app quality, and optimize user experience.

- **Android Demo**: [apm_android_demo](https://github.com/martinliu/alicloud-android-demo/tree/master/apm_android_demo)

- **Product website**: [Link](https://www.aliyun.com/product/emascrash/apm)

### IV. Mobile User Feedback

------

Mobile User Feedback provides app operation services for enterprise customers and mobile developers. It is used to set in-app feedback pages, collect/manage user feedback within the app and across external app markets, so issues can be addressed quickly to improve service quality and user satisfaction.

- **Android Demo**: [feedback_android_demo](https://github.com/martinliu/alicloud-android-demo/tree/master/feedback_android_demo)

- **Product website**: [Link](https://www.aliyun.com/product/feedback)

### V. Mobile Hotfix

------

Mobile Hotfix is an online hotfix service for Android apps based on Alibaba's Sophix technology. It provides fine-grained hotfix capability so issues can be fixed in real time without waiting for releases, with no user perception.

- **Android Demo**: [hotfix_android_demo](https://github.com/martinliu/alicloud-android-demo/tree/master/hotfix_android_demo)

- **Product website**: [Link](https://www.aliyun.com/product/hotfix)

### VI. Mobile DevOps

------

Mobile DevOps includes cloud build and cloud release. It is a one-stop R&D support platform for multi-end application scenarios (including but not limited to mobile apps, H5 apps, mini programs, web apps, PC apps, etc.), connecting the full application lifecycle (development, testing, canary rollout, distribution, monitoring, feedback) through automated processes to help enterprises achieve standardized, automated, and digital delivery.

- **Android Demo**: [devops_android_demo](https://github.com/martinliu/alicloud-android-demo/tree/master/devops_android_demo)

- **Product website**: [Link](https://www.aliyun.com/product/emascrash/mobile_devops)
