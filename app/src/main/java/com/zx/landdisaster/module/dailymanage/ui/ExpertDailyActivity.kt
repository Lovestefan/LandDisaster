package com.zx.landdisaster.module.dailymanage.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.zx.landdisaster.R
import com.zx.landdisaster.base.BaseActivity

import com.zx.landdisaster.module.dailymanage.mvp.contract.ExpertDailyContract
import com.zx.landdisaster.module.dailymanage.mvp.model.ExpertDailyModel
import com.zx.landdisaster.module.dailymanage.mvp.presenter.ExpertDailyPresenter
import kotlinx.android.synthetic.main.activity_expert_daily.*


/**
 * Create By admin On 2017/7/11
 * 功能：专家日报
 */
class ExpertDailyActivity : BaseActivity<ExpertDailyPresenter, ExpertDailyModel>(), ExpertDailyContract.View {

    override var canSwipeBack: Boolean = false

    private lateinit var dailyAddFragment: ExpertDailyAddFragment
    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean) {
            val intent = Intent(activity, ExpertDailyActivity::class.java)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_expert_daily
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        tvp_daily.setManager(supportFragmentManager)
                .setTabScrollable(false)
                .setViewpagerCanScroll(true)
                .addTab(ExpertDailyAddFragment.newInstance().apply { dailyAddFragment = this }, "上报")
                .addTab(ExpertDailyListFragment.newInstance(), "记录")
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
        dailyAddFragment.onActivityResult(requestCode, resultCode, data)
    }
}
