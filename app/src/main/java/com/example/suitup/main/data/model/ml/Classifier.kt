package com.example.suitup.main.data.model.ml

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.imgproc.Imgproc
import org.opencv.imgproc.Imgproc.contourArea
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

    private fun predictCategory(bitmap: Bitmap): String {
        OpenCVLoader.initDebug()
        val byteBuffer = convertBitmapToByteBuffer(scaleImage(bitmap))
        byteBuffer.rewind()
        val inputFeature0 =
            TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(byteBuffer)

        val outputs = modelCategory.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        val str = context.assets.open("labels_type.txt").bufferedReader().use { it.readText() }
        val labels = str.split("\r\n")

        modelCategory.close()

        return labels[outputFeature0.floatArray.indices.maxBy { indices -> outputFeature0.floatArray[indices] }]
    }

    private fun predictColor(bitmap: Bitmap): List<String> {
        val byteBuffer = convertBitmapToByteBuffer(scaleImage(cutImage(bitmap)))
        byteBuffer.rewind()
        val inputFeature0 =
            TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(byteBuffer)

        val outputs = modelColor.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        val str = context.assets.open("labels_color.txt").bufferedReader().use { it.readText() }
        val labels = str.split("\r\n")
        modelColor.close()
        return getFirstThreePredictions(outputFeature0.floatArray).map { labels[it.key] }.toList()
    }

    private fun cutImage(bitmap: Bitmap): Bitmap {
        val original = Mat()
        val bmp32: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        Utils.bitmapToMat(bmp32, original)

        val gray = Mat()
        val mask = Mat()

        val contours: MutableList<MatOfPoint> = ArrayList()

        Imgproc.cvtColor(original, gray, Imgproc.COLOR_RGB2GRAY)
        Imgproc.threshold(gray, mask, 200.0, 255.0, Imgproc.THRESH_BINARY_INV)
        Imgproc.findContours(
            mask,
            contours,
            Mat(),
            Imgproc.RETR_EXTERNAL,
            Imgproc.CHAIN_APPROX_SIMPLE
        )
        val biggestPolygonIndex = getBiggestPolygonIndex(contours)
        val rect = Imgproc.boundingRect(contours[biggestPolygonIndex ?: 0])
        val imgCropped = original.submat(rect)

        val bmp = Bitmap.createBitmap(imgCropped.cols(), imgCropped.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(imgCropped, bmp)
        return bmp
    }

    private fun getBiggestPolygonIndex(contours: List<MatOfPoint>): Int? {
        var maxVal = 0.0
        var maxValIdx: Int? = null
        for (contourIdx in contours.indices) {
            val contourArea: Double = contourArea(contours[contourIdx])
            if (maxVal < contourArea) {
                maxVal = contourArea
                maxValIdx = contourIdx
            }
        }
        return maxValIdx
    }

    suspend fun predictCombine(bitmap: Bitmap): Pair<Any, Any> =
        coroutineScope {
            val category = async { predictCategory(bitmap) }
            val colors = async { predictColor(bitmap) }

            val (resCategory, resColor) = awaitAll(category, colors)

            return@coroutineScope Pair(resCategory, resColor)
        }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocate(4 * inputSize * inputSize * pixelSize)
        byteBuffer.order(ByteOrder.nativeOrder())

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

    private fun getFirstThreePredictions(array: FloatArray): Map<Int, Float> {
        val tempArray = array.copyOf()
        tempArray.sortDescending()
        return mapOf(
            array.indexOfFirst { it == tempArray[0] } to tempArray[0],
            array.indexOfFirst { it == tempArray[1] } to tempArray[1],
            array.indexOfFirst { it == tempArray[2] } to tempArray[2],
        )
    }
}