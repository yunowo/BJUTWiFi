package me.liuyun.bjutlgn.ui

import android.content.DialogInterface
import android.net.CaptivePortal
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.WindowManager

class StatusLockedActivity : AppCompatActivity(), DialogInterface.OnDismissListener {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val captivePortal = intent.getParcelableExtra<CaptivePortal>(ConnectivityManager.EXTRA_CAPTIVE_PORTAL)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= 27) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
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
