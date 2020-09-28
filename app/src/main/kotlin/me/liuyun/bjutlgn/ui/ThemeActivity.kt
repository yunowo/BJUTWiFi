package me.liuyun.bjutlgn.ui

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import me.liuyun.bjutlgn.R
import me.liuyun.bjutlgn.databinding.ActivityThemeBinding
import me.liuyun.bjutlgn.databinding.ItemThemeBinding
import me.liuyun.bjutlgn.util.ThemeHelper
import me.liuyun.bjutlgn.util.ThemeRes

class ThemeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityThemeBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThemeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBar.toolbar)
        binding.appBar.toolbar.setNavigationOnClickListener { onBackPressed() }

        binding.recycler.adapter = ThemeAdapter()
    }

    internal inner class ThemeAdapter : RecyclerView.Adapter<ThemeAdapter.ThemeItemHolder>() {
        var white = resources.getColor(android.R.color.white, theme)
        var grey = resources.getColor(android.R.color.darker_gray, theme)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                ThemeItemHolder(ItemThemeBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        override fun onBindViewHolder(holder: ThemeItemHolder, position: Int) {
            val res = ThemeRes.values()[position]
            val themeColor = resources.getColor(res.color, theme)
            holder.themeId = res.style
            holder.binding.tvTitle.setText(res.themeName)
            holder.binding.tvTitle.setTextColor(themeColor)
            holder.binding.btnChoose.setOnClickListener { ThemeHelper.setTheme(this@ThemeActivity, holder.themeId) }
            if (ThemeHelper.currentStyle == res.style) {
                holder.binding.btnChoose.isChecked = true
                holder.binding.btnChoose.setTextColor(themeColor)
                holder.binding.btnChoose.setText(R.string.theme_using)
                holder.binding.colorDot.setTextColor(white)
            } else {
                holder.binding.btnChoose.isChecked = false
                holder.binding.btnChoose.setTextColor(grey)
                holder.binding.btnChoose.setText(R.string.theme_use)
                holder.binding.colorDot.setTextColor(themeColor)
            }
            val gradientDrawable = GradientDrawable()
            gradientDrawable.setColor(themeColor)
            gradientDrawable.cornerRadius = 40f
            holder.binding.colorDot.background = gradientDrawable
        }

        override fun getItemCount() = ThemeRes.values().size

        internal inner class ThemeItemHolder(val binding: ItemThemeBinding, var themeId: Int = 0) : RecyclerView.ViewHolder(binding.root)
    }
}
