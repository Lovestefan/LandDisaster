package com.zx.landdisaster.module.worklog.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.zx.landdisaster.R
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.module.disaster.bean.AddFileBean
import com.zx.landdisaster.module.disaster.bean.AuditReportFileBean
import com.zx.landdisaster.module.other.ui.AddFileFragment
import com.zx.landdisaster.module.worklog.bean.DailyAuditListBean

import com.zx.landdisaster.module.worklog.mvp.contract.DailyAuditContract
import com.zx.landdisaster.module.worklog.mvp.model.DailyAuditModel
import com.zx.landdisaster.module.worklog.mvp.presenter.DailyAuditPresenter
import com.zx.zxutils.util.ZXFragmentUtil
import com.zx.zxutils.util.ZXTimeUtil
import kotlinx.android.synthetic.main.activity_daily_audit.*


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class DailyAuditActivity : BaseActivity<DailyAuditPresenter, DailyAuditModel>(), DailyAuditContract.View {

    private lateinit var dailyBean: DailyAuditListBean
    private lateinit var addFileFragment: AddFileFragment

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean, item: DailyAuditListBean) {
            val intent = Intent(activity, DailyAuditActivity::class.java)
            intent.putExtra("data", item)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_daily_audit
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        ZXFragmentUtil.addFragment(supportFragmentManager, AddFileFragment.newInstance(AddFileFragment.FileType.JUST_SHOW)
                .apply { addFileFragment = this }, R.id.fm_add_file)

        dailyBean = intent.getSerializableExtra("data") as DailyAuditListBean

        tvName.text = dailyBean.logpersonname
        tvTime.text = ZXTimeUtil.millis2String(dailyBean.logtime)
        tvContent.text = dailyBean.workcontent
        mPresenter.getDailyFile(ApiParamUtil.getDailyFile(dailyBean.logid))

        if (dailyBean.status != 0) {
            downLayout.visibility = View.GONE
            tvAuditopinion.setText(dailyBean.auditopinion)
            tvAuditopinion.isEnabled = false
            tvAuditopinion.hint = ""
            tvAuditTime.text = ZXTimeUtil.millis2String(dailyBean.audittime.toLong())
            auditTimeLayout.visibility = View.VISIBLE
        }

        uploadLog(303, 2, "查看日报详情")
    }

    /**
     * 文件回调
     */
    override fun onDailyFileResult(fileList: List<AuditReportFileBean>) {
        val addFileList = arrayListOf<AddFileBean>()
        if (fileList.isNotEmpty()) {
            fileList.forEach {
                addFileList.add(AddFileBean.reSetType(it.type, it.name, it.path))
            }
        }
        addFileFragment.setData(addFileList)
    }

    fun submit(auditChoices: Int) {
        num=auditChoices
        mPresenter.getAuditReport(ApiParamUtil.auditReport(dailyBean.auditid, tvAuditopinion.text.toString(), auditChoices.toString()))
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {
        btn_pass.setOnClickListener {
            submit(1)
        }
        btn_noPass.setOnClickListener {
            submit(-1)
        }
        btn_close.setOnClickListener {
            submit(-2)
        }
    }

    var num = 0
    override fun onAuditReportResult(data: String) {
        uploadLog(303, 3, "日报审核：$num")
        showToast("审核成功！")
        finish()
    }

}
