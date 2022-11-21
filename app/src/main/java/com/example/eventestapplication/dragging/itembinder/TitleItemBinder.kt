package com.example.eventestapplication.dragging.itembinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.drakeet.multitype.ItemViewBinder
import com.example.eventestapplication.R
import com.example.eventestapplication.entities.TitleBean
import com.example.eventestapplication.utils.KotlinViewHolder
import kotlinx.android.synthetic.main.item_layout_title.*
import kotlinx.android.synthetic.main.item_layout_title.view.*

class TitleItemBinder: ItemViewBinder<TitleBean, KotlinViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): KotlinViewHolder {
        return KotlinViewHolder(inflater.inflate(R.layout.item_layout_title, parent, false))
    }

    override fun onBindViewHolder(holder: KotlinViewHolder, item: TitleBean) {
        holder.itemView.titleTV.text = item.title
    }
}