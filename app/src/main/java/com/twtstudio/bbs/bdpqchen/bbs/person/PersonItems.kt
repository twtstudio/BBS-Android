package com.twtstudio.bbs.bdpqchen.bbs.person

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemController
import com.twtstudio.bbs.bdpqchen.bbs.R
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.*
import com.twtstudio.bbs.bdpqchen.bbs.forum.boards.thread.model.ThreadBean
import com.twtstudio.bbs.bdpqchen.bbs.forum.boards.thread.model.ThreadModel
import org.jetbrains.anko.layoutInflater

class PersonHeaderItem(val people : PeopleModel, val context: Context, val uid : Int) : Item{

    companion object Controller : ItemController{

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as ViewHolder
            item as PersonHeaderItem
            ImageUtil.loadAvatar(item.context,item.uid,holder.userAvatarIv)
            ImageUtil.loadBgByUid(item.context,item.uid,holder.background)
            holder.backArrowIv.setOnClickListener{
                val ac = item.context as Activity
                ac.onBackPressed()
            }
            holder.apply {
                userLevelTv.text = TextUtil.getHonor(item.people.points)
                userNameTv.text = TextUtil.getTwoNames(item.people.name,item.people.nickname)
                userSignTv.text = TextUtil.getUserSignature(item.people.signature)
                userPointTv.text = ""+ item.people.points
                userThreadTV.text = "" + item.people.c_thread
                userAgeTv.text = TextUtil.getPastDays(item.context, item.people.t_create)
                messageaOrModify.apply {
                    if (PrefUtil.getAuthUid() == item.uid){
                        this.setImageResource(R.drawable.modify)
                        this.setOnClickListener{
                            item.context.startActivity(IntentUtil.toUpDateInfo(item.context))
                        }
                    } else {
                        this.setImageResource(R.drawable.priviate_message)
                        this.setOnClickListener {
                            item.context.startActivity(IntentUtil.toLetter(item.context,item.uid,item.people.name))
                        }
                    }
                }
                userAvatarIv.setOnClickListener {
                    item.context.startActivity(IntentUtil.toBigPhoto(item.context, UrlUtil.getAvatarUrl(item.uid)))
                }
            }


        }

        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val inflater = parent.context.layoutInflater
            val view = inflater.inflate(R.layout.item_ind_header ,parent,false)
            val background = view.findViewById<ImageView>(R.id.ind_cover)
            val backArrowIv = view.findViewById<ImageView>(R.id.item_ind_header_back_arrow)
            val userAvatarIv = view.findViewById<ImageView>(R.id.ind_ac_avatar)
            val userNameTv = view.findViewById<TextView>(R.id.ind_ac_username)
            val userLevelTv = view.findViewById<TextView>(R.id.ind_ac_userlevel)
            val userSignTv = view.findViewById<TextView>(R.id.ind_ac_user_sign)
            val userPointTv = view.findViewById<TextView>(R.id.ind_ac_points)
            val userThreadTv = view.findViewById<TextView>(R.id.ind_ac_threads)
            val userAgeTv = view.findViewById<TextView>(R.id.ind_ac_station_age)
            val messageOrModify = view.findViewById<ImageView>(R.id.modify_or_message)
            return ViewHolder(view,background ,messageOrModify,backArrowIv,userAvatarIv,userNameTv,userLevelTv,userSignTv,userPointTv,userThreadTv,userAgeTv)
        }

    }

    private class ViewHolder(itemView : View?,val background:ImageView,val messageaOrModify:ImageView, val backArrowIv: ImageView, val userAvatarIv : ImageView,
                             val userNameTv: TextView, val userLevelTv:TextView, val userSignTv:TextView,
                             val userPointTv: TextView,val userThreadTV:TextView, val userAgeTv:TextView
                             ) : RecyclerView.ViewHolder(itemView)

    override val controller: ItemController
        get() = Controller
}

class SingleTextItem(val content : String):Item{

    companion object Controller : ItemController{

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as ViewHolder
            item as SingleTextItem
            holder.singleText.text = item.content
        }

        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val inflater = parent.context.layoutInflater
            val view = inflater.inflate(R.layout.item_single_text,parent,false)
            val singleText = view.findViewById<TextView>(R.id.single_text)
            return ViewHolder(view,singleText)
        }

    }

    class ViewHolder(itemView: View?, val singleText:TextView):RecyclerView.ViewHolder(itemView)

    override val controller: ItemController
        get() = Controller
}

class IndThreadItem(val threadBean: ThreadBean, val context: Context, val uid: Int) : Item {

    companion object Controller : ItemController {
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as ViewHolder
            item as IndThreadItem
            val threadBean = item.threadBean
            ImageUtil.loadAvatar(item.context, item.uid, holder.threadAvatarIv)
            holder.threadNameTv.text = " " + threadBean.author_name + " 发布了帖子"
            holder.threadTitleTv.text = threadBean.title
            holder.threadContentTv.text = threadBean.content
            holder.threadContentTv.maxLines = 3
            holder.threadCommentNumTv.text = "" + threadBean.c_post
            holder.threadFavorNumTv.text = "" + threadBean.like
            holder.threadTimeTv.text = TextUtil.getThreadDateTime(threadBean.t_create,threadBean.t_modify)
            holder.card.setOnClickListener {
                item.context.startActivity(IntentUtil.toThread(item.context,threadBean.id))
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val layoutInflater = parent.context.layoutInflater
            val view = layoutInflater.inflate(R.layout.item_thread_indiviual,parent,false)
            val card = view.findViewById<CardView>(R.id.ind_item_card)
            val threadAvatarIv = view.findViewById<ImageView>(R.id.ind_item_avatar)
            val threadNameTv = view.findViewById<TextView>(R.id.ind_item_name)
            val threadTitleTv = view.findViewById<TextView>(R.id.ind_item_title)
            val threadContentTv = view.findViewById<TextView>(R.id.ind_item_content)
            val threadCommentNumTv = view.findViewById<TextView>(R.id.ind_item_comment_num)
            val threadFavorNumTv = view.findViewById<TextView>(R.id.ind_item_favor_num)
            val threadTimeTv = view.findViewById<TextView>(R.id.ind_item_time)
            return ViewHolder(view, card, threadAvatarIv, threadNameTv, threadTitleTv, threadContentTv, threadCommentNumTv, threadFavorNumTv, threadTimeTv)
        }

    }

    class ViewHolder(itemView: View?, val card: CardView, val threadAvatarIv: ImageView, val threadNameTv: TextView, val threadTitleTv: TextView,
                     val threadContentTv : TextView, val threadCommentNumTv : TextView, val threadFavorNumTv : TextView,
                     val threadTimeTv : TextView) : RecyclerView.ViewHolder(itemView)

    override val controller: ItemController
        get() = Controller
}