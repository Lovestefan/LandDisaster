package com.zx.landdisaster.module.worklog.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.zx.landdisaster.R
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.module.worklog.mvp.contract.WorkLogReportContract
import com.zx.landdisaster.module.worklog.mvp.model.WorkLogReportModel
import com.zx.landdisaster.module.worklog.mvp.presenter.WorkLogReportPresenter
import kotlinx.android.synthetic.main.activity_job_log_report.*


/**
 * Create By admin On 2017/7/11
 * 功能：工作日志
 */
class WorkLogReportActivity : BaseActivity<WorkLogReportPresenter, WorkLogReportModel>(), WorkLogReportContract.View {

    private lateinit var workLogAddActivity: WorkLogAddActivity

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean) {
            val intent = Intent(activity, WorkLogReportActivity::class.java)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_job_log_report
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        tvp_joblog.setManager(supportFragmentManager)
                .setTabScrollable(false)
                .setViewpagerCanScroll(true)
                .addTab(WorkLogListFragment.newInstance(0), "未上报")
                .addTab(WorkLogListFragment.newInstance(1), "已上报")
                .setTitleColor(ContextCompat.getColor(this, R.color.text_color_normal), ContextCompat.getColor(this, R.color.colorPrimary))
                .setIndicatorHeight(10)
                .setIndicatorColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setTablayoutHeight(40)
                .setTabTextSize(12, 12)
                .setTablayoutBackgroundColor(ContextCompat.getColor(this, R.color.white))
                .showDivider(ContextCompat.getColor(this, R.color.text_color_light))
                .build()
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {

    }

}
