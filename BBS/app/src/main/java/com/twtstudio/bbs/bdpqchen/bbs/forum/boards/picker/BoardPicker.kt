package com.twtstudio.bbs.bdpqchen.bbs.forum.boards.picker

import android.content.Context
import android.util.AttributeSet
import com.ycuwq.datepicker.WheelPicker

class BoardPicker @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : WheelPicker<String>(context, attrs, defStyle) {

    private var selectedItem = "未选择"
    private var selectedPos = 0
    private var mSelectedListener: OnBoardItemSelectedListener? = null

    init {
        dataList = mutableListOf("未选择")
        setOnWheelChangeListener { item, position ->
            selectedItem = item
            selectedPos = position
            mSelectedListener?.onBoardSelected(position)
        }
    }

    fun setData(list: List<String>) {
        dataList = list
    }

    fun getSelectedItem() = selectedItem

    fun getSelectedPos() = selectedPos

    fun setOnBoardSelectListener(listener: OnBoardItemSelectedListener) {
        mSelectedListener = listener
    }

    interface OnBoardItemSelectedListener {
        fun onBoardSelected(position: Int)
    }

}