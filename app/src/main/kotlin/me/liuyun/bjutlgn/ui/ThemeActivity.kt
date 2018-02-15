package me.liuyun.bjutlgn.ui

import android.app.Activity
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_theme.*
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.item_theme.view.*
import me.liuyun.bjutlgn.R
import me.liuyun.bjutlgn.util.ThemeHelper
import me.liuyun.bjutlgn.util.ThemeRes

class ThemeActivity : AppCompatActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theme)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        recycler.adapter = ThemeAdapter()
    }

    internal inner class ThemeAdapter : RecyclerView.Adapter<ThemeAdapter.ThemeItemHolder>() {
        var white = resources.getColor(android.R.color.white, theme)
        var grey = resources.getColor(android.R.color.darker_gray, theme)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                ThemeItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_theme, parent, false))

        override fun onBindViewHolder(holder: ThemeItemHolder, position: Int) {
            val res = ThemeRes.values()[position]
            val themeColor = resources.getColor(res.color, theme)
            holder.themeId = res.style
            holder.v.tv_title.setText(res.themeName)
            holder.v.tv_title.setTextColor(themeColor)
            holder.v.btn_choose.setOnClickListener { ThemeHelper.setTheme(holder.v.context as Activity, holder.themeId) }
            if (ThemeHelper.currentStyle == res.style) {
                holder.v.btn_choose.isChecked = true
                holder.v.btn_choose.setTextColor(themeColor)
                holder.v.btn_choose.setText(R.string.theme_using)
                holder.v.color_dot.setTextColor(white)
            } else {
                holder.v.btn_choose.isChecked = false
                holder.v.btn_choose.setTextColor(grey)
                holder.v.btn_choose.setText(R.string.theme_use)
                holder.v.color_dot.setTextColor(themeColor)
            }
            val gradientDrawable = GradientDrawable()
            gradientDrawable.setColor(themeColor)
            gradientDrawable.cornerRadius = 40f
            holder.v.color_dot.background = gradientDrawable
        }

        override fun getItemCount() = ThemeRes.values().size

        internal inner class ThemeItemHolder(val v: View, var themeId: Int = 0) : RecyclerView.ViewHolder(v)
    }
}
