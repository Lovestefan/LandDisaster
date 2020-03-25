package com.zx.landdisaster.module.disaster.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.AdapterView
import com.zx.landdisaster.R
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.base.tool.ActionLog
import com.zx.landdisaster.base.tool.EmptyViewTool
import com.zx.landdisaster.module.disaster.func.adapter.HiddenPointListAdapter
import com.zx.landdisaster.module.disaster.mvp.contract.HiddenPointListContract
import com.zx.landdisaster.module.disaster.mvp.model.HiddenPointListModel
import com.zx.landdisaster.module.disaster.mvp.presenter.HiddenPointListPresenter
import com.zx.landdisaster.module.main.bean.HiddenPointBean
import com.zx.zxutils.entity.KeyValueEntity
import com.zx.zxutils.views.SwipeRecylerView.ZXSRListener
import kotlinx.android.synthetic.main.activity_hidden_point_list.*
import java.util.*


/**
 * Create By admin On 2017/7/11
 * 功能：隐患点点集列表
 */
class HiddenPointListActivity : BaseActivity<HiddenPointListPresenter, HiddenPointListModel>(), HiddenPointListContract.View {

    private var pageNo = 0

    private val hiddenList = ArrayList<HiddenPointBean>()
    private var mAdapter = HiddenPointListAdapter(hiddenList)

    private var disasterType = ""
    private var scaleLevel = ""

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean) {
            val intent = Intent(activity, HiddenPointListActivity::class.java)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_hidden_point_list
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        sp_filter_disastertype.apply {
            setItemHeightDp(30)
            setData(mutableListOf(KeyValueEntity("滑坡", "滑坡"), KeyValueEntity("泥石流", "泥石流"), KeyValueEntity("地面沉降", "地面沉降"),
                    KeyValueEntity("斜坡", "斜坡"), KeyValueEntity("地面塌陷", "地面塌陷"), KeyValueEntity("地裂缝", "地裂缝"),
                    KeyValueEntity("崩塌", "崩塌"), KeyValueEntity("库岸调查", "库岸调查"), KeyValueEntity("其它", "其它")))
            setDefaultItem("全部类型")
            showSelectedTextColor(true, R.color.colorPrimary)
            showUnderineColor(false)
        }.build()
        sp_filter_scale.apply {
            setItemHeightDp(30)
            setData(mutableListOf(KeyValueEntity("中型", "D"), KeyValueEntity("小型", "C"), KeyValueEntity("大型", "B"), KeyValueEntity("特大型", "A")))
            setDefaultItem("全部规模")
            showSelectedTextColor(true, R.color.colorPrimary)
            showUnderineColor(false)
        }.build()

        sr_point_hidden.setLayoutManager(LinearLayoutManager(this))
                .setAdapter(mAdapter)
                .setPageSize(pageSize)
                .autoLoadMore()
                .setSRListener(object : ZXSRListener<HiddenPointBean> {
                    override fun onItemLongClick(item: HiddenPointBean?, position: Int) {

                    }

                    override fun onLoadMore() {
                        pageNo++
                        loadData()
                    }

                    override fun onRefresh() {
                        loadData(true)
                    }

                    override fun onItemClick(item: HiddenPointBean?, position: Int) {
                        if (item != null) {
                            HiddenPointActivity.startAction(this@HiddenPointListActivity, false, item.pkiaa)
                            uploadLog(306, 3, "查看隐患点详情：${item.pkiaa}")
                        }
                    }

                })
        loadData(true)
    }


    /**
     * 数据加载
     */
    fun loadData(refresh: Boolean = false) {
        if (refresh) {
            pageNo = 1
            sr_point_hidden.clearStatus()
        }
        mPresenter.getHiddenList(ApiParamUtil.hiddenListParam(disasterType, scaleLevel), pageNo, pageSize)
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {
        sp_filter_disastertype.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                disasterType = sp_filter_disastertype.selectedValue.toString()
                loadData(true)
            }
        }
        sp_filter_scale.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                scaleLevel = sp_filter_scale.selectedValue.toString()
                loadData(true)
            }
        }
    }

    override fun onHiddenResult(list: NormalList<HiddenPointBean>?) {
        if (list != null) {
            toolbar_view.setMidText("隐患点(${list.totalRecords})")
            sr_point_hidden.refreshData(list.result!!, list.totalRecords)
        } else {
            toolbar_view.setMidText("隐患点(0)")
            mAdapter.emptyView = EmptyViewTool.getInstance(this).getEmptyView { loadData(true) }
        }

//        sr_point_hidden.stopRefresh()
//        if (hiddenList == null) {
//            mAdapter.emptyView = EmptyViewTool.getInstance(this).getEmptyView { loadData() }
//            sr_point_hidden.setLoadInfo(0)
//            toolbar_view.setMidText("隐患点(0)")
//            this.hiddenList.clear()
//        } else if (hiddenList.result == null) {
//            sr_point_hidden.setLoadInfo(0)
//            toolbar_view.setMidText("隐患点(0)")
//            this.hiddenList.clear()
//        } else {
//            sr_point_hidden.setLoadInfo(hiddenList.totalRecords)
//            toolbar_view.setMidText("隐患点(${hiddenList.totalRecords})")
//            this.hiddenList.clear()
//            this.hiddenList.addAll(hiddenList.result!!)
//            sr_point_hidden.recyclerView.scrollToPosition(0)
//        }
//        sr_point_hidden.notifyDataSetChanged()
    }

}
