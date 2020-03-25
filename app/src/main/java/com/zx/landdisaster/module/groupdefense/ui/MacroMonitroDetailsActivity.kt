package com.zx.landdisaster.module.groupdefense.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.zx.landdisaster.R
import com.zx.landdisaster.api.ApiConfigModule
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.module.disaster.bean.AddFileBean
import com.zx.landdisaster.module.disaster.bean.AuditReportFileBean
import com.zx.landdisaster.module.groupdefense.bean.MacroMonitroBean
import com.zx.landdisaster.module.groupdefense.bean.MacroMonitroCurrentTypeBean
import com.zx.landdisaster.module.groupdefense.func.adapter.MacroMonitroDetailsAdapter
import com.zx.landdisaster.module.groupdefense.mvp.contract.MacroMonitroDetailsContract
import com.zx.landdisaster.module.groupdefense.mvp.model.MacroMonitroDetailsModel
import com.zx.landdisaster.module.groupdefense.mvp.presenter.MacroMonitroDetailsPresenter
import com.zx.landdisaster.module.other.ui.AddFileFragment
import com.zx.zxutils.other.ZXInScrollRecylerManager
import com.zx.zxutils.util.ZXFragmentUtil
import com.zx.zxutils.util.ZXTimeUtil
import kotlinx.android.synthetic.main.activity_macro_monitro_details.*


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class MacroMonitroDetailsActivity : BaseActivity<MacroMonitroDetailsPresenter, MacroMonitroDetailsModel>(), MacroMonitroDetailsContract.View {


    private lateinit var addFileFragment: AddFileFragment
    private var fileNameMap = hashMapOf<String, String>()

    private lateinit var macroMonitroBean: MacroMonitroBean


    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean, bean: MacroMonitroBean) {
            val intent = Intent(activity, MacroMonitroDetailsActivity::class.java)
            intent.putExtra("MacroMonitroBean", bean)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_macro_monitro_details
    }


    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        macroMonitroBean = intent.getSerializableExtra("MacroMonitroBean") as MacroMonitroBean

        fileNameMap.put("0001", "地面新裂缝")
        fileNameMap.put("0002", "地面新塌陷")
        fileNameMap.put("0003", "墙上新塌陷")
        fileNameMap.put("0004", "墙上新裂缝")
        fileNameMap.put("0005", "新地鼓")
        fileNameMap.put("0006", "小型崩塌")
        fileNameMap.put("0007", "房屋变形")
        fileNameMap.put("0008", "新泉水新湿地")
        fileNameMap.put("0009", "井塘漏水")
        fileNameMap.put("0010", "树木歪斜")
        fileNameMap.put("0011", "地声")
        fileNameMap.put("0012", "动物异常")
        fileNameMap.put("0013", "其他现象")


        val dataList = arrayListOf<Pair<String, String>>()

        dataList.add(Pair("灾害点编号", if (macroMonitroBean.pkiaa != null) macroMonitroBean.pkiaa else ""))
        dataList.add(Pair("灾害点名称", if (macroMonitroBean.pkiaaname != null) macroMonitroBean.pkiaaname else ""))
        dataList.add(Pair("监测时间", if (macroMonitroBean.createtime != null) ZXTimeUtil.getTime(macroMonitroBean.createtime)!! else ""))


        if (macroMonitroBean.legal != null) {
            if (macroMonitroBean.legal == 1) {
                dataList.add(Pair("是否合法", "合法"))
            } else {
                dataList.add(Pair("是否合法", "不合法"))
            }
        } else {
            dataList.add(Pair("是否合法", ""))
        }

        dataList.add(Pair("数据有效性", if (macroMonitroBean.effectivity != null) macroMonitroBean.effectivity else ""))
        dataList.add(Pair("告警等级", if (macroMonitroBean.alarmlevel != null) macroMonitroBean.alarmlevel else ""))


        dataList.add(Pair("其他现象", if (macroMonitroBean.others != null) macroMonitroBean.others else ""))


        dataList.add(Pair("经度", if (macroMonitroBean.longitude != null) macroMonitroBean.longitude.toString() else ""))
        dataList.add(Pair("纬度", if (macroMonitroBean.latitude != null) macroMonitroBean.latitude.toString() else ""))


        edit_macro_patrol_note.setText(if (macroMonitroBean.note != null) macroMonitroBean.note else "")
        edit_macro_patrol_note.isEnabled = false

        edit_macro_patrol_macroappear.setText(if (macroMonitroBean.macroappear != null) macroMonitroBean.macroappear else "")
        edit_macro_patrol_macroappear.isEnabled = false

        rv_macro_monitro_details.apply {
            layoutManager = ZXInScrollRecylerManager(mContext)
            adapter = MacroMonitroDetailsAdapter(dataList)
        }

        ZXFragmentUtil.addFragment(supportFragmentManager, AddFileFragment.newInstance().apply {
            addFileFragment = this
        }, R.id.fm_add_file)

        addFileFragment.setOnEditCallBack {
            scroll_view.fullScroll(0)
        }

        mPresenter.findCurrentType(macroMonitroBean.setid)
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {

    }

    override fun findCurrentTypeResult(list: List<MacroMonitroCurrentTypeBean>) {

        fileNameMap.clear()
        list.forEach {

            if (it.typecode.length < 2) {
                it.typecode = "000" + it.typecode
            } else {
                it.typecode = "00" + it.typecode
            }

            fileNameMap.put(it.typecode, it.desc)
        }

        mPresenter.getMacroMonitrolDetailsFileList(macroMonitroBean.logid)


    }

    override fun getMacroMonitrolDetailsFileResult(disasterList: List<AuditReportFileBean>) {
        var list = arrayListOf<AddFileBean>()
        disasterList.forEach {
            val type = if (it.name.endsWith("jpg") || it.name.endsWith("png")) {
                1
            } else if (it.name.endsWith("avi") || it.name.endsWith("mp4") || it.name.endsWith("rmvb") || it.name.endsWith("3gp")) {
                2
            } else {
                3
            }

            if (type == 1) {
                var name = fileNameMap[it.name.substring(0, 4)]
                list.add(AddFileBean(name!!, type, ApiConfigModule.BASE_IP + ApiConfigModule.URL_FILE + it.path))
            }

        }

        addFileFragment.setData(list)
        addFileFragment.setCanEdit(false)
    }

    override fun getMacroMonitrolDetailsFileResult() {
        addFileFragment.setCanEdit(false)

    }

    override fun getMacroMonitrolDetailsFileError() {
        addFileFragment.setCanEdit(false)
    }


}
