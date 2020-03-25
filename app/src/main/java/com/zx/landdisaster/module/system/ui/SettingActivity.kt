package com.zx.landdisaster.module.system.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.zx.landdisaster.R
import com.zx.landdisaster.app.MyApplication
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.module.main.bean.FuncBean
import com.zx.landdisaster.module.main.func.adapter.FuncAdapter
import com.zx.landdisaster.module.system.mvp.contract.SettingContract
import com.zx.landdisaster.module.system.mvp.model.SettingModel
import com.zx.landdisaster.module.system.mvp.presenter.SettingPresenter
import kotlinx.android.synthetic.main.activity_setting.*


/**
 * Create By admin On 2017/7/11
 * 功能：设置
 */
class SettingActivity : BaseActivity<SettingPresenter, SettingModel>(), SettingContract.View {

    var dataBeans = arrayListOf<FuncBean>()
    var listAdapter = FuncAdapter(dataBeans)

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean) {
            val intent = Intent(activity, SettingActivity::class.java)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_setting
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        rv_setting.apply {
            layoutManager = LinearLayoutManager(this@SettingActivity)
            adapter = listAdapter
        }
        dataBeans.add(FuncBean("字体大小", R.drawable.func_textsize))
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {
        //菜单点击事件
        listAdapter.setOnItemClickListener { adapter, view, position ->
            when (dataBeans[position].title) {
                "字体大小" -> {
                    mSharedPrefUtil.putFloat("fontScaleSize", 1.5f)
                    var intent = MyApplication.instance.getPackageManager()
                            .getLaunchIntentForPackage(MyApplication.instance.getPackageName());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    MyApplication.instance.startActivity(intent);
                }
            }
        }
    }

}
