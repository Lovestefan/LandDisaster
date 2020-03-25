package com.zx.landdisaster.module.worklog.func.adapter

import android.support.v4.content.ContextCompat
import com.zx.landdisaster.R
import com.zx.landdisaster.module.worklog.bean.DailyAuditListBean
import com.zx.zxutils.other.QuickAdapter.ZXBaseHolder
import com.zx.zxutils.other.ZXRecyclerAdapter.ZXRecyclerQuickAdapter
import com.zx.zxutils.util.ZXTimeUtil

/**
 * Created by Xiangb on 2019/3/25.
 * 功能：
 */
class DailyAuditListAdapter(dataBeans: List<DailyAuditListBean>) : ZXRecyclerQuickAdapter<DailyAuditListBean,
        ZXBaseHolder>(R.layout.item_daily_list, dataBeans) {
    override fun quickConvert(helper: ZXBaseHolder?, item: DailyAuditListBean?) {
        if (helper != null && item != null) {
            helper.setText(R.id.tv_daily_date, ZXTimeUtil.millis2String(item.logtime))
            helper.setText(R.id.tv_daily_type, item.logpersonname)
            if (item.status == -1) {
                helper.setText(R.id.tv_daily_status, "驳回")
                helper.setTextColor(R.id.tv_daily_status, ContextCompat.getColor(mContext, R.color.orange))
            } else if (item.status == 1) {
                helper.setText(R.id.tv_daily_status, "已入库")
                helper.setTextColor(R.id.tv_daily_status, ContextCompat.getColor(mContext, R.color.green))
            } else if (item.status == -2) {
                helper.setText(R.id.tv_daily_status, "已终止")
                helper.setTextColor(R.id.tv_daily_status, ContextCompat.getColor(mContext, R.color.red))
            } else {
                helper.setText(R.id.tv_daily_status, "待审核")
                helper.setTextColor(R.id.tv_daily_status, ContextCompat.getColor(mContext, R.color.text_color_normal))
            }
        }
    }
}