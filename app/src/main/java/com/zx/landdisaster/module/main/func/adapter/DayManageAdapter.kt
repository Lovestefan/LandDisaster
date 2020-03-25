package com.zx.landdisaster.module.main.func.adapter

import com.zx.landdisaster.R
import com.zx.landdisaster.module.main.bean.HomeBean
import com.zx.zxutils.other.QuickAdapter.ZXBaseHolder
import com.zx.zxutils.other.QuickAdapter.ZXQuickAdapter

/**
 * Created by zc on 2019/5/23.
 * 功能：
 */
class DayManageAdapter(dataBeans: List<HomeBean>) : ZXQuickAdapter<HomeBean, ZXBaseHolder>(R.layout.item_day_manage, dataBeans) {
    override fun convert(helper: ZXBaseHolder?, item: HomeBean?) {
        if (helper != null && item != null) {
            helper.setText(R.id.text, item.title)
            helper.setBackgroundRes(R.id.img, item.icon!!)
            helper.setBackgroundRes(R.id.bg, item.bg!!)
//            if (item.taskNum == 0) {
//                helper.getView<TextView>(R.id.tv_mapBtn_num).visibility = View.INVISIBLE
//            } else if (item.taskNum >= 100) {
//                helper.getView<TextView>(R.id.tv_mapBtn_num).visibility = View.VISIBLE
//                helper.setText(R.id.tv_mapBtn_num, "99+")
//            } else {
//                helper.getView<TextView>(R.id.tv_mapBtn_num).visibility = View.VISIBLE
//                helper.setText(R.id.tv_mapBtn_num, item.taskNum.toString())
//            }
        }
    }
}