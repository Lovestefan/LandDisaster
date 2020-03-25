package com.zx.landdisaster.module.worklog.func.adapter

import android.support.v4.content.ContextCompat
import com.zx.landdisaster.R
import com.zx.landdisaster.module.worklog.bean.DailyListBean
import com.zx.zxutils.other.QuickAdapter.ZXBaseHolder
import com.zx.zxutils.other.ZXRecyclerAdapter.ZXRecyclerQuickAdapter
import com.zx.zxutils.util.ZXTimeUtil

/**
 * Created by Xiangb on 2019/3/25.
 * 功能：
 */
class DailyListAdapter(dataBeans: List<DailyListBean>) : ZXRecyclerQuickAdapter<DailyListBean, ZXBaseHolder>(R.layout.item_daily_list, dataBeans) {
    override fun quickConvert(helper: ZXBaseHolder?, item: DailyListBean?) {
        if (helper != null && item != null) {
            helper.setText(R.id.tv_daily_date, ZXTimeUtil.millis2String(item.logtime))
            helper.setText(R.id.tv_daily_type, "")
            if (item.status.equals("-1")) {
                helper.setText(R.id.tv_daily_status, "驳回")
                helper.setTextColor(R.id.tv_daily_status, ContextCompat.getColor(mContext, R.color.red))
            } else if (item.status.equals("0")) {
                helper.setText(R.id.tv_daily_status, "审核中")
                helper.setTextColor(R.id.tv_daily_status, ContextCompat.getColor(mContext, R.color.orange))
            } else
                helper.setText(R.id.tv_daily_status, "已入库")
        }
    }
}