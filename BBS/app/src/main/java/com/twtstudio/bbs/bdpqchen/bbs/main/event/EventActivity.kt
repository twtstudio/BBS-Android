package com.twtstudio.bbs.bdpqchen.bbs.main.event

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ImageView
import cn.edu.twt.retrox.recyclerviewdsl.withItems
import com.twtstudio.bbs.bdpqchen.bbs.R
import com.twtstudio.bbs.bdpqchen.bbs.commons.base.BaseActivity
import com.twtstudio.bbs.bdpqchen.bbs.commons.base.BasePresenter
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.SnackBarUtil
import kotterknife.bindView

class EventActivity : BaseActivity(), EventContract.View {

    val presenter = EventPresenter(this)
    val arrowBack : ImageView by bindView(R.id.activity_event_ic_back)
    val recyclerView: RecyclerView by bindView(R.id.event_recycler_view)
    private val layoutManager = LinearLayoutManager(this)

    override fun getLayoutResourceId() = R.layout.activity_event

    override fun getToolbarView() = null

    override fun getPresenter() : BasePresenter = presenter

    override fun onGetEventListFailed(msg: String) {
        SnackBarUtil.error(this,msg)
    }

    override fun onGetEventListSuccess(data: EventBean) {
        recyclerView.withItems(data.data.map { EventItem(this@EventActivity,it) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            val option = (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            decorView.systemUiVisibility = option
            window.statusBarColor = Color.WHITE
            window.navigationBarColor = Color.BLACK
        }
        enableLightStatusBarMode(true)
        recyclerView.layoutManager = layoutManager
        arrowBack.setOnClickListener {
            onBackPressed()
        }
        presenter.getEventList()
    }
}
