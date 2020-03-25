package com.zx.landdisaster.module.groupdefense.func.adapter

import android.app.Activity
import com.zx.landdisaster.R
import com.zx.landdisaster.module.groupdefense.bean.MonitorPatrolBean
import com.zx.landdisaster.module.groupdefense.ui.MonitorPatrolDetailsActivity
import com.zx.zxutils.other.QuickAdapter.ZXBaseHolder
import com.zx.zxutils.other.ZXRecyclerAdapter.ZXRecyclerQuickAdapter
import com.zx.zxutils.util.ZXTimeUtil

/**
 * Created by Xiangb on 2019/4/2.
 * 功能：
 */
class MonitorPatrolListAdapter(dataBeans: List<MonitorPatrolBean>) : ZXRecyclerQuickAdapter<MonitorPatrolBean, ZXBaseHolder>(R.layout.item_monitor_patrol, dataBeans) {
    override fun quickConvert(helper: ZXBaseHolder?, item: MonitorPatrolBean?) {
        if (helper != null && item != null) {
            helper.setText(R.id.tv_monitor_patrol_name, item.name)
            helper.setText(R.id.tv_monitor_patrol_createtime, ZXTimeUtil.getTime(item.createtime))
            helper.setText(R.id.tv_monitor_patrol_actualdata, item.actualdata.toString())


            helper.itemView.setOnClickListener {
                MonitorPatrolDetailsActivity.startAction(mContext as Activity, false, item)
            }
        }
    }
}
