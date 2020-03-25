package com.zx.landdisaster.module.disaster.func.adapter

import com.zx.landdisaster.R
import com.zx.landdisaster.module.disaster.bean.ReportListBean
import com.zx.zxutils.other.QuickAdapter.ZXBaseHolder
import com.zx.zxutils.other.ZXRecyclerAdapter.ZXRecyclerQuickAdapter

/**
 * Created by Xiangb on 2019/3/22.
 * 功能：
 */
class ReportListAdapter(dataBeans: List<ReportListBean>) : ZXRecyclerQuickAdapter<ReportListBean, ZXBaseHolder>(R.layout.item_report_list, dataBeans) {
    override fun quickConvert(helper: ZXBaseHolder?, item: ReportListBean?) {
        if (helper != null && item != null) {
            helper.setText(R.id.tv_reportList_date, item.hazardname)
            helper.setText(R.id.tv_reportList_type, item.getHazardTypeString())
            helper.setText(R.id.tv_reportList_status, item.currentstepDes)
        }
    }
}