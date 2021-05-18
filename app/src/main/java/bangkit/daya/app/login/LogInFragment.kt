package bangkit.daya.app.login

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import bangkit.daya.R
import bangkit.daya.databinding.FragmentLogInBinding
import bangkit.daya.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import org.koin.android.viewmodel.ext.android.viewModel

class LogInFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentLogInBinding
    private val loginViewModel: LoginViewModel by viewModel()

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLogInBinding.inflate(layoutInflater, container, false)
        binding.user = User()
        initGoogleSignIn()
        auth = FirebaseAuth.getInstance()
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
            binding.btnLogin -> {
                if (loginViewModel.isValid(binding.user)) {
                    binding.progressBar.visibility = View.VISIBLE
                    val signInIntent = googleSignInClient.signInIntent
                    startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN_REQUEST) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
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

    private fun initGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                binding.progressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Login Success", Toast.LENGTH_SHORT).show()
                    loginViewModel.sendEvent(LOGIN_SUCCESS)
                } else {
                    Toast.makeText(requireContext(), "Sign In Failed: ${task.exception}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    companion object {
        private const val GOOGLE_SIGN_IN_REQUEST = 100
    }
}