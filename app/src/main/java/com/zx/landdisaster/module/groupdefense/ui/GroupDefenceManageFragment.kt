package com.zx.landdisaster.module.groupdefense.ui

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import com.zx.landdisaster.R
import com.zx.landdisaster.base.BaseFragment
import com.zx.landdisaster.module.areamanager.ui.PatrolUploadActivity
import com.zx.landdisaster.module.disaster.bean.ReportFuncBean
import com.zx.landdisaster.module.disaster.func.adapter.ReportFuncAdapter
import com.zx.landdisaster.module.groupdefense.mvp.contract.GroupDefenceManageContract
import com.zx.landdisaster.module.groupdefense.mvp.model.GroupDefenceManageModel
import com.zx.landdisaster.module.groupdefense.mvp.presenter.GroupDefenceManagePresenter
import kotlinx.android.synthetic.main.fragment_area_manager.*

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class GroupDefenceManageFragment : BaseFragment<GroupDefenceManagePresenter, GroupDefenceManageModel>(), GroupDefenceManageContract.View {

    private var dataBeans = arrayListOf<ReportFuncBean>()
    private var listAdapter = ReportFuncAdapter(dataBeans)

    companion object {
        /**
         * 启动器
         */
        fun newInstance(): GroupDefenceManageFragment {
            val fragment = GroupDefenceManageFragment()
            val bundle = Bundle()

            fragment.arguments = bundle
            return fragment
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.fragment_group_defence_manage
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        dataBeans.add(ReportFuncBean("巡查报告", R.drawable.iv_work_add))
        dataBeans.add(ReportFuncBean("监测上报", R.drawable.iv_jcsb))
        rv_report.apply {
            layoutManager = GridLayoutManager(activity, 2)
            adapter = listAdapter
        }
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {

        listAdapter.setOnItemClickListener { adapter, view, position ->
            when (dataBeans[position].name) {
                "巡查报告" -> PatrolUploadActivity.startAction(this.mActivity, false)
                "监测上报" -> GroupDefenceFragment.startAction(this.mActivity, false)
            }
        }
    }
}
