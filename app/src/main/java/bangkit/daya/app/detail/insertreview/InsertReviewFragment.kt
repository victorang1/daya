package bangkit.daya.app.detail.insertreview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import bangkit.daya.R
import bangkit.daya.databinding.FragmentInsertReviewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.viewmodel.ext.android.viewModel

class InsertReviewFragment : BottomSheetDialogFragment(), View.OnClickListener {

    private lateinit var binding: FragmentInsertReviewBinding
    private val insertReviewViewModel: InsertReviewViewModel by viewModel()
    private val args: InsertReviewFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInsertReviewBinding.inflate(inflater, container, false)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListener()
        setObserver()
    }

    override fun onClick(v: View) {
        when (v) {
            binding.btnPost -> postReview()
        }
    }

    private fun setListener() {
        binding.btnPost.setOnClickListener(this)
    }

    private fun setObserver() {
        insertReviewViewModel.insertReviewEvent.observe(viewLifecycleOwner) { event ->
            val result = Bundle().apply {
                putInt(INSERT_REVIEW_EVENT, event)
            }
            setFragmentResult(INSERT_REVIEW_REQUEST_CODE, result)
            findNavController().navigateUp()
        }
    }

    private fun postReview() {
        val description = binding.description ?: ""
        if (description.isNullOrEmpty()) {
            binding.etDescription.error = getString(R.string.text_field_cannot_empty)
        }
        else {
            insertReviewViewModel.insertReview(args.placeId, description)
        }
    }

    companion object {
        const val INSERT_REVIEW_EVENT = "insertReviewEvent"
        const val INSERT_REVIEW_REQUEST_CODE = "insertReviewRequestCode"
    }
}