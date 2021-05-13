package bangkit.daya.app.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import bangkit.daya.databinding.FragmentHomeBinding
import org.koin.android.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by viewModel()
    private val homeAdapter: HomeAdapter by lazy { HomeAdapter { destinationId ->
        findNavController().navigate(destinationId)
    }}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        setObserver()
        homeViewModel.loadDashboardData()
    }

    private fun initAdapter() {
        binding.rvFeatures.layoutManager = GridLayoutManager(requireContext(), GRID_COUNT)
        binding.rvFeatures.adapter = homeAdapter
    }

    private fun setObserver() {
        homeViewModel.dashboardItems.observe(viewLifecycleOwner) { dashBoardItems ->
            homeAdapter.setData(dashBoardItems)
        }
    }

    companion object {
        private const val GRID_COUNT = 2
    }
}