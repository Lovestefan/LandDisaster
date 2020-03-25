package com.zx.landdisaster.module.other.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.zx.landdisaster.R
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.module.other.bean.CountDisasterBean
import com.zx.landdisaster.module.other.mvp.contract.CountDisasterContract
import com.zx.landdisaster.module.other.mvp.model.CountDisasterModel
import com.zx.landdisaster.module.other.mvp.presenter.CountDisasterPresenter


/**
 * Create By admin On 2017/7/11
 * 功能：统计-灾险情
 */
class CountDisasterActivity : BaseActivity<CountDisasterPresenter, CountDisasterModel>(), CountDisasterContract.View {

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean) {
            val intent = Intent(activity, CountDisasterActivity::class.java)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_count_disaster
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        mPresenter.countDisaster(ApiParamUtil.countDisasterParam())
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {

    }

    override fun onCountDisasterResult(countList: List<CountDisasterBean>) {

    }

}
