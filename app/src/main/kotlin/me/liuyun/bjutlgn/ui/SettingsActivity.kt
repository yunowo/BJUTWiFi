package me.liuyun.bjutlgn.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
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
        supportFragmentManager.beginTransaction().replace(R.id.content, SettingsFragment()).commit()
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        private var tapCount = 0

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.settings)
            findPreference<Preference>("theme")?.setOnPreferenceClickListener {
                context?.startActivity<ThemeActivity>()
                true
            }
            findPreference<Preference>("version")?.summary = BuildConfig.VERSION_NAME
            findPreference<Preference>("version")?.setOnPreferenceClickListener {
                tapCount++
                if (tapCount < 5) return@setOnPreferenceClickListener false
                context?.startActivity<EasterEggActivity>()
                tapCount = 0
                true
            }
            findPreference<Preference>("licenses")?.setOnPreferenceClickListener {
                context?.startActivity<LicenseActivity>()
                true
            }
            findPreference<Preference>("source")?.setOnPreferenceClickListener {
                context?.browse(resources.getString(R.string.source_code_url))
                true
            }
        }
    }
}
