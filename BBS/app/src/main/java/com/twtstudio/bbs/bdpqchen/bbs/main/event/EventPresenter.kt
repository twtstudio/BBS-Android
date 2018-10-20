package com.twtstudio.bbs.bdpqchen.bbs.main.event

import com.twtstudio.bbs.bdpqchen.bbs.commons.presenter.RxPresenter
import com.twtstudio.bbs.bdpqchen.bbs.commons.rx.RxDoHttpClientTest
import com.twtstudio.bbs.bdpqchen.bbs.commons.rx.SimpleObserver
import com.twtstudio.bbs.bdpqchen.bbs.main.AcEntity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class EventPresenter(val view:EventContract.View) : RxPresenter(),EventContract.Presenter{


    override fun getEventList() {
        val observer = object : SimpleObserver<EventBean>() {
            override fun _onError(msg: String) {
                view.onGetEventListFailed(msg)
            }

            override fun _onNext(t: EventBean) {
                view.onGetEventListSuccess(t)
            }
        }

        addSubscribe(RxDoHttpClientTest
                .getInstance()
                .event
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer))

    }
}