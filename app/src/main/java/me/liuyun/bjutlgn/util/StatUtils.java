package me.liuyun.bjutlgn.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.liuyun.bjutlgn.R;
import me.liuyun.bjutlgn.entity.Stat;


public class StatUtils {
    public static Stat parseStat(String text) {
        int time = 0, flow = 0, fee = 0;
        try {
            Pattern p = Pattern.compile("time='(.*?)';flow='(.*?)';fsele=1;fee='(.*?)'");
            Matcher m = p.matcher(text.replace(" ", ""));
            while (m.find()) {
                time = Integer.parseInt(m.group(1));
                flow = Integer.parseInt(m.group(2));
                fee = Integer.parseInt(m.group(3));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Stat(flow, time, fee, true);
        //TODO if flow=0 then check real connection
    }

    public static int getPack(Context context) {
        Resources resources=context.getResources();
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(context);
        return resources.getIntArray(R.array.packages_values)[prefs.getInt("current_package", 0)];
    }

    public static int getPercent(Stat stat, Context context ) {
        return Math.round((float) stat.getFlow() / 1024 / 1024 / getPack(context) * 100);
    }
}
