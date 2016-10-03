package me.liuyun.bjutlgn;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utils {
    public static Stat parseStat(String text) {
        int time = 0;
        float flow = 0f, fee = 0f;
        try {
            Pattern p = Pattern.compile("time='(.*?)';flow='(.*?)';fsele=1;fee='(.*?)'");
            Matcher m = p.matcher(text.replace(" ", ""));
            while (m.find()) {
                time = Integer.parseInt(m.group(1));
                flow = Float.parseFloat(m.group(2)) / 1024;
                fee = Float.parseFloat(m.group(3)) / 100;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Stat(flow, time, fee);
    }
}
