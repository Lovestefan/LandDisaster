package com.zx.landdisaster.module.areamanager.func

import com.zx.landdisaster.R
import com.zx.landdisaster.module.areamanager.bean.WeekWorkListBean
import com.zx.zxutils.other.QuickAdapter.ZXBaseHolder
import com.zx.zxutils.other.ZXRecyclerAdapter.ZXRecyclerQuickAdapter
import com.zx.zxutils.util.ZXTimeUtil

/**
 * Created by Xiangb on 2019/3/19.
 * 功能：
 */
class WeekWorkListAdapter(dataBeans: List<WeekWorkListBean>) : ZXRecyclerQuickAdapter<WeekWorkListBean,
        ZXBaseHolder>(R.layout.item_weekwork_list, dataBeans) {
    override fun quickConvert(helper: ZXBaseHolder?, item: WeekWorkListBean?) {
        if (helper != null && item != null) {
            helper.setText(R.id.tv_name_time, item.townsname + "\n" + ZXTimeUtil.millis2String(item.createtime))
        }
    }
}