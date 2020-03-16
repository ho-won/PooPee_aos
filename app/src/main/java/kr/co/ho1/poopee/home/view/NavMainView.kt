package kr.co.ho1.poopee.home.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Toast
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.nav_main.view.*
import kr.co.ho1.poopee.R
import kr.co.ho1.poopee.common.ObserverManager
import kr.co.ho1.poopee.common.data.SharedManager
import kr.co.ho1.poopee.common.util.EmailSender
import kr.co.ho1.poopee.common.util.MySpannableString
import kr.co.ho1.poopee.common.util.MyUtil
import kr.co.ho1.poopee.login.LoginActivity
import kr.co.ho1.poopee.menu.MyInfoActivity
import kr.co.ho1.poopee.menu.NoticeActivity
import kr.co.ho1.poopee.menu.SettingActivity

@Suppress("DEPRECATION")
class NavMainView : NavigationView {
    private var mContext: Context? = null

    constructor(context: Context) : super(context, null) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        this.mContext = context
        LayoutInflater.from(context).inflate(R.layout.nav_main, this, true)

        refresh()
        setListener()
    }

    fun refresh() {
        if (SharedManager.isLoginCheck()) {
            tv_name.text = SharedManager.getMemberName()
            iv_login.setImageDrawable(ObserverManager.context!!.resources.getDrawable(R.drawable.img_profile))
            btn_logout.setImageDrawable(ObserverManager.context!!.resources.getDrawable(R.drawable.ic_logout))
        } else {
            val lineString: Array<String> = arrayOf(ObserverManager.context!!.resources.getString(R.string.nav_text_06))
            val span = MySpannableString(ObserverManager.context!!.resources.getString(R.string.nav_text_06))
            span.setLine(lineString)
            tv_name.text = span.getSpannableString()
            iv_login.setImageDrawable(ObserverManager.context!!.resources.getDrawable(R.drawable.img_logingo))
            btn_logout.setImageDrawable(ObserverManager.context!!.resources.getDrawable(R.drawable.img_menu_bottom))
        }
    }

    private fun setListener() {
        layout_login.setOnClickListener {
            if (SharedManager.getMemberUsername() == "master") {
                try {
                    val clipboard = ObserverManager.context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
                    val clip = ClipData.newPlainText("PooPee", MyUtil.getHashKey())
                    clipboard!!.setPrimaryClip(clip)
                    Toast.makeText(ObserverManager.context!!, ObserverManager.context!!.resources.getString(R.string.toast_copy_complete), Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            if (!SharedManager.isLoginCheck()) {
                ObserverManager.root!!.startActivity(Intent(ObserverManager.context!!, LoginActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                )
            }
        }
        layout_my_info.setOnClickListener {
            if (SharedManager.isLoginCheck()) {
                ObserverManager.root!!.startActivity(Intent(ObserverManager.context!!, MyInfoActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                )
            } else {
                ObserverManager.root!!.startActivity(Intent(ObserverManager.context!!, LoginActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                )
            }
        }
        layout_notice.setOnClickListener {
            ObserverManager.root!!.startActivity(Intent(ObserverManager.context!!, NoticeActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
            )
        }
        layout_request.setOnClickListener {
            EmailSender.send("ho1.poopee@gmail.com", ObserverManager.context!!.getString(R.string.nav_text_05))
        }
        layout_setting.setOnClickListener {
            ObserverManager.root!!.startActivity(Intent(ObserverManager.context!!, SettingActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
            )
        }
        btn_logout.setOnClickListener {
            if (SharedManager.isLoginCheck()) {
                ObserverManager.logout()
                refresh()
            }
        }
    }

}