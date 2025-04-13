package com.quangduy.chatapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.quangduy.chatapp.R
import com.quangduy.chatapp.data.model.Messages
import com.quangduy.chatapp.ultils.Constants.getUidLoggedIn

class MessageAdapter : RecyclerView.Adapter<MessageHolder>() {

    private var listOfMessage = listOf<Messages>()

    private val LEFT = 0
    private val RIGHT = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == RIGHT) {
            val view = inflater.inflate(R.layout.chat_item_right, parent, false)
            MessageHolder(view)
        } else {
            val view = inflater.inflate(R.layout.chat_item_left, parent, false)
            MessageHolder(view)
        }
    }

    override fun getItemCount() = listOfMessage.size

    override fun onBindViewHolder(holder: MessageHolder, position: Int) {
        val message = listOfMessage[position]

        holder.messageText.visibility = View.VISIBLE
        holder.timeOfSent.visibility = View.VISIBLE

        holder.messageText.text = message.message
        holder.timeOfSent.text = message.time?.substring(0, 5) ?: ""
    }

    override fun getItemViewType(position: Int) =
        if (listOfMessage[position].sender == getUidLoggedIn()) RIGHT else LEFT

    fun setList(newList: List<Messages>) {
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = listOfMessage.size
            override fun getNewListSize() = newList.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return listOfMessage[oldItemPosition].id == newList[newItemPosition].id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return listOfMessage[oldItemPosition] == newList[newItemPosition]
            }
        })
        listOfMessage = newList
        diffResult.dispatchUpdatesTo(this)
    }
}

class MessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val messageText: TextView = itemView.findViewById(R.id.show_message)
    val timeOfSent: TextView = itemView.findViewById(R.id.timeView)
}