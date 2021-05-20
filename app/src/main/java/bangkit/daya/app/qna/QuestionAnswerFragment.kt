package bangkit.daya.app.qna

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bangkit.daya.R
import bangkit.daya.databinding.FragmentQuestionAnswerBinding

class QuestionAnswerFragment : Fragment() {

    private lateinit var binding: FragmentQuestionAnswerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuestionAnswerBinding.inflate(inflater, container, false)
        return binding.root
    }
}