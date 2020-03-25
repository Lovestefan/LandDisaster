package com.zx.landdisaster.module.disaster.func.adapter

import android.graphics.Paint
import android.widget.TextView
import com.zx.landdisaster.R
import com.zx.landdisaster.module.disaster.bean.ReportstatisBean
import com.zx.zxutils.other.QuickAdapter.ZXBaseHolder
import com.zx.zxutils.other.ZXRecyclerAdapter.ZXRecyclerQuickAdapter

/**
 * Created by zc on 2019/5/23.
 * 功能：
 */
class ReportStatisAdapter(dataBeans: List<ReportstatisBean>) : ZXRecyclerQuickAdapter<ReportstatisBean,
        ZXBaseHolder>(R.layout.item_report_statis, dataBeans) {

    override fun quickConvert(helper: ZXBaseHolder?, item: ReportstatisBean?) {
        if (helper != null && item != null) {
            if (item.areaname != null && item.areaname != "")
                helper.setText(R.id.tv_area, item.areaname)
            else
                if (item.streetname != null && item.streetname != "")
                    helper.setText(R.id.tv_area, item.streetname)

            if (item.needreport != null)
                helper.setText(R.id.tv_count, item.needreport)
            else
                helper.setText(R.id.tv_count, "0")

            if (item.reportcount != null)
                helper.setText(R.id.tv_ysb, item.reportcount)
            else
                helper.setText(R.id.tv_ysb, "0")

            if (item.noreport != null)
                helper.setText(R.id.tv_wsb, item.noreport)
            else
                helper.setText(R.id.tv_wsb, "0")

            if (item.reportRate != null)
                helper.setText(R.id.tv_sbl, item.reportRate + "%")
            else
                helper.setText(R.id.tv_sbl, "0%")

            //已上报
            if (item.reportcount != null && item.reportcount.toInt() > 0) {
                helper.addOnClickListener(R.id.tv_ysb)
                helper.getView<TextView>(R.id.tv_ysb).paint.flags = Paint.UNDERLINE_TEXT_FLAG //下划线
                helper.getView<TextView>(R.id.tv_ysb).paint.isAntiAlias = true//抗锯齿
            }
            if (type == "1") {//区县日报
                //未上报
                if (item.noreport != null && item.noreport.toInt() > 0) {
                    helper.addOnClickListener(R.id.tv_wsb)
                    helper.getView<TextView>(R.id.tv_wsb).paint.flags = Paint.UNDERLINE_TEXT_FLAG //下划线
                    helper.getView<TextView>(R.id.tv_wsb).paint.isAntiAlias = true//抗锯齿
                }
            }
        }
    }

    private var type = ""
    fun setTypes(t: String) {
        type = t
    }
}
