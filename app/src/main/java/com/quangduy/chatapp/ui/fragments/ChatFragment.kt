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
import com.quangduy.chatapp.databinding.FragmentChatBinding
import com.quangduy.chatapp.ui.base.BaseFragment
import com.quangduy.chatapp.ultils.Logger
import com.quangduy.chatapp.ultils.setOnSingClickListener
import com.quangduy.chatapp.viewmodel.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : BaseFragment<FragmentChatBinding>() {
    private val chatViewModel by viewModels<ChatViewModel>()
    private lateinit var args: ChatFragmentArgs
    private lateinit var messageAdapter: MessageAdapter
    override fun inflateBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentChatBinding {
        return FragmentChatBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args = ChatFragmentArgs.fromBundle(requireArguments())
        Logger.logI("ChatFragment: ${args}")
        setupUI()
        sendMessage()
        getMessage()
        setupRecyclerView()
    }

    private fun setupUI() {
        binding.chatBackBtn.setOnSingClickListener {
            findNavController().navigate(R.id.action_chatFragment_to_homeFragment)
        }
        Glide.with(requireContext()).load(args.users.imageUrl).into(binding.chatImageViewUser)
        binding.chatUserStatus.text = args.users.status
        binding.chatUserName.text = args.users.username
    }

    private fun sendMessage() {
        binding.editTextMessage.doAfterTextChanged { text ->
            chatViewModel.setMessage(text.toString())
        }
        binding.sendBtn.setOnSingClickListener {
            chatViewModel.sendMessage(
                args.users.userid!!, args.users.username!!, args.users.imageUrl!!
            )
            binding.editTextMessage.setText("")
        }

    }

    private fun getMessage() {
        chatViewModel.getMessages(args.users.userid!!).observe(viewLifecycleOwner) {
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