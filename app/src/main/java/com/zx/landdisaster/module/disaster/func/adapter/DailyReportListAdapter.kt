package com.zx.landdisaster.module.disaster.func.adapter

import com.zx.landdisaster.R
import com.zx.landdisaster.module.worklog.bean.DailyListBean
import com.zx.zxutils.other.QuickAdapter.ZXBaseHolder
import com.zx.zxutils.other.ZXRecyclerAdapter.ZXRecyclerQuickAdapter
import com.zx.zxutils.util.ZXTimeUtil

/**
 * Created by Xiangb on 2019/3/25.
 * 功能：
 */
class DailyReportListAdapter(dataBeans: List<DailyListBean>) : ZXRecyclerQuickAdapter<DailyListBean, ZXBaseHolder>(R.layout.item_daily_report_list, dataBeans) {
    override fun quickConvert(helper: ZXBaseHolder?, item: DailyListBean?) {
        if (helper != null && item != null) {
            helper.setText(R.id.tv_time, ZXTimeUtil.millis2String(item.logtime))
            helper.setText(R.id.tv_name, item.personName)
            helper.setText(R.id.tv_area, item.areaname)
            helper.setText(R.id.tv_content, item.workcontent)
        }
    }
}