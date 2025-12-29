package com.alibaba.push.android.demo

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("android:alpha")
fun setTextViewAlpha(textView: TextView, alpha: Float?) {
    textView.alpha = alpha ?: 1.0f // Set a default value if alpha is null
}