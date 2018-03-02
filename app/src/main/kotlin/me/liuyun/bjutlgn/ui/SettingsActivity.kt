package me.liuyun.bjutlgn.ui

import android.os.Bundle
import android.preference.PreferenceFragment
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.app_bar.*
import me.liuyun.bjutlgn.BuildConfig
import me.liuyun.bjutlgn.R
import org.jetbrains.anko.browse
import org.jetbrains.anko.startActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prefs)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        fragmentManager.beginTransaction().replace(R.id.content, SettingsFragment()).commit()
    }

    class SettingsFragment : PreferenceFragment() {
        private var tapCount = 0

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.settings)
            findPreference("theme").setOnPreferenceClickListener {
                startActivity<ThemeActivity>()
                true
            }
            findPreference("version").summary = BuildConfig.VERSION_NAME
            findPreference("version").setOnPreferenceClickListener {
                tapCount++
                if (tapCount < 5) return@setOnPreferenceClickListener false
                startActivity<EasterEggActivity>()
                tapCount = 0
                true
            }
            findPreference("licenses").setOnPreferenceClickListener {
                startActivity<LicenseActivity>()
                true
            }
            findPreference("source").setOnPreferenceClickListener {
                browse(resources.getString(R.string.source_code_url))
                true
            }
        }
    }
}
