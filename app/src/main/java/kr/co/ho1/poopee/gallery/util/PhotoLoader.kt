package kr.co.ho1.poopee.gallery.util

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.provider.MediaStore
import android.widget.ImageView
import androidx.collection.LruCache
import kr.co.ho1.poopee.R
import kr.co.ho1.poopee.common.ObserverManager
import kr.co.ho1.poopee.common.util.MyUtil
import kr.co.ho1.poopee.gallery.model.FileInfo

@Suppress("DEPRECATION")
class PhotoLoader// Get max available VM memory, exceeding this amount will throw an
// OutOfMemory exception. Stored in kilobytes as LruCache takes an
// int in its constructor.

// Use 1/8th of the available memory for this memory cache.
() {
    private var loadingDrawable: Drawable // 로딩중 표현할 Drawable
    private var bitmapCache: LruCache<Int, Bitmap>? = null // 이미지캐시목록

    companion object {
        private var INSTANCE: PhotoLoader? = null
        fun getInstance() =
                INSTANCE ?: synchronized(this) {
                    INSTANCE
                            ?: PhotoLoader().also {
                        INSTANCE = it
                    }
                }
    }

    init {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 4
        bitmapCache = object : LruCache<Int, Bitmap>(cacheSize) {
            override fun sizeOf(key: Int, bitmap: Bitmap): Int {
                return bitmap.byteCount / 1024
            }
        }
        bitmapCache = LruCache(cacheSize)
        loadingDrawable = ColorDrawable(MyUtil.getColor(R.color.text_gray))
    }

    /**
     * 이미지 로딩
     * 이미지캐시목록에 이미지가 있으면 이미지캐시목록에서 반환
     * 이미지캐시목록에 이미지가 없으면 썸네일이미지Task 에서 이미지 호출
     *
     * @param imageView
     */
    fun load(fileInfo: FileInfo, imageView: ImageView) {
        if (imageView.tag != null && imageView.tag is LoadThumbTask) {
            val loadThumbTask = imageView.tag as LoadThumbTask
            if (fileInfo.id == loadThumbTask.imageId) {
                return
            } else {
                imageView.setImageDrawable(loadingDrawable)
            }
            loadThumbTask.cancel(true)
            loadThumbTask.setImageView(null)
            imageView.tag = null
        }

        val cached = bitmapCache!!.get(Integer.parseInt(fileInfo.id))
        if (cached == null) {
            val loadThumbTask = LoadThumbTask()
            loadThumbTask.setImageView(imageView)
            loadThumbTask.execute(fileInfo.id, fileInfo.path)
        } else {
            imageView.setImageBitmap(cached)
        }
    }


    /**
     * 썸네일 이미지 로딩 Task
     */
    @SuppressLint("StaticFieldLeak")
    inner class LoadThumbTask : AsyncTask<String, Void, Bitmap>() {
        var imageId: String? = null
            private set
        private var imagePath: String? = null
        private var imageView: ImageView? = null

        override fun doInBackground(vararg params: String): Bitmap? {
            imageId = params[0]
            imagePath = params[1]

            var thumbImage: Bitmap? = null
            try {
                thumbImage = MediaStore.Images.Thumbnails.getThumbnail(ObserverManager.context!!.contentResolver, Integer.parseInt(imageId!!).toLong(), MediaStore.Images.Thumbnails.MINI_KIND, null)
                thumbImage = ImageManager.rotateBitmap(thumbImage, imagePath!!)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return thumbImage
        }

        override fun onPostExecute(bitmap: Bitmap?) {
            if (bitmap != null) {
                bitmapCache!!.put(Integer.parseInt(imageId!!), bitmap)
            }
            if (imageView != null) {
                if (bitmap == null) {
                    imageView!!.setImageDrawable(loadingDrawable)
                } else {
                    imageView!!.setImageBitmap(bitmap)
                }
            }
        }

        fun setImageView(imageView: ImageView?) {
            this.imageView = imageView
            if (imageView != null) {
                imageView.tag = this
            }
        }
    }

}