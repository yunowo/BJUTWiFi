package me.liuyun.bjutlgn.ui

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.artitk.licensefragment.RecyclerViewLicenseFragment
import com.artitk.licensefragment.model.CustomUI
import com.artitk.licensefragment.model.License
import com.artitk.licensefragment.model.LicenseID
import com.artitk.licensefragment.model.LicenseType
import kotlinx.android.synthetic.main.app_bar.*
import me.liuyun.bjutlgn.R
import me.liuyun.bjutlgn.util.ThemeHelper

class LicenseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_license)

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        val licenses = arrayListOf(
                License(this, "Android Support Library", LicenseType.APACHE_LICENSE_20, "2005-2013", "The Android Open Source Project"),
                License(this, "RxJava", LicenseType.APACHE_LICENSE_20, "2016-present", "RxJava Contributors"),
                License(this, "RxAndroid", LicenseType.APACHE_LICENSE_20, "2015", "The RxAndroid authors"),
                License(this, "WaveLoadingView", LicenseType.APACHE_LICENSE_20, "2017", "Qi Tang")
        )

        val fragment = RecyclerViewLicenseFragment.newInstance()
                .addCustomLicense(licenses)
                .addLicense(intArrayOf(LicenseID.OKHTTP, LicenseID.RETROFIT, LicenseID.LICENSE_FRAGMENT))
                .setCustomUI(CustomUI()
                        .setTitleTextColor(ThemeHelper.getThemeAccentColor(this))
                        .setLicenseBackgroundColor(resources.getColor(R.color.grey, theme))
                        .setLicenseTextColor(Color.DKGRAY))

        fragmentManager.beginTransaction().replace(R.id.content, fragment).commit()
    }
}
