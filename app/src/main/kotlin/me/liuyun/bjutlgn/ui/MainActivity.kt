package me.liuyun.bjutlgn.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.databinding.DataBindingUtil
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
import me.liuyun.bjutlgn.R
import me.liuyun.bjutlgn.WiFiApplication
import me.liuyun.bjutlgn.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val binding: ActivityMainBinding by lazy { DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main) }
    lateinit var statusCard: StatusCard
    lateinit var graphCard: GraphCard
    lateinit var receiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)

        graphCard = GraphCard(binding.graphCardView?.root as CardView, application as WiFiApplication)
        statusCard = StatusCard(binding.statusCardView?.statusView?.root as FrameLayout, graphCard, application as WiFiApplication, null)

        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                statusCard.onRefresh()
            }
        }

        binding.fab.setOnClickListener { statusCard.onRefresh() }

        binding.swipeRefresh.setColorSchemeColors(resources.getColor(R.color.colorAccent, theme))
        binding.swipeRefresh.setOnRefreshListener {
            statusCard.onRefresh()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    override fun onResume() {
        super.onResume()
        statusCard.onRefresh()
        graphCard.show()
        registerReceiver(receiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        listOf(binding.statusCardView!!.root, binding.graphCardView!!.root, binding.fab, binding.toolbar)
                .forEach { startSpringAnimation(it) }
    }

    override fun onPause() {
        unregisterReceiver(receiver)
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_settings) {
            val intent = Intent(this, SettingsActivity::class.java)
            this.startActivity(intent)
            return true
        } else if (id == R.id.action_users) {
            val intent = Intent(this, UserActivity::class.java)
            this.startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
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
