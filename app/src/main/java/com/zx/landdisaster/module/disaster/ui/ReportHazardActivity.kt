package com.zx.landdisaster.module.disaster.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import com.zx.landdisaster.R
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.module.disaster.mvp.contract.ReportHazardContract
import com.zx.landdisaster.module.disaster.mvp.model.ReportHazardModel
import com.zx.landdisaster.module.disaster.mvp.presenter.ReportHazardPresenter
import kotlinx.android.synthetic.main.activity_report_hazard.*
import kotlinx.android.synthetic.main.item_day_manage.view.*


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class ReportHazardActivity : BaseActivity<ReportHazardPresenter, ReportHazardModel>(), ReportHazardContract.View {

    override var canSwipeBack: Boolean = false

    private var hazardAddFragment: ReportHazardAddFragment?=null
    private lateinit var hazardListFragment: ReportHazardListFragment

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean, isSBJL: Boolean = false) {
            val intent = Intent(activity, ReportHazardActivity::class.java)
            activity.startActivity(intent.putExtra("isSBJL", isSBJL))
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_report_hazard
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        tvp_hazard.setManager(supportFragmentManager)
                .setTabScrollable(false)
                .setViewpagerCanScroll(true)
        if (!intent.getBooleanExtra("isSBJL", false)) {
            tvp_hazard.addTab(ReportHazardAddFragment.newInstance().apply { hazardAddFragment = this }, "上报")
        }
        tvp_hazard.addTab(ReportHazardListFragment.newInstance().apply { hazardListFragment = this }, "记录")
                .setTitleColor(ContextCompat.getColor(this, R.color.text_color_normal), ContextCompat.getColor(this, R.color.colorPrimary))
                .setIndicatorHeight(10)
                .setIndicatorColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setTablayoutHeight(40)
                .setTabTextSize(12, 12)
                .setTablayoutBackgroundColor(ContextCompat.getColor(this, R.color.white))
                .showDivider(ContextCompat.getColor(this, R.color.text_color_light))
                .build()

        if (intent.getBooleanExtra("isSBJL", false)) {
            tvp_hazard.tabLayout.visibility = View.GONE
            toolbar_view.setMidText("灾险情上报记录")
        }
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {
        //返回按钮点击事件
        toolbar_view.setLeftClickListener { onBackPressed() }
    }

    override fun onBackPressed() {
        if (tvp_hazard.selectedPosition == 0 && hazardAddFragment != null) {
            hazardAddFragment!!.onBackPressed()
        } else {
            super.onBackPressed()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (hazardAddFragment != null)
            hazardAddFragment!!.onActivityResult(requestCode, resultCode, data)
        hazardListFragment.onActivityResult(requestCode, resultCode, data)
    }

}
