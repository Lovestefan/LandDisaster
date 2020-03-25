package com.zx.landdisaster.module.areamanager.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.zx.landdisaster.R
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.module.areamanager.mvp.contract.PatrolUploadContract
import com.zx.landdisaster.module.areamanager.mvp.model.PatrolUploadModel
import com.zx.landdisaster.module.areamanager.mvp.presenter.PatrolUploadPresenter
import kotlinx.android.synthetic.main.activity_patrol_upload.*


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class PatrolUploadActivity : BaseActivity<PatrolUploadPresenter, PatrolUploadModel>(), PatrolUploadContract.View {

    override var canSwipeBack: Boolean = false

    private lateinit var addPatrolFragment: AddPatrolFragment
    private lateinit var patrolListFragment: PatrolListFrgment

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean) {
            val intent = Intent(activity, PatrolUploadActivity::class.java)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_patrol_upload
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        tvp_patrol.setManager(supportFragmentManager)
                .setTabScrollable(false)
                .setViewpagerCanScroll(true)
                .addTab(AddPatrolFragment.newInstance().apply { addPatrolFragment = this }, "上报")
                .addTab(PatrolListFrgment.newInstance().apply { patrolListFragment = this }, "记录")
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
        toolbar_view.setLeftClickListener { onBackPressed() }
    }

    override fun onBackPressed() {
        if (tvp_patrol.selectedPosition == 0) {
            addPatrolFragment.onBackPressed()
        } else {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        addPatrolFragment.onActivityResult(requestCode, resultCode, data)
        patrolListFragment.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
