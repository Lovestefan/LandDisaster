package com.zx.landdisaster.module.groupdefense.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.zx.landdisaster.R
import com.zx.landdisaster.api.ApiConfigModule
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.module.disaster.bean.AddFileBean
import com.zx.landdisaster.module.disaster.bean.AuditReportFileBean
import com.zx.landdisaster.module.groupdefense.bean.MonitorPatrolBean
import com.zx.landdisaster.module.groupdefense.func.adapter.MacroMonitroDetailsAdapter
import com.zx.landdisaster.module.groupdefense.mvp.contract.MonitorPatrolDetailsContract
import com.zx.landdisaster.module.groupdefense.mvp.model.MonitorPatrolDetailsModel
import com.zx.landdisaster.module.groupdefense.mvp.presenter.MonitorPatrolDetailsPresenter
import com.zx.landdisaster.module.other.ui.AddFileFragment
import com.zx.zxutils.other.ZXInScrollRecylerManager
import com.zx.zxutils.util.ZXFragmentUtil
import com.zx.zxutils.util.ZXTimeUtil
import kotlinx.android.synthetic.main.activity_monitor_patrol_details.*


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class MonitorPatrolDetailsActivity : BaseActivity<MonitorPatrolDetailsPresenter, MonitorPatrolDetailsModel>(), MonitorPatrolDetailsContract.View {


    private lateinit var addFileFragment: AddFileFragment


    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean, bean: MonitorPatrolBean) {
            val intent = Intent(activity, MonitorPatrolDetailsActivity::class.java)
            intent.putExtra("MonitorPatrolBean", bean)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_monitor_patrol_details
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        var monitorPatrolBean = intent.getSerializableExtra("MonitorPatrolBean") as MonitorPatrolBean

        val dataList = arrayListOf<Pair<String, String>>()

        dataList.add(Pair("灾害点名称", if (monitorPatrolBean.pkiaaname != null) monitorPatrolBean.pkiaaname!! else ""))
        dataList.add(Pair("监测点名称", if (monitorPatrolBean.name != null) monitorPatrolBean.name!! else ""))
        dataList.add(Pair("监测时间", if (monitorPatrolBean.createtime != null) ZXTimeUtil.getTime(monitorPatrolBean.createtime)!! else ""))
        dataList.add(Pair("实测数据", if (monitorPatrolBean.actualdata != null) monitorPatrolBean.actualdata.toString()!! else ""))


        if (monitorPatrolBean.legal != null) {
            if (monitorPatrolBean.legal == 1) {
                dataList.add(Pair("是否合法", "合法"))
            } else {
                dataList.add(Pair("是否合法", "不合法"))
            }
        } else {
            dataList.add(Pair("是否合法", ""))
        }
        dataList.add(Pair("数据有效性", if (monitorPatrolBean.effectivity != null) monitorPatrolBean.effectivity!! else ""))
        dataList.add(Pair("相邻告警", if (monitorPatrolBean.alarmlevel != null) monitorPatrolBean.alarmlevel!! else ""))
        dataList.add(Pair("经度", if (monitorPatrolBean.longitude != null) monitorPatrolBean.longitude.toString()!! else ""))
        dataList.add(Pair("纬度", if (monitorPatrolBean.latitude != null) monitorPatrolBean.latitude.toString()!! else ""))

        rv_monitor_patrol_details.apply {
            layoutManager = ZXInScrollRecylerManager(mContext)
            adapter = MacroMonitroDetailsAdapter(dataList)
        }

        ZXFragmentUtil.addFragment(supportFragmentManager, AddFileFragment.newInstance().apply {
            addFileFragment = this
        }, R.id.fm_add_file)

        addFileFragment.setOnEditCallBack {
            scroll_view.fullScroll(0)
        }


        mPresenter.getMonitorPatrolDetailsFileList(monitorPatrolBean.logid)
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {

    }


    override fun getMonitorPatrolDetailsFileResult(disasterList: List<AuditReportFileBean>) {
        var list = arrayListOf<AddFileBean>()
        disasterList.forEach {
            val type = if (it.name.endsWith("jpg") || it.name.endsWith("png")) {
                1
            } else if (it.name.endsWith("avi") || it.name.endsWith("mp4") || it.name.endsWith("rmvb") || it.name.endsWith("3gp")) {
                2
            } else {
                3
            }
            list.add(AddFileBean(it.name, type, ApiConfigModule.BASE_IP + ApiConfigModule.URL_FILE + it.path))
        }

        addFileFragment.setData(list)
        addFileFragment.setCanEdit(false)

    }

    override fun getMonitorPatrolDetailsFileResult() {
        addFileFragment.setCanEdit(false)
    }

    override fun getMonitorPatrolDetailsFileError() {
        addFileFragment.setCanEdit(false)
    }


}
