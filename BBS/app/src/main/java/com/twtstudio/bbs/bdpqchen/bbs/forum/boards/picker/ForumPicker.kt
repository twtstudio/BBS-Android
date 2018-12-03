package com.twtstudio.bbs.bdpqchen.bbs.forum.boards.picker

import android.content.Context
import android.util.AttributeSet
import com.ycuwq.datepicker.WheelPicker

class ForumPicker @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : WheelPicker<String>(context, attrs, defStyle) {

    private var selectedItem = "未选择"
    private var selectedPos = 0
    private var mForumItemSelectedListener: OnForumItemSelectedListener? = null

    init {
        dataList = mutableListOf("未选择")
        setOnWheelChangeListener { item, position ->
            selectedPos = position
            selectedItem = item
            mForumItemSelectedListener?.onForumSelected(position)
        }
    }

    public fun getSelectedItem() = selectedItem

    public fun getSelectedPos() = selectedPos

    public fun setOnForumSelectedListener(listener: OnForumItemSelectedListener) {
        mForumItemSelectedListener = listener
    }

    interface OnForumItemSelectedListener {
        fun onForumSelected(position: Int)
    }

}