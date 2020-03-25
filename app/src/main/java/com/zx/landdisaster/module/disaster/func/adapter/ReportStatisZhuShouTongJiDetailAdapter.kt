package com.zx.landdisaster.module.disaster.func.adapter

import com.zx.landdisaster.R
import com.zx.landdisaster.module.worklog.bean.WorkLogListBean
import com.zx.zxutils.other.QuickAdapter.ZXBaseHolder
import com.zx.zxutils.other.ZXRecyclerAdapter.ZXRecyclerQuickAdapter
import com.zx.zxutils.util.ZXTimeUtil

/**
 * Created by zc on 2019/5/23.
 * 功能：
 */
class ReportStatisZhuShouTongJiDetailAdapter(dataBeans: List<WorkLogListBean>) : ZXRecyclerQuickAdapter<WorkLogListBean,
        ZXBaseHolder>(R.layout.item_report_statis_zsrz_tj_detail, dataBeans) {
    override fun quickConvert(helper: ZXBaseHolder?, item: WorkLogListBean?) {
        if (helper != null && item != null) {
            if (item.recordername != null)
                helper.setText(R.id.tv_recordername, "记录人：" + item.recordername)

            if (item.reporttime != null)
                helper.setText(R.id.tv_reporttime, ZXTimeUtil.getTime(item.reporttime))

            if (item.onduty != null)
                helper.setText(R.id.tv_onduty, "在岗情况：" + item.onduty)

            if (item.worktype != null)
                helper.setText(R.id.tv_worktype, "工作类型：" + item.worktype)

            if (item.content != null)
                helper.setText(R.id.tv_content, "日志内容：" + item.content)

            if (item.note != null)
                helper.setText(R.id.tv_note, "备注：" + item.note)

        }
    }

}
