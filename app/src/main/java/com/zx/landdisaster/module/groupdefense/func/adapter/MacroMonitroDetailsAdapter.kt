package com.zx.landdisaster.module.groupdefense.func.adapter

import com.zx.landdisaster.R
import com.zx.zxutils.other.QuickAdapter.ZXBaseHolder
import com.zx.zxutils.other.QuickAdapter.ZXQuickAdapter

/**
 * Created by Xiangb on 2019/4/2.
 * 功能：
 */
class MacroMonitroDetailsAdapter(dataBeans: List<Pair<String, String>>) : ZXQuickAdapter<Pair<String, String>, ZXBaseHolder>(R.layout.item_macro_monitro_detailsl, dataBeans) {
    override fun convert(helper: ZXBaseHolder?, item: Pair<String, String>?) {
        if (helper != null && item != null) {
            helper.setText(R.id.tv_monitor_patrol_key, item.first)
            helper.setText(R.id.tv_monitor_patrol_value, item.second)
        }
    }
}
