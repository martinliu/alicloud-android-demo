package com.alibaba.push.android.demo

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import com.alibaba.push.android.demo.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private var originalStatusBarColor: Int? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        originalStatusBarColor = window.statusBarColor
        setupStatusBar()
        setupUI()
        loadCurrentConfig()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupStatusBar() {
        val statusBarHeight = getStatusBarHeight()
        binding.vStatusBg.layoutParams?.height = statusBarHeight
        var collapseDistance = statusBarHeight.coerceAtLeast(1).toFloat()
        binding.ivLogo.doOnLayout {
            collapseDistance = (it.height - statusBarHeight).coerceAtLeast(1).toFloat()
        }
        binding.nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            val alpha = (scrollY.toFloat() / collapseDistance).coerceAtMost(1f)
            binding.vStatusBg.alpha = alpha
        }
    }

    private fun setupUI() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.btnSave.setOnClickListener {
            saveConfig()
        }

        binding.btnClear.setOnClickListener {
            clearConfig()
        }
    }

    private fun loadCurrentConfig() {
        if (Config.hasUserConfig(this)) {
            binding.etAppKey.setText(Config.APP_KEY)
            binding.etAppSecret.setText(Config.APP_SECRET)
            binding.tvStatus.text = getString(R.string.settings_status_user_config)
        } else {
            binding.etAppKey.setText(Config.APP_KEY)
            binding.etAppSecret.setText(Config.APP_SECRET)
            binding.tvStatus.text = getString(R.string.settings_status_default_config)
        }
    }

    private fun saveConfig() {
        val appKey = binding.etAppKey.text.toString().trim()
        val appSecret = binding.etAppSecret.text.toString().trim()

        if (TextUtils.isEmpty(appKey) || TextUtils.isEmpty(appSecret)) {
            Toast.makeText(this, getString(R.string.settings_app_key_secret_empty), Toast.LENGTH_SHORT).show()
            return
        }

        Config.saveUserConfig(this, appKey, appSecret)
        Toast.makeText(this, getString(R.string.settings_save_success_restart), Toast.LENGTH_SHORT).show()
        
        // 延迟重启，让Toast显示
        binding.btnSave.postDelayed({
            restartApp()
        }, 1000)
    }

    private fun clearConfig() {
        Config.clearUserConfig(this)
        loadCurrentConfig()
        Toast.makeText(this, getString(R.string.settings_user_config_cleared), Toast.LENGTH_SHORT).show()
    }

    private fun restartApp() {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finishAffinity()
    }

    override fun onDestroy() {
        originalStatusBarColor?.let { window.statusBarColor = it }
        originalStatusBarColor = null
        super.onDestroy()
    }
}
