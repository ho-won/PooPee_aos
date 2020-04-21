package kr.co.ho1.poopee.home.view

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kakao.kakaolink.v2.KakaoLinkResponse
import com.kakao.kakaolink.v2.KakaoLinkService
import com.kakao.kakaonavi.KakaoNaviParams
import com.kakao.kakaonavi.KakaoNaviService
import com.kakao.kakaonavi.Location
import com.kakao.kakaonavi.NaviOptions
import com.kakao.kakaonavi.options.CoordType
import com.kakao.message.template.ButtonObject
import com.kakao.message.template.ContentObject
import com.kakao.message.template.LinkObject
import com.kakao.message.template.LocationTemplate
import com.kakao.network.ErrorResult
import com.kakao.network.callback.ResponseCallback
import kotlinx.android.synthetic.main.dialog_share.*
import kr.co.ho1.poopee.R
import kr.co.ho1.poopee.common.ObserverManager
import kr.co.ho1.poopee.common.base.BaseDialog
import kr.co.ho1.poopee.common.util.MyUtil
import kr.co.ho1.poopee.home.model.Toilet


@Suppress("DEPRECATION")
@SuppressLint("ValidFragment")
class ShareDialog : BaseDialog() {
    companion object {
        const val ACTION_NAVI = "ACTION_NAVI"
        const val ACTION_SHARE = "ACTION_SHARE"
    }

    private var mAction: String = ACTION_NAVI
    private var mToilet: Toilet = Toilet()
    private var mAddressText: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_share, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        setListener()
    }

    private fun init() {
        if (mAction == ACTION_NAVI) {
            tv_title.text = MyUtil.getString(R.string.home_text_16)
            tv_01.text = MyUtil.getString(R.string.home_text_18)
            tv_02.text = MyUtil.getString(R.string.home_text_19)
            iv_01.setImageDrawable(MyUtil.getDrawable(R.drawable.ic_tmap))
            iv_02.setImageDrawable(MyUtil.getDrawable(R.drawable.ic_kakaonavi))
        } else if (mAction == ACTION_SHARE) {
            tv_title.text = MyUtil.getString(R.string.home_text_17)
            tv_01.text = MyUtil.getString(R.string.home_text_20)
            tv_02.text = MyUtil.getString(R.string.home_text_21)
            iv_01.setImageDrawable(MyUtil.getDrawable(R.drawable.ic_kakaotalk))
            iv_02.setImageDrawable(MyUtil.getDrawable(R.drawable.ic_sms))
        }

        mAddressText = ""

        when {
            mToilet.address_new.count() > 0 -> mAddressText = mToilet.address_new
            mToilet.address_old.count() > 0 -> mAddressText = mToilet.address_old
        }
    }

    private fun setListener() {
        layout_01.setOnClickListener {
            if (mAction == ACTION_NAVI) {
                try {
                    ObserverManager.root!!.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("tmap://route?goalx=${mToilet.longitude}&goaly=${mToilet.latitude}&goalname=${mAddressText}")).apply {
                        `package` = "com.skt.tmap.ku"
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    })
                } catch (e: Exception) {
                    ObserverManager.root!!.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.skt.tmap.ku"))
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                }
            } else if (mAction == ACTION_SHARE) {
                val params = LocationTemplate.newBuilder(
                        mAddressText,
                        ContentObject.newBuilder(
                                MyUtil.getString(R.string.home_text_14) + mAddressText,
                                "http://poopee.ho1.co.kr/image/banner.png",
                                LinkObject.newBuilder().build())
                                .setImageHeight(0)
                                .build())
                        .setAddressTitle(mToilet.name)
                        .addButton(ButtonObject(MyUtil.getString(R.string.home_text_15), LinkObject.newBuilder().build()))
                        .build()
                KakaoLinkService.getInstance()
                        .sendDefault(ObserverManager.root, params, object : ResponseCallback<KakaoLinkResponse>() {
                            override fun onFailure(errorResult: ErrorResult) {

                            }

                            override fun onSuccess(result: KakaoLinkResponse) {

                            }
                        })
            }
            dismiss()
        }
        layout_02.setOnClickListener {
            if (mAction == ACTION_NAVI) {
                val builder = KakaoNaviParams.newBuilder(Location.newBuilder(
                        mAddressText,
                        mToilet.longitude,
                        mToilet.latitude).build())
                        .setNaviOptions(NaviOptions.newBuilder().setCoordType(CoordType.WGS84).build())
                KakaoNaviService.getInstance().navigate(ObserverManager.root, builder.build())
            } else if (mAction == ACTION_SHARE) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.type = "vnd.android-dir/mms-sms"
                intent.putExtra("sms_body", MyUtil.getString(R.string.home_text_14) + mAddressText)
                startActivity(intent)
            }
            dismiss()
        }
        btn_close.setOnClickListener {
            dismiss()
        }
    }

    fun setAction(action: String) {
        this.mAction = action
    }

    fun setToilet(toilet: Toilet) {
        this.mToilet = toilet
    }

}