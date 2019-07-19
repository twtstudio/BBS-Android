package com.twtstudio.bbs.bdpqchen.bbs.forum.boards.edit_thread

import com.twtstudio.bbs.bdpqchen.bbs.commons.presenter.RxPresenter
import com.twtstudio.bbs.bdpqchen.bbs.commons.rx.SimpleObserver
import com.twtstudio.bbs.bdpqchen.bbs.forum.boards.thread.model.ThreadBean
import com.twtstudio.bbs.bdpqchen.bbs.forum.boards.thread.model.ThreadModel
import com.twtstudio.bbs.bdpqchen.bbs.forum.boards.thread.model.UploadImageModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class EditThreadPresenter(val mView : EditThreadContract.View) : RxPresenter(),EditThreadContract.Presenter{

    override fun upLoadMessage(url: String) {
        val observer = object : SimpleObserver<UploadImageModel>(){
            override fun _onError(msg: String) {
                mView.onUpLoadFailed(msg)
            }

            override fun _onNext(t: UploadImageModel) {
                mView.onUpLoadSuccess(t)
            }

        }

        addSubscribe(sHttpClient.uploadImage(url)
                .map { it.data }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer))
    }

    override fun doModifyThread(tid : String ,title: String, content: String) {
        val observer = object : SimpleObserver<EditModel>(){
            override fun _onError(msg: String) {
                mView.onModifyFailed(msg)
            }

            override fun _onNext(t: EditModel) {
                mView.onModifySuccess(t)
            }
        }

        addSubscribe(sHttpClient.doEdit(tid,title,content)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer))
    }

    override fun getDetailedContent(tid: String) {
        val observer = object : SimpleObserver<ThreadBean>(){
            override fun _onError(msg: String) {
                mView.onGetContentFailed(msg)
            }

            override fun _onNext(t: ThreadBean) {
                mView.onGetContentSuccess(t)
            }
        }

        addSubscribe(sHttpClient.getThread(tid.toInt(),0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { it.data }
                .map { it.thread!! }//可能导致空?(bug
                .subscribeWith(observer))
    }
}