package me.liuyun.bjutlgn.ui

import android.content.DialogInterface
import android.net.CaptivePortal
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

class StatusLockedActivity : AppCompatActivity(), DialogInterface.OnDismissListener {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val captivePortal = intent.getParcelableExtra<CaptivePortal>(ConnectivityManager.EXTRA_CAPTIVE_PORTAL)

        setShowWhenLocked(true)
        setTurnScreenOn(true)
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
