package me.liuyun.bjutlgn.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import me.liuyun.bjutlgn.R;
import me.liuyun.bjutlgn.WiFiApplication;
import me.liuyun.bjutlgn.widget.StatusCard;

public class StatusDialog {

    public static Dialog statusDialog(@NonNull Context context) {
        context.setTheme(R.style.AppTheme_Dialog);
        FrameLayout cardView = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.status_view, null);
        new StatusCard(cardView, null, (WiFiApplication) context.getApplicationContext());
        //statusCard.onRefresh();
        return new AlertDialog.Builder(context, R.style.AppTheme_Dialog)
                .setTitle(R.string.app_name)
                .setView(cardView)
                .setPositiveButton(R.string.button_ok, null)
                .setNegativeButton(R.string.button_open_app, (dialogInterface, i) -> context.startActivity(new Intent(context, MainActivity.class)))
                .create();
    }
}
