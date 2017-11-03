package me.liuyun.bjutlgn.ui

import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.content.SharedPreferences
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.util.DiffUtil
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import me.liuyun.bjutlgn.App
import me.liuyun.bjutlgn.R
import me.liuyun.bjutlgn.databinding.ActivityUsersBinding
import me.liuyun.bjutlgn.databinding.UserCardBinding
import me.liuyun.bjutlgn.databinding.UserDialogBinding
import me.liuyun.bjutlgn.db.UserDao
import me.liuyun.bjutlgn.entity.User

class UserActivity : AppCompatActivity() {

    val binding: ActivityUsersBinding by lazy { DataBindingUtil.setContentView<ActivityUsersBinding>(this, R.layout.activity_users) }
    lateinit internal var adapter: UserAdapter
    lateinit private var userDao: UserDao
    lateinit private var prefs: SharedPreferences
    private var currentId: Int = 0
    private var currentPackage: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        binding.fab.setOnClickListener { openUserDialog(true, User(0, "", "", 0, 0)) }

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

        val llm = LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL
        binding.recycler.layoutManager = llm
        binding.recycler.adapter = adapter
        binding.recycler.itemAnimator = DefaultItemAnimator()
        ItemTouchHelper(
                object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.START) {
                    override fun onMove(view: RecyclerView, holder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                        adapter.onItemMove(holder.adapterPosition, target.adapterPosition)
                        return true
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        adapter.onItemDismiss(viewHolder.adapterPosition)
                    }
                }).attachToRecyclerView(binding.recycler)

        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        currentId = prefs.getInt("current_user", 0)
    }

    internal fun openUserDialog(newUser: Boolean, user: User) {
        val context = this@UserActivity
        val binding: UserDialogBinding = DataBindingUtil.inflate(layoutInflater, R.layout.user_dialog, null, false)
        binding.user = user
        if (!newUser) {
            binding.spinnerPack.setSelection(user.pack)
        }
        binding.spinnerPack.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                currentPackage = position
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        AlertDialog.Builder(context)
                .setTitle(R.string.pref_user)
                .setPositiveButton(R.string.button_ok) { _, _ ->
                    if (!newUser) {
                        user.pack = currentPackage
                        userDao.update(user)
                    } else {
                        user.position = userDao.maxPosition()?.let { it.position + 1 } ?: 0
                        userDao.insert(user)
                    }
                }
                .setNegativeButton(R.string.button_cancel) { _, _ -> }
                .setView(binding.root)
                .show()
    }

    internal inner class UserAdapter(var users: MutableList<User> = mutableListOf()) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, i: Int): UserViewHolder {
            val binding: UserCardBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.user_card, parent, false)
            return UserViewHolder(binding.root)
        }

        override fun getItemCount(): Int {
            return users.size
        }

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
            holder.binding.user.text = builder
            if (user.id == currentId) {
                holder.binding.user.isChecked = true
            }

            holder.binding.buttonEdit.setOnClickListener { openUserDialog(false, user.copy()) }
            holder.binding.buttonDelete.setOnClickListener { userDao.delete(user) }
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

        internal inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val binding: UserCardBinding = DataBindingUtil.getBinding(view)
        }
    }

    internal inner class UsersDiffCallback(val oldUsers: List<User>, val newUsers: List<User>) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldUsers.size
        }

        override fun getNewListSize(): Int {
            return newUsers.size
        }

        override fun areItemsTheSame(p0: Int, p1: Int): Boolean {
            return oldUsers[p0].id == newUsers[p1].id
        }

        override fun areContentsTheSame(p0: Int, p1: Int): Boolean {
            return oldUsers[p0] == newUsers[p1]
        }

    }

}
