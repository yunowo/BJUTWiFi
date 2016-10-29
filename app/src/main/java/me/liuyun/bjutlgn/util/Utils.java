package me.liuyun.bjutlgn.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.liuyun.bjutlgn.entity.Stat;


public class Utils {
    public static Stat parseStat(String text) {
        int time = 0,flow = 0, fee = 0;
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
        return new Stat(flow, time, fee);
    }
}
