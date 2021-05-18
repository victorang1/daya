package bangkit.daya.app.signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import bangkit.daya.databinding.FragmentSignUpBinding
import bangkit.daya.model.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.viewmodel.ext.android.viewModel

class SignUpFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentSignUpBinding
    private val signUpViewModel: SignUpViewModel by viewModel()

    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        binding.user = User()
        mAuth = FirebaseAuth.getInstance()
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
            binding.btnNext -> registerUser()
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
                    REGISTER_SUCCESS -> {
                        val direction = SignUpFragmentDirections.actionSignUpFragmentToDashboardGraph()
                        findNavController().navigate(direction)
                    }
                }
            }
        }
    }

    private fun registerUser() {
        binding.progressBar.visibility = View.VISIBLE
        val user = binding.user
        if (user != null && signUpViewModel.isValid(user)) {
            mAuth.createUserWithEmailAndPassword(user.email, user.password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        signUpViewModel.sendEvent(REGISTER_SUCCESS)
                        Toast.makeText(requireContext(), "Register Success", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        Toast.makeText(requireContext(), "${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                    binding.progressBar.visibility = View.GONE
                }
        }
    }
}