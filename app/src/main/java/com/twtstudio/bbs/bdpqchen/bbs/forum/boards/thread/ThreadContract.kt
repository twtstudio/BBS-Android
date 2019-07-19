package com.twtstudio.bbs.bdpqchen.bbs.forum.boards.thread

import com.twtstudio.bbs.bdpqchen.bbs.commons.base.BasePresenter
import com.twtstudio.bbs.bdpqchen.bbs.commons.base.BaseView
import com.twtstudio.bbs.bdpqchen.bbs.commons.model.BaseModel
import com.twtstudio.bbs.bdpqchen.bbs.forum.boards.thread.model.PostModel
import com.twtstudio.bbs.bdpqchen.bbs.forum.boards.thread.model.ThreadModel
import com.twtstudio.bbs.bdpqchen.bbs.forum.boards.thread.model.UploadImageModel

interface ThreadContract{
    interface View:BaseView{
        fun onGotThread(model: ThreadModel)

        fun onGetThreadFailed(m: String)

        fun onCommentFailed(m:String?)

        fun onCommented(model: PostModel)

        fun onStarFailed(m: String)

        fun onUnStarFailed(m: String)

        fun onStarred()

        fun onUnStarred()

        fun onUploadFailed(m: String)

        fun onUploaded(model: UploadImageModel)

        fun onLike(model: BaseModel)

        fun onLikeFailed(m: String, position: Int, isLike: Boolean)

        fun onUnlike(entity: BaseModel)

        fun onUnlikeFailed(m: String, position: Int, isLike: Boolean)

    }

    interface Presenter : BasePresenter {
        fun getThread(threadId: Int, postPage: Int)

        fun doComment(threadId: Int, comment: String, replyId: Int, isAnonymous: Boolean)

        fun starThread(id: Int)

        fun unStarThread(id: Int)

        fun uploadImages(uri: String)

        fun like(id: Int, position: Int, isLike: Boolean, isPost: Boolean)
    }
}