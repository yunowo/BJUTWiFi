package me.liuyun.bjutlgn.ui

import android.net.CaptivePortal
import android.support.animation.SpringAnimation
import android.support.animation.SpringForce
import android.support.design.widget.Snackbar
import android.text.TextUtils
import android.text.format.Formatter
import android.view.View
import android.widget.FrameLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.status_view.view.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import me.liuyun.bjutlgn.App
import me.liuyun.bjutlgn.R
import me.liuyun.bjutlgn.api.BjutRetrofit
import me.liuyun.bjutlgn.entity.Flow
import me.liuyun.bjutlgn.entity.Stats
import me.liuyun.bjutlgn.util.NetworkUtils
import me.liuyun.bjutlgn.util.NetworkUtils.NetworkState.*
import me.liuyun.bjutlgn.util.StatsUtils
import me.liuyun.bjutlgn.util.ThemeHelper

class StatusCard(private val cv: FrameLayout, private val graphCard: GraphCard?, private val app: App, private val captivePortal: CaptivePortal?) {
    private val res = app.resources

    init {
        cv.sign_in_button.setOnClickListener { onLogin() }
        cv.sign_out_button.setOnClickListener { onLogout() }
    }

    fun onRefresh() {
        val state = if (app.prefs.getBoolean("debug", false)) STATE_BJUT_WIFI else NetworkUtils.getNetworkState(app)
        if (state == STATE_BJUT_WIFI) {
            arrayOf(cv.user, cv.progress_ring, cv.info_layout, cv.buttons_layout).forEach { it.visibility = View.VISIBLE }
            cv.non_bjut.visibility = View.GONE
        } else {
            arrayOf(cv.user, cv.progress_ring, cv.info_layout, cv.buttons_layout).forEach { it.visibility = View.GONE }
            cv.non_bjut.visibility = View.VISIBLE
        }
        when (state) {
            STATE_NO_NETWORK -> cv.non_bjut.text = res.getString(R.string.status_no_network)
            STATE_MOBILE -> cv.non_bjut.text = res.getString(R.string.status_mobile_network)
            STATE_BJUT_WIFI -> refreshStatus()
            STATE_OTHER_WIFI -> cv.non_bjut.text = res.getString(R.string.status_other_wifi).format(NetworkUtils.getWifiSSID(app))
        }
    }

    private fun refreshStatus() = async(UI) {
        val job = async(CommonPool) {
            BjutRetrofit.evictAll()
        }
        job.await()

        cv.user.text = app.prefs.getString("account", "")

        if (app.prefs.getBoolean("debug", false)) {
            statsSuccess(Stats(4000000, 100, 1000, true))
            return@async
        }

        BjutRetrofit.bjutService.stats()
                .map(StatsUtils::parseStats)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ statsSuccess(it) }, { Snackbar.make(cv, R.string.stats_refresh_failed, Snackbar.LENGTH_SHORT).show() })
    }

    private fun statsSuccess(stats: Stats) {
        cv.fee.text = res.getString(R.string.stats_fee).format(stats.fee / 10000f)
        cv.time.text = res.getString(R.string.stats_time).format(stats.time)

        val percent = StatsUtils.getPercent(stats, app)
        cv.progress_ring.centerTitle = "$percent %"
        cv.progress_ring.bottomTitle = Formatter.formatFileSize(app, stats.flow * 1024L)
        val w = res.getColor(android.R.color.white, app.theme)
        val a = ThemeHelper.getThemeAccentColor(cv.context)
        if (percent > 50) {
            cv.progress_ring.centerTitleColor = w
            cv.progress_ring.setCenterTitleStrokeColor(a)
        } else {
            cv.progress_ring.centerTitleColor = a
            cv.progress_ring.setCenterTitleStrokeColor(w)
        }
        if (percent > 20) {
            cv.progress_ring.bottomTitleColor = w
            cv.progress_ring.setBottomTitleStrokeColor(a)
        } else {
            cv.progress_ring.bottomTitleColor = a
            cv.progress_ring.setBottomTitleStrokeColor(w)
        }
        cv.progress_ring.progressValue = percent
        startSpringAnimation(cv.progress_ring)

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
        val account = app.prefs.getString("account", "")
        val password = app.prefs.getString("password", "")

        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password)) {
            BjutRetrofit.bjutService.login(account, password, "1", "123")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ refreshStatus() },
                            { Snackbar.make(cv, R.string.stats_login_failed, Snackbar.LENGTH_SHORT).show() })
        }
    }

    private fun onLogout() {
        BjutRetrofit.bjutService.logout()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ Snackbar.make(cv, R.string.stats_logout_ok, Snackbar.LENGTH_SHORT).show() },
                        { Snackbar.make(cv, R.string.stats_logout_failed, Snackbar.LENGTH_SHORT).show() })
    }
}
