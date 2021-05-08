package bangkit.daya.app.landing

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import bangkit.daya.R
import bangkit.daya.databinding.FragmentLandingBinding
import bangkit.daya.model.LandingItem
import bangkit.daya.util.DepthTransformer

/**
 * Created by victor on 26-Apr-21 7:24 PM.
 */
class LandingFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentLandingBinding
    private val mAdapter: LandingAdapter by lazy { LandingAdapter(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLandingBinding.inflate(inflater, container, false)
        initAdapter()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        binding.btnLogin.setOnClickListener(this)
        binding.btnSignUp.setOnClickListener(this)
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

    private fun initData() {
        val items = mutableListOf<LandingItem>()
        items.add(LandingItem("Neque porro quisquam est qui dolorem ipsum quia dolor sit amet", R.drawable.user_navigation))
        items.add(LandingItem("Neque porro quisquam est qui dolorem ipsum quia dolor sit amet", R.drawable.user_navigation))
        items.add(LandingItem("Neque porro quisquam est qui dolorem ipsum quia dolor sit amet", R.drawable.user_navigation))
        mAdapter.setData(items)
    }
}