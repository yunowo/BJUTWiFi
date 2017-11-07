/*
* Copyright (C) 2010 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package me.liuyun.bjutlgn.ui

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.drawable.RippleDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.view.animation.PathInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import me.liuyun.bjutlgn.R

class EasterEggActivity : Activity() {

    lateinit var layout: FrameLayout
    private var tapCount: Int = 0
    private var interpolator = PathInterpolator(0f, 0f, 0.5f, 1f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        layout = FrameLayout(this)
        setContentView(layout)
    }

    override fun onAttachedToWindow() {
        val dm = resources.displayMetrics
        val dp = dm.density
        val size = (Math.min(Math.min(dm.widthPixels, dm.heightPixels).toFloat(), 600 * dp) - 100 * dp).toInt()

        val im = ImageView(this)
        val pad = (40 * dp).toInt()
        im.setPadding(pad, pad, pad, pad)
        im.translationZ = 20f
        im.scaleX = 0.5f
        im.scaleY = 0.5f
        im.alpha = 0f
        im.background = RippleDrawable(ColorStateList.valueOf(0xFFFFFFFF.toInt()), getDrawable(R.drawable.logo), null)
        im.isClickable = true
        im.setOnClickListener { tapCount++ }
        im.setOnLongClickListener {
            if (tapCount < 5) return@setOnLongClickListener false
            im.post { Toast.makeText(this, R.string.app_name, Toast.LENGTH_LONG).show() }
            true
        }
        layout.addView(im, FrameLayout.LayoutParams(size, size, Gravity.CENTER))

        im.animate().scaleX(1f).scaleY(1f).alpha(1f)
                .setInterpolator(interpolator)
                .setDuration(500)
                .setStartDelay(800)
                .start()
    }
}
