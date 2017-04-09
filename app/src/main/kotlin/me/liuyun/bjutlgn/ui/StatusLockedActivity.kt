package me.liuyun.bjutlgn.ui

import android.content.DialogInterface
import android.net.CaptivePortal
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager


class StatusLockedActivity : AppCompatActivity(), DialogInterface.OnDismissListener {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val captivePortal = intent.getParcelableExtra<CaptivePortal>(ConnectivityManager.EXTRA_CAPTIVE_PORTAL)

        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        val dialog = StatusDialog.statusDialog(this, captivePortal)
        dialog.setOnDismissListener(this)
        dialog.show()
    }

    override fun onDismiss(dialog: DialogInterface) {
        finish()
    }
}
