package com.twtstudio.bbs.bdpqchen.bbs.message2

import com.twtstudio.bbs.bdpqchen.bbs.commons.base.BasePresenter
import com.twtstudio.bbs.bdpqchen.bbs.commons.base.BaseView

/**
 * Created by linjiaxin on 2018/7/18.
 */
interface Message2Contract {
    interface View : BaseView {
        fun onGetMessageFailed(m: String)
        fun showMessageList(messageList: List<MessageModel>)
        fun onCleared()
        fun onClearFailed(msg: String)
        fun onGotMessageCount(integer: Int)
    }

    interface Presenter : BasePresenter {
        fun getMessageList(page: Int)
        fun doClearUnreadMessage()
        fun getUnreadMessageCount()
    }
}