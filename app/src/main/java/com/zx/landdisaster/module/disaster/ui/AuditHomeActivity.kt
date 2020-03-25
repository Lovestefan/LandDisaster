package com.zx.landdisaster.module.disaster.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import com.zx.landdisaster.R
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.base.other.UserManager
import com.zx.landdisaster.module.disaster.bean.ReportFuncBean
import com.zx.landdisaster.module.disaster.func.adapter.ReportFuncAdapter

import com.zx.landdisaster.module.disaster.mvp.contract.AuditHomeContract
import com.zx.landdisaster.module.disaster.mvp.model.AuditHomeModel
import com.zx.landdisaster.module.disaster.mvp.presenter.AuditHomePresenter
import com.zx.landdisaster.module.main.bean.TaskNumBean
import com.zx.landdisaster.module.worklog.ui.DailyAuditListActivity
import kotlinx.android.synthetic.main.activity_audit_home.*


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class AuditHomeActivity : BaseActivity<AuditHomePresenter, AuditHomeModel>(), AuditHomeContract.View {

    private var dataBeans = arrayListOf<ReportFuncBean>()
    private var listAdapter = ReportFuncAdapter(dataBeans)

    var auditNum = 0
    var reportNum = 0
    override var canSwipeBack = false

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean) {
            val intent = Intent(activity, AuditHomeActivity::class.java)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    override fun onResume() {
        super.onResume()
        mPresenter.getTaskNum()
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_audit_home
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
    }

    override fun onTaskNumResult(numBean: TaskNumBean) {
        if (numBean.auditTask != null) auditNum = numBean.auditTask!!
        if (numBean.safeReports != null) reportNum = numBean.safeReports!!

        dataBeans.clear()
        if (UserManager.getUser().personRole.ringStandAudit) {//区县地环站的审核权限
            dataBeans.add(ReportFuncBean("日报审核", R.drawable.report_daily1, reportNum))
        }
        dataBeans.add(ReportFuncBean("灾险情审核", R.drawable.report_disaster1, auditNum))
        rv_audit.apply {
            layoutManager = GridLayoutManager(this@AuditHomeActivity, 2)
            adapter = listAdapter
        }
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {
        //列表点击事件
        listAdapter.setOnItemClickListener { adapter, view, position ->
            when (dataBeans[position].name) {
                "日报审核" -> DailyAuditListActivity.startAction(this, false)
                "灾险情审核" -> AuditActivity.startAction(this, false)
            }
        }
    }
}
