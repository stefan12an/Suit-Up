package com.example.suitup.main.ui.photosearch

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
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
import androidx.navigation.fragment.findNavController
import com.example.suitup.common.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import suitup.R
import suitup.databinding.FragmentPhotoSearchBinding
import suitup.ml.ModelGpt
import java.nio.ByteBuffer
import java.nio.ByteOrder


const val GALLERY_REQUEST_CODE = 1

@AndroidEntryPoint
class PhotoSearchFragment : Fragment() {
    private lateinit var binding: FragmentPhotoSearchBinding
    private val mInputSize = 128
    private val mPixelSize = 3
    private val mImageMean = 0
    private val mImageStd = 255
    private val mModelPath = "model_kaggle.tflite"
    private val mLabelPath = "labels_gender.txt"
    private val viewModel: PhotoSearchViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotoSearchBinding.inflate(inflater, container, false)
        val sideEffectsObserver = EventObserver<PhotoSearchSideEffects> {
            handleSideEffect(it)
        }
        viewModel.sideEffect.observe(viewLifecycleOwner, sideEffectsObserver)
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
                        binding.prediction.text = predict(bitmap)
                        binding.searchPredictionResult.visibility = View.VISIBLE
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

    private fun predict(bitmap: Bitmap): String{
        val model = ModelGpt.newInstance(requireContext())
        val byteBuffer = convertBitmapToByteBuffer(scaleImage(bitmap))
        byteBuffer.rewind()
        // Creates inputs for reference.
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 128, 128, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(byteBuffer)

        // Runs model inference and gets result.
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        val str = requireContext().assets.open("labels_master.txt").bufferedReader().use { it.readText() }
        val labels = str.split("\r\n")
        // Releases model resources if no longer used.
        model.close()

        return labels[outputFeature0.floatArray.indices.maxBy { outputFeature0.floatArray[it] }]
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {

        // Preallocate memory for bytebuffer
        val byteBuffer = ByteBuffer.allocate(4 * mInputSize * mInputSize * mPixelSize)
        byteBuffer.order(ByteOrder.nativeOrder())

        // Initialize pixel data array and populate from bitmap
        val intArray = IntArray(mInputSize * mInputSize)
        bitmap.getPixels(
            intArray, 0, bitmap.width, 0, 0,
            bitmap.width, bitmap.height
        )
        var pixel = 0 // pixel indexer
        for (i in 0 until mInputSize) {
            for (j in 0 until mInputSize) {
                val input = intArray[pixel++]
                byteBuffer.putFloat((((input shr 16 and 0x000000FF) - mImageMean) / mImageStd).toFloat())
                byteBuffer.putFloat((((input shr 8 and 0x000000FF) - mImageMean) / mImageStd).toFloat())
                byteBuffer.putFloat((((input and 0x000000FF) - mImageMean) / mImageStd).toFloat())
            }
        }
        return byteBuffer
    }

    private fun scaleImage(bitmap: Bitmap): Bitmap {
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height

        val scaleWidth = mInputSize.toFloat() / originalWidth
        val scaleHeight = mInputSize.toFloat() / originalHeight

        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)

        return Bitmap.createBitmap(bitmap, 0, 0, originalWidth, originalHeight, matrix, true)
    }

}