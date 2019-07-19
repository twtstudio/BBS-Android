package com.twtstudio.bbs.bdpqchen.bbs.forum.boards.edit_thread
import com.twtstudio.bbs.bdpqchen.bbs.forum.boards.thread.model.ThreadBean
import com.twtstudio.bbs.bdpqchen.bbs.forum.boards.thread.model.UploadImageModel

interface EditThreadContract {

    interface Presenter{
        fun upLoadMessage(url : String)

        fun doModifyThread(tid : String,title: String , content: String)

        fun getDetailedContent(tid : String)
    }

    interface View{
        fun onUpLoadSuccess(model: UploadImageModel)

        fun onUpLoadFailed(msg :String)

        fun onModifySuccess(model: EditModel)

        fun onModifyFailed(msg: String)

        fun onGetContentSuccess(model: ThreadBean)

        fun onGetContentFailed(msg: String)
    }
}