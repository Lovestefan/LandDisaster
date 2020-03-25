package com.zx.landdisaster.module.disaster.func.adapter

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.zx.landdisaster.R
import com.zx.landdisaster.module.disaster.bean.AddFileBean
import com.zx.zxutils.other.QuickAdapter.ZXBaseHolder
import com.zx.zxutils.other.QuickAdapter.ZXQuickAdapter

/**
 * Created by Xiangb on 2019/3/19.
 * 功能：
 */
class AddFileAdapter(dataBeans: List<AddFileBean>, var deleteFile: (Int) -> Unit) : ZXQuickAdapter<AddFileBean, ZXBaseHolder>(R.layout.item_report_file, dataBeans) {

    var canDelete = true

    constructor(dataBeans: List<AddFileBean>) : this(dataBeans, {}) {
        canDelete = false
    }

    override fun convert(helper: ZXBaseHolder?, item: AddFileBean?) {
        if (helper != null && item != null) {
            helper.setText(R.id.tv_file_text, item.name)
            if (item.type == 1) {
                helper.setBackgroundRes(R.id.iv_file_type, R.drawable.report_file_image)
                Glide.with(mContext)
                        .load(item.path)
                        .apply(RequestOptions().placeholder(R.drawable.unknown_file))
                        .into(helper.getView(R.id.iv_file_bg))
            } else if (item.type == 2) {
                helper.setBackgroundRes(R.id.iv_file_type, R.drawable.report_file_vedio)
                Glide.with(mContext)
                        .load(item.path)
                        .apply(RequestOptions().placeholder(R.drawable.unknown_file))
                        .into(helper.getView(R.id.iv_file_bg))
            } else {
                helper.setBackgroundRes(R.id.iv_file_type, R.drawable.report_file_file)
                Glide.with(mContext)
                        .load(R.drawable.unknown_file)
                        .apply(RequestOptions().placeholder(R.drawable.unknown_file))
                        .into(helper.getView(R.id.iv_file_bg))
            }
            if (!canDelete) {
                helper.setVisible(R.id.iv_file_delete, false)
            }
            helper.getView<ImageView>(R.id.iv_file_delete).setOnClickListener {
                deleteFile(helper.adapterPosition)
                this.notifyDataSetChanged()
            }
        }
    }
}