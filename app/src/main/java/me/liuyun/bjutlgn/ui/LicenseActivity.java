package me.liuyun.bjutlgn.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.artitk.licensefragment.model.CustomUI;
import com.artitk.licensefragment.model.License;
import com.artitk.licensefragment.model.LicenseID;
import com.artitk.licensefragment.model.LicenseType;
import com.artitk.licensefragment.support.v4.RecyclerViewLicenseFragment;

import java.util.ArrayList;

import me.liuyun.bjutlgn.R;

public class LicenseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);

        ArrayList<License> licenses = new ArrayList<>();
        licenses.add(new License(this, "Android Support Library", LicenseType.APACHE_LICENSE_20, "2005-2013", "The Android Open Source Project"));
        licenses.add(new License(this, "RxJava", LicenseType.APACHE_LICENSE_20, "2016-present", "RxJava Contributors"));
        licenses.add(new License(this, "RxAndroid", LicenseType.APACHE_LICENSE_20, "2015", "The RxAndroid authors"));
        licenses.add(new License(this, "Butter Knife", LicenseType.APACHE_LICENSE_20, "2013", "Jake Wharton"));
        licenses.add(new License(this, "RingProgressBar", LicenseType.APACHE_LICENSE_20, "2017", "HotBitmapGG"));
        licenses.add(new License(this, "Rebound", LicenseType.BSD_2_CLAUSE, "2013", "Facebook, Inc."));
        licenses.add(new License(this, "ORMLite", R.raw.isc, "", ""));
        licenses.add(new License(this, "streamsupport", LicenseType.GPL_30, "", ""));

        Fragment fragment = RecyclerViewLicenseFragment.newInstance()
                .addCustomLicense(licenses)
                .addLicense(new int[]{LicenseID.OKHTTP, LicenseID.RETROFIT, LicenseID.LICENSE_FRAGMENT})
                .setCustomUI(new CustomUI()
                        .setTitleTextColor(getResources().getColor(R.color.colorAccent, getTheme()))
                        .setLicenseBackgroundColor(getResources().getColor(R.color.background_grey, getTheme()))
                        .setLicenseTextColor(Color.DKGRAY));

        getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();
    }
}
