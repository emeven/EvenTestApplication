package com.example.eventestapplication.dragging.itembinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.drakeet.multitype.ItemViewBinder
import com.example.eventestapplication.R
import com.example.eventestapplication.utils.KotlinViewHolder

class HeadImageItemBinder: ItemViewBinder<String, KotlinViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): KotlinViewHolder {
        return KotlinViewHolder(inflater.inflate(R.layout.item_layout_head_image, parent, false))
    }

    override fun onBindViewHolder(holder: KotlinViewHolder, item: String) {
    }
}