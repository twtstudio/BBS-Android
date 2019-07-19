package com.twtstudio.bbs.bdpqchen.bbs.forum.boards.create_thread

import android.os.Bundle
import com.twtstudio.bbs.bdpqchen.bbs.commons.base.BasePresenter
import com.twtstudio.bbs.bdpqchen.bbs.commons.base.BaseView
import com.twtstudio.bbs.bdpqchen.bbs.forum.ForumModel
import com.twtstudio.bbs.bdpqchen.bbs.forum.boards.BoardsModel
import com.twtstudio.bbs.bdpqchen.bbs.forum.boards.thread.model.UploadImageModel

interface CreateThreadContract {
    interface View : BaseView {
        fun onPublished()

        fun onPublishFailed(msg: String?)

        fun onUploadFailed(m: String?)

        fun onUploaded(model: UploadImageModel)

        fun onGetBoardList(model: BoardsModel)

        fun onGetBoardListFailed(m: String)

        fun onGetForumList(list: MutableList<ForumModel>)

        fun onGetForumFailed(m: String)

        open fun setUpPicker() {

        }

    }

    interface Presenter : BasePresenter {
        fun doPublishThread(bundle: Bundle)

        fun uploadImages(uri: String)

        fun getBoardList(forumId: Int)

        fun getForumList()
    }
}
