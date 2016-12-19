package me.liuyun.bjutlgn.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.Spinner;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.liuyun.bjutlgn.R;
import me.liuyun.bjutlgn.db.UserManager;
import me.liuyun.bjutlgn.entity.User;

public class UserActivity extends AppCompatActivity {
    @BindView(R.id.rec_list) RecyclerView recyclerView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fab) FloatingActionButton fab;
    UserAdapter adapter;
    private UserManager userManager;
    private SharedPreferences prefs;
    private int currentId;
    private int currentPackage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        fab.setOnClickListener(v -> openUserDialog(true, null));

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        userManager = new UserManager(this);
        adapter = new UserAdapter(userManager.getAllUsers());
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.START) {
                    public boolean onMove(RecyclerView view, RecyclerView.ViewHolder holder, RecyclerView.ViewHolder target) {
                        final int from = holder.getAdapterPosition();
                        final int to = target.getAdapterPosition();
                        final int step = from < to ? 1 : -1;
                        User first = adapter.users.get(from);
                        int previousPos = first.getPosition();
                        for (int i = from; from < to ? i < to : i > to; i += step) {
                            User next = adapter.users.get(i + step);
                            int pos = next.getPosition();
                            next.setPosition(previousPos);
                            previousPos = pos;
                            adapter.users.set(i, next);
                            userManager.updateUser(next);
                        }
                        first.setPosition(previousPos);
                        adapter.users.set(to, first);
                        userManager.updateUser(first);
                        adapter.notifyItemMoved(from, to);
                        return true;
                    }

                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        int pos = viewHolder.getAdapterPosition();
                        userManager.deleteUser(adapter.users.get(pos).getId());
                        adapter.users.remove(pos);
                        adapter.notifyItemRemoved(pos);
                    }
                }).attachToRecyclerView(recyclerView);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        currentId = prefs.getInt("current_user", 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    void openUserDialog(boolean newUser, @Nullable User currentUser) {
        Context context = UserActivity.this;
        View dialogView = LayoutInflater.from(context)
                .inflate(R.layout.user_dialog, (ViewGroup) findViewById(R.id.user_dialog));
        TextInputEditText account = ButterKnife.findById(dialogView, R.id.account);
        TextInputEditText password = ButterKnife.findById(dialogView, R.id.password);
        Spinner spinner = ButterKnife.findById(dialogView, R.id.spinner_pack);
        if (!newUser) {
            account.setText(currentUser.getAccount());
            password.setText(currentUser.getPassword());
            spinner.setSelection(currentUser.getPack());
        }
        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        currentPackage = position;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );
        new AlertDialog.Builder(context)
                .setTitle(R.string.pref_user)
                .setPositiveButton(R.string.button_ok, (dialog1, which) -> {
                    if (!newUser) {
                        currentUser.setAccount(account.getText().toString());
                        currentUser.setPassword(password.getText().toString());
                        currentUser.setPack(currentPackage);
                        userManager.updateUser(currentUser);
                        adapter.users.clear();
                        adapter.users.addAll(userManager.getAllUsers());
                        adapter.notifyItemChanged(currentUser.getPosition());
                    } else {
                        userManager.insertUser(account.getText().toString(), password.getText().toString(), currentPackage);
                        adapter.users.clear();
                        adapter.users.addAll(userManager.getAllUsers());
                        adapter.notifyItemInserted(adapter.users.size());
                    }
                })
                .setNegativeButton(R.string.button_cancel, (dialog2, which) -> {
                })
                .setView(dialogView)
                .show();
    }

    class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
        List<User> users;

        UserAdapter(List<User> users) {
            this.users = users;
        }

        @Override
        public UserViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.user_card, viewGroup, false);
            return new UserViewHolder(itemView);
        }

        @Override
        public int getItemCount() {
            if (users == null) {
                return 0;
            }
            return users.size();
        }

        @Override
        public void onBindViewHolder(UserViewHolder holder, int i) {
            User user = users.get(holder.getAdapterPosition());

            holder.itemView.setOnClickListener(v -> {
                prefs.edit()
                        .putString("account", user.getAccount())
                        .putString("password", user.getPassword())
                        .putInt("current_package", user.getPack())
                        .putInt("current_user", user.getId())
                        .apply();
                UserActivity.this.finish();
            });

            SpannableStringBuilder builder = new SpannableStringBuilder()
                    .append(user.getAccount())
                    .append("\n")
                    .append(getResources().getStringArray(R.array.packages)[user.getPack()]);
            builder.setSpan(new TextAppearanceSpan(UserActivity.this, android.R.style.TextAppearance_Small),
                    user.getAccount().length() + 1, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.userView.setText(builder);
            if (user.getId() == currentId) {
                holder.userView.setChecked(true);
            }

            holder.buttonEdit.setOnClickListener(v -> openUserDialog(false, user));
            holder.buttonDelete.setOnClickListener(v -> {
                userManager.deleteUser(user.getId());
                adapter.users.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
            });
        }

        class UserViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.user) CheckedTextView userView;
            @BindView(R.id.button_edit) Button buttonEdit;
            @BindView(R.id.button_delete) Button buttonDelete;

            UserViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }

}
