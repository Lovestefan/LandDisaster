package com.zx.landdisaster.module.other.func.adapter

import com.zx.landdisaster.R
import com.zx.landdisaster.module.other.bean.CountFuncBean
import com.zx.zxutils.other.QuickAdapter.ZXBaseHolder
import com.zx.zxutils.other.QuickAdapter.ZXQuickAdapter

/**
 * Created by Xiangb on 2019/3/16.
 * 功能：
 */
class CountFuncAdapter(dataBeans: List<CountFuncBean>) : ZXQuickAdapter<CountFuncBean, ZXBaseHolder>(R.layout.item_report_func, dataBeans) {
    override fun convert(helper: ZXBaseHolder?, item: CountFuncBean?) {
        if (helper != null && item != null) {
            helper.setText(R.id.tv_report_func_name, item.name)
            helper.setBackgroundRes(R.id.iv_report_func_bg, item.bg)
        }
    }
}