package bangkit.daya.app.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import bangkit.daya.R
import bangkit.daya.databinding.FragmentSplashBinding
import com.google.firebase.auth.FirebaseAuth

class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Handler(Looper.getMainLooper()).postDelayed({
            handleUserNavigation()
        }, SPLASH_DELAY)
    }

    private fun handleUserNavigation() {
        val resId =
            if (FirebaseAuth.getInstance().currentUser != null) R.id.action_splashFragment_to_dashboard_graph
            else R.id.action_splashFragment_to_landingFragment
        findNavController().navigate(resId)
    }

    companion object {
        const val SPLASH_DELAY = 2000L
    }
}