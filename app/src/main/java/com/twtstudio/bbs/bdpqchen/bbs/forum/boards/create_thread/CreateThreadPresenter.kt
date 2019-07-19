package com.twtstudio.bbs.bdpqchen.bbs.forum.boards.create_thread

import android.os.Bundle
import com.twtstudio.bbs.bdpqchen.bbs.commons.presenter.RxPresenter
import com.twtstudio.bbs.bdpqchen.bbs.commons.rx.ResponseTransformer
import com.twtstudio.bbs.bdpqchen.bbs.commons.rx.SimpleObserver
import com.twtstudio.bbs.bdpqchen.bbs.forum.boards.thread.model.UploadImageModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CreateThreadPresenterextends(private val mView:CreateThreadContract.View?) : RxPresenter(),CreateThreadContract.Presenter{


    override fun doPublishThread(bundle: Bundle) {

        val observer = object:SimpleObserver<CreateThreadModel>(){
            override fun _onError(msg: String?) {
                mView?.onPublishFailed(msg)
            }

            override fun _onNext(t: CreateThreadModel?) {
                mView?.onPublished()
            }

        }
        addSubscribe(sHttpClient.doPublishThread(bundle)
                .map(ResponseTransformer())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer)
        )

    }

    override fun uploadImages(uri: String) {
        val obserber = object:SimpleObserver<UploadImageModel>(){
            override fun _onError(msg: String?) {
                mView?.onUploadFailed(msg)
            }

            override fun _onNext(t: UploadImageModel) {
                mView?.onUploaded(t)
            }
        }
        addSubscribe(sHttpClient.uploadImage(uri)
                .map(ResponseTransformer())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(obserber)
        )
    }

    override fun getBoardList(forumId: Int) {

    }

    override fun getForumList() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}