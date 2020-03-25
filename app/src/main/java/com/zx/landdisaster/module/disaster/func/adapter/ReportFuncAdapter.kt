package com.zx.landdisaster.module.disaster.func.adapter

import com.zx.landdisaster.R
import com.zx.landdisaster.module.disaster.bean.ReportFuncBean
import com.zx.zxutils.other.QuickAdapter.ZXBaseHolder
import com.zx.zxutils.other.QuickAdapter.ZXQuickAdapter

/**
 * Created by Xiangb on 2019/3/16.
 * 功能：
 */
class ReportFuncAdapter(dataBeans: List<ReportFuncBean>) : ZXQuickAdapter<ReportFuncBean, ZXBaseHolder>(R.layout.item_report_func, dataBeans) {
    override fun convert(helper: ZXBaseHolder?, item: ReportFuncBean?) {
        if (helper != null && item != null) {
            helper.setText(R.id.tv_report_func_name, item.name)
            helper.setBackgroundRes(R.id.iv_report_func_bg, item.bg)
            helper.setText(R.id.tv_report_func_num, item.num.toString())
            helper.setVisible(R.id.tv_report_func_num, item.num > 0)
        }
    }
}