package com.twtstudio.bbs.bdpqchen.bbs.main.event


interface EventContract {

    interface Presenter{
        fun getEventList()
    }

    interface View{
        fun onGetEventListFailed(msg:String)

        fun onGetEventListSuccess(data: EventBean)

    }
}