package kr.ho1.poopee.gallery.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import kr.ho1.poopee.R
import kr.ho1.poopee.common.ObserverManager
import kr.ho1.poopee.gallery.model.GalleryDir
import kotlinx.android.synthetic.main.gallery_dialog_dir_select.*
import kotlinx.android.synthetic.main.gallery_item_dir_select.view.*
import java.util.*

@SuppressLint("ValidFragment")
class DirSelectDialog(private val listener: DialogListener) : DialogFragment() {

    private var mDirList = ArrayList<GalleryDir>() // 디렉토리 목록

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MyDialog)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) // 상태바 알파 백그라운드
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.gallery_dialog_dir_select, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_title.text = ObserverManager.context!!.resources.getString(R.string.gallery_select_dir)

        recycler_view.layoutManager = LinearLayoutManager(activity)
        val dirListAdapter = DirListAdapter()
        recycler_view.adapter = dirListAdapter

        val height: Int = if (mDirList.size > 4) {
            // 사이즈가 4보다 크면 최대 높이값 지정
            ObserverManager.context!!.resources.getDimensionPixelOffset(R.dimen.dialog_max_height)
        } else {
            // 사이즈가 4보다 작거나 같으면 사이즈에 따른 높이값 설정
            ObserverManager.context!!.resources.getDimensionPixelOffset(R.dimen.dialog_content_height) * mDirList.size
        }

        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height)
        recycler_view.layoutParams = layoutParams

        if (isCancelable) {
            view!!.setOnClickListener { dismiss() }
        }
    }

    /**
     * 디렉토리목록 세팅
     */
    fun setGroups(galleryDirs: ArrayList<GalleryDir>?) {
        mDirList = galleryDirs!!
    }

    /**
     * 디렉토리목록 adapter.
     */
    inner class DirListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.gallery_item_dir_select, parent, false))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as ViewHolder).update(position)
        }

        override fun getItemCount(): Int {
            return mDirList.size
        }

        override fun getItemViewType(position: Int): Int {
            return 0
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            fun update(position: Int) {
                itemView.tv_name.text = (mDirList[position].name + "  (" + mDirList[position].count + ")")
                setListener(position)
            }

            private fun setListener(position: Int) {
                itemView.setOnClickListener {
                    listener.onSelectDir(position)
                    dismiss()
                }
            }
        }
    }

    /**
     * 디렉토리선택 콜백
     */
    interface DialogListener {
        fun onSelectDir(position: Int)
    }

}