package com.quangduy.chatapp.ui.fragments.signin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.quangduy.chatapp.R
import com.quangduy.chatapp.databinding.FragmentSignInBinding
import com.quangduy.chatapp.ui.base.BaseFragment
import com.quangduy.chatapp.ultils.Logger
import com.quangduy.chatapp.ultils.setOnSingClickListener
import com.quangduy.chatapp.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : BaseFragment<FragmentSignInBinding>() {
    private val viewModel by viewModels<AuthViewModel>()
    private lateinit var googleSignInClient: GoogleSignInClient
    private val GOOGLE_SIGN_IN_REQUEST = 1001
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSignInBinding {
        return FragmentSignInBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            txtSignup.setOnSingClickListener {
                findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
            }
        }
        login()
        loginWithGoogle()
        setupObservers()
    }

    private fun login() {
        binding.btnLogin.setOnSingClickListener {
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()

            if (email.isBlank() || password.isBlank()) {
                if (email.isBlank()) Toast.makeText(
                    context,
                    "Email không được bỏ trống",
                    Toast.LENGTH_SHORT
                ).show()
                if (password.isBlank()) Toast.makeText(
                    context,
                    "Password không được bỏ trống",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                viewModel.login(email, password)
            }
        }
    }

    private fun setupObservers() {
        observeResource(
            liveData = viewModel.authResult,
            onSuccess = {
                binding.progressBar.visibility = View.VISIBLE
                findNavController().navigate(R.id.action_signInFragment_to_homeFragment)
                binding.progressBar.visibility = View.GONE
            },
            onError = {
                binding.progressBar.visibility = View.VISIBLE
                Logger.logE("DEBUG Error: $it")
                Toast.makeText(requireContext(), "Lỗi: $it", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
                      },
            onLoading = { binding.progressBar.visibility = View.VISIBLE })
    }

    private fun loginWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Lấy từ google-services.json
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        binding.btnLoginGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST)
        }
    }

    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            findNavController().navigate(
                R.id.action_signInFragment_to_homeFragment,
                null,
                NavOptions.Builder().setPopUpTo(R.id.signInFragment, true).build()
            )
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN_REQUEST) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                if (idToken != null) {
                    viewModel.firebaseAuthWithGoogle(idToken)
                } else {
                    Toast.makeText(requireContext(), "ID Token is null", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                Toast.makeText(
                    requireContext(),
                    "Google sign in failed: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


}