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
import com.zx.landdisaster.module.disaster.func.adapter.DisasterPointListAdapter
import com.zx.landdisaster.module.disaster.mvp.contract.DisasterPointListContract
import com.zx.landdisaster.module.disaster.mvp.model.DisasterPointListModel
import com.zx.landdisaster.module.disaster.mvp.presenter.DisasterPointListPresenter
import com.zx.landdisaster.module.main.bean.DisasterPointBean
import com.zx.zxutils.entity.KeyValueEntity
import com.zx.zxutils.views.SwipeRecylerView.ZXSRListener
import kotlinx.android.synthetic.main.activity_disaster_point_list.*
import java.util.*


/**
 * Create By admin On 2017/7/11
 * 功能：灾险情集合列表
 */
class DisasterPointListActivity : BaseActivity<DisasterPointListPresenter, DisasterPointListModel>(), DisasterPointListContract.View {

    private var pageNo = 0

    private val disasterList = ArrayList<DisasterPointBean>()
    private var mAdapter = DisasterPointListAdapter(disasterList)

    private var hazardType = ""
    private var scaleLevel = ""

    var zxqType = ""

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean, type: String) {
            val intent = Intent(activity, DisasterPointListActivity::class.java)
            intent.putExtra("type", type)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_disaster_point_list
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        zxqType = intent.getStringExtra("type")
        sp_filter_timeType.apply {
            setItemHeightDp(30)
            setData(mutableListOf(KeyValueEntity("今日", "1"), KeyValueEntity("本周", "2"), KeyValueEntity("本月", "3"),
                    KeyValueEntity("三个月", "4"), KeyValueEntity("今年", "5"), KeyValueEntity("全部", "6")))

            setDefaultItem(zxqType.toInt() - 1)
            showSelectedTextColor(true, R.color.colorPrimary)
            showUnderineColor(false)
        }.build()
        sp_filter_hazardtype.apply {
            setItemHeightDp(30)
            setData(mutableListOf(KeyValueEntity("灾情", "1"), KeyValueEntity("险情", "2")))
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


        sr_point_disaster.setLayoutManager(LinearLayoutManager(this))
                .setAdapter(mAdapter)
                .setPageSize(pageSize)
                .autoLoadMore()
                .setSRListener(object : ZXSRListener<DisasterPointBean> {
                    override fun onItemLongClick(item: DisasterPointBean?, position: Int) {

                    }

                    override fun onLoadMore() {
                        pageNo++
                        loadData()
                    }

                    override fun onRefresh() {
                        loadData(true)
                    }

                    override fun onItemClick(item: DisasterPointBean?, position: Int) {
                        if (item != null) {
                            ReportDetailActivity.startAction(this@DisasterPointListActivity, item.pkidd, false)
                            uploadLog(306, 6, "查看灾险情详情：${item.pkidd}")
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
            sr_point_disaster.clearStatus()
        }
        mPresenter.getDisasterList(ApiParamUtil.disasterListParam(pageNo, pageSize, zxqType, hazardType, scaleLevel))
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {
        sp_filter_timeType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                zxqType = sp_filter_timeType.selectedValue.toString()
                loadData(true)
            }
        }
        sp_filter_hazardtype.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                hazardType = sp_filter_hazardtype.selectedValue.toString()
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

    override fun onDisasterResult(list: NormalList<DisasterPointBean>?) {
        if (list != null) {
            toolbar_view.setMidText("灾险情(${list.totalRecords})")
            sr_point_disaster.refreshData(list.result!!, list.totalRecords)
        } else {
            toolbar_view.setMidText("灾险情(0)")
            mAdapter.emptyView = EmptyViewTool.getInstance(this).getEmptyView { loadData(true) }
        }

//        sr_point_disaster.stopRefresh()
//        if (disasterList == null) {
//            mAdapter.emptyView = EmptyViewTool.getInstance(this).getEmptyView { loadData() }
//            sr_point_disaster.setLoadInfo(0)
//            toolbar_view.setMidText("灾险情(0)")
//            this.disasterList.clear()
//        } else if (disasterList.result == null) {
//            sr_point_disaster.setLoadInfo(0)
//            toolbar_view.setMidText("灾险情(0)")
//            this.disasterList.clear()
//        } else {
//            sr_point_disaster.setLoadInfo(disasterList.totalRecords)
//            toolbar_view.setMidText("灾险情(${disasterList.totalRecords})")
//            this.disasterList.clear()
//            this.disasterList.addAll(disasterList.result!!)
//            sr_point_disaster.recyclerView.scrollToPosition(0)
//        }
//        sr_point_disaster.notifyDataSetChanged()
    }

}
