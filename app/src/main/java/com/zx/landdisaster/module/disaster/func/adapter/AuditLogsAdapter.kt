package com.zx.landdisaster.module.disaster.func.adapter

import com.zx.landdisaster.R
import com.zx.landdisaster.module.disaster.bean.AuditLogsBean
import com.zx.zxutils.other.QuickAdapter.ZXBaseHolder
import com.zx.zxutils.other.ZXRecyclerAdapter.ZXRecyclerQuickAdapter

/**
 * Created by Xiangb on 2019/3/22.
 * 功能：
 */
class AuditLogsAdapter(dataBeans: List<AuditLogsBean>) : ZXRecyclerQuickAdapter<AuditLogsBean, ZXBaseHolder>(R.layout.item_audit_log, dataBeans) {
    override fun quickConvert(helper: ZXBaseHolder?, item: AuditLogsBean?) {
        if (helper != null && item != null) {
            if (item.logtype == 1) {//上报
                helper.setText(R.id.tv_log_dateName, "提交时间：")
                helper.setText(R.id.tv_log_deptName, "提交单位：")
                helper.setText(R.id.tv_log_personName, "提交人：")
                helper.setText(R.id.tv_log_noteName, "提交描述：")
                helper.setText(R.id.tv_log_dateValue, item.reporttime)
                helper.setText(R.id.tv_log_deptValue, item.reportdept)
                helper.setText(R.id.tv_log_personValue, "${item.reportor}（${if (item.reportphone != null) item.reportphone else ""}）")
                helper.setVisible(R.id.iv_log_fromapp, !item.reportimei.isNullOrEmpty())
            } else {//审核
                helper.setText(R.id.tv_log_dateName, "审核时间：")
                helper.setText(R.id.tv_log_deptName, "审核单位：")
                helper.setText(R.id.tv_log_personName, "审核人：")
                helper.setText(R.id.tv_log_noteName, "审核描述：")
                helper.setText(R.id.tv_log_dateValue, item.aduittime)
                helper.setText(R.id.tv_log_deptValue, item.aduitdept)
                helper.setText(R.id.tv_log_personValue, "${item.aduitor}（${if (item.auditphone != null) item.auditphone else ""}）")
                helper.setVisible(R.id.iv_log_fromapp, !item.auditimei.isNullOrEmpty())
            }
            helper.setText(R.id.tv_log_noteValue, item.result + if (item.auditopinion.isNullOrEmpty()) "" else "---" + item.auditopinion)
        }
    }
}