package com.zx.landdisaster.module.main.func.adapter

import android.view.View
import android.widget.TextView
import com.zx.landdisaster.R
import com.zx.landdisaster.module.main.bean.FuncBean
import com.zx.zxutils.other.QuickAdapter.ZXBaseHolder
import com.zx.zxutils.other.QuickAdapter.ZXQuickAdapter

/**
 * Created by Xiangb on 2019/3/21.
 * 功能：
 */
class MapBtnAdapter(dataBeans: List<FuncBean>) : ZXQuickAdapter<FuncBean, ZXBaseHolder>(R.layout.item_map_btn, dataBeans) {
    override fun convert(helper: ZXBaseHolder?, item: FuncBean?) {
        if (helper != null && item != null) {
            helper.setText(R.id.tv_mapBtn_text, item.title)
            helper.setBackgroundRes(R.id.iv_mapBtn_bg, item.icon!!)
            if (item.taskNum == 0) {
                helper.getView<TextView>(R.id.tv_mapBtn_num).visibility = View.INVISIBLE
            } else if (item.taskNum >= 100) {
                helper.getView<TextView>(R.id.tv_mapBtn_num).visibility = View.VISIBLE
                helper.setText(R.id.tv_mapBtn_num, "99+")
            } else {
                helper.getView<TextView>(R.id.tv_mapBtn_num).visibility = View.VISIBLE
                helper.setText(R.id.tv_mapBtn_num, item.taskNum.toString())
            }
        }
    }
}