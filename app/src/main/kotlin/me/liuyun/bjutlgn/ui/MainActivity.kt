package me.liuyun.bjutlgn.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import me.liuyun.bjutlgn.App
import me.liuyun.bjutlgn.R
import me.liuyun.bjutlgn.databinding.ActivityMainBinding
import me.liuyun.bjutlgn.util.ThemeHelper
import me.liuyun.bjutlgn.util.startActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var statusCard: StatusCard
    lateinit var graphCard: GraphCard
    private var receiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        graphCard = GraphCard(binding.graphCardView, application as App)
        statusCard = StatusCard(binding.statusCardView.statusView, graphCard, application as App, null)

        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) = statusCard.onRefresh()
        }

        binding.swipeRefresh.setColorSchemeColors(ThemeHelper.getThemeAccentColor(this))
        binding.swipeRefresh.setOnRefreshListener {
            statusCard.onRefresh()
            binding.swipeRefresh.isRefreshing = false
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
