package bangkit.daya.app.landing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import bangkit.daya.R
import bangkit.daya.databinding.FragmentLandingBinding
import bangkit.daya.util.DepthTransformer
import org.koin.android.viewmodel.ext.android.viewModel

class LandingFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentLandingBinding
    private val landingViewModel: LandingViewModel by viewModel()
    private val mAdapter: LandingAdapter by lazy { LandingAdapter(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLandingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        setListener()
        setObserver()
        landingViewModel.loadLandingData()
    }

    override fun onClick(v: View) {
        when (v) {
            binding.btnLogin -> findNavController().navigate(R.id.action_landingFragment_to_logInFragment)
            binding.btnSignUp -> findNavController().navigate(R.id.action_landingFragment_to_signUpFragment)
        }
    }

    private fun initAdapter() {
        binding.viewPager.adapter = mAdapter
        binding.viewPager.setPageTransformer(DepthTransformer())
        binding.circleIndicator.setViewPager(binding.viewPager)
        mAdapter.registerAdapterDataObserver(binding.circleIndicator.adapterDataObserver)
    }

    private fun setListener() {
        binding.btnLogin.setOnClickListener(this)
        binding.btnSignUp.setOnClickListener(this)
    }

    private fun setObserver() {
        landingViewModel.landingItems.observe(viewLifecycleOwner) { landingItems ->
            mAdapter.setData(landingItems)
        }
    }
}