package com.zx.landdisaster.module.disaster.func.adapter

import com.zx.landdisaster.R
import com.zx.landdisaster.module.disaster.bean.ReportPianQuDetailBean
import com.zx.zxutils.other.QuickAdapter.ZXBaseHolder
import com.zx.zxutils.other.ZXRecyclerAdapter.ZXRecyclerQuickAdapter

/**
 * Created by zc on 2019/5/23.
 * 功能：
 */
class ReportStatisAreaManageDetailAdapter(dataBeans: List<ReportPianQuDetailBean>) : ZXRecyclerQuickAdapter<ReportPianQuDetailBean,
        ZXBaseHolder>(R.layout.item_report_statis_pqzb_detail, dataBeans) {
    override fun quickConvert(helper: ZXBaseHolder?, item: ReportPianQuDetailBean?) {
        if (helper != null && item != null) {
            var xz = ""
            if (item.managetown != null)
                xz = item.managetown
            helper.setText(R.id.tv_sbr, item.reportor + "  " + xz)

            if (item.neednum != null)
                helper.setText(R.id.tv_ysb, item.neednum)
            else
                helper.setText(R.id.tv_ysb, "0")

            if (item.alreadynum != null)
                helper.setText(R.id.tv_sjsb, item.alreadynum)
            else
                helper.setText(R.id.tv_sjsb, "0")

            if (item.noReportnum != null)
                helper.setText(R.id.tv_wsb, item.noReportnum)
            else
                helper.setText(R.id.tv_wsb, "0")

            if (item.reportRate != null)
                helper.setText(R.id.tv_sbl, item.reportRate + "%")
            else
                helper.setText(R.id.tv_sbl, "0%")


        }
    }

}
