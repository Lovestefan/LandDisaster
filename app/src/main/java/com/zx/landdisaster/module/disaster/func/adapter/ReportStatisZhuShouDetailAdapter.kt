package com.zx.landdisaster.module.disaster.func.adapter

import android.graphics.Paint
import android.widget.TextView
import com.zx.landdisaster.R
import com.zx.landdisaster.module.disaster.bean.ReportZhuShouDetailBean
import com.zx.zxutils.other.QuickAdapter.ZXBaseHolder
import com.zx.zxutils.other.ZXRecyclerAdapter.ZXRecyclerQuickAdapter

/**
 * Created by zc on 2019/5/23.
 * 功能：
 */
class ReportStatisZhuShouDetailAdapter(dataBeans: List<ReportZhuShouDetailBean>) : ZXRecyclerQuickAdapter<ReportZhuShouDetailBean,
        ZXBaseHolder>(R.layout.item_report_statis_zsrz_detail, dataBeans) {
    override fun quickConvert(helper: ZXBaseHolder?, item: ReportZhuShouDetailBean?) {
        if (helper != null && item != null) {
            if (item.reportor != null)
                helper.setText(R.id.tv_sbr, item.reportor)

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

            if (item.managetown != null)
                helper.setText(R.id.tv_xiangzhen, "驻守乡镇：" + item.managetown)

            //有效上报
            if (item.alreadynum != null && item.alreadynum.toInt() > 0) {
                helper.addOnClickListener(R.id.tv_sjsb)
                helper.getView<TextView>(R.id.tv_sjsb).paint.flags = Paint.UNDERLINE_TEXT_FLAG //下划线
                helper.getView<TextView>(R.id.tv_sjsb).paint.isAntiAlias = true//抗锯齿
            }
        }
    }

}
