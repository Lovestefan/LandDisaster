package com.zx.landdisaster.module.disaster.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import com.zx.landdisaster.R
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.base.bean.Jurisdiction
import com.zx.landdisaster.base.other.UserManager
import com.zx.landdisaster.module.areamanager.ui.PatrolUploadActivity
import com.zx.landdisaster.module.disaster.bean.ReportFuncBean
import com.zx.landdisaster.module.disaster.func.adapter.ReportFuncAdapter
import com.zx.landdisaster.module.disaster.mvp.contract.ReportContract
import com.zx.landdisaster.module.disaster.mvp.model.ReportModel
import com.zx.landdisaster.module.disaster.mvp.presenter.ReportPresenter
import com.zx.landdisaster.module.worklog.ui.DailyAuditListActivity
import com.zx.landdisaster.module.worklog.ui.DailyReportActivity
import com.zx.landdisaster.module.worklog.ui.WorkLogAddActivity
import com.zx.landdisaster.module.worklog.ui.WorkLogReportActivity
import kotlinx.android.synthetic.main.activity_report.*


/**
 * Create By admin On 2017/7/11
 * 功能：上报主页
 */
class ReportActivity : BaseActivity<ReportPresenter, ReportModel>(), ReportContract.View {

    private var dataBeans = arrayListOf<ReportFuncBean>()
    private var listAdapter = ReportFuncAdapter(dataBeans)

    override var canSwipeBack = false

    var auditNum = 0
    var reportNum = 0

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean) {
            val intent = Intent(activity, ReportActivity::class.java)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_report
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        if (haveQuanXian(Jurisdiction.sbltj)) {
            dataBeans.add(ReportFuncBean("上报率统计", R.drawable.report_statistics))
        }
        if (UserManager.getUser().personRole.ringStand) {//区县地环站的允许日报
            dataBeans.add(ReportFuncBean("日报", R.drawable.report_daily))
        }

        if (UserManager.getUser().personRole.garrison) {//驻守地质成员允许工作日志
            dataBeans.add(ReportFuncBean("日志填报", R.drawable.report_worklog))
            dataBeans.add(ReportFuncBean("日志上报情况", R.drawable.iv_work_list))
        }
        if (UserManager.canReport()) {
            dataBeans.add(ReportFuncBean("巡查报告", R.drawable.iv_work_add))
            dataBeans.add(ReportFuncBean("灾险情填报", R.drawable.report_disaster))
        } else
            if (haveQuanXian(Jurisdiction.sbjl)) {
                dataBeans.add(ReportFuncBean("灾险情上报记录", R.drawable.report_disaster))
            }

        if (getUserType() == 2) {
            tbv_title.setMidText("区县上报")

//            if (haveQuanXian(Jurisdiction.sh)) {
            if (UserManager.getUser().personRole.ringStandAudit) {//区县地环站的审核权限
                dataBeans.add(ReportFuncBean("日报审核", R.drawable.report_daily1, reportNum))
            }
            if (UserManager.canAudit()) {
                dataBeans.add(ReportFuncBean("灾险情审核", R.drawable.report_disaster1))
            } else if (UserManager.canPreview()) {
                dataBeans.add(ReportFuncBean("灾险情审阅", R.drawable.report_disaster1))
            }
//            }
        }
        rv_report.apply {
            layoutManager = GridLayoutManager(this@ReportActivity, 2) as RecyclerView.LayoutManager?
            adapter = listAdapter
        }
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {
        //列表点击事件
        listAdapter.setOnItemClickListener { _, _, position ->
            when (dataBeans[position].name) {
                "上报率统计" -> ReportStatisticsActivity.startAction(this, false)
                "日报" -> DailyReportActivity.startAction(this, false)
                "巡查报告" -> PatrolUploadActivity.startAction(this, false)
                "灾险情填报" -> ReportHazardActivity.startAction(this, false)
                "日志填报" -> WorkLogAddActivity.startAction(this, false)
                "日志上报情况" -> WorkLogReportActivity.startAction(this, false)
                "灾险情审核" -> AuditActivity.startAction(this, false)
                "灾险情审阅" -> AuditActivity.startAction(this, false)
                "日报审核" -> DailyAuditListActivity.startAction(this, false)
                "灾险情上报记录" -> ReportHazardActivity.startAction(this, false, true)
            }
        }

    }

}
