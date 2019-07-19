package com.twtstudio.bbs.bdpqchen.bbs.search

import android.view.View
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.LogUtil

/**
 * Created by bdpqchen on 17-10-28.
 */
interface OnUserItemClick : View.OnClickListener {
    override fun onClick(v: View?) {
        LogUtil.dd("OnUserItemClick", v.toString())
    }

    fun onClick(position: Int)
}
