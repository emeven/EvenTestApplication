package com.example.eventestapplication.utils

import android.content.Context
import android.content.res.Resources
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer

/**
 * 使用 LayoutContainer 实现的通用 ViewHolder，使用的时候可以直接放在 binder
 * 或者 adapter 对应 ViewHolder 的泛型的地方，在使用的时候可以直接 holder.xxxview 就可以直接使用 view 了，
 * 同时还会获得 kotlin 提供的 View Cache 的功能
 *
 * @property containerView
 */
open class KotlinViewHolder(override val containerView: View) :
    RecyclerView.ViewHolder(containerView), LayoutContainer {

    /**
     * get context.
     *
     * @return Context
     */
    fun getContext(): Context = itemView.context

    /**
     * get resource.
     *
     * @return Resources
     */
    fun getResource(): Resources = itemView.context.resources

}