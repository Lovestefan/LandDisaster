package com.zx.landdisaster.module.dailymanage.ui

import android.os.Bundle
import com.zx.landdisaster.R
import com.zx.landdisaster.base.BaseFragment
import com.zx.landdisaster.module.dailymanage.mvp.contract.ExpertDailyAddContract
import com.zx.landdisaster.module.dailymanage.mvp.model.ExpertDailyAddModel
import com.zx.landdisaster.module.dailymanage.mvp.presenter.ExpertDailyAddPresenter

/**
 * Create By admin On 2017/7/11
 * 功能：专家日报-上报模块
 */
class ExpertDailyAddFragment : BaseFragment<ExpertDailyAddPresenter, ExpertDailyAddModel>(), ExpertDailyAddContract.View {
    companion object {
        /**
         * 启动器
         */
        fun newInstance(): ExpertDailyAddFragment {
            val fragment = ExpertDailyAddFragment()
            val bundle = Bundle()

            fragment.arguments = bundle
            return fragment
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.fragment_expert_daily_add
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {

    }
}
