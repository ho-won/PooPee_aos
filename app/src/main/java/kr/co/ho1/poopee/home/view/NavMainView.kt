package kr.co.ho1.poopee.home.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Toast
import com.google.android.material.navigation.NavigationView
import kr.co.ho1.poopee.R
import kr.co.ho1.poopee.common.ObserverManager
import kr.co.ho1.poopee.common.data.SharedManager
import kr.co.ho1.poopee.common.http.RetrofitService
import kr.co.ho1.poopee.common.util.MySpannableString
import kr.co.ho1.poopee.common.util.MyUtil
import kr.co.ho1.poopee.databinding.ViewNavMainBinding
import kr.co.ho1.poopee.home.ToiletSearchActivity
import kr.co.ho1.poopee.login.LoginActivity
import kr.co.ho1.poopee.menu.DonateActivity
import kr.co.ho1.poopee.menu.MyInfoActivity
import kr.co.ho1.poopee.menu.NoticeActivity
import kr.co.ho1.poopee.menu.SettingActivity

@Suppress("DEPRECATION")
class NavMainView : NavigationView {
    private lateinit var binding: ViewNavMainBinding

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
        binding = ViewNavMainBinding.inflate(LayoutInflater.from(context), this, true)

        refresh()
        setListener()
    }

    fun refresh() {
        if (SharedManager.isLoginCheck) {
            binding.tvName.text = SharedManager.memberName
            binding.ivLogin.setImageDrawable(MyUtil.getDrawable(R.drawable.img_profile))
            binding.btnLogout.setImageDrawable(MyUtil.getDrawable(R.drawable.ic_logout))
        } else {
            val lineString: Array<String> = arrayOf(MyUtil.getString(R.string.nav_text_06))
            val span = MySpannableString(MyUtil.getString(R.string.nav_text_06))
            span.setLine(lineString)
            binding.tvName.text = span.getSpannableString()
            binding.ivLogin.setImageDrawable(MyUtil.getDrawable(R.drawable.img_logingo))
            binding.btnLogout.setImageDrawable(MyUtil.getDrawable(R.drawable.img_menu_bottom))
        }
    }

    private fun setListener() {
        binding.layoutLogin.setOnClickListener {
            if (SharedManager.memberUsername == "master") {
                try {
                    val clipboard = ObserverManager.context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
                    val clip = ClipData.newPlainText("PooPee", MyUtil.getHashKey())
                    clipboard!!.setPrimaryClip(clip)
                    Toast.makeText(ObserverManager.context!!, MyUtil.getString(R.string.toast_copy_complete), Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            if (!SharedManager.isLoginCheck) {
                ObserverManager.root!!.startActivity(
                    Intent(ObserverManager.context!!, LoginActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                )
            }
        }
        binding.layoutToiletCreate.setOnClickListener {
            if (SharedManager.isLoginCheck) {
                ObserverManager.root!!.startActivity(
                    Intent(ObserverManager.context!!, ToiletSearchActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                )
            } else {
                ObserverManager.root!!.startActivity(
                    Intent(ObserverManager.context!!, LoginActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                )
            }
        }
        binding.layoutMyInfo.setOnClickListener {
            if (SharedManager.isLoginCheck) {
                ObserverManager.root!!.startActivity(
                    Intent(ObserverManager.context!!, MyInfoActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                )
            } else {
                ObserverManager.root!!.startActivity(
                    Intent(ObserverManager.context!!, LoginActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                )
            }
        }
        binding.layoutNotice.setOnClickListener {
            ObserverManager.root!!.startActivity(
                Intent(ObserverManager.context!!, NoticeActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
            )
        }
        binding.layoutRequest.setOnClickListener {
            // EmailSender.send("ho1.poopee@gmail.com", ObserverManager.context!!.getString(R.string.nav_text_05))
            if (SharedManager.isLoginCheck) {
                val dialog = SuggestDialog()
                dialog.show(ObserverManager.root!!.supportFragmentManager, "SuggestDialog")
            } else {
                ObserverManager.root!!.startActivity(
                    Intent(ObserverManager.context!!, LoginActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                )
            }
        }
        binding.layoutAppInstall.setOnClickListener {
            MyUtil.shareText(RetrofitService.BASE_APP + RetrofitService.APP_INSTALL)
        }
        binding.layoutSetting.setOnClickListener {
            ObserverManager.root!!.startActivity(
                Intent(ObserverManager.context!!, SettingActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
            )
        }
        binding.layoutDonate.setOnClickListener {
            ObserverManager.root!!.startActivity(
                Intent(ObserverManager.context!!, DonateActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
            )
        }
        binding.btnLogout.setOnClickListener {
            if (SharedManager.isLoginCheck) {
                ObserverManager.logout()
                refresh()
            }
        }
    }

}