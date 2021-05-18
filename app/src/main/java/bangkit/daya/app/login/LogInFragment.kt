package bangkit.daya.app.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import bangkit.daya.databinding.FragmentLogInBinding
import bangkit.daya.model.User
import com.google.android.material.snackbar.Snackbar
import org.koin.android.viewmodel.ext.android.viewModel

class LogInFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentLogInBinding
    private val loginViewModel: LoginViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLogInBinding.inflate(layoutInflater, container, false)
        binding.user = User()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListener()
        setObserver()
    }

    override fun onClick(view: View) {
        when (view) {
            binding.btnBack -> findNavController().navigateUp()
            binding.btnLogin -> loginViewModel.login(binding.user)
        }
    }

    private fun setListener() {
        binding.btnBack.setOnClickListener(this)
        binding.btnLogin.setOnClickListener(this)
    }

    private fun setObserver() {
        loginViewModel.snackBarText.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { errorMessage ->
                Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT)
                    .show()
            }
        }

        loginViewModel.loginEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { loginEvent ->
                when (loginEvent) {
                    LOGIN_SUCCESS -> {
                        val direction = LogInFragmentDirections.actionLogInFragmentToDashboardGraph()
                        findNavController().navigate(direction)
                    }
                }
            }
        }
    }
}