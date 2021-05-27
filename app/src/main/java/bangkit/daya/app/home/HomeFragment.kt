package bangkit.daya.app.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import bangkit.daya.R
import bangkit.daya.databinding.FragmentHomeBinding

class HomeFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cvOne.setOnClickListener(this)
        binding.cvTwo.setOnClickListener(this)
        binding.fabIm.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v) {
            binding.fabIm -> findNavController().navigate(R.id.action_homeFragment_to_imageRecognitionFragment)
            binding.cvOne -> findNavController().navigate(R.id.action_homeFragment_to_arFragment)
            binding.cvTwo -> findNavController().navigate(R.id.action_homeFragment_to_datasetListActivity)
        }
    }
}