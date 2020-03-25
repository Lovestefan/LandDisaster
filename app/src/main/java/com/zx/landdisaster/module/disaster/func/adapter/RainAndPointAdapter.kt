package com.zx.landdisaster.module.disaster.func.adapter

import com.zx.landdisaster.R
import com.zx.landdisaster.module.main.bean.LegendBean
import com.zx.zxutils.other.QuickAdapter.ZXBaseHolder
import com.zx.zxutils.other.QuickAdapter.ZXQuickAdapter

/**
 * Created by zc on 2019/5/23.
 * 功能：乡镇/雨量站
 */
class RainAndPointAdapter(dataBeans: List<LegendBean>) : ZXQuickAdapter<LegendBean, ZXBaseHolder>(R.layout.item_rain_and_point, dataBeans) {
    override fun convert(helper: ZXBaseHolder?, item: LegendBean?) {
        if (helper != null && item != null) {
            helper.setText(R.id.tv_data, item.text)
            helper.setImageResource(R.id.iv_img, item.bg)
        }
    }
}
