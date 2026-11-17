package com.example.aimentor.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aimentor.R
import com.example.aimentor.models.ChatItem

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private var items: List<ChatItem> = emptyList()

    fun submitList(newList: List<ChatItem>) {
        items = newList
        notifyDataSetChanged()
    }

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)

        fun bind(item: ChatItem) {
            tvMessage.text = item.text
            // Tin lỗi hiển thị chữ đỏ; tin AI bình thường dùng màu chữ đen
            if (!item.isUser) {
                tvMessage.setTextColor(if (item.isError) Color.parseColor("#D32F2F") else Color.BLACK)
            }
        }
    }

    // 0 = tin của người dùng (bên phải), 1 = tin của AI (bên trái)
    override fun getItemViewType(position: Int): Int = if (items[position].isUser) 0 else 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val layout = if (viewType == 0) R.layout.item_chat_user else R.layout.item_chat_ai
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
