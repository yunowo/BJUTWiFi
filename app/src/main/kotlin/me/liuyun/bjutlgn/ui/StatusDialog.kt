package me.liuyun.bjutlgn.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.CaptivePortal
import android.view.LayoutInflater
import android.widget.FrameLayout

import me.liuyun.bjutlgn.R
import me.liuyun.bjutlgn.WiFiApplication
import me.liuyun.bjutlgn.widget.StatusCard

object StatusDialog {

    fun statusDialog(context: Context, captivePortal: CaptivePortal?): Dialog {
        context.setTheme(R.style.AppTheme_Dialog)
        val cardView = LayoutInflater.from(context).inflate(R.layout.status_view, null) as FrameLayout
        StatusCard(cardView, null, context.applicationContext as WiFiApplication, captivePortal).onRefresh()
        return AlertDialog.Builder(context, R.style.AppTheme_Dialog)
                .setTitle(R.string.app_name)
                .setView(cardView)
                .setPositiveButton(R.string.button_ok, null)
                .setNegativeButton(R.string.button_open_app) { _, _ -> context.startActivity(Intent(context, MainActivity::class.java)) }
                .create()
    }
}
