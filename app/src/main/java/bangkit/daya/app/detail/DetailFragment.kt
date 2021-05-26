package bangkit.daya.app.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import bangkit.daya.app.detail.DetailViewModel.Companion.LOAD_SUCCESS
import bangkit.daya.databinding.FragmentDetailBinding
import bangkit.daya.model.Review
import com.bumptech.glide.Glide
import org.koin.android.viewmodel.ext.android.viewModel

class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding
    private val reviewAdapter: ReviewAdapter by lazy { ReviewAdapter { onButtonLikeClick(it) }}

    private val detailViewModel: DetailViewModel by viewModel()
    private val args: DetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        setObserver()
        showLoading(true)
        detailViewModel.loadDetail(args.placeId)
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
            }
            else
                binding.flErrorContainer.visibility = View.GONE
            showLoading(false)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.llContentContainer.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        }
        else {
            binding.llContentContainer.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun onButtonLikeClick(review: Review) {

    }
}