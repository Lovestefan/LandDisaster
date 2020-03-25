package com.zx.landdisaster.module.disaster.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import com.zx.landdisaster.R
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.base.bean.Jurisdiction
import com.zx.landdisaster.base.other.UserManager
import com.zx.landdisaster.module.disaster.mvp.contract.ReportStatisticsContract
import com.zx.landdisaster.module.disaster.mvp.model.ReportStatisticsModel
import com.zx.landdisaster.module.disaster.mvp.presenter.ReportStatisticsPresenter
import kotlinx.android.synthetic.main.activity_report_statistics.*


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class ReportStatisticsActivity : BaseActivity<ReportStatisticsPresenter, ReportStatisticsModel>(), ReportStatisticsContract.View {

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean) {
            val intent = Intent(activity, ReportStatisticsActivity::class.java)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_report_statistics
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        tvp_report_statistics.setManager(supportFragmentManager)
                .setTabScrollable(false)
                .setViewpagerCanScroll(true)
                .setTitleColor(ContextCompat.getColor(this, R.color.text_color_normal), ContextCompat.getColor(this, R.color.colorPrimary))
                .setIndicatorHeight(10)
                .setIndicatorColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setTabTextSize(12, 12)
                .setTablayoutBackgroundColor(ContextCompat.getColor(this, R.color.white))
                .showDivider(ContextCompat.getColor(this, R.color.text_color_light))
        var num = 0
        var title = ""
        if (haveQuanXian(Jurisdiction.qxrb)) {
            tvp_report_statistics.addTab(ReportStatisFragment.newInstance().apply { setType("1") }, "区县日报")
            num++
            title = "区县日报"
        }
        if (haveQuanXian(Jurisdiction.zsrz)) {
            tvp_report_statistics.addTab(ReportStatisFragment.newInstance().apply { setType("2") }, "驻守日志")
            num++
            title = "驻守日志"
        }
        if (haveQuanXian(Jurisdiction.pqzb)) {
            tvp_report_statistics.addTab(ReportStatisFragment.newInstance().apply { setType("3") }, "片区周报")
            num++
            title = "片区周报"
        }
        if (haveQuanXian(Jurisdiction.jcsj)) {
            tvp_report_statistics.addTab(ReportStatisFragment.newInstance().apply { setType("4") }, "监测数据")
            num++
            title = "监测数据"
        }
        if (num < 2) {
            tbv_title.setMidText(title)
            tvp_report_statistics.tabLayout.visibility = View.GONE
            if (num == 0) tbv_title.setMidText("上报率统计")
        } else
            tvp_report_statistics.setTablayoutHeight(40).build()
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {
    }

}
