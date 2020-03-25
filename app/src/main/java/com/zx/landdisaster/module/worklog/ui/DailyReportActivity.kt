package com.zx.landdisaster.module.worklog.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.zx.landdisaster.R
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.module.worklog.mvp.contract.DailyReportContract
import com.zx.landdisaster.module.worklog.mvp.model.DailyReportModel
import com.zx.landdisaster.module.worklog.mvp.presenter.DailyReportPresenter
import kotlinx.android.synthetic.main.activity_daily_report.*


/**
 * Create By admin On 2017/7/11
 * 功能：日报
 */
class DailyReportActivity : BaseActivity<DailyReportPresenter, DailyReportModel>(), DailyReportContract.View {

    override var canSwipeBack: Boolean = false

    private lateinit var dailyReportAddFragment: DailyReportAddFragment

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean) {
            val intent = Intent(activity, DailyReportActivity::class.java)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_daily_report
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        tvp_daily.setManager(supportFragmentManager)
                .setTabScrollable(false)
                .setViewpagerCanScroll(true)
                .addTab(DailyReportAddFragment.newInstance().apply { dailyReportAddFragment = this }, "上报")
                .addTab(DailyReportListFragment.newInstance(), "记录")
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        dailyReportAddFragment.onActivityResult(requestCode, resultCode, data)
    }


}
