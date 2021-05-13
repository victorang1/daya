package bangkit.daya.app.signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import bangkit.daya.databinding.FragmentSignUpBinding
import bangkit.daya.model.User
import com.google.android.material.snackbar.Snackbar
import org.koin.android.viewmodel.ext.android.viewModel

class SignUpFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentSignUpBinding
    private val signUpViewModel: SignUpViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
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
            binding.btnNext -> signUpViewModel.registerUser(binding.user)
        }
    }

    private fun setListener() {
        binding.btnBack.setOnClickListener(this)
        binding.btnNext.setOnClickListener(this)
    }

    private fun setObserver() {
        signUpViewModel.snackBarText.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { errorMessage ->
                Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT)
                    .show()
            }
        }

        signUpViewModel.registerEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { signUpEvent ->
                when (signUpEvent) {
                    REDIRECT_TO_SECOND_STEP -> {
                        val direction = SignUpFragmentDirections.actionSignUpFragmentToSignUpTwoFragment(binding.user!!)
                        findNavController().navigate(direction)
                    }
                }
            }
        }
    }
}