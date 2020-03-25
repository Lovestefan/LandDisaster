package com.zx.landdisaster.module.disaster.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.zx.landdisaster.R
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.base.other.UserManager
import com.zx.landdisaster.module.disaster.mvp.contract.AuditContract
import com.zx.landdisaster.module.disaster.mvp.model.AuditModel
import com.zx.landdisaster.module.disaster.mvp.presenter.AuditPresenter
import kotlinx.android.synthetic.main.activity_audit.*


/**
 * Create By admin On 2017/7/11
 * 功能：审核
 */
class AuditActivity : BaseActivity<AuditPresenter, AuditModel>(), AuditContract.View {

    override var canSwipeBack = false

    lateinit var listFragment1: AuditListFragment
    lateinit var listFragment2: AuditListFragment
    lateinit var listFragment3: AuditListFragment

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean) {
            val intent = Intent(activity, AuditActivity::class.java)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_audit
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        if (UserManager.getUser().personRole.dispatch || UserManager.getUser().personRole.leader) {
            toolbar_view.setMidText("审阅")
        }
        tvp_audit.setManager(supportFragmentManager)
                .setTabScrollable(false)
                .setViewpagerCanScroll(true)
                .addTab(AuditListFragment.newInstance(0).apply { listFragment1 = this }, "全部")
                .addTab(AuditListFragment.newInstance(1).apply { listFragment2 = this }, "灾情")
                .addTab(AuditListFragment.newInstance(2).apply { listFragment3 = this }, "险情")
                .setTitleColor(ContextCompat.getColor(this, R.color.text_color_normal), ContextCompat.getColor(this, R.color.colorPrimary))
                .setIndicatorHeight(10)
                .setIndicatorColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setTablayoutHeight(40)
                .setTabTextSize(12, 12)
                .setTablayoutBackgroundColor(ContextCompat.getColor(this, R.color.white))
                .showDivider(ContextCompat.getColor(this, R.color.text_color_light))
                .build()
        uploadLog(303, 4, "查询灾险情审核列表")
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 0x01) {
            listFragment1.loadData(true)
            listFragment2.loadData(true)
            listFragment3.loadData(true)
        }
    }

}
