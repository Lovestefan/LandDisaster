package com.zx.landdisaster.module.main.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.zx.landdisaster.R
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.base.bean.Jurisdiction
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.base.other.UserManager
import com.zx.landdisaster.module.main.bean.InfoDeliveryBean

import com.zx.landdisaster.module.main.mvp.contract.InfoReleaseContract
import com.zx.landdisaster.module.main.mvp.model.InfoReleaseModel
import com.zx.landdisaster.module.main.mvp.presenter.InfoReleasePresenter
import kotlinx.android.synthetic.main.activity_info_release.*


/**
 * Create By zc On 2019/5/24
 * 功能： 信息发布
 */
class InfoReleaseActivity : BaseActivity<InfoReleasePresenter, InfoReleaseModel>(), InfoReleaseContract.View {
    override fun onFindWeatherdecisionResult(list: NormalList<InfoDeliveryBean>?) {
    }

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean) {
            val intent = Intent(activity, InfoReleaseActivity::class.java)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_info_release
    }

    var types = ""
    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        if (haveQuanXian(Jurisdiction.yjxx)) {
            types += ",1"
        }
        if (haveQuanXian(Jurisdiction.rbzb)) {
            types += ",2"
        }
        if (haveQuanXian(Jurisdiction.hcbg)) {
            types += ",3"
        }
        if (haveQuanXian(Jurisdiction.hstz)) {
            types += ",4"
        }
        if (haveQuanXian(Jurisdiction.zjdd)) {
            types += ",5"
        }
        if (haveQuanXian(Jurisdiction.zcwj)) {
            types += ",6"
        }
        if (haveQuanXian(Jurisdiction.sbtj)) {
            types += ",7"
        }
        if (!types.isEmpty()) {
            types = types.substring(1, types.length)
        }
        if (types == "1,2,3,4,5,6,7") {
            types = ""
        }

        tvp_info_release.setManager(supportFragmentManager)
                .setTabScrollable(true)
                .setViewpagerCanScroll(true)
                .addTab(InfoReleaseFragment.newInstance().apply { setType(types) }, "全部")

        if (haveQuanXian(Jurisdiction.yjxx)) {
            tvp_info_release.addTab(InfoReleaseFragment.newInstance().apply { setType("1") }, "预警信息")
        }
        if (haveQuanXian(Jurisdiction.rbzb)) {
            tvp_info_release.addTab(InfoReleaseFragment.newInstance().apply { setType("2") }, "日报专报")
        }
        if (haveQuanXian(Jurisdiction.hcbg)) {
            tvp_info_release.addTab(InfoReleaseFragment.newInstance().apply { setType("3") }, "核查报告")
        }
        if (haveQuanXian(Jurisdiction.sbtj)) {
            tvp_info_release.addTab(InfoReleaseFragment.newInstance().apply { setType("7") }, "上报率发布")
        }
        if (haveQuanXian(Jurisdiction.hstz)) {
            tvp_info_release.addTab(InfoReleaseFragment.newInstance().apply { setType("4") }, "会商通知")
        }
        if (haveQuanXian(Jurisdiction.zjdd)) {
            tvp_info_release.addTab(InfoReleaseFragment.newInstance().apply { setType("5") }, "专家调度")
        }
        if (haveQuanXian(Jurisdiction.zcwj)) {
            tvp_info_release.addTab(InfoReleaseFragment.newInstance().apply { setType("6") }, "政策文件")
        }
        tvp_info_release.setTitleColor(ContextCompat.getColor(this, R.color.text_color_normal), ContextCompat.getColor(this, R.color.colorPrimary))
                .setIndicatorHeight(10)
                .setIndicatorColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setTablayoutHeight(40)
                .setTabTextSize(12, 12)
                .setTablayoutBackgroundColor(ContextCompat.getColor(this, R.color.white))
                .showDivider(ContextCompat.getColor(this, R.color.text_color_light))
                .build()
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {

    }

}
