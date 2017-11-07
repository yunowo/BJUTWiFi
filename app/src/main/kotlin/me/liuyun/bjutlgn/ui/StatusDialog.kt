package me.liuyun.bjutlgn.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.databinding.DataBindingUtil
import android.net.CaptivePortal
import android.view.LayoutInflater
import android.widget.FrameLayout
import me.liuyun.bjutlgn.App
import me.liuyun.bjutlgn.R
import me.liuyun.bjutlgn.databinding.StatusViewBinding
import org.jetbrains.anko.startActivity

object StatusDialog {

    fun statusDialog(context: Context, captivePortal: CaptivePortal?): Dialog {
        context.setTheme(R.style.AppTheme_Dialog)
        val binding: StatusViewBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.status_view, null, false)
        StatusCard(binding.root as FrameLayout, null, context.applicationContext as App, captivePortal).onRefresh()
        return AlertDialog.Builder(context, R.style.AppTheme_Dialog)
                .setTitle(R.string.app_name)
                .setView(binding.root)
                .setPositiveButton(R.string.button_ok, null)
                .setNegativeButton(R.string.button_open_app) { _, _ -> context.startActivity<MainActivity>() }
                .create()
    }
}
