package com.twtstudio.bbs.bdpqchen.bbs.main.mainV3


data class BannerBean(
        val err: Int,
        val data: List<Data>
)

data class Data(
        val id: Int,
        val image_id: Int,
        val thread_id: Int
)