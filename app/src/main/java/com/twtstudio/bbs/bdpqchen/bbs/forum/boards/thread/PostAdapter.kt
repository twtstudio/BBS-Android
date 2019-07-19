package com.twtstudio.bbs.bdpqchen.bbs.forum.boards.thread

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.twtstudio.bbs.bdpqchen.bbs.R
import com.twtstudio.bbs.bdpqchen.bbs.commons.base.viewholder.BaseFooterViewHolder
import com.twtstudio.bbs.bdpqchen.bbs.commons.support.Constants.*
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.*
import com.twtstudio.bbs.bdpqchen.bbs.commons.view.ThumbView
import com.twtstudio.bbs.bdpqchen.bbs.commons.viewholder.TheEndViewHolder
import com.twtstudio.bbs.bdpqchen.bbs.forum.boards.thread.model.PostBean
import com.twtstudio.bbs.bdpqchen.bbs.forum.boards.thread.model.ThreadModel
import com.twtstudio.bbs.bdpqchen.bbs.htmltextview.GlideImageGeter
import com.twtstudio.bbs.bdpqchen.bbs.htmltextview.PostHtmlTextView
import de.hdodenhof.circleimageview.CircleImageView
import kotterknife.bindView
import java.util.ArrayList

class PostAdapter(val mContext:Context, val mListener:OnPostClickListener ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mIsNoMore:Boolean =false
    private val mPostData= ArrayList<PostBean>()
    private var mPage:Int = 0
    fun getPostList():List<PostBean>{ return mPostData }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        return when (viewType){
            ITEM_NORMAL -> PostHolder(LayoutInflater.from(mContext).inflate(R.layout.item_rv_thread_post, parent, false))
            ITEM_FOOTER -> BaseFooterViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_common_footer, parent, false))
            ITEM_HEADER -> HeaderHolder(LayoutInflater.from(mContext).inflate(R.layout.item_rv_thread_thread, parent, false))
            ITEM_END -> TheEndViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_common_no_more, parent, false))
            ITEM_JUST_HEADER -> JustHeaderHolder(LayoutInflater.from(mContext).inflate(R.layout.item_common_just_header, parent, false))
            else -> null
        }
    }

    override fun getItemCount(): Int {
        return if (mPostData.size == 0) { 0 } else { mPostData.size + 1 }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (mPostData.size > 0){
            when (holder) {
                is HeaderHolder -> {
                    val p = mPostData[position]
                    if (IsUtil.isAnon(p.anonymous)) {
                        p.author_name=ANONYMOUS_NAME
                        p.author_id=0
                        holder.mCivAvatarThread.setOnClickListener(null)
                    } else {
                        holder.mCivAvatarThread.setOnClickListener { startToPeople(p.author_id, p.author_name, holder.mCivAvatarThread) }
                    }
                    ImageUtil.loadAvatarButAnon(mContext, p.author_id, holder.mCivAvatarThread)
                    holder.mTvTitle.text = p.title
                    holder.mTvUsernameThread.text = TextUtil.getNameWithFriend(p.author_name, p.author_nickname, p.friend)
                    holder.mHtvContent.setHtml(p.content_converted!!, GlideImageGeter(mContext, holder.mHtvContent))//content_converted!!
                    holder.mTvDatetimeThread.text = TextUtil.getThreadDateTime(p.t_create, p.t_modify)
                }
                is PostHolder -> run {
                    val p = mPostData[position]
                    val uid = p.author_id
                    if (IsUtil.isAnon(p.anonymous)) {
                        p.author_name = ANONYMOUS_NAME
                        p.author_id = 0
                        holder.mCivAvatarPost.setOnClickListener(null)
                    } else {
                        holder.mCivAvatarPost.setOnClickListener { startToPeople(uid, p.author_name, holder.mCivAvatarPost) }
                    }
                    ImageUtil.loadAvatarButAnon(mContext, uid, holder.mCivAvatarPost)
                    holder.mTvUsernamePost.text = TextUtil.getNameWithFriend(p.author_name, p.author_nickname, p.friend)
                    holder.mTvPostDatetime.text = StampUtil.getDatetimeByStamp(p.t_create)
                    holder.mTvFloorPost.text = (p.floor.toString() + "楼") //floor先tostring
                    holder.mHtvPostContent.setHtml(p.content_converted!!, GlideImageGeter(mContext, holder.mHtvPostContent))//content_converted!!
                    val isLiked = IsUtil.isLiked(p.liked)
                    holder.mThumbView.setIsLiked(isLiked)
                    holder.mThumbView.setLikeCount(p.like)
                    holder.mThumbView.setThumbOnClickListener { mListener.onLikeClick(position, !isLiked, true) }
                    holder.mIvReply.setOnClickListener { mListener.onReplyClick(position) }
                }
                is BaseFooterViewHolder -> {
                    //LogUtil.dd("base footer view");
                }
                is TheEndViewHolder -> {
                    //LogUtil.dd("the end view");
                }
                is JustHeaderHolder -> {
                    //LogUtil.dd("just header view");
                }
            }
        }
    }

    interface OnPostClickListener {
        fun onLikeClick(position: Int, isLike: Boolean, isPost: Boolean)
        fun onReplyClick(position: Int)
    }

    internal class PostHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mCivAvatarPost: CircleImageView by bindView(R.id.civ_avatar_post)
        val mTvUsernamePost: TextView by bindView(R.id.tv_username_post)
        val mTvPostDatetime: TextView by bindView(R.id.tv_post_datetime)
        val mTvFloorPost: TextView by bindView(R.id.tv_floor_post)
        val mHtvPostContent: PostHtmlTextView by bindView(R.id.htv_post_content)
        val mIvReply: ImageView by bindView(R.id.iv_post_reply)
        val mThumbView: ThumbView by bindView(R.id.custom_thumb_post)
    }

    internal class HeaderHolder(view: View):RecyclerView.ViewHolder(view){//bindview
        val mCivAvatarThread:CircleImageView by bindView(R.id.civ_avatar_thread)
        val mTvUsernameThread:TextView by bindView(R.id.tv_username_thread)
        val mTvDatetimeThread:TextView by bindView(R.id.tv_datetime_thread)
        val mTvTitle: TextView by bindView(R.id.tv_title)
        val mHtvContent: PostHtmlTextView by bindView(R.id.htv_content)
    }

    internal inner class JustHeaderHolder(view: View) : RecyclerView.ViewHolder(view)

    private fun startToPeople(uid: Int, username: String, transitionView: View) {
        mContext.startActivity(IntentUtil.toPeople(mContext, uid, username),
        TransUtil.getAvatarTransOptions(mContext, transitionView))
    }

    override fun getItemViewType(position: Int): Int {
        if (mPostData.size > 0) {
            if (position == 0) { return ITEM_HEADER }
            if (mPostData.size == 1) { return ITEM_JUST_HEADER }
            if (position + 1 == itemCount) {
                if (itemCount < mPage * MAX_LENGTH_POST + 1) {
                    return ITEM_END
                }
                if (mIsNoMore) {
                    mIsNoMore = false
                    return ITEM_END
                }
                return ITEM_FOOTER
            } else { return ITEM_NORMAL }
        }
        return ITEM_NORMAL
    }

    fun updateThreadPost(postList:List<PostBean>?,page:Int){
        mPage = page+1
        if (postList == null ||postList.isEmpty()){ mIsNoMore = true }
        if (postList != null){
            mPostData.addAll(postList)
        }
        notifyDataSetChanged()
    }

    fun refreshList(model:List<PostBean>){
        mPostData.removeAll(mPostData)
        mPostData.addAll(model)
        notifyDataSetChanged()
    }

    fun refreshThisPage(postList:List<PostBean>,page:Int){
        //        LogUtil.dd("refreshThisPage()");
        //LogUtil.dd(mPostData.size.toString()) 以前测试用的？（暂时注释
        if (page ==0){
            mPostData.removeAll(mPostData)
        }else{
            for (index in page * MAX_LENGTH_POST until mPostData.size) {
                LogUtil.dd(mPostData.size.toString())
                mPostData.removeAt(index)
                LogUtil.dd(mPostData.size.toString())
            }
        }
        mPostData.addAll(postList)
        notifyDataSetChanged()
        //LogUtil.dd(String.valueOf(mPostData.size()));
    }

    fun likeItem(position:Int,isLike:Boolean){
        var add = 1
        var liked = 1
        if (!isLike){
            add = -1
            liked =0
        }
        val entity:PostBean = mPostData[position];
        entity.like = entity.like + add
        entity.liked = liked
        mPostData[position] = entity
        notifyItemChanged(position)
    }

    fun comment2reply(postPosition:Int,content:String):String{
        val post:PostBean = mPostData[postPosition]
        val added:String = TextUtil.addTwoQuote(TextUtil.cutTwoQuote(post.content))
        return content +TextUtil.getCommentContent(post.floor, getAuthorName(postPosition)) + added
    }

    private fun getAuthorName(position: Int): String {
        var uid = 0
        if (mPostData.size > 0) {
            uid = mPostData[position].author_id
        }
        return if (uid == 0) {
            ANONYMOUS_NAME
        } else {
            mPostData[position].author_name
        }
    //    return "."
    }

    fun getDynamicHint(postPosition: Int):String{
    val hint:String
        hint = if (postPosition==0){
            "评论帖主 " + getAuthorName(0)
        }else {
            val post:PostBean =mPostData[postPosition]
            "回复 " + post.floor + "楼 " + getAuthorName(postPosition)
        }
        return hint
    }

    internal fun getPostId(position: Int): Int {
        return mPostData[position].id
    }

}


