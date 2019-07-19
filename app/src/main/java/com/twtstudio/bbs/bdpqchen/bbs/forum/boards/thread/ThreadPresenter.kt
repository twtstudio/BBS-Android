package com.twtstudio.bbs.bdpqchen.bbs.forum.boards.thread

import com.twtstudio.bbs.bdpqchen.bbs.commons.model.BaseModel
import com.twtstudio.bbs.bdpqchen.bbs.commons.presenter.RxPresenter
import com.twtstudio.bbs.bdpqchen.bbs.commons.rx.BaseResponse
import com.twtstudio.bbs.bdpqchen.bbs.commons.rx.ResponseTransformer
import com.twtstudio.bbs.bdpqchen.bbs.commons.rx.SimpleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.TextUtil
import com.twtstudio.bbs.bdpqchen.bbs.forum.boards.thread.model.PostModel
import com.twtstudio.bbs.bdpqchen.bbs.forum.boards.thread.model.ThreadModel
import com.twtstudio.bbs.bdpqchen.bbs.forum.boards.thread.model.UploadImageModel

import io.reactivex.Observable

internal class ThreadPresenter(private val mView: ThreadContract.View?) : RxPresenter(),ThreadContract.Presenter {

    override fun getThread(threadId: Int, postPage: Int) {
        val observer = object : SimpleObserver<ThreadModel>() {
            override fun _onError(msg: String) { mView?.onGetThreadFailed(msg) }
            override fun _onNext(model: ThreadModel) { mView?.onGotThread(model) }
        }
        addSubscribe(RxPresenter.sHttpClient.getThread(threadId, postPage)
                .map(ResponseTransformer<ThreadModel>())
                .map { threadModel ->
                        if (threadModel.thread != null) {
                            threadModel.thread!!.content_converted = TextUtil.convert2HtmlContent(threadModel.thread!!.content)
                        }
                        if (threadModel.post != null && threadModel.post!!.isNotEmpty()) {
                            val postList = threadModel.post
                            for (i in postList!!.indices) {
                                val content = postList[i].content
                                postList[i].content_converted = TextUtil.convert2HtmlContent(content)
                            }
                        }
                    threadModel
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer)
        )
    }

    override fun doComment(threadId: Int, comment: String, replyId: Int, isAnonymous: Boolean) {
    val observer = object:SimpleObserver<PostModel>(){
        override fun _onError(msg: String) {
         mView?.onCommentFailed(msg)
        }

        override fun _onNext(t: PostModel) {
        mView?.onCommented(t)
        }

    }
        addSubscribe(sHttpClient.doComment(threadId,comment,replyId,isAnonymous)
                .map(ResponseTransformer())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer)
        )
    }

   /* ResponseTransformer<BaseModel> transformer = new ResponseTransformer<>();
   *  这种写法全改成在map实例化里了
   */
    override fun starThread(id: Int) {
    val observer =object:SimpleObserver<BaseModel>(){
        override fun _onNext(t: BaseModel) { mView?.onStarred() }

        override fun _onError(msg: String) { mView?.onStarFailed(msg) }
    }
    addSubscribe(sHttpClient.starThread(id)
            .map(ResponseTransformer<BaseModel>()) //这里把之前的精简了一下（不知道会不会出bug
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(observer)
    )
    }

    override fun unStarThread(id: Int) {
        val observer =object:SimpleObserver<BaseModel>(){
            override fun _onNext(t: BaseModel) { mView?.onUnStarred() }

            override fun _onError(msg: String) { mView?.onUnStarFailed(msg) }
        }
        addSubscribe(sHttpClient.unStarThread(id)
                .map(ResponseTransformer<BaseModel>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer)
        )

    }

    override fun uploadImages(uri: String) {
        val observer =object:SimpleObserver<UploadImageModel>(){
            override fun _onError(msg: String) { mView?.onUploadFailed(msg) }

            override fun _onNext(t: UploadImageModel) { mView?.onUploaded(t) }
        }
        addSubscribe(sHttpClient.uploadImage(uri)
                .map(ResponseTransformer<UploadImageModel>())//????
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer)
        )
    }

    override fun like(id: Int, position: Int, isLike: Boolean, isThread: Boolean) {
        val observer =object:SimpleObserver<BaseModel>(){
            override fun _onNext(t: BaseModel) {
                if (isLike) mView?.onLike(t) //isLike意思应该不是是指已经点赞了吧???
                else mView?.onUnlike(t)
            }

            override fun _onError(msg: String) {
                if (isLike) mView?.onLikeFailed(msg,position,true)
            else mView?.onLikeFailed(msg,position,false)
            }
        }
        addSubscribe(getLikeHeader(id, isLike, isThread)
                .map(ResponseTransformer<BaseModel>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer)
        )
    }

    private fun getLikeHeader(id: Int, isLike: Boolean, isPost: Boolean): Observable<BaseResponse<BaseModel>> {
        return if (isPost) {
            if (isLike)
                RxPresenter.sHttpClient.likePost(id)
            else
                RxPresenter.sHttpClient.unlikePost(id)
        } else {
            if (isLike)
                RxPresenter.sHttpClient.likeThread(id)
            else
                RxPresenter.sHttpClient.unlikeThread(id)
        }
    }

}

