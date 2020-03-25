package com.zx.landdisaster.module.main.func.adapter

import com.zx.landdisaster.R
import com.zx.landdisaster.module.main.bean.Area
import com.zx.zxutils.other.QuickAdapter.ZXBaseHolder
import com.zx.zxutils.other.QuickAdapter.ZXQuickAdapter
import java.lang.Exception


/**
 * Created by zc on 2019/5/23.
 * 功能：
 */
class InfoDeliveryReportAdapter(dataBeans: List<Area>) : ZXQuickAdapter<Area, ZXBaseHolder>(R.layout.item_info_delivery_report, dataBeans) {
    override fun convert(helper: ZXBaseHolder?, item: Area?) {
        if (helper != null && item != null) {
            helper.setText(R.id.area, item.area)
            helper.setText(R.id.ringstand, getData(item.ringstand))
            helper.setText(R.id.groupdefense, getData(item.groupdefense))
            helper.setText(R.id.garrison, getData(item.garrison))
            helper.setText(R.id.areamanager, getData(item.areamanager))
        }
    }

    fun getData(str: String): String {
        try {
            return Math.floor(str.toDouble() * 100).toString() + "%"
        } catch (e: Exception) {
            return str
        }
    }
}