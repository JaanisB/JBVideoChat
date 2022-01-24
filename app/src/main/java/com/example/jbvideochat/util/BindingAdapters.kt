package com.example.jbvideochat.util

import android.text.Layout
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jbvideochat.model.Message
import com.example.jbvideochat.ui.chat.ChatAdapter

// Bind Recyclerview to XML
@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Message>?) {
    val adapter = recyclerView.adapter as ChatAdapter
    adapter.submitList(data)
}
