package com.quangduy.chatapp.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.viewbinding.ViewBinding
import com.quangduy.chatapp.ultils.Resource
import com.quangduy.chatapp.ultils.Status


abstract class BaseFragment<VB : ViewBinding> : Fragment() {
    private var _binding: VB? = null
    protected val binding get() = _binding!!

    abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflateBinding(inflater, container)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected fun showLoading() {
        // show progress bar
    }

    protected fun hideLoading() {
        // hide progress bar
    }

    protected inline fun <T> observeResource(
        liveData: LiveData<Resource<T>>,
        crossinline onSuccess: (T) -> Unit,
        crossinline onError: (String) -> Unit = {},
        crossinline onLoading: () -> Unit = {}
    ) {
        liveData.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.SUCCESS -> resource.data?.let { onSuccess(it) }
                Status.ERROR -> onError(resource.message ?: "Unknown error")
                Status.LOADING -> onLoading()
            }
        }
    }

}