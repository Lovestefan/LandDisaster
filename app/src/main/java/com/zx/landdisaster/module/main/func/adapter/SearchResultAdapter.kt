package com.zx.landdisaster.module.main.func.adapter

import com.zx.landdisaster.R
import com.zx.landdisaster.module.main.bean.SearchBean
import com.zx.zxutils.other.QuickAdapter.ZXBaseHolder
import com.zx.zxutils.other.ZXRecyclerAdapter.ZXRecyclerQuickAdapter

/**
 * Created by Xiangb on 2019/3/15.
 * 功能：
 */
class SearchResultAdapter(dataBeans: List<SearchBean>) : ZXRecyclerQuickAdapter<SearchBean, ZXBaseHolder>(R.layout.item_map_search, dataBeans) {
    override fun quickConvert(helper: ZXBaseHolder?, item: SearchBean?) {
        if (helper != null && item != null) {
            helper.setBackgroundRes(R.id.iv_search_type, when (item.type) {
                1 -> R.drawable.search_icon_hidden
                2 -> R.drawable.search_icon_disaster
                3 -> R.drawable.search_icon_disaster
                4 -> R.drawable.search_icon_user
                5 -> R.drawable.search_icon_location
                else -> R.drawable.search_icon_location
            })
            helper.setText(R.id.tv_search_num, (helper.adapterPosition+1).toString())
            helper.setText(R.id.tv_search_name, item.name)
            helper.setText(R.id.tv_search_addr, item.addr)
        }
    }
}