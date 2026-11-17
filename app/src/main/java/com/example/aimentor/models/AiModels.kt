package com.example.aimentor.models

import com.google.gson.annotations.SerializedName

// ===== Các model cho API AI (Anthropic Messages API) =====

// 1 tin nhắn gửi lên API. role = "user" (người hỏi) hoặc "assistant" (AI trả lời)
data class ChatMessage(
    val role: String,
    val content: String
)

// Body JSON gửi lên: POST https://api.anthropic.com/v1/messages
data class MessageRequest(
    val model: String,
    @SerializedName("max_tokens") val maxTokens: Int,
    val system: String? = null,          // chỉ dẫn vai trò cho AI (null thì Gson tự bỏ qua)
    val messages: List<ChatMessage>      // toàn bộ lịch sử hội thoại để AI nhớ ngữ cảnh
)

// JSON nhận về: { "content": [ { "type": "text", "text": "..." } ], ... }
data class MessageResponse(
    val content: List<ContentBlock>?
)

data class ContentBlock(
    val type: String?,
    val text: String?
)

// JSON lỗi: { "error": { "type": "...", "message": "..." } }
data class ApiErrorResponse(
    val error: ApiErrorDetail?
)

data class ApiErrorDetail(
    val type: String?,
    val message: String?
)
