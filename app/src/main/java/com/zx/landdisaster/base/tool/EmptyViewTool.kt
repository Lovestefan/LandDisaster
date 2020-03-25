package com.zx.landdisaster.base.tool

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.zx.landdisaster.R

/**
 * Created by Xiangb on 2019/4/28.
 * 功能：
 */
class EmptyViewTool(var context: Context) {

    companion object {
        fun getInstance(context: Context) = EmptyViewTool(context)
    }

    fun getEmptyView(reload: () -> Unit): View {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_empty_view, null, false)
        view.setOnClickListener { reload() }
        return view
    }

}