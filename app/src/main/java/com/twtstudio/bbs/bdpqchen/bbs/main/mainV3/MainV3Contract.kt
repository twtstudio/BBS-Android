package com.twtstudio.bbs.bdpqchen.bbs.main.mainV3

import com.twtstudio.bbs.bdpqchen.bbs.commons.base.BasePresenter
import com.twtstudio.bbs.bdpqchen.bbs.commons.base.BaseView
import com.twtstudio.bbs.bdpqchen.bbs.main.LatestEntity

interface MainV3Contract {

    interface Presenter : BasePresenter {
        fun getLastest(page: Int)
        fun getBanner()
    }

    interface View : BaseView {
        fun onLatestSucess(latestList: List<LatestEntity>)
        fun onLatestFail(msg: String)
        fun onGetBanner(bannerBean: BannerBean)
        fun onGetBannerFailed(msg: String)
    }
}