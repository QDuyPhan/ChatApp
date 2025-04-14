package com.quangduy.chatapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.quangduy.chatapp.R
import com.quangduy.chatapp.adapter.MessageAdapter
import com.quangduy.chatapp.databinding.FragmentChatFromHomeBinding
import com.quangduy.chatapp.ui.base.BaseFragment
import com.quangduy.chatapp.ultils.Logger
import com.quangduy.chatapp.ultils.setOnSingClickListener
import com.quangduy.chatapp.viewmodel.ChatFromHomeViewModel
import com.quangduy.chatapp.viewmodel.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFromHomeFragment : BaseFragment<FragmentChatFromHomeBinding>() {
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var args: ChatFromHomeFragmentArgs
    private val viewModel by viewModels<ChatFromHomeViewModel>()
    private val chatViewModel by viewModels<ChatViewModel>()
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentChatFromHomeBinding {
        return FragmentChatFromHomeBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args = ChatFromHomeFragmentArgs.fromBundle(requireArguments())
        Logger.logI("ChatFromHomeFragment: ${args}")
        setupUI()
        sendMessage()
        getMessage()
        setupRecyclerView()

    }

    private fun setupUI() {
        binding.chatBackBtn.setOnSingClickListener {
            findNavController().navigate(R.id.action_chatFromHomeFragment_to_homeFragment)
        }
        Glide.with(requireContext()).load(args.recentschat.friendsImage)
            .into(binding.chatImageViewUser)
        chatViewModel.getStatus(args.recentschat.friendId!!)
        chatViewModel.status.observe(viewLifecycleOwner) {
            binding.chatUserStatus.text = it
        }
        binding.chatUserName.text = args.recentschat.name
    }

    private fun sendMessage() {
        binding.editTextMessage.doAfterTextChanged { text ->
            chatViewModel.setMessage(text.toString())
        }
        binding.sendBtn.setOnSingClickListener {
            chatViewModel.sendMessage(
                args.recentschat.friendId!!,
                args.recentschat.name!!,
                args.recentschat.friendsImage!!
            )
            binding.editTextMessage.setText("")
        }

    }

    private fun getMessage() {
        chatViewModel.getMessages(args.recentschat.friendId!!).observe(viewLifecycleOwner) {
            Logger.logI("ChatFragment: ${it}")
            messageAdapter.setList(it)

        }
    }

    private fun setupRecyclerView() {
        messageAdapter = MessageAdapter()
        binding.messagesRecyclerView.layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true
        }
        binding.messagesRecyclerView.adapter = messageAdapter

    }
}