package com.zx.landdisaster.module.disaster.func.adapter

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.zx.landdisaster.R
import com.zx.landdisaster.base.bean.KeyValueBean
import com.zx.zxutils.other.QuickAdapter.ZXBaseHolder
import com.zx.zxutils.other.QuickAdapter.ZXQuickAdapter

/**
 * Created by Xiangb on 2019/4/2.
 * 功能：
 */
class HiddenDetailAdapter(dataBeans: List<Pair<String, List<KeyValueBean>>>) : ZXQuickAdapter<Pair<String, List<KeyValueBean>>, ZXBaseHolder>(R.layout.item_hidden_point, dataBeans) {
    override fun convert(helper: ZXBaseHolder?, item: Pair<String, List<KeyValueBean>>?) {
        if (helper != null && item != null) {
            helper.setText(R.id.tv_title, item.first)

            helper.getView<RecyclerView>(R.id.recyclerview).apply {
                layoutManager = LinearLayoutManager(mContext)
                adapter = HiddenDetailItemAdapter(item.second)
            }
        }
    }

    class HiddenDetailItemAdapter(dataBeans: List<KeyValueBean>) : ZXQuickAdapter<KeyValueBean, ZXBaseHolder>(R.layout.item_hidden_point_item, dataBeans) {
        override fun convert(helper: ZXBaseHolder?, item: KeyValueBean?) {
            if (helper != null && item != null) {
                helper.setText(R.id.tv_hidden_key, item.key)
                helper.setText(R.id.tv_hidden_value, item.value)
            }
        }
    }
}

