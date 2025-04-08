package com.quangduy.chatapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.quangduy.chatapp.databinding.FragmentChatFromHomeBinding
import com.quangduy.chatapp.ui.base.BaseFragment
import com.quangduy.chatapp.viewmodel.ChatFromHomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFromHomeFragment : BaseFragment<FragmentChatFromHomeBinding>() {

    private val viewModel by viewModels<ChatFromHomeViewModel>()
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentChatFromHomeBinding {
        return FragmentChatFromHomeBinding.inflate(layoutInflater)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}