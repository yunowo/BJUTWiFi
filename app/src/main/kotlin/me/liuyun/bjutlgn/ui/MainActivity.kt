package me.liuyun.bjutlgn.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.LinearLayout
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

        graphCard = GraphCard(graph_card_view as LinearLayout, application as App)
        statusCard = StatusCard(status_card_view.status_view as FrameLayout, graphCard, application as App, null)

        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) = statusCard.onRefresh()
        }

        swipe_refresh.setColorSchemeColors(ThemeHelper.getThemeAccentColor(this))
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
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu.findItem(R.id.action_refresh).setOnMenuItemClickListener { statusCard.onRefresh();true }
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

}
