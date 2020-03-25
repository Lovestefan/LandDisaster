package com.zx.landdisaster.module.disaster.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.zx.landdisaster.R
import com.zx.landdisaster.base.tool.ActionLog
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.module.main.ui.MapPointActivity
import com.zx.landdisaster.module.disaster.bean.HiddenDetailBean
import com.zx.landdisaster.module.disaster.mvp.contract.HiddenPointContract
import com.zx.landdisaster.module.disaster.mvp.model.HiddenPointModel
import com.zx.landdisaster.module.disaster.mvp.presenter.HiddenPointPresenter
import kotlinx.android.synthetic.main.activity_hidden_point.*


/**
 * Create By admin On 2017/7/11
 * 功能：隐患点详情
 */
class HiddenPointActivity : BaseActivity<HiddenPointPresenter, HiddenPointModel>(), HiddenPointContract.View {

    private var hiddenDetailBean: HiddenDetailBean? = null

    private lateinit var hiddenPointInfoFragment: HiddenPointInfoFragment
    private lateinit var hiddenPointPersonFragment: HiddenPointPersonFragment

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean, pkiaa: String) {
            val intent = Intent(activity, HiddenPointActivity::class.java)
            intent.putExtra("pkiaa", pkiaa)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_hidden_point
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        tvp_hidden_point.setManager(supportFragmentManager)
                .setTabScrollable(false)
                .setViewpagerCanScroll(true)
                .addTab(HiddenPointInfoFragment.newInstance().apply { hiddenPointInfoFragment = this }, "隐患点详情")
                .addTab(HiddenPointPersonFragment.newInstance().apply { hiddenPointPersonFragment = this }, "四重网格人员")
                .setTitleColor(ContextCompat.getColor(this, R.color.text_color_normal), ContextCompat.getColor(this, R.color.colorPrimary))
                .setIndicatorHeight(10)
                .setIndicatorColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setTablayoutHeight(40)
                .setTabTextSize(12, 12)
                .setTablayoutBackgroundColor(ContextCompat.getColor(this, R.color.white))
                .showDivider(ContextCompat.getColor(this, R.color.text_color_light))
                .build()

        mPresenter.getHiddenDetail(intent.getStringExtra("pkiaa"))
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {
        toolbar_view.setRightClickListener {
            if (hiddenDetailBean != null && hiddenDetailBean!!.longitude != null && hiddenDetailBean!!.latitude != null) {
                MapPointActivity.startAction(this, false, hiddenDetailBean!!)
            } else {
                showToast("未获取到坐标信息")
            }
        }
    }

    override fun onHiddenDetail(hiddenDetailBean: HiddenDetailBean) {
        this.hiddenDetailBean = hiddenDetailBean
        toolbar_view.setMidText(hiddenDetailBean.name!!)
        hiddenPointInfoFragment.setData(hiddenDetailBean)
        hiddenPointPersonFragment.setData(hiddenDetailBean)

    }

}
