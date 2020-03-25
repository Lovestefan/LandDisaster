package com.zx.landdisaster.module.disaster.func.adapter

import com.zx.landdisaster.R
import com.zx.landdisaster.module.disaster.bean.DisasterPkiaaBean
import com.zx.zxutils.other.QuickAdapter.ZXBaseHolder
import com.zx.zxutils.other.ZXRecyclerAdapter.ZXRecyclerQuickAdapter

/**
 * Created by Xiangb on 2019/3/30.
 * 功能：
 */
class DisasterPkiaaListAdapter(dataBeans: List<DisasterPkiaaBean>) : ZXRecyclerQuickAdapter<DisasterPkiaaBean, ZXBaseHolder>(R.layout.item_pkiaa_view, dataBeans) {
    override fun quickConvert(helper: ZXBaseHolder?, item: DisasterPkiaaBean?) {
        if (helper != null && item != null) {
            helper.setText(R.id.tv_pkiaa_name, item.name)
            helper.setText(R.id.tv_pkiaa_pkiaa, item.pkiaa)
        }
    }
}