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
import android.graphics.*
import android.graphics.Paint.*
import android.graphics.Shader.TileMode
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.SparseIntArray
import android.util.TypedValue
import android.view.View
import me.liuyun.bjutlgn.R

class UsageGraph(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val mLinePaint: Paint
    private val mFillPaint: Paint
    private val mDottedPaint: Paint

    private val mDivider: Drawable
    private val mTintedDivider: Drawable
    private val mDividerSize: Int

    private val mPath = Path()

    // Paths in coordinates they are passed in.
    private val mPaths = SparseIntArray()
    // Paths in local coordinates for drawing.
    private val mLocalPaths = SparseIntArray()
    private val mCornerRadius: Int

    private var mAccentColor: Int = 0
    private var mShowProjection: Boolean = false
    private var mProjectUp: Boolean = false

    private var mMaxX = 100f
    private var mMaxY = 100f

    private var mMiddleDividerLoc = .5f
    private var mMiddleDividerTint = -1
    private var mTopDividerTint = -1

    init {
        val resources = context.resources

        mLinePaint = Paint()
        mLinePaint.style = Style.STROKE
        mLinePaint.strokeCap = Cap.ROUND
        mLinePaint.strokeJoin = Join.ROUND
        mLinePaint.isAntiAlias = true
        mCornerRadius = resources.getDimensionPixelSize(R.dimen.usage_graph_line_corner_radius)
        mLinePaint.pathEffect = CornerPathEffect(mCornerRadius.toFloat())
        mLinePaint.strokeWidth = resources.getDimensionPixelSize(R.dimen.usage_graph_line_width).toFloat()

        mFillPaint = Paint(mLinePaint)
        mFillPaint.style = Style.FILL

        mDottedPaint = Paint(mLinePaint)
        mDottedPaint.style = Style.STROKE
        val dots = resources.getDimensionPixelSize(R.dimen.usage_graph_dot_size).toFloat()
        val interval = resources.getDimensionPixelSize(R.dimen.usage_graph_dot_interval).toFloat()
        mDottedPaint.strokeWidth = dots * 3
        mDottedPaint.pathEffect = DashPathEffect(floatArrayOf(dots, interval), 0f)
        mDottedPaint.color = context.getColor(R.color.usage_graph_dots)

        val v = TypedValue()
        context.theme.resolveAttribute(android.R.attr.listDivider, v, true)
        mDivider = context.getDrawable(v.resourceId)
        mTintedDivider = context.getDrawable(v.resourceId)
        mDividerSize = resources.getDimensionPixelSize(R.dimen.usage_graph_divider_size)
    }

    internal fun clearPaths() {
        mPaths.clear()
    }

    internal fun setMax(maxX: Int, maxY: Int) {
        mMaxX = maxX.toFloat()
        mMaxY = maxY.toFloat()
    }

    internal fun setDividerLoc(height: Int) {
        mMiddleDividerLoc = 1 - height / mMaxY
    }

    internal fun setDividerColors(middleColor: Int, topColor: Int) {
        mMiddleDividerTint = middleColor
        mTopDividerTint = topColor
    }

    fun addPath(points: SparseIntArray) {
        for (i in 0..points.size() - 1) {
            mPaths.put(points.keyAt(i), points.valueAt(i))
        }
        mPaths.put(points.keyAt(points.size() - 1) + 1, PATH_DELIM)
        calculateLocalPaths()
        postInvalidate()
    }

    internal fun setAccentColor(color: Int) {
        mAccentColor = color
        mLinePaint.color = mAccentColor
        updateGradient()
        postInvalidate()
    }

    internal fun setShowProjection(showProjection: Boolean, projectUp: Boolean) {
        mShowProjection = showProjection
        mProjectUp = projectUp
        postInvalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateGradient()
        calculateLocalPaths()
    }

    private fun calculateLocalPaths() {
        if (width == 0) return
        mLocalPaths.clear()
        var pendingXLoc = 0
        var pendingYLoc = PATH_DELIM
        for (i in 0..mPaths.size() - 1) {
            val x = mPaths.keyAt(i)
            val y = mPaths.valueAt(i)
            if (y == PATH_DELIM) {
                if (i == mPaths.size() - 1 && pendingYLoc != PATH_DELIM) {
                    // Connect to the end of the graph.
                    mLocalPaths.put(pendingXLoc, pendingYLoc)
                }
                // Clear out any pending points.
                pendingYLoc = PATH_DELIM
                mLocalPaths.put(pendingXLoc + 1, PATH_DELIM)
            } else {
                val lx = getX(x.toFloat())
                val ly = getY(y.toFloat())
                pendingXLoc = lx
                if (mLocalPaths.size() > 0) {
                    val lastX = mLocalPaths.keyAt(mLocalPaths.size() - 1)
                    val lastY = mLocalPaths.valueAt(mLocalPaths.size() - 1)
                    if (lastY != PATH_DELIM && !hasDiff(lastX, lx) && !hasDiff(lastY, ly)) {
                        pendingYLoc = ly
                        continue
                    }
                }
                mLocalPaths.put(lx, ly)
            }
        }
    }

    private fun hasDiff(x1: Int, x2: Int): Boolean {
        return Math.abs(x2 - x1) >= mCornerRadius
    }

    private fun getX(x: Float): Int {
        return (x / mMaxX * width).toInt()
    }

    private fun getY(y: Float): Int {
        return (height * (1 - y / mMaxY)).toInt()
    }

    private fun updateGradient() {
        mFillPaint.shader = LinearGradient(0f, 0f, 0f, height.toFloat(),
                getColor(mAccentColor, .2f), 0, TileMode.CLAMP)
    }

    private fun getColor(color: Int, alphaScale: Float): Int {
        return color and ((0xff * alphaScale).toInt() shl 24 or 0xffffff)
    }

    override fun onDraw(canvas: Canvas) {
        // Draw lines across the top, middle, and bottom.
        if (mMiddleDividerLoc != 0f) {
            drawDivider(0, canvas, mTopDividerTint)
        }
        drawDivider(((canvas.height - mDividerSize) * mMiddleDividerLoc).toInt(), canvas,
                mMiddleDividerTint)
        drawDivider(canvas.height - mDividerSize, canvas, -1)

        if (mLocalPaths.size() == 0) {
            return
        }
        if (mShowProjection) {
            drawProjection(canvas)
        }
        drawFilledPath(canvas)
        drawLinePath(canvas)
    }

    private fun drawProjection(canvas: Canvas) {
        mPath.reset()
        val x = mLocalPaths.keyAt(mLocalPaths.size() - 2)
        val y = mLocalPaths.valueAt(mLocalPaths.size() - 2)
        mPath.moveTo(x.toFloat(), y.toFloat())
        mPath.lineTo(canvas.width.toFloat(), (if (mProjectUp) 0 else canvas.height).toFloat())
        canvas.drawPath(mPath, mDottedPaint)
    }

    private fun drawLinePath(canvas: Canvas) {
        mPath.reset()
        mPath.moveTo(mLocalPaths.keyAt(0).toFloat(), mLocalPaths.valueAt(0).toFloat())
        var i = 1
        while (i < mLocalPaths.size()) {
            val x = mLocalPaths.keyAt(i)
            val y = mLocalPaths.valueAt(i)
            if (y == PATH_DELIM) {
                if (++i < mLocalPaths.size()) {
                    mPath.moveTo(mLocalPaths.keyAt(i).toFloat(), mLocalPaths.valueAt(i).toFloat())
                }
            } else {
                mPath.lineTo(x.toFloat(), y.toFloat())
            }
            i++
        }
        canvas.drawPath(mPath, mLinePaint)
    }

    private fun drawFilledPath(canvas: Canvas) {
        mPath.reset()
        var lastStartX = mLocalPaths.keyAt(0).toFloat()
        mPath.moveTo(mLocalPaths.keyAt(0).toFloat(), mLocalPaths.valueAt(0).toFloat())
        var i = 1
        while (i < mLocalPaths.size()) {
            val x = mLocalPaths.keyAt(i)
            val y = mLocalPaths.valueAt(i)
            if (y == PATH_DELIM) {
                mPath.lineTo(mLocalPaths.keyAt(i - 1).toFloat(), height.toFloat())
                mPath.lineTo(lastStartX, height.toFloat())
                mPath.close()
                if (++i < mLocalPaths.size()) {
                    lastStartX = mLocalPaths.keyAt(i).toFloat()
                    mPath.moveTo(mLocalPaths.keyAt(i).toFloat(), mLocalPaths.valueAt(i).toFloat())
                }
            } else {
                mPath.lineTo(x.toFloat(), y.toFloat())
            }
            i++
        }
        canvas.drawPath(mPath, mFillPaint)
    }

    private fun drawDivider(y: Int, canvas: Canvas, tintColor: Int) {
        var d = mDivider
        if (tintColor != -1) {
            mTintedDivider.setTint(tintColor)
            d = mTintedDivider
        }
        d.setBounds(0, y, canvas.width, y + mDividerSize)
        d.draw(canvas)
    }

    companion object {

        private val PATH_DELIM = -1
    }
}