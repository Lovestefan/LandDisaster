package com.zx.landdisaster.module.disaster.func.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.widget.ImageView
import com.zx.landdisaster.R
import com.zx.landdisaster.module.main.bean.HiddenPointBean
import com.zx.zxutils.other.QuickAdapter.ZXBaseHolder
import com.zx.zxutils.other.ZXRecyclerAdapter.ZXRecyclerQuickAdapter

/**
 * Created by Xiangb on 2019/3/19.
 * 功能：
 */
class HiddenPointListAdapter(dateBeans: List<HiddenPointBean>) : ZXRecyclerQuickAdapter<HiddenPointBean, ZXBaseHolder>(R.layout.item_point_list, dateBeans) {
    override fun quickConvert(helper: ZXBaseHolder?, item: HiddenPointBean?) {
        if (helper != null && item != null) {
            helper.setText(R.id.tv_pointList_name, item.name)
            helper.setText(R.id.tv_pointList_address, item.address)
            helper.setText(R.id.tv_date, item.type)
            if (item.scalelevel != null) {
                helper.setVisible(R.id.iv_scalelevel, true)
                when (item.scalelevel) {
                    "A" -> {
                        helper.setText(R.id.tv_scalelevel, "特大型")
                        (helper.getView<ImageView>(R.id.iv_scalelevel).background as GradientDrawable).setColor(Color.RED)
                    }
                    "B" -> {
                        helper.setText(R.id.tv_scalelevel, "大型")
                        (helper.getView<ImageView>(R.id.iv_scalelevel).background as GradientDrawable).setColor(Color.YELLOW)
                    }
                    "C" -> {
                        helper.setText(R.id.tv_scalelevel, "小型")
                        (helper.getView<ImageView>(R.id.iv_scalelevel).background as GradientDrawable).setColor(Color.GREEN)
                    }
                    "D" -> {
                        helper.setText(R.id.tv_scalelevel, "中型")
                        (helper.getView<ImageView>(R.id.iv_scalelevel).background as GradientDrawable).setColor(Color.BLUE)
                    }
                    else -> {
                        helper.setText(R.id.tv_scalelevel, "")
                        (helper.getView<ImageView>(R.id.iv_scalelevel).background as GradientDrawable).setColor(Color.WHITE)
                    }
                }
            } else {
                helper.setText(R.id.tv_scalelevel, "")
                helper.setVisible(R.id.iv_scalelevel, false)
            }
//            if (item. != null && item.happentime!!.isNotEmpty()) {
//                helper.setText(R.id.tv_date, ZXTimeUtil.getTime(item.happentime!!.toLong()))
//            } else {
//                helper.setText(R.id.tv_date, "")
//            }
        }
    }
}