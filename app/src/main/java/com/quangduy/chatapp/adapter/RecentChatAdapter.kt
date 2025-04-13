package com.quangduy.chatapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.quangduy.chatapp.R
import com.quangduy.chatapp.data.model.RecentChats
import com.quangduy.chatapp.ultils.Logger
import de.hdodenhof.circleimageview.CircleImageView

class RecentChatAdapter(
    private val listener: OnChatClicked
) : ListAdapter<RecentChats, MyChatListHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyChatListHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recent_chat_list, parent, false)
        return MyChatListHolder(view)
    }

    override fun onBindViewHolder(holder: MyChatListHolder, position: Int) {
        val chat = getItem(position)

        holder.userName.text = chat.name

        val messagePreview = chat.message?.split(" ")?.take(4)?.joinToString(" ") ?: ""
        val lastMessage = "${chat.person}: $messagePreview"
        holder.lastMessage.text = lastMessage

        Glide.with(holder.itemView.context)
            .load(chat.friendsImage)
            .placeholder(R.drawable.placeholder_avatar)
            .into(holder.imageView)

        Logger.logI("RecentChatAdapter Image URL: ${chat.friendsImage}")

        holder.timeView.text = chat.time?.substring(0, 5) ?: ""

        holder.itemView.setOnClickListener {
            listener.onChatClicked(position, chat)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<RecentChats>() {
        override fun areItemsTheSame(oldItem: RecentChats, newItem: RecentChats): Boolean {
            return oldItem.friendId == newItem.friendId
        }

        override fun areContentsTheSame(oldItem: RecentChats, newItem: RecentChats): Boolean {
            return oldItem == newItem
        }
    }
}

class MyChatListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val imageView: CircleImageView = itemView.findViewById(R.id.recentChatImageView)
    val userName: TextView = itemView.findViewById(R.id.recentChatTextName)
    val lastMessage: TextView = itemView.findViewById(R.id.recentChatTextLastMessage)
    val timeView: TextView = itemView.findViewById(R.id.recentChatTextTime)
}

interface OnChatClicked {
    fun onChatClicked(position: Int, chat: RecentChats)
}