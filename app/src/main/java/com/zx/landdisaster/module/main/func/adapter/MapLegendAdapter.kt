package com.zx.landdisaster.module.main.func.adapter

import android.view.View
import android.widget.TextView
import com.zx.landdisaster.R
import com.zx.landdisaster.module.main.ui.MapBtnFragment
import com.zx.zxutils.other.QuickAdapter.ZXBaseHolder
import com.zx.zxutils.other.QuickAdapter.ZXQuickAdapter

/**
 * Created by Xiangb on 2019/4/1.
 * 功能：
 */
class MapLegendAdapter(dataBeans: List<MapBtnFragment.LegendBean>, open: Boolean) : ZXQuickAdapter<MapBtnFragment.LegendBean, ZXBaseHolder>(R.layout.item_map_legend, dataBeans) {
    var isOpen = open
    override fun convert(helper: ZXBaseHolder?, item: MapBtnFragment.LegendBean?) {
        if (helper != null && item != null) {
            helper.setText(R.id.tv_legend_text, item.text)
            helper.setBackgroundRes(R.id.iv_legend_icon, item.bg)
            if (item.remark.isNotEmpty() && isOpen) {
                helper.getView<TextView>(R.id.tv_legend_remark).visibility = View.VISIBLE
                helper.setText(R.id.tv_legend_remark, item.remark)
            } else {
                helper.getView<TextView>(R.id.tv_legend_remark).visibility = View.GONE
            }
        }
    }

}