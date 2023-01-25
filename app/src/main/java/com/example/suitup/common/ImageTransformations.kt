package com.example.suitup.common

import android.graphics.*
import dagger.hilt.internal.Preconditions.checkArgument
import org.tensorflow.lite.support.image.ColorSpaceType
import org.tensorflow.lite.support.image.ImageOperator
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

fun Bitmap.toGrayscale(): Bitmap {
    val height: Int = this.height
    val width: Int = this.width
    val bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val c = Canvas(bmpGrayscale)
    val paint = Paint()
    val cm = ColorMatrix()
    cm.setSaturation(0f)
    val f = ColorMatrixColorFilter(cm)
    paint.colorFilter = f
    c.drawBitmap(this, 0f, 0f, paint)
    return bmpGrayscale
}