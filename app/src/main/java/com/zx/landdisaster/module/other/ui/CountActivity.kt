package com.zx.landdisaster.module.other.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import com.zx.landdisaster.R
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.module.other.bean.CountFuncBean
import com.zx.landdisaster.module.other.func.adapter.CountFuncAdapter
import com.zx.landdisaster.module.other.mvp.contract.CountContract
import com.zx.landdisaster.module.other.mvp.model.CountModel
import com.zx.landdisaster.module.other.mvp.presenter.CountPresenter
import kotlinx.android.synthetic.main.activity_count.*


/**
 * Create By admin On 2017/7/11
 * 功能：统计
 */
class CountActivity : BaseActivity<CountPresenter, CountModel>(), CountContract.View {

    private var dataBeans = arrayListOf<CountFuncBean>()
    private var listAdapter = CountFuncAdapter(dataBeans)

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean) {
            val intent = Intent(activity, CountActivity::class.java)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_count
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        dataBeans.add(CountFuncBean("灾险情统计", R.drawable.report_disaster))
        dataBeans.add(CountFuncBean("灾险情统计", R.drawable.report_disaster))
        rv_count_list.apply {
            layoutManager = GridLayoutManager(this@CountActivity, 2)
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
                "灾险情统计" -> CountDisasterActivity.startAction(this, false)
                "灾险情统计" -> CountDisasterActivity.startAction(this, false)
            }
        }
    }

}
