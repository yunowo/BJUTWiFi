package me.liuyun.bjutlgn.ui

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.artitk.licensefragment.RecyclerViewLicenseFragment
import com.artitk.licensefragment.model.CustomUI
import com.artitk.licensefragment.model.License
import com.artitk.licensefragment.model.LicenseID
import com.artitk.licensefragment.model.LicenseType
import me.liuyun.bjutlgn.R
import me.liuyun.bjutlgn.databinding.ActivityLicenseBinding
import me.liuyun.bjutlgn.util.ThemeHelper

class LicenseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLicenseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLicenseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBar.toolbar)
        binding.appBar.toolbar.setNavigationOnClickListener { onBackPressed() }

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
