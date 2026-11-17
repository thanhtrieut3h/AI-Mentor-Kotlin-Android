package com.example.aimentor.api

import com.example.aimentor.models.MessageRequest
import com.example.aimentor.models.MessageResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AnthropicApi {

    // POST https://api.anthropic.com/v1/messages
    @POST("v1/messages")
    suspend fun createMessage(@Body request: MessageRequest): MessageResponse
}
