package me.liuyun.bjutlgn.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.animation.SpringAnimation
import android.support.animation.SpringForce
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.status_card.view.*
import me.liuyun.bjutlgn.App
import me.liuyun.bjutlgn.R
import me.liuyun.bjutlgn.util.ThemeHelper
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {
    lateinit var statusCard: StatusCard
    lateinit var graphCard: GraphCard
    private var receiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        graphCard = GraphCard(graph_card_view as CardView, application as App)
        statusCard = StatusCard(status_card_view.status_view as FrameLayout, graphCard, application as App, null)

        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) = statusCard.onRefresh()
        }

        fab.setOnClickListener { statusCard.onRefresh() }

        swipe_refresh.setColorSchemeColors(ThemeHelper.getThemePrimaryColor(this))
        swipe_refresh.setOnRefreshListener {
            statusCard.onRefresh()
            swipe_refresh.isRefreshing = false
        }
    }

    override fun onResume() {
        super.onResume()
        statusCard.onRefresh()
        graphCard.show()

        receiver?.let { registerReceiver(receiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)) }

        listOf(status_card_view, graph_card_view, fab, toolbar).forEach { startSpringAnimation(it) }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            startActivity<SettingsActivity>()
            true
        }
        R.id.action_users -> {
            startActivity<UserActivity>()
            true
        }
        else -> super.onOptionsItemSelected(item)
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
}
