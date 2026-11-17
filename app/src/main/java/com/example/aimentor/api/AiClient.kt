package com.example.aimentor.api

import com.example.aimentor.BuildConfig
import com.example.aimentor.models.ApiErrorResponse
import com.example.aimentor.models.ChatMessage
import com.example.aimentor.models.MessageRequest
import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

// Client riêng cho API AI. Tách khỏi RetrofitClient (FakeStore) để API key
// chỉ được gửi tới api.anthropic.com, không gửi nhầm sang server khác.
object AiClient {

    private const val BASE_URL = "https://api.anthropic.com/"
    private const val MODEL = "claude-haiku-5-5"   // có thể đổi sang model khác
    private const val MAX_TOKENS = 1024
    private const val SYSTEM_PROMPT =
        "Bạn là AI-Mentor, trợ lý học tập cho sinh viên công nghệ thông tin. " +
        "Luôn trả lời bằng tiếng Việt, ngắn gọn, dễ hiểu, có ví dụ khi cần. " +
        "Chỉ trả lời dạng văn bản thuần, không dùng ký hiệu markdown như **, # hay dấu `."

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)   // AI trả lời chậm hơn API thường
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("x-api-key", BuildConfig.ANTHROPIC_API_KEY)
                .header("anthropic-version", "2023-06-01")
                .build()
            chain.proceed(request)
        }
        .build()

    private val api: AnthropicApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AnthropicApi::class.java)
    }

    // Gửi lịch sử hội thoại, trả về câu trả lời của AI.
    // Nếu lỗi sẽ ném Exception với message tiếng Việt để hiển thị cho người dùng.
    suspend fun ask(history: List<ChatMessage>): String {
        if (BuildConfig.ANTHROPIC_API_KEY.isBlank()) {
            throw IllegalStateException(
                "Chưa cấu hình API key. Hãy thêm dòng ANTHROPIC_API_KEY=... vào file local.properties rồi build lại."
            )
        }
        try {
            val response = api.createMessage(
                MessageRequest(
                    model = MODEL,
                    maxTokens = MAX_TOKENS,
                    system = SYSTEM_PROMPT,
                    messages = history
                )
            )
            val text = response.content.orEmpty()
                .filter { it.type == "text" }
                .joinToString("\n") { it.text.orEmpty() }
                .trim()
            return text.ifEmpty { "(AI không trả về nội dung)" }
        } catch (e: HttpException) {
            val raw = e.response()?.errorBody()?.string()
            val detail = try {
                Gson().fromJson(raw, ApiErrorResponse::class.java)?.error?.message
            } catch (parseError: Exception) {
                null
            }
            throw Exception("Lỗi ${e.code()}: ${detail ?: "Máy chủ AI từ chối yêu cầu."}")
        } catch (e: IOException) {
            throw Exception("Không kết nối được tới máy chủ AI. Kiểm tra Internet rồi thử lại.")
        }
    }
}
