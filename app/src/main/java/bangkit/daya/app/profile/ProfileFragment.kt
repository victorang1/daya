package bangkit.daya.app.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import bangkit.daya.R
import bangkit.daya.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        binding.name = currentUser?.displayName ?: currentUser?.email
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListener()
    }

    override fun onClick(view: View) {
        when (view) {
            binding.btnLogOut -> {
                FirebaseAuth.getInstance().signOut()
                val deepLink = NavDeepLinkRequest.Builder
                    .fromUri(getString(R.string.text_deep_link_landing).toUri())
                    .build()
                findNavController().popBackStack(R.id.dashboard_graph, true)
                findNavController().navigate(deepLink)
                Toast.makeText(requireContext(), "Logout Success", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setListener() {
        binding.btnLogOut.setOnClickListener(this)
    }

}