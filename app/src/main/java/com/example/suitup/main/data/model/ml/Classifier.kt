package com.example.suitup.main.data.model.ml

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import suitup.ml.ModelColor
import suitup.ml.ModelType
import java.nio.ByteBuffer
import java.nio.ByteOrder

class Classifier(private val context: Context, private val inputSize: Int, isGray: Boolean) {

    private val pixelSize = if (isGray) 1 else 3
    private val modelCategory = ModelType.newInstance(context)
    private val modelColor = ModelColor.newInstance(context)

    fun predictCategory(bitmap: Bitmap): String {
        val byteBuffer = convertBitmapToByteBuffer(scaleImage(bitmap))
        byteBuffer.rewind()
        // Creates inputs for reference.
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(byteBuffer)

        // Runs model inference and gets result.
        val outputs = modelCategory.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        val str = context.assets.open("labels_type.txt").bufferedReader().use { it.readText() }
        val labels = str.split("\r\n")
        Log.e(TAG, "predictCategory: $labels")
        // Releases model resources if no longer used.
        modelCategory.close()

        return labels[outputFeature0.floatArray.indices.maxBy { indices -> outputFeature0.floatArray[indices] }]
    }

   fun predictColor(bitmap: Bitmap): String {
        val byteBuffer = convertBitmapToByteBuffer(scaleImage(bitmap))
        byteBuffer.rewind()
        // Creates inputs for reference.
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(byteBuffer)

        // Runs model inference and gets result.
        val outputs = modelColor.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        val str = context.assets.open("labels_color.txt").bufferedReader().use { it.readText() }
        val labels = str.split("\r\n")
        // Releases model resources if no longer used.
        modelColor.close()
       outputFeature0.floatArray.indices.forEach { Log.e(TAG, "predictColor: $it ${outputFeature0.floatArray[it]}", ) }
        return labels[outputFeature0.floatArray.indices.maxBy { outputFeature0.floatArray[it] }]
    }

    suspend fun predictCombine(bitmap: Bitmap): String =
        coroutineScope {
            val category = async { predictCategory(bitmap) }
            val color = async { predictColor(bitmap) }

            val (resCategory, resColor) = awaitAll(category,color)

            return@coroutineScope "$resColor $resCategory"
        }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {

        // Preallocate memory for bytebuffer
        val byteBuffer = ByteBuffer.allocate(4 * inputSize * inputSize * pixelSize)
        byteBuffer.order(ByteOrder.nativeOrder())

        // Initialize pixel data array and populate from bitmap
        val intArray = IntArray(inputSize * inputSize)
        bitmap.getPixels(
            intArray, 0, bitmap.width, 0, 0,
            bitmap.width, bitmap.height
        )
        var pixel = 0 // pixel indexer
        for (i in 0 until inputSize) {
            for (j in 0 until inputSize) {
                val input = intArray[pixel++]
                byteBuffer.putFloat((((input shr 16 and 0x000000FF))).toFloat())
                byteBuffer.putFloat((((input shr 8 and 0x000000FF))).toFloat())
                byteBuffer.putFloat((((input and 0x000000FF))).toFloat())
            }
        }
        return byteBuffer
    }

    private fun scaleImage(bitmap: Bitmap): Bitmap {
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height

        val scaleWidth = inputSize.toFloat() / originalWidth
        val scaleHeight = inputSize.toFloat() / originalHeight

        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)

        return Bitmap.createBitmap(bitmap, 0, 0, originalWidth, originalHeight, matrix, true)
    }
}