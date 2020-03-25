package com.zx.landdisaster.module.areamanager.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import com.jzxiang.pickerview.TimePickerDialog
import com.jzxiang.pickerview.data.Type
import com.zx.landdisaster.R
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.base.BaseFragment
import com.zx.landdisaster.base.other.UserManager
import com.zx.landdisaster.module.areamanager.bean.WeekWorkListBean
import com.zx.landdisaster.module.areamanager.mvp.contract.AddWeekWorkContract
import com.zx.landdisaster.module.areamanager.mvp.model.AddWeekWorkModel
import com.zx.landdisaster.module.areamanager.mvp.presenter.AddWeekWorkPresenter
import com.zx.landdisaster.module.disaster.bean.AddFileBean
import com.zx.landdisaster.module.disaster.bean.AuditReportFileBean
import com.zx.landdisaster.module.other.ui.AddFileFragment
import com.zx.zxutils.util.ZXDialogUtil
import com.zx.zxutils.util.ZXFragmentUtil
import com.zx.zxutils.util.ZXSystemUtil
import com.zx.zxutils.util.ZXTimeUtil
import kotlinx.android.synthetic.main.activity_addweekwork.*
import java.io.File

/**
 * Create By admin On 2019/4/18
 * 功能：工作周报填报
 */
class AddWeekWork : BaseFragment<AddWeekWorkPresenter, AddWeekWorkModel>(), AddWeekWorkContract.View {

    private lateinit var addFileFragment: AddFileFragment

    companion object {
        /**
         * 启动器
         */

        fun newInstance(): AddWeekWork {
            val fragment = AddWeekWork()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }

        fun newInstance(data: WeekWorkListBean): AddWeekWork {
            val fragment = AddWeekWork()
            val bundle = Bundle()
            bundle.putString("townsname", data.townsname)
            bundle.putLong("createtime", data.createtime)
            bundle.putString("recordername", data.recordername)
            bundle.putString("content", data.content)
            bundle.putString("reportid", data.reportid)
            fragment.arguments = bundle
            return fragment
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_addweekwork
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        if (arguments!!.getString("townsname") != null) {
            topLayout.setMidText("工作周报查看")

            val townsname = arguments!!.getString("townsname")
            val createtime = arguments!!.getLong("createtime", 0)
            val recordername = arguments!!.getString("recordername")
            val content = arguments!!.getString("content")
            val reportid = arguments!!.getString("reportid")

            et_xzName.setText(townsname)
            et_xzName.isEnabled = false
            tv_weekWorkTime.setText(ZXTimeUtil.millis2String(createtime))
            et_pianquPeople.setText(recordername)
            et_pianquPeople.isEnabled = false
            et_weekWorkInfo.setText(content)
            et_weekWorkInfo.isEnabled = false
            btn_upload.visibility = View.GONE
            tv_weekWorkTime.setCompoundDrawables(null, null, null, null)

            mPresenter.getWeekWorkFile(ApiParamUtil.getWeekWorkFileList(reportid, 2))

        } else {
            tv_weekWorkTime.text = ZXTimeUtil.getCurrentTime()
            et_pianquPeople.setText(UserManager.getUser().currentUser!!.name)
        }

        ZXFragmentUtil.addFragment(childFragmentManager,
                AddFileFragment.newInstance(AddFileFragment.FileType.VEDIO).apply {
                    addFileFragment = this
                }, R.id.fm_add_file)
        addFileFragment.setOnEditCallBack {
            scroll_view.fullScroll(0)
        }
    }

    override fun getWeekWorkFileResult() {
        fm_add_file.visibility = View.GONE
    }

    override fun getWeekWorkFileResult(fileList: List<AuditReportFileBean>) {
        val addFileList = arrayListOf<AddFileBean>()
        if (fileList.isNotEmpty()) {
            fileList.forEach {
                addFileList.add(AddFileBean.reSetType(it.type, it.name, it.path))
            }
        }
        addFileFragment.setData(addFileList)
        addFileFragment.setCanEdit(false)
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {
        //上传
        btn_upload.setOnClickListener {
            if (et_xzName.text.isEmpty())
                showToast("请输入乡镇名称")
            else if (et_pianquPeople.text.isEmpty())
                showToast("请输入片区负责人")
            else if (et_weekWorkInfo.text.isEmpty())
                showToast("请输入本周工作情况")
            else
                ZXDialogUtil.showYesNoDialog(activity!!, "提示", "是否上传？") { dialog, which ->
                    mPresenter.uploadWeekWork(ApiParamUtil.uploadWeekWork(et_xzName.text.toString(), tv_weekWorkTime.text.toString(),
                            et_pianquPeople.text.toString(), et_weekWorkInfo.text.toString()))
                }
            ZXSystemUtil.closeKeybord(activity!!)
        }
        topLayout.setLeftClickListener { onBackPressed() }

        tv_weekWorkTime.setOnClickListener {
            if (btn_upload.visibility == View.VISIBLE) {
                TimePickerDialog.Builder().apply {
                    setCallBack { _, millseconds ->
                        tv_weekWorkTime.text = ZXTimeUtil.getTime(millseconds)
                    }
                    setCyclic(true)
                    setMaxMillseconds(System.currentTimeMillis())
                    setThemeColor(ContextCompat.getColor(mContext!!, R.color.colorPrimary))
                    setType(Type.ALL)
                    setWheelItemTextNormalColor(ContextCompat.getColor(mContext!!, R.color.text_color_light))
                    setWheelItemTextSelectorColor(ContextCompat.getColor(mContext!!, R.color.text_color_normal))
                    setTitleStringId("请选择时间")

                }.build().show(fragmentManager, "time_select")
            }
        }
    }

    fun onBackPressed() {
        if (btn_upload.visibility == View.GONE) {
            activity!!.finish()
            return
        }
        ZXDialogUtil.showYesNoDialog(activity!!, "提示", "确定要退出周报填写吗？") { dialog, which ->
            activity!!.finish()
        }
    }

    override fun onUploadFileResult() {
        showToast("提交成功！")
        activity!!.finish()
    }

    override fun onUploadFilePregress(progress: Int) {
        handler.post {
            showLoading("正在上传中...", progress)
        }
    }

    /**
     * 上传回调
     */
    override fun onUploadResult(logid: String) {
        val files = arrayListOf<File>()
        if (addFileFragment.fileBeans.isNotEmpty()) {
            addFileFragment.fileBeans.forEach {
                val path = if (it.type == 1) {
                    it.path
                } else {
                    it.vedioPath
                }
                if (!path.startsWith("http")) {//只上传本地选择的文件
                    val file = File(path)
                    files.add(file)
                }
            }
        }
        if (files.isNotEmpty()) {
            mPresenter.uploadWeekWorkFile(files, logid, "2")
        } else {
            onUploadFileResult()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        addFileFragment.onActivityResult(requestCode, resultCode, data)
    }

}