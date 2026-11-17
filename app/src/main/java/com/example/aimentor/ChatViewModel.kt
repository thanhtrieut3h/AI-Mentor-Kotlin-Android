package com.example.aimentor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aimentor.api.AiClient
import com.example.aimentor.models.ChatItem
import com.example.aimentor.models.ChatMessage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Giữ lịch sử chat ở cấp Activity: đổi tab / xoay màn hình vẫn không mất hội thoại.
class ChatViewModel : ViewModel() {

    // Lịch sử gửi cho API (chỉ gồm user/assistant, không có lỗi)
    private val history = mutableListOf<ChatMessage>()

    private val _items = MutableStateFlow<List<ChatItem>>(emptyList())
    val items: StateFlow<List<ChatItem>> = _items

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun send(question: String) {
        val q = question.trim()
        if (q.isEmpty() || _loading.value) return

        history.add(ChatMessage("user", q))
        _items.value = _items.value + ChatItem(q, isUser = true)
        _loading.value = true

        viewModelScope.launch {
            try {
                val answer = AiClient.ask(history.toList())
                history.add(ChatMessage("assistant", answer))
                _items.value = _items.value + ChatItem(answer, isUser = false)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // Lỗi: bỏ câu hỏi vừa gửi khỏi lịch sử để lần sau không bị lệch lượt
                history.removeAt(history.lastIndex)
                _items.value = _items.value + ChatItem(
                    e.message ?: "Đã xảy ra lỗi không xác định.",
                    isUser = false,
                    isError = true
                )
            } finally {
                _loading.value = false
            }
        }
    }

    fun clear() {
        if (_loading.value) return
        history.clear()
        _items.value = emptyList()
    }
}
