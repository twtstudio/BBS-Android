package com.twtstudio.bbs.bdpqchen.bbs.person

import com.twtstudio.bbs.bdpqchen.bbs.commons.base.BasePresenter
import com.twtstudio.bbs.bdpqchen.bbs.commons.base.BaseView
import com.twtstudio.bbs.bdpqchen.bbs.forum.boards.thread.model.ThreadBean

interface PersonContract {

    interface Presenter : BasePresenter{
        fun getPersonInfo(uid:Int)
    }

    interface View : BaseView {
        fun onPersonInfoSuccess(person : PeopleModel)
        fun onLoadFailed(info : String)
        fun onThreadInfoSuccess(thread: List<ThreadBean>)
    }

}