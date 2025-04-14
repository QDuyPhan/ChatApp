package com.quangduy.chatapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.quangduy.chatapp.R
import com.quangduy.chatapp.adapter.OnChatClicked
import com.quangduy.chatapp.adapter.OnItemClickListener
import com.quangduy.chatapp.adapter.RecentChatAdapter
import com.quangduy.chatapp.adapter.UserAdapter
import com.quangduy.chatapp.data.model.RecentChats
import com.quangduy.chatapp.data.model.Users
import com.quangduy.chatapp.databinding.FragmentHomeBinding
import com.quangduy.chatapp.ui.base.BaseFragment
import com.quangduy.chatapp.ultils.Logger
import com.quangduy.chatapp.ultils.setOnSingClickListener
import com.quangduy.chatapp.viewmodel.AuthViewModel
import com.quangduy.chatapp.viewmodel.ChatViewModel
import com.quangduy.chatapp.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(), OnItemClickListener {
    private val homeViewModel by viewModels<HomeViewModel>()
    private val chatViewModel by viewModels<ChatViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()
    private lateinit var adapter: UserAdapter
    private lateinit var recentChatAdapter: RecentChatAdapter

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logout()
        setupRecyclerView()
        observeChatList()
        setUpAvatar()
    }

    private fun logout() {
        binding.logOut.setOnSingClickListener {
            authViewModel.logout()
            findNavController().navigate(
                R.id.action_homeFragment_to_signInFragment,
                null,
                NavOptions.Builder().setPopUpTo(R.id.homeFragment, true).build()
            )
        }
    }

    private fun setupRecyclerView() {
        setupUserRecyclerView()
        setupRecentChatRecyclerView()
    }

    private fun setupUserRecyclerView() {
        binding.rvUsers.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        adapter = UserAdapter().apply {
            setOnClickListener(this@HomeFragment)
            showSkeleton()
        }

        binding.rvUsers.adapter = adapter

        observeResource(
            liveData = chatViewModel.users,
            onSuccess = { adapter.setList(it) },
            onError = { Logger.logE(it) },
            onLoading = {
                Logger.logE("Loading...")
                adapter.showSkeleton()
            }
        )
    }

    private fun setupRecentChatRecyclerView() {
        recentChatAdapter = RecentChatAdapter(object : OnChatClicked {
            override fun onChatClicked(position: Int, chat: RecentChats) {
                val action = HomeFragmentDirections.actionHomeFragmentToChatFromHomeFragment(chat)
                findNavController().navigate(action)
            }
        })

        binding.rvRecentChats.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recentChatAdapter
        }
    }

    private fun observeChatList() {
        chatViewModel.chatList.observe(viewLifecycleOwner) { list ->
            recentChatAdapter.submitList(list)
        }
    }

    private fun setUpAvatar() {
        binding.apply {
            chatViewModel.imageUrl.observe(viewLifecycleOwner) {
                Glide.with(requireContext())
                    .load(it)
                    .into(tlImage)
            }
            tlImage.setOnSingClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_settingFragment)
            }
        }
    }

    override fun onUserSelected(
        position: Int,
        users: Users
    ) {
        val action = HomeFragmentDirections.actionHomeFragmentToChatFragment(users)
        findNavController().navigate(action)
    }


}