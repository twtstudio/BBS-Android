package com.twtstudio.bbs.bdpqchen.bbs.forum.boards.thread.model


data class ThreadModel (
        //threadbean 非空?(测试一下
var thread: ThreadBean,
var board: BoardBean?,
var post: List<PostBean>?)

data class ThreadBean (
    var id: Int,
    var title: String,
    var author_id: Int,
    var anonymous: Int,
    var author_name: String,
    var author_nickname: String,
    var c_post: Int,
    var b_top: Int,
    var b_elite: Int,
    var b_locked: Int,
    var visibility: Int,
    var t_reply: Int,
    var t_create: Int,
    var t_modify: Int,
    var content: String,
    var content_converted: String,
    var in_collection: Int,
    var like: Int,
    var liked: Int,
    var friend: Int)

    /**
     * id : 159
     * forum_id : 30
     * forum_name : 知性感性
     * name : 求实焦点
     * anonymous : 1
     */
    data class BoardBean (
            var id: Int,
            var forum_id: Int,
            var forum_name: String,
            var name: String,
            var anonymous: Int
    )

    /**
     * id : 299394
     * author_id : 13202
     * author_name : lazierboy
     * author_nickname : 哈哈
     * content : 认识了好多人
     * 你最熟悉的ID是啥？
     *
     *
     * anonymous : 0
     * floor : 2
     * t_create : 1496237957
     * t_modify : 0
     */
    data class PostBean (
    var id: Int,
    var author_id: Int,
    var author_name: String,
    var author_nickname: String,
    var content: String?,
    var content_converted: String?,
    var anonymous: Int,
    var floor: Int,
    var t_create: Int,
    var t_modify: Int,
    var title: String?,
    var like: Int,
    var liked: Int,
    var friend: Int)

