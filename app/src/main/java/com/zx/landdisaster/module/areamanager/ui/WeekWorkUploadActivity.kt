package com.zx.landdisaster.module.areamanager.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.zx.landdisaster.R
import com.zx.landdisaster.base.BaseActivity

import com.zx.landdisaster.module.areamanager.mvp.contract.WeekWorkUploadContract
import com.zx.landdisaster.module.areamanager.mvp.model.WeekWorkUploadModel
import com.zx.landdisaster.module.areamanager.mvp.presenter.WeekWorkUploadPresenter
import kotlinx.android.synthetic.main.activity_week_work_upload.*


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class WeekWorkUploadActivity : BaseActivity<WeekWorkUploadPresenter, WeekWorkUploadModel>(), WeekWorkUploadContract.View {

    override var canSwipeBack: Boolean = false

    private lateinit var weekWorkAddFragment: AddWeekWork
    private lateinit var weekWorkListFragment: WeekWorkListActivity

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean) {
            val intent = Intent(activity, WeekWorkUploadActivity::class.java)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_week_work_upload
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        tvp_weekwork.setManager(supportFragmentManager)
                .setTabScrollable(false)
                .setViewpagerCanScroll(true)
                .addTab(AddWeekWork.newInstance().apply { weekWorkAddFragment = this }, "上报")
                .addTab(WeekWorkListActivity.newInstance().apply { weekWorkListFragment = this }, "记录")
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
        //返回按钮点击事件
        toolbar_view.setLeftClickListener { onBackPressed() }
    }

    override fun onBackPressed() {
        if (tvp_weekwork.selectedPosition == 0) {
            weekWorkAddFragment.onBackPressed()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        weekWorkAddFragment.onActivityResult(requestCode, resultCode, data)
        weekWorkListFragment.onActivityResult(requestCode, resultCode, data)
    }

}
