package com.zx.landdisaster.module.worklog.func.adapter

import com.zx.landdisaster.R
import com.zx.landdisaster.module.worklog.bean.WorkLogListBean
import com.zx.zxutils.other.QuickAdapter.ZXBaseHolder
import com.zx.zxutils.other.ZXRecyclerAdapter.ZXRecyclerQuickAdapter
import com.zx.zxutils.util.ZXTimeUtil

/**
 * Created by Xiangb on 2019/4/19.
 * 功能：
 */
class WorkLogListAdapter(dataBeans: List<WorkLogListBean>) : ZXRecyclerQuickAdapter<WorkLogListBean, ZXBaseHolder>(R.layout.item_work_log, dataBeans) {
    override fun quickConvert(helper: ZXBaseHolder?, item: WorkLogListBean?) {
        if (helper != null && item != null) {
            helper.setText(R.id.tv_worklog_date, ZXTimeUtil.millis2String(item.reporttime))
            helper.setText(R.id.tv_worklog_type, "")
            helper.setText(R.id.tv_worklog_status, "工作日志")
        }
    }
}