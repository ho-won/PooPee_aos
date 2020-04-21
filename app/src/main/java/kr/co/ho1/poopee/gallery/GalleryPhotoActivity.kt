package kr.co.ho1.poopee.gallery

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.gallery_activity.*
import kotlinx.android.synthetic.main.gallery_item_photo.view.*
import kr.co.ho1.poopee.R
import kr.co.ho1.poopee.common.ObserverManager
import kr.co.ho1.poopee.common.base.BaseActivity
import kr.co.ho1.poopee.common.util.MyUtil
import kr.co.ho1.poopee.gallery.model.FileInfo
import kr.co.ho1.poopee.gallery.model.GalleryDir
import kr.co.ho1.poopee.gallery.util.ImageManager
import kr.co.ho1.poopee.gallery.util.PhotoLoader
import kr.co.ho1.poopee.gallery.view.DirSelectDialog
import java.io.File
import java.util.*

class GalleryPhotoActivity : BaseActivity(), DirSelectDialog.DialogListener {
    companion object {
        const val MULTI_SELECT_SIZE = "MULTI_SELECT_SIZE" // 이미지 최대 선택 가능 수
        const val PICK_FROM_CAMERA = 2001 // 카메라에서 선택
        const val PICK_FROM_ALBUM = 2002 // 엘범에서 선택
    }

    private var mDirList: ArrayList<GalleryDir>? = null // 디렉토리목록
    private var mPhotoList: ArrayList<FileInfo>? = null // 이미지목록
    private var mSelectList: ArrayList<Int>? = null // 이미지목록에서 선택한 이미지의 position 목록
    private var mMultiSelectSize = 10 // 이미지 최대 선택 가능 수

