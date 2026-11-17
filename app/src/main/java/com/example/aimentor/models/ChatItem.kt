package com.example.aimentor.models

// 1 dòng hiển thị trên màn hình chat (khác ChatMessage: ChatMessage dùng để gửi API,
// ChatItem dùng để vẽ giao diện và có thêm cờ isError)
data class ChatItem(
    val text: String,
    val isUser: Boolean,
    val isError: Boolean = false
)
