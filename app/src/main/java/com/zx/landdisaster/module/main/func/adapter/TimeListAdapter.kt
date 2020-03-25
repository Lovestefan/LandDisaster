package com.zx.landdisaster.module.main.func.adapter

import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.TextView
import com.zx.landdisaster.R
import com.zx.landdisaster.module.main.bean.TimeListBean
import com.zx.zxutils.other.QuickAdapter.ZXBaseHolder
import com.zx.zxutils.other.QuickAdapter.ZXQuickAdapter

/**
 * Created by Xiangb on 2019/6/20.
 * 功能：
 */
class TimeListAdapter(dataList: List<TimeListBean>) : ZXQuickAdapter<TimeListBean, ZXBaseHolder>(R.layout.item_time_list, dataList) {
    override fun convert(helper: ZXBaseHolder?, item: TimeListBean?) {
        if (helper != null && item != null) {
            if (helper.adapterPosition == data.size - 1) {
                helper.getView<View>(R.id.view_divider).visibility = View.GONE
            } else {
                helper.getView<View>(R.id.view_divider).visibility = View.VISIBLE
            }
            if (item.select) {
                helper.getView<TextView>(R.id.tv_time_name).setBackgroundColor(ContextCompat.getColor(mContext, R.color.sandybrown))
            } else {
                helper.getView<TextView>(R.id.tv_time_name).background = null
            }
            helper.setText(R.id.tv_time_name, item.name)
        }
    }
}