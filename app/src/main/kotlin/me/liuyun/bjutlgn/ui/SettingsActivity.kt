package me.liuyun.bjutlgn.ui

import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceFragment
import android.support.v7.app.AppCompatActivity
import me.liuyun.bjutlgn.BuildConfig
import me.liuyun.bjutlgn.R
import me.liuyun.bjutlgn.databinding.ActivityPrefsBinding

class SettingsActivity : AppCompatActivity() {
    val binding: ActivityPrefsBinding by lazy { DataBindingUtil.setContentView<ActivityPrefsBinding>(this, R.layout.activity_prefs) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        fragmentManager.beginTransaction().replace(R.id.content, SettingsFragment()).commit()
    }

    class SettingsFragment : PreferenceFragment() {
        private var tapCount: Int = 0

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.settings)
            findPreference("theme").setOnPreferenceClickListener {
                context.startActivity(Intent(context, ThemeActivity::class.java))
                true
            }
            findPreference("version").summary = BuildConfig.VERSION_NAME
            findPreference("version").setOnPreferenceClickListener {
                tapCount++
                if (tapCount < 5) return@setOnPreferenceClickListener false
                startActivity(Intent(context, EasterEggActivity::class.java))
                tapCount = 0
                true
            }
            findPreference("licenses").setOnPreferenceClickListener {
                startActivity(Intent(context, LicenseActivity::class.java))
                true
            }
            findPreference("source").setOnPreferenceClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(resources.getString(R.string.source_code_url))))
                true
            }
        }


    }
}
