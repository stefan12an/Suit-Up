package com.example.suitup.main.ui.photosearch

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.suitup.common.EventObserver
import com.example.suitup.main.data.model.ml.Classifier
import dagger.hilt.android.AndroidEntryPoint
import suitup.R
import suitup.databinding.FragmentPhotoSearchBinding


const val GALLERY_REQUEST_CODE = 1

@AndroidEntryPoint
class PhotoSearchFragment : Fragment() {
    private lateinit var binding: FragmentPhotoSearchBinding
    private val mInputSize = 224
    private val viewModel: PhotoSearchViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotoSearchBinding.inflate(inflater, container, false)
        val sideEffectsObserver = EventObserver<PhotoSearchSideEffects> {
            handleSideEffect(it)
        }
        val photoSearchUiStateObserver = Observer<PhotoSearchUiState> {
            if (!it.loading) {
                binding.prediction.text = it.prediction
                binding.searchPredictionResult.visibility = View.VISIBLE
            }
        }

        viewModel.sideEffect.observe(viewLifecycleOwner, sideEffectsObserver)
        viewModel.photoSearchUiState.observe(viewLifecycleOwner, photoSearchUiStateObserver)

        binding.takeImage.setOnClickListener { openCamera() }
        binding.loadImage.setOnClickListener { openGallery() }
        binding.searchPredictionResult.setOnClickListener {
            viewModel.action(
                PhotoSearchIntent.GetSearchResult(
                    binding.prediction.text
                )
            )
        }
        return binding.root
    }

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            takePicturePreview.launch(null)
        } else {
            requestPermission.launch(android.Manifest.permission.CAMERA)
        }
    }

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                takePicturePreview.launch(null)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Permission Denied !! Try again",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    private val takePicturePreview =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                binding.desiredImage.setImageBitmap(bitmap)

            }
        }

    private fun openGallery() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            val mimeType = arrayOf("image/jpeg", "image/png", "image/jpg")
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType)
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            onResult.launch(intent)
        } else {
            requestPermission.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private val onResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.e(TAG, "This is the result: ${result.data}, ${result.resultCode}")
            onResultRecieved(GALLERY_REQUEST_CODE, result)
        }

    private fun onResultRecieved(requestCode: Int, result: ActivityResult?) {
        when (requestCode) {
            GALLERY_REQUEST_CODE -> {
                if (result?.resultCode == Activity.RESULT_OK) {
                    result.data?.data?.let { uri ->
                        Log.e(TAG, "onResultRecieved: $uri")
                        val bitmap =
                            BitmapFactory.decodeStream(context?.contentResolver?.openInputStream(uri))
                        binding.desiredImage.setImageBitmap(bitmap)
                        val classifier = Classifier(requireContext(), mInputSize, false)
                        viewModel.action(PhotoSearchIntent.Predict(bitmap, classifier))
                    }
                }
            }
        }
    }

    private fun handleSideEffect(sideEffect: PhotoSearchSideEffects) {
        when (sideEffect) {
            is PhotoSearchSideEffects.NavigateToSeeAll -> findNavController().navigate(sideEffect.directions)
            is PhotoSearchSideEffects.NavigateToRequest -> findNavController().navigate(R.id.action_homeFragment_to_requestAccesFragment)
            is PhotoSearchSideEffects.Feedback ->
                Toast.makeText(requireContext(), sideEffect.msg, Toast.LENGTH_SHORT).show()
        }
    }


}