    private var mImageCaptureUri: Uri? = null // 카메라에서 찍었을 경우 uri
    private var mImageCapturePath: String? = null // 카메라에서 찍었을 경우 저장되는 path

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallery_activity)

        init()
        setListener()
    }

    private fun init() {
        recycler_view.layoutManager = GridLayoutManager(this, 4)
        recycler_view.addItemDecoration(SpacesItemDecoration(2))

        // 이미지 최대 선택 가능 수 설정
        mMultiSelectSize = intent.getIntExtra(MULTI_SELECT_SIZE, 1)

        setDir()
    }

    private fun setListener() {
        layout_category.setOnClickListener {
            // 디렉토리 선택
            val dialogFragment = DirSelectDialog(this)
            dialogFragment.setGroups(mDirList)
            dialogFragment.show(fragmentManager, "dialog")
        }
        btn_camera.setOnClickListener {
            // 카메라
            val imageFile = ImageManager.createImageFile()!!
            mImageCapturePath = imageFile.absolutePath
            mImageCaptureUri = if (android.os.Build.VERSION.SDK_INT >= 24) {
                FileProvider.getUriForFile(ObserverManager.context!!, "kr.co.ho1.poopee.fileprovider", imageFile)
            } else {
                Uri.fromFile(imageFile)
            }

            startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                    .putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri), PICK_FROM_CAMERA
            )
        }
        tv_send.setOnClickListener {
            // 이미지 선택 완료
            val selectFileList = ArrayList<FileInfo>()
            for (position in mSelectList!!) {
                selectFileList.add(mPhotoList!![position])
            }

            val intent = Intent()
            intent.putExtra("images", selectFileList)
            intent.putExtra("type", PICK_FROM_ALBUM)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    /**
     * 디렉토리 세팅
     */
    private fun setDir() {
        mDirList = ArrayList()
        mSelectList = ArrayList()

        val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA)
        val cursor = MediaStore.Images.Media.query(contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null)
        val list = HashMap<String, GalleryDir>()

        try {
            if (cursor.moveToFirst()) {
                do {
                    val file = File(cursor.getString(1)).parentFile
                    if (!list.containsKey(file.path)) {
                        // 디렉토리목록에 현재 디렉토리가 없으면 디렉토리 목록에 추가
                        list[file.path] = GalleryDir(file, file.name, 1)
                    } else {
                        // 디렉토리목록에 현재 디렉토리가 있으면 이미지 카운트 변경
                        list[file.path] = GalleryDir(file, file.name, list[file.path]!!.count + 1)
                    }
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {

        }

        mDirList!!.add(GalleryDir(null, MyUtil.getString(R.string.gallery_show_all), cursor.count)) // 디렉토리 목록에 전체보기 추가
        mDirList!!.addAll(list.values) // 디렉토리 목록 추가
        cursor.close()

        setPhoto(0) // 처음은 전체보기로 세팅
    }

    /**
     * 이미지목록 세팅
     */
    private fun setPhoto(position: Int) {
        tv_count.text = ""
        layout_bottom.visibility = View.GONE
        mPhotoList = ArrayList()
        mSelectList = ArrayList()
        val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE)
        val cursor = MediaStore.Images.Media.query(contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, String.format("%s DESC", MediaStore.Images.Media._ID))

        if (cursor.moveToFirst()) {
            do {
                val file = File(cursor.getString(1)).parentFile
                if (position == 0) {
                    // 전체보기일 경우 이미지목록에 모든 이미지 파일 추가
                    val thumbnailInfo = FileInfo(cursor.getString(0), cursor.getString(1), cursor.getString(2))
                    mPhotoList!!.add(thumbnailInfo)
                } else {
                    // 선택한폴더일 경우 이미지목록에 해당폴더의 이미지 파일만 추가
                    if (file.path == mDirList!![position].file!!.path) {
                        val thumbnailInfo = FileInfo(cursor.getString(0), cursor.getString(1), cursor.getString(2))
                        mPhotoList!!.add(thumbnailInfo)
                    }
                }
            } while (cursor.moveToNext())
        }
        cursor.close()

        tv_category.text = mDirList!![position].name

        val photoAdapter = PhotoAdapter()
        recycler_view.adapter = photoAdapter
    }

    /**
     * 이미지목록에 이미지간의 간격 지정
     */
    inner class SpacesItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            outRect.left = space
            outRect.right = space
            outRect.bottom = space
            outRect.top = space
        }
    }

    /**
     * 이미지목록 adapter
     */
    inner class PhotoAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.gallery_item_photo, parent, false))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as ViewHolder).update(position)
        }

        override fun getItemCount(): Int {
            return mPhotoList!!.size
        }

        override fun getItemViewType(position: Int): Int {
            return 0
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            fun update(position: Int) {
                PhotoLoader.getInstance().load(mPhotoList!![position], itemView.iv_image)

                if (mSelectList!!.contains(position)) {
                    // 선택목록에 있는 이미지면 선택한상태로 뷰 표현
                    itemView.tv_index.text = (mSelectList!!.indexOf(position) + 1).toString()
                    itemView.layout_select.visibility = View.VISIBLE
                } else {
                    // 선택목록에 없는 이미지면 선택 안한상태로 뷰 표현
                    itemView.layout_select.visibility = View.GONE
                }

                itemView.setOnClickListener {
                    if (mMultiSelectSize > 1) {
                        selectMulti(position)
                    } else {
                        selectSingle(position)
                    }
                }
            }
        }

        /**
         * 멀티 선택일 경우 뷰 설정
         */
        private fun selectMulti(position: Int) {
            if (mSelectList!!.contains(position)) {
                val index = mSelectList!!.indexOf(position)
                mSelectList!!.removeAt(index)
                notifyItemChanged(position)
            } else {
                if (mSelectList!!.size < mMultiSelectSize) {
                    mSelectList!!.add(position)
                }
            }

            for (p in mSelectList!!) {
                notifyItemChanged(p)
            }

            if (mSelectList!!.size > 0) {
                tv_count.text = (mSelectList!!.size.toString() + "")
                layout_bottom.visibility = View.VISIBLE
            } else {
                tv_count.text = ""
                layout_bottom.visibility = View.GONE
            }
        }

        /**
         * 싱글선택일 경우 뷰 설정
         */
        private fun selectSingle(position: Int) {
            mSelectList = ArrayList()
            mSelectList!!.add(position)

            notifyDataSetChanged()

            if (mSelectList!!.size > 0) {
                tv_count.text = (mSelectList!!.size.toString() + "")
                layout_bottom.visibility = View.VISIBLE
            } else {
                tv_count.text = ""
                layout_bottom.visibility = View.GONE
            }
        }
    }

    override fun onSelectDir(position: Int) {
        setPhoto(position)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            PICK_FROM_CAMERA // 카메라 선택일 경우
            -> {
                val selectFileList = ArrayList<FileInfo>()
                val fileInfo = FileInfo("", mImageCapturePath!!, "")
                selectFileList.add(fileInfo)

                val intent = Intent()
                intent.putExtra("images", selectFileList)
                intent.putExtra("type", PICK_FROM_CAMERA)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

}
