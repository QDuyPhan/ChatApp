package com.quangduy.chatapp.ui.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.quangduy.chatapp.BuildConfig
import com.quangduy.chatapp.R
import com.quangduy.chatapp.databinding.FragmentSettingBinding
import com.quangduy.chatapp.ui.base.BaseFragment
import com.quangduy.chatapp.ultils.Logger
import com.quangduy.chatapp.ultils.setOnSingClickListener
import com.quangduy.chatapp.viewmodel.ChatViewModel
import com.quangduy.chatapp.viewmodel.SettingViewModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException

@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentSettingBinding>() {
    private val viewModel by viewModels<SettingViewModel>()
    private val chatViewModel by viewModels<ChatViewModel>()
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingBinding {
        return FragmentSettingBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObserver()
        setupUI()
        updateProfile()
    }

    private fun setupObserver() {
        chatViewModel.imageUrl.observe(viewLifecycleOwner) {
            loadImageAvatar(it)
        }
    }

    private fun loadImageAvatar(image: String) {
        Glide.with(requireContext()).load(image).placeholder(R.drawable.person).dontAnimate()
            .into(binding.settingUpdateImage)
    }

    private fun updateProfile() {
        binding.settingUpdateButton.setOnSingClickListener {
            Logger.logI("SettingFragment Clicked update profile 1")
            chatViewModel.updateProfile()
            Logger.logI("SettingFragment Clicked update profile 2")
        }
    }

    private fun setupUI() {
        binding.apply {
            settingBackBtn.setOnSingClickListener {
                findNavController().popBackStack()
            }
            settingUpdateImage.setOnSingClickListener {
                val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
                AlertDialog.Builder(requireContext())
                    .setTitle("Choose your profile picture")
                    .setItems(options) { dialog, item ->
                        when (item) {
                            0 -> takePhotoWithCamera()
                            1 -> pickImageFromGallery()
                            2 -> dialog.dismiss()
                        }
                    }
                    .show()
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun takePhotoWithCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePhotoLauncher.launch(intent)
    }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data ?: return@registerForActivityResult
                val bitmap =
                    MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
                uploadImageToCloudinary(bitmap)
            }
        }

    private val takePhotoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val bitmap = result.data?.extras?.get("data") as? Bitmap
                bitmap?.let { uploadImageToCloudinary(it) }
            }
        }

    private fun uploadImageToCloudinary(bitmap: Bitmap) {
        binding.settingUpdateImage.setImageBitmap(bitmap)

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes = baos.toByteArray()

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file", "image.jpg",
                imageBytes.toRequestBody("image/*".toMediaTypeOrNull())
            )
            .addFormDataPart(
                "upload_preset",
                "my_unsigned_preset"
            ) // replace with your actual preset
            .build()

        val request = Request.Builder()
            .url("https://api.cloudinary.com/v1_1/${BuildConfig.YOUR_CLOUD_NAME}/image/upload")
            .post(requestBody)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Upload failed!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val imageUrl = JSONObject(response.body?.string() ?: "").getString("secure_url")
                    chatViewModel.imageUrl.postValue(imageUrl)

                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Image uploaded!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        })
    }

}