package com.zx.landdisaster.module.groupdefense.func.adapter

import android.app.Activity
import com.zx.landdisaster.R
import com.zx.landdisaster.module.groupdefense.bean.MacroMonitroBean
import com.zx.landdisaster.module.groupdefense.ui.MacroMonitroDetailsActivity
import com.zx.zxutils.other.QuickAdapter.ZXBaseHolder
import com.zx.zxutils.other.ZXRecyclerAdapter.ZXRecyclerQuickAdapter
import com.zx.zxutils.util.ZXTimeUtil

/**
 * Created by Xiangb on 2019/4/2.
 * 功能：
 */
class MacroMonitroListAdapter(dataBeans: List<MacroMonitroBean>) : ZXRecyclerQuickAdapter<MacroMonitroBean, ZXBaseHolder>(R.layout.item_macro_monitro, dataBeans) {
    override fun quickConvert(helper: ZXBaseHolder?, item: MacroMonitroBean?) {
        if (helper != null && item != null) {
            helper.setText(R.id.tv_macro_monitro_name, item.pkiaaname)
            helper.setText(R.id.tv_macro_monitro_createtime, ZXTimeUtil.getTime(item.createtime))
            helper.setText(R.id.tv_macro_monitro_macroappear, item.macroappear)

            helper.itemView.setOnClickListener {
                MacroMonitroDetailsActivity.startAction(mContext as Activity, false, item)
            }
        }
    }
}

