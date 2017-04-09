package me.liuyun.bjutlgn.ui

import android.app.Activity
import android.databinding.DataBindingUtil
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.liuyun.bjutlgn.R
import me.liuyun.bjutlgn.databinding.ActivityThemeBinding
import me.liuyun.bjutlgn.databinding.ThemeItemBinding
import me.liuyun.bjutlgn.util.ThemeHelper
import me.liuyun.bjutlgn.util.ThemeRes

class ThemeActivity : AppCompatActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityThemeBinding by lazy { DataBindingUtil.setContentView<ActivityThemeBinding>(this, R.layout.activity_theme) }
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = ThemeAdapter()
    }

    internal inner class ThemeAdapter : RecyclerView.Adapter<ThemeAdapter.ThemeItemHolder>() {
        var resources = getResources()
        var theme = getTheme()
        var white = resources.getColor(android.R.color.white, theme)
        var grey = resources.getColor(android.R.color.darker_gray, theme)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemeItemHolder {
            val binding: ThemeItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.theme_item, parent, false)
            return ThemeItemHolder(binding.root)
        }

        override fun onBindViewHolder(holder: ThemeItemHolder, position: Int) {
            val res = ThemeRes.values()[position]
            val themeColor = resources.getColor(res.color, theme)
            holder.themeId = res.style
            holder.tvTheme.setText(res.themeName)
            holder.tvTheme.setTextColor(themeColor)
            if (ThemeHelper.currentStyle == res.style) {
                holder.btnChoose.isChecked = true
                holder.btnChoose.setTextColor(themeColor)
                holder.btnChoose.setText(R.string.theme_using)
                holder.colorDot.setTextColor(white)
            } else {
                holder.btnChoose.isChecked = false
                holder.btnChoose.setTextColor(grey)
                holder.btnChoose.setText(R.string.theme_use)
                holder.colorDot.setTextColor(themeColor)
            }
            val gradientDrawable = GradientDrawable()
            gradientDrawable.setColor(themeColor)
            gradientDrawable.cornerRadius = 40f
            holder.colorDot.background = gradientDrawable
        }

        override fun getItemCount(): Int {
            return ThemeRes.values().size
        }

        internal inner class ThemeItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val binding: ThemeItemBinding = DataBindingUtil.getBinding(itemView)
            var colorDot = binding.colorDot
            var tvTheme = binding.tvTitle
            var btnChoose = binding.btnChoose
            var themeId: Int = 0
                set

            init {
                btnChoose.setOnClickListener { onItemClick() }
            }

            private fun onItemClick() {
                ThemeHelper.setTheme(itemView.context as Activity, themeId)
            }
        }
    }
}
