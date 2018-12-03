package com.twtstudio.bbs.bdpqchen.bbs.forum.boards.create_thread

import com.twtstudio.bbs.bdpqchen.bbs.forum.boards.BoardsModel

interface DataContract {

    fun getData(): Map<String, List<BoardsModel.BoardsBean>>

    fun setSelected(forumId: Int, boardId: Int, boardName: String)

    fun getBoardPos(): Int

    fun getForumPos(): Int

    fun isSpecified(): Boolean
}