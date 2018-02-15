package me.liuyun.bjutlgn.ui

import android.arch.lifecycle.Observer
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.util.DiffUtil
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import kotlinx.android.synthetic.main.activity_users.*
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.item_user.view.*
import kotlinx.android.synthetic.main.user_dialog.view.*
import me.liuyun.bjutlgn.App
import me.liuyun.bjutlgn.R
import me.liuyun.bjutlgn.db.UserDao
import me.liuyun.bjutlgn.entity.User
import org.jetbrains.anko.alert

class UserActivity : AppCompatActivity() {
    private lateinit var adapter: UserAdapter
    private lateinit var userDao: UserDao
    private lateinit var prefs: SharedPreferences
    private var currentId: Int = 0
    private var currentPackage: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        fab.setOnClickListener { openUserDialog(true, User(0, "", "", 0, 0)) }

        userDao = (application as App).appDatabase.userDao()
        adapter = UserAdapter()
        val users = userDao.all()
        users.observe(this@UserActivity, Observer {
            it?.let {
                val diff = DiffUtil.calculateDiff(UsersDiffCallback(adapter.users, it))
                adapter.users = it
                diff.dispatchUpdatesTo(adapter)
            }
        })

        recycler.adapter = adapter
        recycler.itemAnimator = DefaultItemAnimator()
        ItemTouchHelper(
                object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.START) {
                    override fun onMove(view: RecyclerView, holder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                        adapter.onItemMove(holder.adapterPosition, target.adapterPosition)
                        return true
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        adapter.onItemDismiss(viewHolder.adapterPosition)
                    }
                }).attachToRecyclerView(recycler)

        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        currentId = prefs.getInt("current_user", 0)
    }

    internal fun openUserDialog(newUser: Boolean, user: User) {
        val view: View = layoutInflater.inflate(R.layout.user_dialog, null, false)
        view.account.setText(user.account)
        view.account.setSelection(user.account.length)
        view.password.setText(user.password)
        if (!newUser) {
            view.spinner_pack.setSelection(user.pack)
        }
        view.spinner_pack.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                currentPackage = position
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        alert(R.string.pref_user) {
            customView = view
            positiveButton(R.string.button_ok) { _ ->
                user.account = view.account.text.toString()
                user.password = view.password.text.toString()
                if (!newUser) {
                    user.pack = currentPackage
                    userDao.update(user)
                } else {
                    user.position = userDao.maxPosition()?.let { it.position + 1 } ?: 0
                    userDao.insert(user)
                }
            }
            negativeButton(R.string.button_cancel) {}
        }.show()
    }

    internal inner class UserAdapter(var users: MutableList<User> = mutableListOf()) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, i: Int) =
                UserViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false))

        override fun getItemCount() = users.size

        override fun onBindViewHolder(holder: UserViewHolder, i: Int) {
            val user = users[holder.adapterPosition]

            holder.itemView.setOnClickListener {
                prefs.edit()
                        .putString("account", user.account)
                        .putString("password", user.password)
                        .putInt("current_package", user.pack)
                        .putInt("current_user", user.id)
                        .apply()
                this@UserActivity.finish()
            }

            val builder = SpannableStringBuilder()
                    .append(user.account)
                    .append("\n")
                    .append(resources.getStringArray(R.array.packages)[user.pack])
            builder.setSpan(TextAppearanceSpan(this@UserActivity, android.R.style.TextAppearance_Small),
                    user.account.length + 1, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            holder.v.user.text = builder
            if (user.id == currentId) {
                holder.v.user.isChecked = true
            }

            holder.v.button_edit.setOnClickListener { openUserDialog(false, user.copy()) }
            holder.v.button_delete.setOnClickListener { userDao.delete(user) }
        }

        fun onItemMove(from: Int, to: Int) {
            val step = if (from < to) 1 else -1
            val first = users[from]
            var previousPos = first.position
            var i = from
            while (if (from < to) i < to else i > to) {
                val next = users[i + step]
                val pos = next.position
                next.position = previousPos
                previousPos = pos
                users[i] = next
                userDao.update(next)
                i += step
            }
            first.position = previousPos
            users[to] = first
            userDao.update(first)
            notifyItemMoved(from, to)
        }

        fun onItemDismiss(pos: Int) {
            userDao.delete(adapter.users[pos])
        }

        internal inner class UserViewHolder(val v: View) : RecyclerView.ViewHolder(v)
    }

    internal inner class UsersDiffCallback(private val oldUsers: List<User>, private val newUsers: List<User>) : DiffUtil.Callback() {
        override fun getOldListSize() = oldUsers.size
        override fun getNewListSize() = newUsers.size
        override fun areItemsTheSame(p0: Int, p1: Int) = oldUsers[p0].id == newUsers[p1].id
        override fun areContentsTheSame(p0: Int, p1: Int) = oldUsers[p0] == newUsers[p1]
    }

}
