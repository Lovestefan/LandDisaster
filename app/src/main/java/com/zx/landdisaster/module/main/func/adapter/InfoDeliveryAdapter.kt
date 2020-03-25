package com.zx.landdisaster.module.main.func.adapter

import com.zx.landdisaster.R
import com.zx.landdisaster.module.main.bean.InfoDeliveryBean
import com.zx.zxutils.other.QuickAdapter.ZXBaseHolder
import com.zx.zxutils.other.QuickAdapter.ZXQuickAdapter
import com.zx.zxutils.util.ZXTimeUtil

/**
 * Created by zc on 2019/5/23.
 * 功能：
 */
class InfoDeliveryAdapter(dataBeans: List<InfoDeliveryBean>) : ZXQuickAdapter<InfoDeliveryBean, ZXBaseHolder>(R.layout.item_info_delivery, dataBeans) {
    override fun convert(helper: ZXBaseHolder?, item: InfoDeliveryBean?) {
        if (helper != null && item != null) {
            helper.setText(R.id.tv_msg, item.servicetitle)
            helper.setText(R.id.tv_time, getTime(item.publishtime))
            if (item.readed == -1)
                helper.setImageResource(R.id.iv_msg, R.drawable.icon_home_msg_wd)
            else
                helper.setImageResource(R.id.iv_msg, R.drawable.icon_home_msg)
        }
    }

    fun getTime(createtime: Long): String {
        var time = ZXTimeUtil.millis2String(createtime)
        if (time.length > 10)
            return time.subSequence(0, 10).toString()
        else
            return time
    }
}
