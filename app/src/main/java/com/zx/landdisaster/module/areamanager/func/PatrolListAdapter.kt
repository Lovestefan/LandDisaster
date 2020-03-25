package com.zx.landdisaster.module.areamanager.func

import com.zx.landdisaster.R
import com.zx.landdisaster.base.other.UserManager
import com.zx.landdisaster.module.areamanager.bean.PatrolListBean
import com.zx.landdisaster.module.disaster.func.util.TextSizeUtil
import com.zx.zxutils.other.QuickAdapter.ZXBaseHolder
import com.zx.zxutils.other.ZXRecyclerAdapter.ZXRecyclerQuickAdapter
import com.zx.zxutils.util.ZXTimeUtil

/**
 * Created by Xiangb on 2019/3/19.
 * 功能：
 */
class PatrolListAdapter(dataBeans: List<PatrolListBean>) : ZXRecyclerQuickAdapter<PatrolListBean,
        ZXBaseHolder>(R.layout.item_patvol_list, dataBeans) {
    override fun quickConvert(helper: ZXBaseHolder?, item: PatrolListBean?) {
        if (helper != null && item != null) {
            helper.setText(R.id.tv_patvol_level, getLevel(item.phase))
            helper.setText(R.id.tv_time, ZXTimeUtil.millis2String(item.createtime))
            helper.setText(R.id.tv_content, item.content)

            val roleBean = UserManager.getUser().personRole
            if (!roleBean.groupDefense && !roleBean.areaManager) {
                TextSizeUtil.resetText(mContext!!, helper.getView(R.id.tv_patvol_level))
                TextSizeUtil.resetText(mContext!!, helper.getView(R.id.tv_time))
                TextSizeUtil.resetText(mContext!!, helper.getView(R.id.tv_content))
            }
        }
    }

    fun getLevel(phase: Int): String {
        if (phase == 1)
            return "雨前"
        else if (phase == 2)
            return "雨中"
        else
            return "雨后"
    }
}