package com.example.aimentor.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.aimentor.R
// [MỚI] các import phục vụ màn hình chat AI
import android.content.Context
import android.os.Build
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aimentor.ChatViewModel
import com.example.aimentor.adapter.ChatAdapter
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [QuizFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class QuizFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    // [MỚI] view + adapter + ViewModel cho màn hình chat AI
    private val chatAdapter = ChatAdapter()
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var recyclerViewChat: RecyclerView
    private lateinit var etQuestion: EditText
    private lateinit var btnSend: Button
    private lateinit var btnClearChat: Button
    private lateinit var tvTyping: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quiz, container, false)
    }

    // [MỚI] Chạy sau onCreateView: ánh xạ view, nối ViewModel, lắng nghe dữ liệu
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerViewChat = view.findViewById(R.id.recyclerViewChat)
        etQuestion       = view.findViewById(R.id.etQuestion)
        btnSend          = view.findViewById(R.id.btnSend)
        btnClearChat     = view.findViewById(R.id.btnClearChat)
        tvTyping         = view.findViewById(R.id.tvTyping)

        // ViewModel gắn với Activity => đổi tab vẫn giữ nguyên hội thoại
        chatViewModel = ViewModelProvider(requireActivity())[ChatViewModel::class.java]

        recyclerViewChat.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewChat.adapter = chatAdapter

        // Android 15+ (targetSdk 35+) bật edge-to-edge: cửa sổ không tự co khi hiện bàn phím,
        // nên tự thêm padding đáy bằng đúng phần bàn phím che để ô nhập luôn nhìn thấy.
        if (Build.VERSION.SDK_INT >= 35) {
            ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
                val imeBottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
                val loc = IntArray(2)
                v.getLocationOnScreen(loc)
                val spaceBelow = v.rootView.height - (loc[1] + v.height)
                v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, maxOf(0, imeBottom - spaceBelow))
                insets
            }
        }

        btnSend.setOnClickListener { sendQuestion() }
        btnClearChat.setOnClickListener { chatViewModel.clear() }

        // Lắng nghe danh sách tin nhắn + trạng thái "đang trả lời" từ ViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    chatViewModel.items.collect { list ->
                        chatAdapter.submitList(list)
                        if (list.isNotEmpty()) recyclerViewChat.scrollToPosition(list.size - 1)
                    }
                }
                launch {
                    chatViewModel.loading.collect { loading ->
                        tvTyping.visibility = if (loading) View.VISIBLE else View.GONE
                        btnSend.isEnabled = !loading
                    }
                }
            }
        }
    }

    // [MỚI] Lấy câu hỏi từ ô nhập, gửi cho ViewModel, ẩn bàn phím
    private fun sendQuestion() {
        val question = etQuestion.text.toString().trim()
        if (question.isEmpty()) return
        chatViewModel.send(question)
        etQuestion.text.clear()
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(etQuestion.windowToken, 0)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment QuizFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            QuizFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}