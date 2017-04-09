package me.liuyun.bjutlgn.ui

import android.app.AlertDialog
import android.content.SharedPreferences
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
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
import android.widget.Button
import android.widget.CheckedTextView
import me.liuyun.bjutlgn.R
import me.liuyun.bjutlgn.databinding.ActivityUsersBinding
import me.liuyun.bjutlgn.databinding.UserCardBinding
import me.liuyun.bjutlgn.databinding.UserDialogBinding
import me.liuyun.bjutlgn.db.UserManager
import me.liuyun.bjutlgn.entity.User

class UserActivity : AppCompatActivity() {
    val binding: ActivityUsersBinding by lazy { DataBindingUtil.setContentView<ActivityUsersBinding>(this, R.layout.activity_users) }
    lateinit internal var adapter: UserAdapter
    lateinit private var userManager: UserManager
    lateinit private var prefs: SharedPreferences
    private var currentId: Int = 0
    private var currentPackage: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        binding.fab.setOnClickListener { openUserDialog(true, null) }

        val llm = LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL
        binding.recycler.layoutManager = llm
        userManager = UserManager(this)
        adapter = UserAdapter(userManager.allUsers)
        binding.recycler.adapter = adapter
        binding.recycler.itemAnimator = DefaultItemAnimator()
        ItemTouchHelper(
                object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.START) {
                    override fun onMove(view: RecyclerView, holder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                        val from = holder.adapterPosition
                        val to = target.adapterPosition
                        val step = if (from < to) 1 else -1
                        val first = adapter.users!![from]
                        var previousPos = first.position
                        var i = from
                        while (if (from < to) i < to else i > to) {
                            val next = adapter.users!![i + step]
                            val pos = next.position
                            next.position = previousPos
                            previousPos = pos
                            adapter.users!![i] = next
                            userManager.updateUser(next)
                            i += step
                        }
                        first.position = previousPos
                        adapter.users!![to] = first
                        userManager.updateUser(first)
                        adapter.notifyItemMoved(from, to)
                        return true
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val pos = viewHolder.adapterPosition
                        userManager.deleteUser(adapter.users!![pos].id)
                        adapter.users!!.removeAt(pos)
                        adapter.notifyItemRemoved(pos)
                    }
                }).attachToRecyclerView(binding.recycler)

        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        currentId = prefs.getInt("current_user", 0)
    }

    override fun onResume() {
        super.onResume()
    }

    internal fun openUserDialog(newUser: Boolean, currentUser: User?) {
        val context = this@UserActivity
        val binding: UserDialogBinding = DataBindingUtil.inflate(layoutInflater, R.layout.user_dialog, null, false)
        val account = binding.account
        val password = binding.password
        val spinner = binding.spinnerPack
        if (!newUser) {
            account.setText(currentUser!!.account)
            password.setText(currentUser.password)
            spinner.setSelection(currentUser.pack)
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                currentPackage = position
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        AlertDialog.Builder(context)
                .setTitle(R.string.pref_user)
                .setPositiveButton(R.string.button_ok) { _, _ ->
                    if (!newUser) {
                        currentUser!!.account = account.text.toString()
                        currentUser.password = password.text.toString()
                        currentUser.pack = currentPackage
                        userManager.updateUser(currentUser)
                        adapter.users!!.clear()
                        adapter.users!!.addAll(userManager.allUsers!!)
                        adapter.notifyItemChanged(currentUser.position)
                    } else {
                        userManager.insertUser(account.text.toString(), password.text.toString(), currentPackage)
                        adapter.users!!.clear()
                        adapter.users!!.addAll(userManager.allUsers!!)
                        adapter.notifyItemInserted(adapter.users!!.size)
                    }
                }
                .setNegativeButton(R.string.button_cancel) { _, _ -> }
                .setView(binding.root)
                .show()
    }

    internal inner class UserAdapter(var users: MutableList<User>?) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, i: Int): UserViewHolder {
            val binding: UserCardBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.user_card, parent, false)
            return UserViewHolder(binding.root)
        }

        override fun getItemCount(): Int {
            return users?.size ?: 0
        }

        override fun onBindViewHolder(holder: UserViewHolder, i: Int) {
            val user = users!![holder.adapterPosition]

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
            holder.userView.text = builder
            if (user.id == currentId) {
                holder.userView.isChecked = true
            }

            holder.buttonEdit.setOnClickListener { openUserDialog(false, user) }
            holder.buttonDelete.setOnClickListener {
                userManager.deleteUser(user.id)
                adapter.users!!.removeAt(holder.adapterPosition)
                notifyItemRemoved(holder.adapterPosition)
            }
        }

        internal inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val binding: UserCardBinding = DataBindingUtil.getBinding(view)
            var userView: CheckedTextView = binding.user
            var buttonEdit: Button = binding.buttonEdit
            var buttonDelete: Button = binding.buttonDelete

            init {

            }
        }
    }

}
