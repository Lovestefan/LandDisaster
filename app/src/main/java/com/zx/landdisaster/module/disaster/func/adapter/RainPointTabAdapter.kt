package com.zx.landdisaster.module.disaster.func.adapter

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
class RainPointTabAdapter(dataList: List<TimeListBean>) : ZXQuickAdapter<TimeListBean, ZXBaseHolder>(R.layout.item_rain_point_tab, dataList) {
    override fun convert(helper: ZXBaseHolder?, item: TimeListBean?) {
        if (helper != null && item != null) {
            if (item.select) {
                helper.getView<TextView>(R.id.tv_time_name).setTextColor(ContextCompat.getColor(mContext, R.color.blue))
                helper.getView<TextView>(R.id.tv_time_name).setTextSize(1,18f)
            } else {
                helper.getView<TextView>(R.id.tv_time_name).setTextColor(ContextCompat.getColor(mContext, R.color.text_color_normal))
                helper.getView<TextView>(R.id.tv_time_name).setTextSize(1,15f)
            }
            helper.setText(R.id.tv_time_name, item.name)
        }
    }
}