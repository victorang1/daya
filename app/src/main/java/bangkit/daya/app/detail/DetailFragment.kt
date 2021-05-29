package bangkit.daya.app.detail

import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import bangkit.daya.R
import bangkit.daya.app.detail.DetailViewModel.Companion.LOAD_SUCCESS
import bangkit.daya.app.detail.insertreview.InsertReviewFragment
import bangkit.daya.app.detail.insertreview.InsertReviewViewModel
import bangkit.daya.databinding.FragmentDetailBinding
import bangkit.daya.model.Review
import bangkit.daya.pref.AppPreferences
import com.bumptech.glide.Glide
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class DetailFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentDetailBinding
    private val reviewAdapter: ReviewAdapter by lazy { ReviewAdapter { onButtonLikeClick(it) } }

    private val detailViewModel: DetailViewModel by viewModel()
    private val args: DetailFragmentArgs by navArgs()

    private val pref: AppPreferences by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fabInsert.visibility = View.GONE
        if (pref.shouldShowMovableInformation()) {
            createTargetMovableInformation()
        }
        initAdapter()
        setObserver()
        setListener()
        showLoading(true)
        detailViewModel.loadDetail(args.placeId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener(InsertReviewFragment.INSERT_REVIEW_REQUEST_CODE) { _, bundle: Bundle ->
            when (bundle.getInt(InsertReviewFragment.INSERT_REVIEW_EVENT)) {
                InsertReviewViewModel.INSERT_SUCCESS -> {
                    detailViewModel.refreshReview(args.placeId)
                    Toast.makeText(requireContext(), "Insert Success", Toast.LENGTH_SHORT).show()
                }
                InsertReviewViewModel.INSERT_ERROR -> {
                    Toast.makeText(requireContext(), "Insert Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onClick(v: View) {
        when (v) {
            binding.fabInsert -> {
                val directions = DetailFragmentDirections.actionDetailFragmentToInsertReviewFragment(args.placeId)
                findNavController().navigate(directions)
            }
        }
    }

    private fun initAdapter() {
        binding.rvComment.layoutManager = LinearLayoutManager(requireContext())
        binding.rvComment.adapter = reviewAdapter
    }

    private fun setObserver() {
        detailViewModel.detailData.observe(viewLifecycleOwner) { detail ->
            binding.flErrorContainer.visibility = View.GONE
            binding.detailPlace = detail
            Glide.with(requireContext())
                .load(detail.photo)
                .into(binding.ivObject)
        }

        detailViewModel.reviews.observe(viewLifecycleOwner) { reviews ->
            reviewAdapter.setData(reviews)
            binding.fabInsert.visibility = View.VISIBLE
        }

        detailViewModel.detailEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                LOAD_SUCCESS -> showLoading(false)
            }
        }

        detailViewModel.error.observe(viewLifecycleOwner) { throwable ->
            if (throwable != null) {
                binding.flErrorContainer.visibility = View.VISIBLE
                binding.error = throwable.message
            } else
                binding.flErrorContainer.visibility = View.GONE
            showLoading(false)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.llContentContainer.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.llContentContainer.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun setListener() {
        binding.fabInsert.setOnClickListener(this)
    }

    private fun onButtonLikeClick(review: Review) {
        detailViewModel.likeReview(review.postId)
    }

    private fun createTargetMovableInformation() {
        TapTargetView.showFor(requireActivity(),
            TapTarget.forView(binding.fabInsert, getString(R.string.text_information), getString(R.string.text_movable_information))
                .targetCircleColor(R.color.tap_target_circle_color)
                .titleTextColor(R.color.tap_target_title_text_color)
                .descriptionTextColor(R.color.tap_target_title_text_color)
                .outerCircleColor(R.color.tap_target_outer_circle_color)
                .textTypeface(Typeface.SANS_SERIF)
                .tintTarget(false)
                .cancelable(false)
                .targetRadius(60),
            object: TapTargetView.Listener() {
                override fun onTargetClick(view: TapTargetView?) {
                    super.onTargetClick(view)
                    pref.disableShowMovableInformation()
                }
            }
        )
    }
}