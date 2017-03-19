package me.liuyun.bjutlgn.ui;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.liuyun.bjutlgn.R;
import me.liuyun.bjutlgn.util.ThemeRes;
import me.liuyun.bjutlgn.util.ThemeHelper;

public class ThemeActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);

        Toolbar toolbar = ButterKnife.findById(this, R.id.theme_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        RecyclerView recyclerView = ButterKnife.findById(this, R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ThemeAdapter());
    }

    class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.ThemeItemHolder> {
        Resources resources = getResources();
        Resources.Theme theme = getTheme();
        int white = resources.getColor(android.R.color.white, theme);
        int grey = resources.getColor(android.R.color.darker_gray, theme);

        @Override
        public ThemeItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.theme_item, parent, false);
            return new ThemeItemHolder(view);
        }

        @Override
        public void onBindViewHolder(ThemeItemHolder holder, int position) {
            ThemeRes res = ThemeRes.values()[position];
            int themeColor = resources.getColor(res.getColor(), theme);
            holder.themeId = res.getStyle();
            holder.tvTheme.setText(res.getName());
            holder.tvTheme.setTextColor(themeColor);
            if (ThemeHelper.getInstance().getCurrentStyle() == res.getStyle()) {
                holder.btnChoose.setChecked(true);
                holder.btnChoose.setTextColor(themeColor);
                holder.btnChoose.setText(R.string.theme_using);
                holder.colorDot.setTextColor(white);
            } else {
                holder.btnChoose.setChecked(false);
                holder.btnChoose.setTextColor(grey);
                holder.btnChoose.setText(R.string.theme_use);
                holder.colorDot.setTextColor(themeColor);
            }
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setColor(themeColor);
            gradientDrawable.setCornerRadius(40f);
            holder.colorDot.setBackground(gradientDrawable);
        }

        @Override
        public int getItemCount() {
            return ThemeRes.values().length;
        }

        class ThemeItemHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.color_dot) TextView colorDot;
            @BindView(R.id.tv_title) TextView tvTheme;
            @BindView(R.id.btn_choose) RadioButton btnChoose;
            private int themeId;

            ThemeItemHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                btnChoose.setOnClickListener(view -> onItemClick());
            }

            private void onItemClick() {
                ThemeHelper.getInstance().setTheme((Activity) itemView.getContext(), themeId);
            }
        }
    }
}
