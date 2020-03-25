package com.zx.landdisaster.module.system.func

import com.zx.landdisaster.R
import com.zx.landdisaster.module.system.bean.MessageRecordBean
import com.zx.zxutils.other.QuickAdapter.ZXBaseHolder
import com.zx.zxutils.other.ZXRecyclerAdapter.ZXRecyclerQuickAdapter

/**
 * Created by Xiangb on 2019/3/19.
 * 功能：
 */
class MessageRecordAdapter(dataBeans: List<MessageRecordBean>) : ZXRecyclerQuickAdapter<MessageRecordBean,
        ZXBaseHolder>(R.layout.item_message_list, dataBeans) {
    override fun quickConvert(helper: ZXBaseHolder?, item: MessageRecordBean?) {
        if (helper != null && item != null) {
            helper.setText(R.id.tv_title, item.title)
            helper.setText(R.id.tv_time, item.time)
            helper.setText(R.id.tv_content, item.content)
        }
    }

}