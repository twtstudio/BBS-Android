package com.twtstudio.bbs.bdpqchen.bbs.forum.boards.picker

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*
import android.widget.Button
import com.twtstudio.bbs.bdpqchen.bbs.R
import com.twtstudio.bbs.bdpqchen.bbs.forum.boards.BoardsModel
import com.twtstudio.bbs.bdpqchen.bbs.forum.boards.create_thread.DataContract

class PickerDialog : DialogFragment() {
    private lateinit var button: Button
    private lateinit var fpicker: ForumPicker
    private lateinit var bpicker: BoardPicker
    private lateinit var dataMap: Map<String, List<BoardsModel.BoardsBean>>

    private var forumName = "未选择"
    private var forumPos = -1
    private var boardName = "未选择"
    private var boardId = 0
    private var boardPos = -1

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_picker, container)
        fpicker = view.findViewById(R.id.forum_picker)
        bpicker = view.findViewById(R.id.board_picker)
        button = view.findViewById(R.id.submit_select)
        getContract().apply {
            dataMap = getData()
            forumPos = getForumPos()
            boardPos = getBoardPos()
            button.setOnClickListener {
                setSelected(forumPos, boardId, boardName)
                dismiss()
            }
        }

        fpicker.apply {
            dataList = dataMap.keys.toMutableList().also { it.add(0, "请选择") }
            setOnForumSelectedListener(object : ForumPicker.OnForumItemSelectedListener {
                override fun onForumSelected(position: Int) {
                    onSelect()
                    if (dataMap[forumName] != null && dataMap[forumName]!!.isNotEmpty()) {
                        boardName = dataMap[forumName]!![0].name
                    }
                }
            })
        }
        bpicker.apply {
            boardName = getSelectedItem()
            boardPos = getSelectedPos()
            setOnBoardSelectListener(object : BoardPicker.OnBoardItemSelectedListener {
                override fun onBoardSelected(position: Int) {
                    if (position != 0) {
                        dataMap[forumName]?.apply {
                            boardId = this[position - 1].id
                        }
                    }
                    boardPos = getSelectedPos()
                    boardName = getSelectedItem()
                }
            })
        }
        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(activity, R.style.DatePickerBottomDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.fragment_picker)
        dialog.setCanceledOnTouchOutside(true)
        val window = dialog.window
        window?.apply {
            val lp = attributes
            lp.gravity = Gravity.BOTTOM
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.dimAmount = 0.35f
            attributes = lp
        }
        return dialog
    }

    private fun onSelect() {
        forumPos = fpicker.getSelectedPos() - 1
        forumName = fpicker.getSelectedItem()
        if (forumName == "未选择") {
            bpicker.setData(mutableListOf("未选择"))
        }
        dataMap[forumName]?.apply {
            val list = map { it.name }
            bpicker.dataList = list.toMutableList().also { it.add(0, "请选择") }
        }
    }

    private fun getContract(): DataContract = activity as DataContract
}
