package me.liuyun.bjutlgn.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.net.CaptivePortal
import android.view.LayoutInflater
import me.liuyun.bjutlgn.App
import me.liuyun.bjutlgn.R
import me.liuyun.bjutlgn.databinding.StatusViewBinding
import me.liuyun.bjutlgn.util.startActivity

object StatusDialog {

    fun statusDialog(context: Context, captivePortal: CaptivePortal?): Dialog {
        context.setTheme(R.style.AppTheme_Dialog)
        val binding = StatusViewBinding.inflate(LayoutInflater.from(context), null, false)
        StatusCard(binding, null, context.applicationContext as App, captivePortal).onRefresh()
        return AlertDialog.Builder(context, R.style.AppTheme_Dialog)
                .setTitle(R.string.app_name)
                .setView(binding.root)
                .setPositiveButton(R.string.button_ok, null)
                .setNegativeButton(R.string.button_open_app) { _, _ -> context.startActivity<MainActivity>() }
                .create()
    }
}
