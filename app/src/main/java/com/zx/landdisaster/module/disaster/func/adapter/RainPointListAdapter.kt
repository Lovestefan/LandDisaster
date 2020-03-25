package com.zx.landdisaster.module.disaster.func.adapter

import com.zx.landdisaster.R
import com.zx.landdisaster.module.disaster.bean.RainPointBean
import com.zx.zxutils.other.QuickAdapter.ZXBaseHolder
import com.zx.zxutils.other.ZXRecyclerAdapter.ZXRecyclerQuickAdapter

/**
 * Created by Xiangb on 2019/3/19.
 * 功能：
 */
class RainPointListAdapter(dataBeans: List<RainPointBean>) : ZXRecyclerQuickAdapter<RainPointBean,
        ZXBaseHolder>(R.layout.item_rainpoint_list, dataBeans) {
    override fun quickConvert(helper: ZXBaseHolder?, item: RainPointBean?) {
        if (helper != null && item != null) {

//            helper.setText(R.id.tvContent, item.code + "\n" + item.areaname + "  " + xz)
//            helper.setText(R.id.tvContent, item.code + "   " + item.areaname + "   " + xz)
//            helper.setText(R.id.tvContent, (item.areaname + "   " + item.townsname).replace("null", ""))
            try {
                helper.setText(R.id.tvContent, item.areaname.replace("null", ""))
                helper.setText(R.id.tvTownsname, item.townsname.replace("null", ""))
            } catch (e: Exception) {
            }
            if (item.sensorname != null)
                helper.setText(R.id.tvSensorname, item.sensorname)

//
            if (item.vol in 0.0..1.0) {
                helper.setText(R.id.tvVol, item.vol.toString() + "毫米")
//                helper.setBackgroundRes(R.id.color, R.drawable.rain_1)
            } else if (item.vol in 1.0..3.0) {
                helper.setText(R.id.tvVol, item.vol.toString() + "毫米")
//                helper.setBackgroundRes(R.id.color, R.drawable.rain_1)
            } else if (item.vol in 3.0..10.0) {
                helper.setText(R.id.tvVol, item.vol.toString() + "毫米")
//                helper.setBackgroundRes(R.id.color, R.drawable.rain_2)
            } else if (item.vol in 10.0..20.0) {
                helper.setText(R.id.tvVol, item.vol.toString() + "毫米")
//                helper.setBackgroundRes(R.id.color, R.drawable.rain_3)
            } else if (item.vol in 20.0..50.0) {
                helper.setText(R.id.tvVol, item.vol.toString() + "毫米")
//                helper.setBackgroundRes(R.id.color, R.drawable.rain_4)
            } else if (item.vol in 50.0..70.0) {
                helper.setText(R.id.tvVol, item.vol.toString() + "毫米")
//                helper.setBackgroundRes(R.id.color, R.drawable.rain_5)
            } else if (item.vol >= 70.0) {
                helper.setText(R.id.tvVol, item.vol.toString() + "毫米")
//                helper.setBackgroundRes(R.id.color, R.drawable.rain_6)
            }
        }
    }
}