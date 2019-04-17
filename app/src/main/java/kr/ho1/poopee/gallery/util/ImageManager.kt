package kr.ho1.poopee.gallery.util

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Environment
import kr.ho1.poopee.common.ObserverManager
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object ImageManager {

    fun getImageFile(imagePath: String, position: Int): File {
        var file = File(imagePath)

        var bitmap = resizeDecodeFile(file, 1024, 1024)
        bitmap = rotateBitmap(bitmap!!, imagePath)

        try {
            file = File.createTempFile("tmp$position", ".jpg", ObserverManager.context!!.cacheDir)
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return file
    }

    fun resizeBitmapImage(source: Bitmap, maxResolution: Int): Bitmap {
        try {
            val width = source.width
            val height = source.height
            var newWidth = width
            var newHeight = height
            val rate: Float

            if (width > height) {
                if (maxResolution < width) {
                    rate = maxResolution / width.toFloat()
                    newHeight = (height * rate).toInt()
                    newWidth = maxResolution
                }
            } else {
                if (maxResolution < height) {
                    rate = maxResolution / height.toFloat()
                    newWidth = (width * rate).toInt()
                    newHeight = maxResolution
                }
            }

            return Bitmap.createScaledBitmap(source, newWidth, newHeight, true)
        } catch (e: Exception) {
            return source
        }

    }

    fun resizeDecodeFile(f: File, width: Int, height: Int): Bitmap? {
        try {
            //Decode image size
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            BitmapFactory.decodeStream(FileInputStream(f), null, o)

            //Find the correct scale value. It should be the power of 2.
            var scale = 1
            while (o.outWidth / scale / 2 >= width && o.outHeight / scale / 2 >= height)
                scale *= 2

            //Decode with inSampleSize
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            return BitmapFactory.decodeStream(FileInputStream(f), null, o2)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    @SuppressLint("SimpleDateFormat")
    fun createImageFile(): File? {
        var image: File? = null
        try {
            // Create an image file name
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val imageFileName = "JPEG_" + timeStamp + "_"
            val storageDir = ObserverManager.context!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            image = File.createTempFile(imageFileName, ".jpg", storageDir)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return image
    }

    /**
     * 이미지 뱡향이 잘못되있을 경우 정상으로 표현하기 위한 함수
     */
    fun rotateBitmap(bitmap: Bitmap, filePath: String): Bitmap {
        var lBitmap = bitmap
        try {
            val imageExif = ExifInterface(filePath)
            val orientation = imageExif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
            var degrees = 0

            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degrees = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degrees = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degrees = 270
            }

            return if (degrees == 0) {
                lBitmap
            } else {
                val matrix = Matrix()
                matrix.postRotate(degrees.toFloat())
                lBitmap = Bitmap.createBitmap(lBitmap, 0, 0, lBitmap.width, lBitmap.height, matrix, true)
                val bmpStream = ByteArrayOutputStream()
                lBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bmpStream)
                lBitmap
            }
        } catch (e: Exception) {
            return lBitmap
        }

    }

}