package com.alibaba.push.android.demo

import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import com.alibaba.sdk.android.push.CloudPushService
import com.alibaba.sdk.android.push.HonorRegister
import com.alibaba.sdk.android.push.huawei.HuaWeiRegister
import com.alibaba.sdk.android.push.noonesdk.PushInitConfig
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory
import com.alibaba.sdk.android.push.register.GcmRegister
import com.alibaba.sdk.android.push.register.MeizuRegister
import com.alibaba.sdk.android.push.register.MiPushRegister
import com.alibaba.sdk.android.push.register.OppoRegister
import com.alibaba.sdk.android.push.register.VivoRegister

class MainApplication:Application() {

    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001
        private const val PREFS_NAME = "push_permissions"
        private const val KEY_ASKED_NOTIFICATION_PERMISSION = "asked_notification_permission"
    }

    override fun onCreate() {
        super.onCreate()
        Config.init(this)
        initPushSdk(this)
        setupNotificationPermissionPrompt()
    }

    private fun initPushSdk(context: Context) {
        if (TextUtils.isEmpty(Config.APP_KEY) && TextUtils.isEmpty(Config.APP_SECRET)) {
            // 如果没有配置AppKey和AppSecret，只初始化基础服务
            // If AppKey and AppSecret are not configured, only initialize the base service.
            PushServiceFactory.init(context)
        } else {
            // 使用配置的AppKey和AppSecret初始化
            // Initialize with the configured AppKey and AppSecret.
            PushServiceFactory.init(PushInitConfig.Builder()
                .application(this)
                .appKey(Config.APP_KEY)
                .appSecret(Config.APP_SECRET)
                .build())
        }
        PushServiceFactory.getCloudPushService().setDebug(true)
        PushServiceFactory.getCloudPushService().setLogLevel(CloudPushService.LOG_DEBUG)
        PushServiceFactory.getCloudPushService().setNotificationSmallIcon(R.mipmap.ic_launcher)
        createNotificationChannel()
        initOthers()
    }

    private fun initOthers() {
        HuaWeiRegister.register(this) // 接入华为辅助推送
        // Enable Huawei auxiliary push.
        HonorRegister.register(this)  //荣耀推送
        // Enable Honor push.
        MiPushRegister.register(this, "", "") // 初始化小米辅助推送
        // Initialize Xiaomi auxiliary push.
        VivoRegister.registerAsync(applicationContext) //接入vivo辅助推送
        // Enable vivo auxiliary push.
        OppoRegister.registerAsync(applicationContext, "", "") //OPPO辅助推送
        // Enable OPPO auxiliary push.
        MeizuRegister.registerAsync(applicationContext, "", "") //接入魅族辅助推送
        // Enable Meizu auxiliary push.
        GcmRegister.register(this, "", "", "", "") //GCM推送
        // Enable GCM push.
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            createChannel(
                mNotificationManager,
                "default",
                "default",
                "default notifications"
            )
            createChannel(
                mNotificationManager,
                "8.0up",
                "notification channel",
                "notification description"
            )
        }
    }

    private fun createChannel(
        notificationManager: NotificationManager,
        id: String,
        name: CharSequence,
        description: String
    ) {
        val channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH)
        channel.description = description
        channel.enableLights(true)
        channel.lightColor = Color.RED
        channel.enableVibration(true)
        channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        notificationManager.createNotificationChannel(channel)
    }

    private fun setupNotificationPermissionPrompt() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return
        }
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityResumed(activity: Activity) {
                maybeRequestNotificationPermission(activity)
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
            override fun onActivityStarted(activity: Activity) = Unit
            override fun onActivityPaused(activity: Activity) = Unit
            override fun onActivityStopped(activity: Activity) = Unit
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
            override fun onActivityDestroyed(activity: Activity) = Unit
        })
    }

    private fun maybeRequestNotificationPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return
        }
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val prefs = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (prefs.getBoolean(KEY_ASKED_NOTIFICATION_PERMISSION, false)) {
            return
        }
        prefs.edit().putBoolean(KEY_ASKED_NOTIFICATION_PERMISSION, true).apply()
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            NOTIFICATION_PERMISSION_REQUEST_CODE
        )
    }

}
