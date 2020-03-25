package com.zx.landdisaster.module.main.func.adapter

import com.zx.landdisaster.R
import com.zx.landdisaster.module.main.bean.InfoDeliveryBean
import com.zx.zxutils.other.QuickAdapter.ZXBaseHolder
import com.zx.zxutils.other.ZXRecyclerAdapter.ZXRecyclerQuickAdapter
import com.zx.zxutils.util.ZXTimeUtil

/**
 * Created by zc on 2019/5/23.
 * 功能：
 */
class InfoDelivery1Adapter(dataBeans: List<InfoDeliveryBean>) : ZXRecyclerQuickAdapter<InfoDeliveryBean, ZXBaseHolder>(R.layout.item_info_delivery1, dataBeans) {
    override fun quickConvert(helper: ZXBaseHolder?, item: InfoDeliveryBean?) {
        if (helper != null && item != null) {
            helper.setText(R.id.tv_title, item.servicetitle)
            helper.setText(R.id.tv_content, item.servicesketch)
            helper.setText(R.id.tv_time, getTime(item.publishtime))
            helper.setVisible(R.id.iv_readed, item.readed == -1)

            if (item.servicetype == 1) {
                helper.setImageResource(R.id.img, R.drawable.icon_home_yjxx)
                helper.setBackgroundRes(R.id.img, R.drawable.shape_home1)
            } else if (item.servicetype == 2) {
                helper.setImageResource(R.id.img, R.drawable.icon_home_rbzb)
                helper.setBackgroundRes(R.id.img, R.drawable.shape_home2)
            } else if (item.servicetype == 3) {
                helper.setImageResource(R.id.img, R.drawable.icon_home_hcbg)
                helper.setBackgroundRes(R.id.img, R.drawable.shape_home3)
            } else if (item.servicetype == 4) {
                helper.setImageResource(R.id.img, R.drawable.icon_home_hstz)
                helper.setBackgroundRes(R.id.img, R.drawable.shape_home4)
            } else if (item.servicetype == 5) {
                helper.setImageResource(R.id.img, R.drawable.icon_home_zjdd)
                helper.setBackgroundRes(R.id.img, R.drawable.shape_home5)
            } else if (item.servicetype == 6) {
                helper.setImageResource(R.id.img, R.drawable.icon_home_zcwj)
                helper.setBackgroundRes(R.id.img, R.drawable.shape_home6)
            } else {
                helper.setImageResource(R.id.img, R.drawable.icon_home_sbtj)
                helper.setBackgroundRes(R.id.img, R.drawable.shape_home7)
            }
        }
    }

    fun getTime(createtime: Long): String {
        var time = ZXTimeUtil.millis2String(createtime)
        if (time.length > 12)
            return time.subSequence(0, 13) as String + "时"
        else
            return time
    }
}
