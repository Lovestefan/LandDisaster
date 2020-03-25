package com.zx.landdisaster.module.groupdefense.func.adapter

import android.widget.CheckBox
import com.bumptech.glide.Glide
import com.zx.landdisaster.R
import com.zx.landdisaster.module.groupdefense.bean.MacroMonitroCurrentTypeBean
import com.zx.landdisaster.module.groupdefense.entity.MacroMonitroFillEntity
import com.zx.zxutils.other.QuickAdapter.ZXBaseHolder
import com.zx.zxutils.other.QuickAdapter.ZXQuickAdapter

/**
 * Created by Xiangb on 2019/4/2.
 * 功能：
 */
class MacroMonitroFillAdapter(dataBeans: List<MacroMonitroCurrentTypeBean>) : ZXQuickAdapter<MacroMonitroCurrentTypeBean, ZXBaseHolder>(R.layout.item_macro_monitro_fill, dataBeans) {

    override fun convert(helper: ZXBaseHolder?, item: MacroMonitroCurrentTypeBean?) {


        if (helper != null && item != null) {
            helper.setText(R.id.tv_macro_monitro_fill_title, item.desc)
            var checkBox = helper.getView<CheckBox>(R.id.cb_macro_monitro_fill_upload)



            checkBox.isChecked = (item.exist == 1)

            checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    item.exist = 1
                } else {
                    item.exist = -1
                }
            }

            if (item.fileBean != null) {
                Glide.with(mContext)
                        .load(item.fileBean!!.path)
                        .into(helper.getView(R.id.iv_iamge))

                helper.setVisible(R.id.iv_file_delete, true)

            } else {
                Glide.with(mContext)
                        .load(R.drawable.ic_group_defence_image)
                        .into(helper.getView(R.id.iv_iamge))

                helper.setVisible(R.id.iv_file_delete, false)
            }


            helper.addOnClickListener(R.id.iv_iamge)

            helper.addOnClickListener(R.id.iv_camera)

            helper.addOnClickListener(R.id.iv_file_delete)
        }
    }


}

