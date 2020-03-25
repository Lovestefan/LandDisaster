package com.zx.landdisaster.module.system.func

import com.zx.landdisaster.R
import com.zx.landdisaster.module.system.bean.VersionRecordBean
import com.zx.zxutils.other.QuickAdapter.ZXBaseHolder
import com.zx.zxutils.other.ZXRecyclerAdapter.ZXRecyclerQuickAdapter

/**
 * Created by Xiangb on 2019/3/19.
 * 功能：
 */
class VersionRecordAdapter(dataBeans: List<VersionRecordBean>) : ZXRecyclerQuickAdapter<VersionRecordBean,
        ZXBaseHolder>(R.layout.item_version_list, dataBeans) {
    override fun quickConvert(helper: ZXBaseHolder?, item: VersionRecordBean?) {
        if (helper != null && item != null) {
            helper.setText(R.id.tv_num, "版本号："+item.versionCode)
            helper.setText(R.id.tv_name, "版本名称："+item.versionName)
            helper.setText(R.id.tv_time, item.updateTime)
            helper.setText(R.id.tv_content, item.content)
        }
    }

}