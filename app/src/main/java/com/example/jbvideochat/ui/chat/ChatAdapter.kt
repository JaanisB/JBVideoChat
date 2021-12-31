package com.example.jbvideochat.ui.chat

import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jbvideochat.databinding.ChatMessageItemBinding

class ChatAdapter (private val messageList: List<String>): RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(private val binding: ChatMessageItemBinding) : RecyclerView.ViewHolder() {
        val textView: TextView

        init {
            // Define click listener for the ViewHolder's View.
            textView = view.findViewById(R.id.textView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }


}