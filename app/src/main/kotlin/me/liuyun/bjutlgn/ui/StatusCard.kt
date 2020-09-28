package me.liuyun.bjutlgn.ui

import android.net.CaptivePortal
import android.text.TextUtils
import android.text.format.Formatter
import android.view.View
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import me.liuyun.bjutlgn.App
import me.liuyun.bjutlgn.R
import me.liuyun.bjutlgn.api.BjutRetrofit
import me.liuyun.bjutlgn.databinding.StatusViewBinding
import me.liuyun.bjutlgn.entity.Flow
import me.liuyun.bjutlgn.entity.Stats
import me.liuyun.bjutlgn.util.NetworkUtils
import me.liuyun.bjutlgn.util.NetworkUtils.NetworkState.*
import me.liuyun.bjutlgn.util.StatsUtils
import me.liuyun.bjutlgn.util.ThemeHelper

class StatusCard(private val sv: StatusViewBinding, private val graphCard: GraphCard?, private val app: App, private val captivePortal: CaptivePortal?) {
    private val res = app.resources

    init {
        sv.signInButton.setOnClickListener { onLogin() }
        sv.signOutButton.setOnClickListener { onLogout() }
    }

    fun onRefresh() {
        val state = if (app.prefs.getBoolean("debug", false)) STATE_BJUT_WIFI else NetworkUtils.getNetworkState(app)
        if (state == STATE_BJUT_WIFI) {
            arrayOf(sv.user, sv.progressRing, sv.infoLayout, sv.buttonsLayout).forEach { it.visibility = View.VISIBLE }
            sv.nonBjut.visibility = View.GONE
        } else {
            arrayOf(sv.user, sv.progressRing, sv.infoLayout, sv.buttonsLayout).forEach { it.visibility = View.GONE }
            sv.nonBjut.visibility = View.VISIBLE
        }
        when (state) {
            STATE_NO_NETWORK -> sv.nonBjut.text = res.getString(R.string.status_no_network)
            STATE_MOBILE -> sv.nonBjut.text = res.getString(R.string.status_mobile_network)
            STATE_BJUT_WIFI -> refreshStatusAsync()
            STATE_OTHER_WIFI -> sv.nonBjut.text = res.getString(R.string.status_other_wifi).format(NetworkUtils.getWifiSSID(app))
        }
    }

    private fun refreshStatusAsync() = GlobalScope.async(Dispatchers.Main) {
        val job = GlobalScope.async(Dispatchers.IO) {
            BjutRetrofit.evictAll()
        }
        job.await()

        sv.user.text = app.prefs.getString("account", "")

        if (app.prefs.getBoolean("debug", false)) {
            statsSuccess(Stats(4000000, 100, 1000, true))
            return@async
        }

        BjutRetrofit.bjutService.stats()
                .map(StatsUtils::parseStats)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ statsSuccess(it) }, { Snackbar.make(sv.root, R.string.stats_refresh_failed, Snackbar.LENGTH_SHORT).show() })
    }

    private fun statsSuccess(stats: Stats) {
        sv.fee.text = res.getString(R.string.stats_fee).format(stats.fee / 10000f)
        sv.time.text = res.getString(R.string.stats_time).format(stats.time)

        val percent = StatsUtils.getPercent(stats, app)
        sv.progressRing.centerTitle = "$percent %"
        sv.progressRing.bottomTitle = Formatter.formatFileSize(app, stats.flow * 1024L)
        val w = res.getColor(android.R.color.white, app.theme)
        val a = ThemeHelper.getThemeAccentColor(sv.root.context)
        if (percent > 50) {
            sv.progressRing.centerTitleColor = w
            sv.progressRing.setCenterTitleStrokeColor(a)
        } else {
            sv.progressRing.centerTitleColor = a
            sv.progressRing.setCenterTitleStrokeColor(w)
        }
        if (percent > 20) {
            sv.progressRing.bottomTitleColor = w
            sv.progressRing.setBottomTitleStrokeColor(a)
        } else {
            sv.progressRing.bottomTitleColor = a
            sv.progressRing.setBottomTitleStrokeColor(w)
        }
        sv.progressRing.progressValue = percent
        startSpringAnimation(sv.progressRing)

        if (stats.isOnline && stats.flow != 0 && !app.prefs.getBoolean("debug", false)) {
            app.appDatabase.flowDao().insert(Flow(0, System.currentTimeMillis() / 1000L, stats.flow))
            graphCard?.show()

            captivePortal?.reportCaptivePortalDismissed()
        }
    }

    private fun startSpringAnimation(view: View) {
        view.scaleX = 0.1f
        view.scaleY = 0.1f
        val spring = SpringForce(1f)
                .setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY)
                .setStiffness(SpringForce.STIFFNESS_LOW)
        SpringAnimation(view, SpringAnimation.SCALE_X)
                .setSpring(spring)
                .setStartVelocity(0.7f)
                .start()
        SpringAnimation(view, SpringAnimation.SCALE_Y)
                .setSpring(spring)
                .setStartVelocity(0.7f)
                .start()
    }

    private fun onLogin() {
        val account = app.prefs.getString("account", "") ?: ""
        val password = app.prefs.getString("password", "") ?: ""

        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password)) {
            BjutRetrofit.bjutService.login(account, password, "1", "123")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ refreshStatusAsync() },
                            { Snackbar.make(sv.root, R.string.stats_login_failed, Snackbar.LENGTH_SHORT).show() })
        }
    }

    private fun onLogout() {
        BjutRetrofit.bjutService.logout()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ Snackbar.make(sv.root, R.string.stats_logout_ok, Snackbar.LENGTH_SHORT).show() },
                        { Snackbar.make(sv.root, R.string.stats_logout_failed, Snackbar.LENGTH_SHORT).show() })
    }
}
