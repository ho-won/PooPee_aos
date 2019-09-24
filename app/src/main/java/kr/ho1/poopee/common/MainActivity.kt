package kr.ho1.poopee.common

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kr.ho1.poopee.R
import kr.ho1.poopee.common.base.BaseActivity
import kr.ho1.poopee.common.base.BaseApp
import kr.ho1.poopee.common.data.SharedManager
import kr.ho1.poopee.common.dialog.BasicDialog
import kr.ho1.poopee.common.http.*
import kr.ho1.poopee.common.util.LocationManager
import kr.ho1.poopee.common.util.PermissionManager
import kr.ho1.poopee.common.util.SleepTask
import kr.ho1.poopee.home.HomeActivity
import org.json.JSONException
import org.json.JSONObject
import retrofit2.http.POST

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    private fun init() {
        val animation = AnimationUtils.loadAnimation(ObserverManager.context, R.anim.intro_drop)
        animation.interpolator = AccelerateInterpolator(0.5f)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                val animationUp = AnimationUtils.loadAnimation(ObserverManager.context, R.anim.intro_drop_up)
                animationUp.interpolator = AccelerateInterpolator(0.5f)
                animationUp.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {

                    }

                    override fun onAnimationEnd(animation: Animation) {
                        if (intent.action != null && intent.action == BaseApp.ACTION_EXIT) {
                            // 앱종료
                            finish()
                        } else {
                            // 퍼미션 체크
                            if (PermissionManager.permissionCheck(Manifest.permission.ACCESS_FINE_LOCATION)
                                    && PermissionManager.permissionCheck(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                onServiceCheck()
                            } else {
                                PermissionManager.requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE), PermissionManager.Permission)
                            }
                        }
                    }

                    override fun onAnimationRepeat(animation: Animation) {

                    }
                })
                layout_drop.startAnimation(animationUp)
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
        layout_drop.startAnimation(animation)
    }

    private fun onServiceCheck() {
        LocationManager.setLocationListener() // 현재위치 리스너 추가
        DBVersionTask(progress_file_download,
                onSuccess = {
                    taskServerCheck()
                },
                onFailed = {
                    val dialog = BasicDialog(
                            onLeftButton = {

                            },
                            onCenterButton = {

                            },
                            onRightButton = {
                                finish()
                            }
                    )
                    dialog.isCancelable = false
                    dialog.setTextContent(ObserverManager.context!!.resources.getString(R.string.dialog_download_request))
                    dialog.setBtnRight(ObserverManager.context!!.resources.getString(R.string.yes))
                    dialog.show(supportFragmentManager, "BasicDialog")
                }
        )
    }

    /**
     * 서버의 앱버전정보와 현재 앱버전정보를 비교하여 팝업창 띄움.
     */
    private fun onVersionCheck(response: JSONObject) {
        try {
            val version = response.getString("version_aos")
            val versions = version.split(".")
            val appVersions = BaseApp.APP_VERSION.split(".")

            when {
                Integer.parseInt(versions[0]) > Integer.parseInt(appVersions[0]) -> {
                    // 첫번째 자리 버전 업데이트로 무조건 업데이트받아야 앱 실행
                    val dialog = BasicDialog(
                            onLeftButton = {
                                finish()
                            },
                            onCenterButton = {

                            },
                            onRightButton = {
                                updateInPlayMarket()
                                finish()
                            }
                    )
                    dialog.isCancelable = false
                    dialog.setTextContent(ObserverManager.context!!.resources.getString(R.string.dialog_force_new_version_update))
                    dialog.setBtnLeft(ObserverManager.context!!.resources.getString(R.string.no))
                    dialog.setBtnRight(ObserverManager.context!!.resources.getString(R.string.yes))
                    dialog.show(supportFragmentManager, "BasicDialog")
                }
                Integer.parseInt(versions[1]) > Integer.parseInt(appVersions[1]) -> {
                    // 두번째 자리 버전 업데이트로 업데이트 팝업 선택 후 앱 실행
                    val dialog = BasicDialog(
                            onLeftButton = {
                                loginCheck()
                            },
                            onCenterButton = {

                            },
                            onRightButton = {
                                updateInPlayMarket()
                                finish()
                            }
                    )
                    dialog.isCancelable = false
                    dialog.setTextContent(ObserverManager.context!!.resources.getString(R.string.dialog_assign_new_version_update))
                    dialog.setBtnLeft(ObserverManager.context!!.resources.getString(R.string.no))
                    dialog.setBtnRight(ObserverManager.context!!.resources.getString(R.string.yes))
                    dialog.show(supportFragmentManager, "BasicDialog")
                }
                Integer.parseInt(versions[0]) < Integer.parseInt(appVersions[0]) -> // 세번째 자리 버전 업데이트로 무시하고 앱실행
                    loginCheck()
                else -> // 현재 앱버전이 최신버전일경우
                    loginCheck()
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    /**
     * 앱의 플레이스토어로 이동.
     */
    private fun updateInPlayMarket() {
        val appPackageName = packageName
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName"))
                    .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
            )
        } catch (e: android.content.ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName"))
                    .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
            )
        }
    }

    /**
     * 로그인 체크
     */
    private fun loginCheck() {
        if (SharedManager.isLoginCheck()) {
            taskLogin()
        } else {
            gotoHomeActivity()
        }
    }

    /**
     * 홈 화면으로 이동.
     */
    private fun gotoHomeActivity() {
        SleepTask(1000, onFinish = {
            startActivity(Intent(ObserverManager.context!!, HomeActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
            )
            finish()
        }).execute()
    }

    /**
     * [POST] 로그인
     */
    private fun taskLogin() {
        val params = RetrofitParams()
        params.put("username", SharedManager.getMemberUsername())
        params.put("password", SharedManager.getMemberPassword())
        params.put("pushkey", "test")
        params.put("os", "aos")

        val request = RetrofitClient.getClient(RetrofitService.BASE_APP).create(RetrofitService::class.java).login(params.getParams())

        RetrofitJSONObject(request,
                onSuccess = {
                    try {
                        if (it.getInt("rst_code") == 0) {
                            SharedManager.setMemberId(it.getString("member_id"))
                            SharedManager.setMemberName(it.getString("name"))
                            SharedManager.setMemberGender(it.getString("gender"))
                            gotoHomeActivity()
                        } else {
                            ObserverManager.logout()
                            gotoHomeActivity()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        ObserverManager.logout()
                        gotoHomeActivity()
                    }
                },
                onFailed = {
                    ObserverManager.logout()
                    gotoHomeActivity()
                }
        )
    }

    /**
     * [GET] 서버상태체크
     */
    private fun taskServerCheck() {
        val params = RetrofitParams()
        params.put("date", SharedManager.getNoticeDate())

        val request = RetrofitClient.getClient(RetrofitService.BASE_APP).create(RetrofitService::class.java).serverCheck(params.getParams())

        RetrofitJSONObject(request,
                onSuccess = {
                    try {
                        if (it.getString("server") == "success") {
                            SharedManager.setNoticeImage(it.getString("notice_image"))
                            onVersionCheck(it) // 버전체크
                        } else {
                            Toast.makeText(ObserverManager.context!!, it.getString("server"), Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(ObserverManager.context!!, ObserverManager.context!!.resources.getString(R.string.toast_checking_service), Toast.LENGTH_SHORT).show()
                        finish()
                    }
                },
                onFailed = {
                    Toast.makeText(ObserverManager.context!!, ObserverManager.context!!.resources.getString(R.string.toast_checking_service), Toast.LENGTH_SHORT).show()
                    finish()
                }
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        var result = 0
        for (grantResult in grantResults) {
            result += grantResult
        }

        if (result == 0) {
            when (requestCode) {
                PermissionManager.Permission -> {
                    onServiceCheck()
                }
            }
        } else {
            Toast.makeText(ObserverManager.context, ObserverManager.context!!.resources.getString(R.string.toast_please_permission), Toast.LENGTH_SHORT).show()
        }
    }

}
