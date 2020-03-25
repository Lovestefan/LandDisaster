package com.zx.landdisaster.module.disaster.func.adapter

import com.zx.landdisaster.R
import com.zx.landdisaster.module.disaster.bean.ReportStatisGroupdeBean
import com.zx.zxutils.other.QuickAdapter.ZXBaseHolder
import com.zx.zxutils.other.ZXRecyclerAdapter.ZXRecyclerQuickAdapter

/**
 * Created by zc on 2019/5/23.
 * 功能：
 */
class ReportStatisGroupdeAdapter(dataBeans: List<ReportStatisGroupdeBean>) : ZXRecyclerQuickAdapter<ReportStatisGroupdeBean,
        ZXBaseHolder>(R.layout.item_report_statis1, dataBeans) {
    override fun quickConvert(helper: ZXBaseHolder?, item: ReportStatisGroupdeBean?) {
        if (helper != null && item != null) {
            helper.setText(R.id.tv_area, item.streetname)

            //定量监测
            if (item.rationrealnum != null)
                helper.setText(R.id.tv_count, item.rationrealnum)
            if (item.rationneednum != null)
                helper.setText(R.id.tv_ysb, item.rationneednum)
            if (item.noreport != null)
                helper.setText(R.id.tv_wsb, item.noreport)
            if (item.rationReportRate != null)
                helper.setText(R.id.tv_sbl, item.rationReportRate + "%")
            else
                helper.setText(R.id.tv_sbl, "0%")

            //宏观巡查
            if (item.macroneednum != null)
                helper.setText(R.id.tv_count_hgxc, item.macroneednum)
            if (item.macrorealnum != null)
                helper.setText(R.id.tv_ysb_hgxc, item.macrorealnum)
            if (item.noMacrorealnum != null)
                helper.setText(R.id.tv_wsb_hgxc, item.noMacrorealnum)
            if (item.macroReportRate != null)
                helper.setText(R.id.tv_sbl_hgxc, item.macroReportRate + "%")
            else
                helper.setText(R.id.tv_sbl_hgxc, "0%")
        }
    }

}
