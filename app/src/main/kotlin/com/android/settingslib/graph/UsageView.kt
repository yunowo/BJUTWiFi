/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.android.settingslib.graph

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.SparseIntArray
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView

import me.liuyun.bjutlgn.R

class UsageView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    private val mUsageGraph: UsageGraph
    private val mLabels: Array<TextView>
    private val mBottomLabels: Array<TextView>

    init {
        LayoutInflater.from(context).inflate(R.layout.usage_view, this)
        mUsageGraph = findViewById(R.id.usage_graph) as UsageGraph
        mLabels = arrayOf(findViewById(R.id.label_bottom) as TextView, findViewById(R.id.label_middle) as TextView, findViewById(R.id.label_top) as TextView)
        mBottomLabels = arrayOf(findViewById(R.id.label_start) as TextView, findViewById(R.id.label_end) as TextView)
        val a = context.obtainStyledAttributes(attrs, R.styleable.UsageView, 0, 0)
        if (a.hasValue(R.styleable.UsageView_sideLabels)) {
            setSideLabels(a.getTextArray(R.styleable.UsageView_sideLabels))
        }
        if (a.hasValue(R.styleable.UsageView_bottomLabels)) {
            setBottomLabels(a.getTextArray(R.styleable.UsageView_bottomLabels))
        }
        if (a.hasValue(R.styleable.UsageView_textColor)) {
            val color = a.getColor(R.styleable.UsageView_textColor, 0)
            for (v in mLabels) {
                v.setTextColor(color)
            }
            for (v in mBottomLabels) {
                v.setTextColor(color)
            }
        }
        if (a.hasValue(R.styleable.UsageView_android_gravity)) {
            val gravity = a.getInt(R.styleable.UsageView_android_gravity, 0)
            if (gravity == Gravity.END) {
                val layout = findViewById(R.id.graph_label_group) as LinearLayout
                val labels = findViewById(R.id.label_group) as LinearLayout
                // Swap the children order.
                layout.removeView(labels)
                layout.addView(labels)
                // Set gravity.
                labels.gravity = Gravity.END
                // Swap the bottom space order.
                val bottomLabels = findViewById(R.id.bottom_label_group) as LinearLayout
                val bottomSpace = bottomLabels.findViewById(R.id.bottom_label_space)
                bottomLabels.removeView(bottomSpace)
                bottomLabels.addView(bottomSpace)
            } else if (gravity != Gravity.START) {
                throw IllegalArgumentException("Unsupported gravity " + gravity)
            }
        }
        mUsageGraph.setAccentColor(a.getColor(R.styleable.UsageView_android_colorAccent, 0))
    }

    fun clearPaths() {
        mUsageGraph.clearPaths()
    }

    fun addPath(points: SparseIntArray) {
        mUsageGraph.addPath(points)
    }

    fun configureGraph(maxX: Int, maxY: Int, showProjection: Boolean, projectUp: Boolean) {
        mUsageGraph.setMax(maxX, maxY)
        mUsageGraph.setShowProjection(showProjection, projectUp)
    }

    fun setAccentColor(color: Int) {
        mUsageGraph.setAccentColor(color)
    }

    fun setDividerLoc(dividerLoc: Int) {
        mUsageGraph.setDividerLoc(dividerLoc)
    }

    fun setDividerColors(middleColor: Int, topColor: Int) {
        mUsageGraph.setDividerColors(middleColor, topColor)
    }

    fun setSideLabelWeights(before: Float, after: Float) {
        setWeight(R.id.space1, before)
        setWeight(R.id.space2, after)
    }

    private fun setWeight(id: Int, weight: Float) {
        val v = findViewById(id)
        val params = v.layoutParams as LinearLayout.LayoutParams
        params.weight = weight
        v.layoutParams = params
    }

    fun setSideLabels(labels: Array<CharSequence>) {
        if (labels.size != mLabels.size) {
            throw IllegalArgumentException("Invalid number of labels")
        }
        for (i in mLabels.indices) {
            mLabels[i].text = labels[i]
        }
    }

    fun setBottomLabels(labels: Array<CharSequence>) {
        if (labels.size != mBottomLabels.size) {
            throw IllegalArgumentException("Invalid number of labels")
        }
        for (i in mBottomLabels.indices) {
            mBottomLabels[i].text = labels[i]
        }
    }

}