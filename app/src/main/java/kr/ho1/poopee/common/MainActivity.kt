package kr.ho1.poopee.common

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kr.ho1.poopee.R
import kr.ho1.poopee.common.base.BaseActivity
import kr.ho1.poopee.common.util.LocationManager
import kr.ho1.poopee.common.util.SleepTask
import kr.ho1.poopee.home.HomeActivity

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    private fun init() {
        LocationManager.setLocationListener() // 현재위치 리스너 추가
        SleepTask(3000, onFinish = {
            startActivity(Intent(ObserverManager.context!!, HomeActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
            )
            finish()
        }).execute()
    }

}
