package com.zx.landdisaster.module.disaster.func.adapter

import com.zx.landdisaster.R
import com.zx.landdisaster.module.disaster.bean.ReportStatisGroupDetailBean
import com.zx.zxutils.other.QuickAdapter.ZXBaseHolder
import com.zx.zxutils.other.ZXRecyclerAdapter.ZXRecyclerQuickAdapter

/**
 * Created by zc on 2019/5/23.
 * 功能：
 */
class ReportStatisGroupDetailAdapter(dataBeans: List<ReportStatisGroupDetailBean>) : ZXRecyclerQuickAdapter<ReportStatisGroupDetailBean,
        ZXBaseHolder>(R.layout.item_report_statis_detail, dataBeans) {
    override fun quickConvert(helper: ZXBaseHolder?, item: ReportStatisGroupDetailBean?) {
        if (helper != null && item != null) {
            helper.setText(R.id.tv_area, item.orgname + "  " + item.town)

            //定量监测
            if (item.rationrealnum != null)
                helper.setText(R.id.tv_count, item.rationrealnum)
            if (item.rationneednum != null)
                helper.setText(R.id.tv_ysb, item.rationneednum)
            if (item.rationReportRate != null)
                helper.setText(R.id.tv_sbl, item.rationReportRate + "%")
            else
                helper.setText(R.id.tv_sbl, "0%")

            //宏观巡查
            if (item.macroneednum != null)
                helper.setText(R.id.tv_count_hgxc, item.macroneednum)
            if (item.macrorealnum != null)
                helper.setText(R.id.tv_ysb_hgxc, item.macrorealnum)
            if (item.macroReportRate != null)
                helper.setText(R.id.tv_sbl_hgxc, item.macroReportRate + "%")
            else
                helper.setText(R.id.tv_sbl_hgxc, "0%")

            //合法率
            if (item.legalnum != null)
                helper.setText(R.id.tv_hfs, item.legalnum)
            if (item.inlegalnum != null)
                helper.setText(R.id.tv_bhfs, item.inlegalnum)
            if (item.legalRate != null)
                helper.setText(R.id.tv_hfl, item.legalRate + "%")
            else
                helper.setText(R.id.tv_hfl, "0%")

            //灾害点
            if (item.disasternum != null)
                helper.setText(R.id.tv_zhd, item.disasternum)
            //监测点
            if (item.monitornum != null)
                helper.setText(R.id.tv_jcd, item.monitornum)
        }
    }

}
