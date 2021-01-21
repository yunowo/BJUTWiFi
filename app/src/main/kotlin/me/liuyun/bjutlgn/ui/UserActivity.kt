package me.liuyun.bjutlgn.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import me.liuyun.bjutlgn.App
import me.liuyun.bjutlgn.R
import me.liuyun.bjutlgn.databinding.ActivityUsersBinding
import me.liuyun.bjutlgn.databinding.ItemUserBinding
import me.liuyun.bjutlgn.databinding.UserDialogBinding
import me.liuyun.bjutlgn.db.UserDao
import me.liuyun.bjutlgn.entity.User

class UserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUsersBinding
    private lateinit var adapter: UserAdapter
    private lateinit var userDao: UserDao
    private lateinit var prefs: SharedPreferences
    private var currentId: Int = 0
    private var currentPackage: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBar.toolbar)
        binding.appBar.toolbar.setNavigationOnClickListener { onBackPressed() }
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
        val binding = UserDialogBinding.inflate(layoutInflater, null, false)
        with(binding) {
            account.setText(user.account)
            account.setSelection(user.account.length)
            password.setText(user.password)

            val items = resources.getStringArray(R.array.packages)
            val adapter = ArrayAdapter(this@UserActivity, R.layout.item_package, items)
            spinnerPack.setAdapter(adapter)
            if (!newUser) {
                spinnerPack.setText(items[user.pack], false)
            }
            spinnerPack.setOnItemClickListener { _, _, position, _ ->
                currentPackage = position
            }
        }
        with(MaterialAlertDialogBuilder(this)) {
            setView(binding.root)
            setPositiveButton(R.string.button_ok) { _, _ ->
                user.account = binding.account.text.toString()
                user.password = binding.password.text.toString()
                if (!newUser) {
                    user.pack = currentPackage
                    userDao.update(user)
                } else {
                    user.position = userDao.maxPosition()?.let { it.position + 1 } ?: 0
                    userDao.insert(user)
                }
            }
            setNegativeButton(R.string.button_cancel) { _, _ -> Unit }
            show()
        }
    }

    internal inner class UserAdapter(var users: MutableList<User> = mutableListOf()) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, i: Int) =
                UserViewHolder(ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        override fun getItemCount() = users.size

        override fun onBindViewHolder(holder: UserViewHolder, i: Int) {
            val user = users[holder.adapterPosition]

            holder.itemView.setOnClickListener {
                prefs.edit {
                    putString("account", user.account)
                    putString("password", user.password)
                    putInt("current_package", user.pack)
                    putInt("current_user", user.id)
                }
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

        internal inner class UserViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)
    }

    internal class UsersDiffCallback(private val oldUsers: List<User>, private val newUsers: List<User>) : DiffUtil.Callback() {
        override fun getOldListSize() = oldUsers.size
        override fun getNewListSize() = newUsers.size
        override fun areItemsTheSame(p0: Int, p1: Int) = oldUsers[p0].id == newUsers[p1].id
        override fun areContentsTheSame(p0: Int, p1: Int) = oldUsers[p0] == newUsers[p1]
    }

}
