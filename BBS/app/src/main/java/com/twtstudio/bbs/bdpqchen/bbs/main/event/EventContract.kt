package com.twtstudio.bbs.bdpqchen.bbs.main.event

import com.twtstudio.bbs.bdpqchen.bbs.main.AcEntity

interface EventContract {

    interface Presenter{
        fun getEventList()
    }

    interface View{
        fun onGetEventListFailed(msg:String)

        fun onGetEventListSuccess(data: EventBean)

    }
}