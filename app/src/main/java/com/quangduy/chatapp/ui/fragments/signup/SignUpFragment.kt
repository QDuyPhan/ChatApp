package com.quangduy.chatapp.ui.fragments.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.quangduy.chatapp.R
import com.quangduy.chatapp.databinding.FragmentSignUpBinding
import com.quangduy.chatapp.ui.base.BaseFragment
import com.quangduy.chatapp.ultils.Logger
import com.quangduy.chatapp.ultils.setOnSingClickListener
import com.quangduy.chatapp.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignUpBinding>() {
    private val viewModel by viewModels<AuthViewModel>()
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSignUpBinding {
        return FragmentSignUpBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            txtSignin.setOnSingClickListener {
                findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
            }
            btnBack.setOnSingClickListener {
                findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
            }
        }
        setupObservers()
        signup()
    }

    private fun signup() {
        binding.btnRegister.setOnSingClickListener {
            val name = binding.edtFullName.text.toString()
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()

            Logger.logI("DEBUG Clicked register with $email")

            if (name.isBlank() || email.isBlank() || password.isBlank()) {
                if (name.isBlank()) Toast.makeText(
                    context,
                    "Tên không được bỏ trống",
                    Toast.LENGTH_SHORT
                ).show()
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
                viewModel.signUp(name, email, password)
            }
        }
    }

    private fun setupObservers() {
        observeResource(
            liveData = viewModel.authResult,
            onSuccess = {
                binding.progressBar.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            },
            onError = {
                binding.progressBar.visibility = View.VISIBLE
                Logger.logE("DEBUG Error: $it")
                Toast.makeText(requireContext(), "Lỗi: $it", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            },
            onLoading = {
                binding.progressBar.visibility = View.VISIBLE
            })
    }

}