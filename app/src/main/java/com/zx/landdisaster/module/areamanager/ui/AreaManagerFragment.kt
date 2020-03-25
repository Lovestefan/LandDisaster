package com.zx.landdisaster.module.areamanager.ui

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import com.zx.landdisaster.R
import com.zx.landdisaster.base.BaseFragment
import com.zx.landdisaster.module.areamanager.bean.AreaManagerBean
import com.zx.landdisaster.module.areamanager.mvp.contract.AreaManagerContract
import com.zx.landdisaster.module.areamanager.mvp.model.AreaManagerModel
import com.zx.landdisaster.module.areamanager.mvp.presenter.AreaManagerPresenter
import com.zx.landdisaster.module.disaster.bean.ReportFuncBean
import com.zx.landdisaster.module.disaster.func.adapter.ReportFuncAdapter
import kotlinx.android.synthetic.main.fragment_area_manager.*

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class AreaManagerFragment : BaseFragment<AreaManagerPresenter, AreaManagerModel>(), AreaManagerContract.View {
    override fun onAreaResult(areaManagerBean: AreaManagerBean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var dataBeans = arrayListOf<ReportFuncBean>()
    private var listAdapter = ReportFuncAdapter(dataBeans)

    companion object {
        /**
         * 启动器
         */
        fun newInstance(): AreaManagerFragment {
            val fragment = AreaManagerFragment()
            val bundle = Bundle()

            fragment.arguments = bundle
            return fragment
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.fragment_area_manager
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        dataBeans.add(ReportFuncBean("巡查报告", R.drawable.iv_work_add))
        dataBeans.add(ReportFuncBean("周报上报", R.drawable.iv_work_list))
        rv_report.apply {
            layoutManager = GridLayoutManager(activity, 2)
            adapter = listAdapter
        }
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {
        //列表点击事件
        listAdapter.setOnItemClickListener { adapter, view, position ->
            when (dataBeans[position].name) {
                "巡查报告" -> PatrolUploadActivity.startAction(this.mActivity, false)
                "周报上报" -> WeekWorkUploadActivity.startAction(this.mActivity, false)
            }
        }
    }
}
