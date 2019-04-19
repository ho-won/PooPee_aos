package kr.ho1.poopee.home

import android.os.Bundle
import android.view.Gravity
import kotlinx.android.synthetic.main.activity_home.*
import kr.ho1.poopee.R
import kr.ho1.poopee.common.ObserverManager
import kr.ho1.poopee.common.base.BaseActivity
import kr.ho1.poopee.common.util.MyUtil
import kr.ho1.poopee.home.model.Toilet
import kr.ho1.poopee.home.view.ToiletDialog

@Suppress("DEPRECATION")
class HomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setToolbar()

        init()
        setListener()
    }

    override fun onResume() {
        super.onResume()
        nav_view.refresh()
    }

    private fun init() {

    }

    private fun setListener() {
        root_view.setOnClickListener {
            MyUtil.keyboardHide(edt_search)
        }
        btn_search_delete.setOnClickListener {
            edt_search.setText("")
        }
        btn_current_location.setOnClickListener {
            val toilet = Toilet()
            toilet.title = "공중화장실"
            toilet.content = "화장실 상세정보"

            val dialog = ToiletDialog()
            dialog.setToilet(toilet)
            dialog.show(supportFragmentManager, "ToiletDialog")
        }
    }

    override fun setToolbar() {
        toolbar.setImageLeftOne(ObserverManager.context!!.resources.getDrawable(R.drawable.ic_bar_menu))
        toolbar.setSelectedListener(
                onBtnLeftOne = {
                    drawer_layout.openDrawer(Gravity.START)
                },
                onBtnLeftTwo = {

                },
                onBtnRightOne = {

                },
                onBtnRightTwo = {

                }
        )
    }

    override fun onBackPressed() {
        if (isShowLoading()) {
            return
        }

        if (drawer_layout.isDrawerOpen(Gravity.START)) {
            drawer_layout.closeDrawer(Gravity.START)
            return
        }

        finish()
    }

}