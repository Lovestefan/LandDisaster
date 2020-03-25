package com.zx.landdisaster.module.main.func.adapter

import android.view.View
import com.zx.landdisaster.R
import com.zx.landdisaster.module.main.bean.FuncBean
import com.zx.zxutils.other.QuickAdapter.ZXBaseHolder
import com.zx.zxutils.other.QuickAdapter.ZXQuickAdapter

/**
 * Created by Xiangb on 2019/3/11.
 * 功能：
 */
class FuncAdapter(dataBeans: List<FuncBean>) : ZXQuickAdapter<FuncBean, ZXBaseHolder>(R.layout.item_func, dataBeans) {
    override fun convert(helper: ZXBaseHolder?, item: FuncBean?) {
        if (helper != null && item != null) {
            helper.setText(R.id.tv_func_text, item.title)
            item.icon?.let { helper.setBackgroundRes(R.id.iv_func_icon, it) }
            if (item.showBottomDivider) {
                helper.getView<View>(R.id.view_func_maxDivider).visibility = View.VISIBLE
                helper.getView<View>(R.id.view_func_minDivider).visibility = View.GONE
            } else {
                helper.getView<View>(R.id.view_func_maxDivider).visibility = View.GONE
                if (helper.adapterPosition == data.size - 1) {
                    helper.getView<View>(R.id.view_func_minDivider).visibility = View.GONE
                } else {
                    helper.getView<View>(R.id.view_func_minDivider).visibility = View.VISIBLE
                }
            }

        }
    }
}