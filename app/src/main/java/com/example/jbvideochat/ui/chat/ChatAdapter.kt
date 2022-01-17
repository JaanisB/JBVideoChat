package com.example.jbvideochat.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jbvideochat.databinding.ChatMessageItemBinding
import com.example.jbvideochat.model.Message

class ChatAdapter (): ListAdapter<Message, ChatAdapter.ChatViewHolder>(DiffCallBack) {

    inner class ChatViewHolder(private val binding: ChatMessageItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message?) {
            binding.message = message
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(ChatMessageItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = getItem(position)
        holder.bind(message)
    }


    companion object DiffCallBack : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }
    }




}