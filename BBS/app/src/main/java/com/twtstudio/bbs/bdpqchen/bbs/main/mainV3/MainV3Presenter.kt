package com.twtstudio.bbs.bdpqchen.bbs.main.mainV3

import com.twtstudio.bbs.bdpqchen.bbs.commons.presenter.RxPresenter
import com.twtstudio.bbs.bdpqchen.bbs.commons.rx.ResponseTransformer
import com.twtstudio.bbs.bdpqchen.bbs.commons.rx.RxDoHttpClientTest
import com.twtstudio.bbs.bdpqchen.bbs.commons.rx.SimpleObserver
import com.twtstudio.bbs.bdpqchen.bbs.main.LatestEntity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainV3Presenter(val view: MainV3Contract.View) : RxPresenter(), MainV3Contract.Presenter {

    override fun getBanner() {
        val observer = object : SimpleObserver<BannerBean>(){
            override fun _onError(msg: String) {
                view.onGetBannerFailed(msg)
            }

            override fun _onNext(t: BannerBean) {
                view.onGetBanner(t)
            }

        }
        addSubscribe(RxDoHttpClientTest.getInstance()
                .banner
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer))
    }

    override fun getLastest(page: Int) {

        val observer = object : SimpleObserver<List<LatestEntity>>() {
            override fun _onError(msg: String) {
                view.onLatestFail(msg)
            }

            override fun _onNext(t: List<LatestEntity>) {
                view.onLatestSucess(t)
            }
        }

        addSubscribe(sHttpClient.getLatestList(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(ResponseTransformer<List<LatestEntity>>())
                .subscribeWith(observer))
    }
}