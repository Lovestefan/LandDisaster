package com.zx.landdisaster.module.disaster.func.adapter

import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.zx.landdisaster.R
import com.zx.landdisaster.api.ApiConfigModule
import com.zx.landdisaster.base.other.UserManager
import com.zx.landdisaster.module.disaster.bean.QueryPersonBean
import com.zx.zxutils.other.QuickAdapter.ZXBaseHolder
import com.zx.zxutils.other.QuickAdapter.ZXQuickAdapter
import com.zx.zxutils.util.ZXDialogUtil
import com.zx.zxutils.util.ZXSystemUtil

/**
 * Created by Xiangb on 2019/4/15.
 * 功能：
 */
class HiddenPersonAdapter(dataBeans: List<QueryPersonBean.PersonBean>) : ZXQuickAdapter<QueryPersonBean.PersonBean, ZXBaseHolder>(R.layout.item_hidden_person, dataBeans) {
    override fun convert(helper: ZXBaseHolder?, item: QueryPersonBean.PersonBean?) {
        if (helper != null && item != null) {
            helper.setText(R.id.tv_hidden_personname, item.name)
            helper.setText(R.id.tv_hidden_personphone, item.telephone)
            if (item.identities != null) {
                helper.setText(R.id.tv_hidden_personrole, UserManager.getUserRoleName(item.identities!!).trim())
            } else {
                helper.setText(R.id.tv_hidden_personrole, "")
            }
            Glide.with(mContext)
                    .load(ApiConfigModule.BASE_IP + ApiConfigModule.URL_FILE + "person/personHeader/" + item.personid)
                    .apply(RequestOptions().error(R.drawable.default_headicon))
                    .into(helper.getView(R.id.iv_hidden_headicon))
            helper.getView<TextView>(R.id.tv_hidden_personphone).setOnClickListener {
                ZXDialogUtil.showYesNoDialog(mContext, "提示", "是否拨打电话：${item.telephone}") { dialog, which ->
                    ZXSystemUtil.callTo(mContext, item.telephone)
                }
            }
        }
    }
}