package com.twtstudio.bbs.bdpqchen.bbs.main.event

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemController
import com.twtstudio.bbs.bdpqchen.bbs.R
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.IntentUtil
import org.jetbrains.anko.layoutInflater

class EventItem(val context: Context, val data: Data) : Item {
    override val controller: ItemController
        get() = Controller

    companion object Controller : ItemController {

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            item as EventItem
            holder as ViewHolder
            holder.itemView.setOnClickListener {
                item.context.startActivity(IntentUtil.toThread(item.context, item.data.id))
            }
            holder.title.text = item.data.title
        }

        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val inflater = parent.context.layoutInflater
            val view = inflater.inflate(R.layout.item_event, parent, false)
            val text = view.findViewById<TextView>(R.id.event_text)
            return ViewHolder(view, text)
        }

    }

    class ViewHolder(itemView: View, val title: TextView) : RecyclerView.ViewHolder(itemView)

}