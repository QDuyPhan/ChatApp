package com.quangduy.chatapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.quangduy.chatapp.R
import com.quangduy.chatapp.data.model.Users
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter : RecyclerView.Adapter<UserHolder>() {

    private var listOfUsers = emptyList<Users>()
    private var listener: OnItemClickListener? = null
    private var isSkeleton = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_list_item, parent, false)
        return UserHolder(view)
    }

    override fun getItemCount(): Int = listOfUsers.size

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        val user = listOfUsers[position]

        if (isSkeleton) {
            bindSkeleton(holder)
        } else {
            bindUser(holder, user, position)
        }
    }

    private fun bindSkeleton(holder: UserHolder) {
        holder.profileName.text = ""
        holder.imageProfile.setImageResource(R.drawable.skeleton_circle)
        holder.statusImageView.visibility = View.GONE
    }

    private fun bindUser(holder: UserHolder, user: Users, position: Int) {
        val firstName = user.username?.split("\\s".toRegex())?.firstOrNull().orEmpty()
        holder.profileName.text = firstName

        holder.statusImageView.apply {
            visibility = View.VISIBLE
            setImageResource(
                if (user.status == "Online") R.drawable.onlinestatus
                else R.drawable.offlinestatus
            )
        }

        Glide.with(holder.itemView.context)
            .load(user.imageUrl)
            .into(holder.imageProfile)

        holder.itemView.setOnClickListener {
            listener?.onUserSelected(position, user)
        }
    }

    fun setOnClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<Users>) {
        isSkeleton = false
        listOfUsers = list
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun showSkeleton() {
        isSkeleton = true
        listOfUsers = List(5) { Users("", "", "", "") }
        notifyDataSetChanged()
    }
}

class UserHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val profileName: TextView = itemView.findViewById(R.id.userName)
    val imageProfile: CircleImageView = itemView.findViewById(R.id.imageViewUser)
    val statusImageView: ImageView = itemView.findViewById(R.id.statusOnline)
}

interface OnItemClickListener {
    fun onUserSelected(position: Int, users: Users)
}
