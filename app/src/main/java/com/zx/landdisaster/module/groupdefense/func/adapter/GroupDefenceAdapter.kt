package com.zx.landdisaster.module.groupdefense.func.adapter

import android.app.Activity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.zx.landdisaster.R
import com.zx.landdisaster.module.groupdefense.bean.GroupDefenceBean
import com.zx.landdisaster.module.groupdefense.ui.MacroMonitroFillActivity
import com.zx.landdisaster.module.groupdefense.ui.MacroMonitroListActivity
import com.zx.landdisaster.module.groupdefense.ui.MonitorPatrolFillActivity
import com.zx.landdisaster.module.groupdefense.ui.MonitorPatrolListActivity
import com.zx.zxutils.other.QuickAdapter.ZXBaseHolder
import com.zx.zxutils.other.QuickAdapter.ZXQuickAdapter
import com.zx.zxutils.other.ZXRecyclerAdapter.ZXRecyclerQuickAdapter

/**
 * Created by Xiangb on 2019/4/2.
 * 功能：
 */
class GroupDefenceAdapter(dataBeans: List<GroupDefenceBean>) : ZXRecyclerQuickAdapter<GroupDefenceBean, ZXBaseHolder>(R.layout.item_group_defence, dataBeans) {
    override fun quickConvert(helper: ZXBaseHolder?, item: GroupDefenceBean?) {
        if (helper != null && item != null) {
            helper.setText(R.id.tv_group_defence_name, item.name)

            helper.setText(R.id.tv_group_defence_type, item.type)
            helper.setText(R.id.tv_group_defence_longitude, item.longitude.toString())
            helper.setText(R.id.tv_group_defence_latitude, item.latitude.toString())


            var recyclerView = helper.getView<RecyclerView>(R.id.rv_group_defence_item)
            recyclerView.visibility = View.GONE

            helper.getView<LinearLayout>(R.id.ll_group_defence).setOnClickListener {
                if (helper.getView<RecyclerView>(R.id.rv_group_defence_item).visibility == View.GONE) {

                    recyclerView.visibility = View.VISIBLE

                    if (item.macropatrolset != null && item.macropatrolset!!.pkiaa != null) {
                        helper.getView<LinearLayout>(R.id.ll_macro_patrol).visibility = View.VISIBLE
                    }

                    helper.setBackgroundRes(R.id.iv_expand, R.drawable.ic_group_defence_expand)
                } else {

                    recyclerView.visibility = View.GONE
                    helper.getView<LinearLayout>(R.id.ll_macro_patrol).visibility = View.GONE

                    helper.setBackgroundRes(R.id.iv_expand, R.drawable.ic_group_defence_close)

                }
            }

            if (item.monitorpointinfo != null && item.monitorpointinfo!!.isNotEmpty()) {
                recyclerView.apply {
                    layoutManager = LinearLayoutManager(mContext)
                    adapter = GroupDefenceAdapter.GroupDefenceItemAdapter(item.monitorpointinfo!!, item.name)
                }
            }

            helper.getView<LinearLayout>(R.id.ll_macro_patrol).visibility = View.GONE

            if (item.macropatrolset != null && item.macropatrolset!!.setid != null) {
                helper.setText(R.id.tv_macro_patrol_name, "宏观观测")

                helper.getView<TextView>(R.id.tv_macro_patrol_look).setOnClickListener {
                    MacroMonitroListActivity.startAction(mContext as Activity, false, item.macropatrolset!!.setid!!)
                }

                helper.getView<TextView>(R.id.tv_macro_patrol_fill).setOnClickListener {
                    MacroMonitroFillActivity.startAction(mContext as Activity, false, item.macropatrolset!!, item.latitude, item.longitude)
                }
            }


        }
    }

    class GroupDefenceItemAdapter(dataBeans: List<GroupDefenceBean.Monitorpointinfo>, var taskName: String) : ZXQuickAdapter<GroupDefenceBean.Monitorpointinfo, ZXBaseHolder>(R.layout.item_group_defence_item, dataBeans) {
        override fun convert(helper: ZXBaseHolder?, item: GroupDefenceBean.Monitorpointinfo?) {
            if (helper != null && item != null) {
                helper.setText(R.id.tv_monitor_patrol_name, "定量监测-" + item.name)

                helper.getView<TextView>(R.id.tv_monitor_patrol_look).setOnClickListener {
                    MonitorPatrolListActivity.startAction(mContext as Activity, false, item.mpid, item.name!!)
                }

                helper.getView<TextView>(R.id.tv_monitor_patrol_fill).setOnClickListener {
                    //                    if (item.name2.isNullOrEmpty()) {
//                        item.name2 = taskName
//                    }
                    MonitorPatrolFillActivity.startAction(mContext as Activity, false, item)
                }
            }

        }
    }
}

