package com.zx.landdisaster.module.disaster.func.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.widget.ImageView
import com.zx.landdisaster.R
import com.zx.landdisaster.base.other.UserManager
import com.zx.landdisaster.module.disaster.bean.AuditListBean
import com.zx.zxutils.other.QuickAdapter.ZXBaseHolder
import com.zx.zxutils.other.ZXRecyclerAdapter.ZXRecyclerQuickAdapter
import com.zx.zxutils.util.ZXTimeUtil

/**
 * Created by Xiangb on 2019/3/19.
 * 功能：
 */
class AuditListAdapter(dateBeans: List<AuditListBean>) : ZXRecyclerQuickAdapter<AuditListBean, ZXBaseHolder>(R.layout.item_point_list, dateBeans) {
    override fun quickConvert(helper: ZXBaseHolder?, item: AuditListBean?) {
        if (helper != null && item != null) {
            helper.setText(R.id.tv_pointList_name, item.hazardname)
            helper.setText(R.id.tv_pointList_address, item.address)
            if (item.hasreview !== null) {
                val typeString = if (UserManager.getUser().personRole.dispatch || UserManager.getUser().personRole.leader) {
                    "审阅"
                } else {
                    "审核"
                }
                if (item.hasreview == "1") {
                    helper.setText(R.id.tv_scalelevel, "已$typeString")
                    helper.setVisible(R.id.iv_scalelevel, false)
                } else if (item.hasreview == "-1") {
                    helper.setText(R.id.tv_scalelevel, "未$typeString")
                    helper.setVisible(R.id.iv_scalelevel, false)
                }else{
                    helper.setText(R.id.tv_scalelevel, "")
                    (helper.getView<ImageView>(R.id.iv_scalelevel).background as GradientDrawable).setColor(Color.WHITE)
                }
            } else if (item.scalelevel != null) {
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
            if (item.happentime != null && item.happentime!!.isNotEmpty()) {
                helper.setText(R.id.tv_date, ZXTimeUtil.getTime(item.happentime!!.toLong()))
            } else {
                helper.setText(R.id.tv_date, "")
            }
        }
    }
}