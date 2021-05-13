package bangkit.daya.app.signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import bangkit.daya.databinding.FragmentSignUpTwoBinding
import com.google.android.material.snackbar.Snackbar
import org.koin.android.viewmodel.ext.android.viewModel

class SignUpTwoFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentSignUpTwoBinding
    private val signUpViewModel: SignUpViewModel by viewModel()
    private val args: SignUpTwoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpTwoBinding.inflate(inflater, container, false)
        binding.user = args.user
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
            binding.btnSignUp -> signUpViewModel.register(binding.user)
        }
    }

    private fun setListener() {
        binding.btnBack.setOnClickListener(this)
        binding.btnSignUp.setOnClickListener(this)
    }

    private fun setObserver() {
        signUpViewModel.snackBarText.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { errorMessage ->
                Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
    }
}