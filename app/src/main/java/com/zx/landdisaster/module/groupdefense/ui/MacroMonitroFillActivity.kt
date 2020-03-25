package com.zx.landdisaster.module.groupdefense.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.zx.landdisaster.R
import com.zx.landdisaster.app.ConstStrings
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.base.tool.LocationTool
import com.zx.landdisaster.module.disaster.bean.AddFileBean
import com.zx.landdisaster.module.groupdefense.bean.GroupDefenceBean
import com.zx.landdisaster.module.groupdefense.bean.MacroMonitroCurrentTypeBean
import com.zx.landdisaster.module.groupdefense.bean.MacroMonitroFillBean
import com.zx.landdisaster.module.groupdefense.bean.MonitorPatrolFillBean
import com.zx.landdisaster.module.groupdefense.entity.MacroMonitroFillEntity
import com.zx.landdisaster.module.groupdefense.func.adapter.MacroMonitroFillAdapter
import com.zx.landdisaster.module.groupdefense.func.utils.DistanceUtils
import com.zx.landdisaster.module.groupdefense.mvp.contract.MacroMonitroFillContract
import com.zx.landdisaster.module.groupdefense.mvp.model.MacroMonitroFillModel
import com.zx.landdisaster.module.groupdefense.mvp.presenter.MacroMonitroFillPresenter
import com.zx.landdisaster.module.other.ui.AddVideoFragment
import com.zx.landdisaster.module.other.ui.CameraVedioActivity
import com.zx.landdisaster.module.other.ui.FilePreviewActivity
import com.zx.zxutils.other.ZXInScrollRecylerManager
import com.zx.zxutils.util.ZXDialogUtil
import com.zx.zxutils.util.ZXFileUtil
import com.zx.zxutils.util.ZXFragmentUtil
import com.zx.zxutils.util.ZXNetworkUtil
import kotlinx.android.synthetic.main.activity_macro_monitro_fill.*
import java.io.File


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class MacroMonitroFillActivity : BaseActivity<MacroMonitroFillPresenter, MacroMonitroFillModel>(), MacroMonitroFillContract.View {

    private lateinit var macroMonitroBean: GroupDefenceBean.Macropatrolset

    private lateinit var addFileFragment: AddVideoFragment

    val dataList = arrayListOf<MacroMonitroCurrentTypeBean>()

    var adapterPosition = 0


    private val macroMonitroFillAdapter = MacroMonitroFillAdapter(dataList)


    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean, bean: GroupDefenceBean.Macropatrolset, latitude: Double, longitude: Double) {
            val intent = Intent(activity, MacroMonitroFillActivity::class.java)
            intent.putExtra("Macropatrolset", bean)
            intent.putExtra("latitude", latitude)
            intent.putExtra("longitude", longitude)

            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_macro_monitro_fill
    }

    override fun onSubmitErrorResult() {
        ZXDialogUtil.showYesNoDialog(this, "提示", "提交失败，是否保存信息？") { dialog, which ->
            btn_macro_patrol_save.performClick()
        }
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        macroMonitroBean = intent.getSerializableExtra("Macropatrolset") as GroupDefenceBean.Macropatrolset



        tv_latitude.text = "经度：正在获取..."
        tv_longitude.text = "纬度：正在获取..."

        mPresenter.findCurrentType(macroMonitroBean.setid)

        if (mSharedPrefUtil.contains("macroMonitroBean-${macroMonitroBean.name}${macroMonitroBean.pkiaa}") && mSharedPrefUtil.getObject<MonitorPatrolFillBean>("macroMonitroBean-${macroMonitroBean.name}${macroMonitroBean.pkiaa}") != null) {
            ZXDialogUtil.showYesNoDialog(this, "提示", "存在未上传的信息，是否恢复？", "恢复", "取消", { dialog, which ->
                val macroMonitroFillEntity = mSharedPrefUtil.getObject<MacroMonitroFillEntity>("macroMonitroBean-${macroMonitroBean.name}${macroMonitroBean.pkiaa}")

                if (macroMonitroFillEntity != null) {

                    dataList.clear()

                    tv_latitude.text = "经度：" + macroMonitroFillEntity.macropatroldata.latitude
                    tv_longitude.text = "纬度：" + macroMonitroFillEntity.macropatroldata.longitude

                    dataList.addAll(macroMonitroFillEntity.macropatrolphenomenas)

                    et_macro_patrol_note.setText(macroMonitroFillEntity.macropatroldata.note)

                    macroMonitroFillAdapter.notifyDataSetChanged()

                    if (mSharedPrefUtil.contains("macroMonitroBean-vedioPath-${macroMonitroBean.name}${macroMonitroBean.pkiaa}") && mSharedPrefUtil.getObject<AddFileBean>("macroMonitroBean-vedioPath-${macroMonitroBean.name}${macroMonitroBean.pkiaa}") != null) {

                        val addVedioBean = mSharedPrefUtil.getObject<AddFileBean>("macroMonitroBean-vedioPath-${macroMonitroBean.name}${macroMonitroBean.pkiaa}")
                        if (addVedioBean != null) {
                            var list = arrayListOf<AddFileBean>()
                            list.add(addVedioBean)
                            addFileFragment.setData(list)
                        }
                    }
                }

            }, { dialog, which ->

                macroMonitroFillAdapter.notifyDataSetChanged()
                mSharedPrefUtil.remove("macroMonitroBean-${macroMonitroBean.name}${macroMonitroBean.pkiaa}")
                ZXFileUtil.deleteFiles(ConstStrings.getLocalPath() + "ReportFile/")
            })
        }

        rv_monitor_patrol_fill.apply {
            layoutManager = ZXInScrollRecylerManager(mContext)
            adapter = macroMonitroFillAdapter
        }

        ZXFragmentUtil.addFragment(supportFragmentManager, AddVideoFragment.newInstance(AddVideoFragment.FileType.VEDIO, 1, "应急调查视频录制").apply {
            addFileFragment = this
        }, R.id.fm_add_file)

        addFileFragment.setOnEditCallBack {
            scroll_view.fullScroll(0)
        }

        macroMonitroFillAdapter.setOnItemChildClickListener { adapter, view, position ->


            adapterPosition = position

            if (view.id == R.id.iv_camera) {

                getPermission(arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)) {
                    CameraVedioActivity.startAction(this, false, 1, 0x10)
                }


            } else if (view.id == R.id.iv_iamge) {

                var fileBean = dataList[position].fileBean
                if (fileBean != null) {
                    if (fileBean.type == 1) {
                        FilePreviewActivity.startAction(this, false, fileBean.name, fileBean.path)
                    }
                }
            } else if (view.id == R.id.iv_file_delete) {

                dataList[adapterPosition].fileBean = null
                macroMonitroFillAdapter.notifyDataSetChanged()
            }

        }


    }


    private fun getMyLocation() {

        getPermission(arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)) {
            LocationTool.getLocation(this@MacroMonitroFillActivity) {
                if (it != null) {
                    tv_latitude.text = "经度：" + it.latitude
                    tv_longitude.text = "纬度：" + it.longitude
                    showToast("定位信息获取成功")
                } else {
                    tv_latitude.text = "经度：获取定位失败！"
                    tv_longitude.text = "纬度：获取定位失败！"
                    showToast("定位信息获取失败")
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0x10) {//拍摄-录像
            if (data != null) {

                val fileParent = File(data.getStringExtra("path")).parent
                var name = ""
                if (dataList[adapterPosition].typecode.length < 2) {
                    name = "000" + dataList[adapterPosition].typecode + "_" + data.getStringExtra("name") + ".jpg"
                } else {
                    name = "00" + dataList[adapterPosition].typecode + "_" + data.getStringExtra("name") + ".jpg"
                }

                ZXFileUtil.rename(data.getStringExtra("path"), name)

                val fileBean = AddFileBean(name, data.getIntExtra("type", 1), fileParent + File.separator + name, data.getStringExtra("vedioPath"))
                dataList[adapterPosition].fileBean = fileBean
                macroMonitroFillAdapter.notifyDataSetChanged()
            }
        } else {
            addFileFragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun findCurrentTypeResult(list: List<MacroMonitroCurrentTypeBean>) {

        getMyLocation()
        dataList.addAll(list)

        macroMonitroFillAdapter.notifyDataSetChanged()

    }


    override fun addMacropatroldataResult(data: String) {

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


        dataList.forEach {

            if (it.fileBean != null) {
                val file = File(it.fileBean!!.path)
                files.add(file)
            }

        }

        if (files.isNotEmpty()) {
            mPresenter.addFile(files, data)
        } else {
            onAddMacropatroldataFileAddResult()
        }
    }

    override fun onAddMacropatroldataFileAddPregress(progress: Int) {
        handler.post {
            showLoading("正在上传中...", progress)
        }
    }

    override fun onAddMacropatroldataFileAddResult() {
        mSharedPrefUtil.remove("macroMonitroBean-${macroMonitroBean.name}${macroMonitroBean.pkiaa}")
        mSharedPrefUtil.remove("macroMonitroBean-vedioPath-${macroMonitroBean.name}${macroMonitroBean.pkiaa}")
        ZXFileUtil.deleteFiles(ConstStrings.getLocalPath() + "ReportFile/")
        showToast("提交成功！")
        finish()
    }


    /**
     * View事件设置
     */
    override fun onViewListener() {
        iv_macro_patrol_location.setOnClickListener {
            getMyLocation()
        }

        btn_macro_patrol_save.setOnClickListener {

            var centerLatitude = intent.getDoubleExtra("latitude", 0.0)
            var centerLongitude = intent.getDoubleExtra("longitude", 0.0)

            var latitude = if (tv_latitude.text != "经度：获取定位失败！") tv_latitude.text.toString().substring(3).toDouble() else 0.0
            var longitude = if (tv_longitude.text != "纬度：获取定位失败！") tv_longitude.text.toString().substring(3).toDouble() else 0.0

            var legal: Int
            if (DistanceUtils.getDistance(centerLatitude, centerLongitude, latitude, longitude) < macroMonitroBean.legalradius) {
                legal = 1
            } else {
                legal = -1
            }

            var bean = MacroMonitroFillEntity(MacroMonitroFillEntity.Macropatroldata(
                    macroMonitroBean.setid,
                    et_macro_patrol_note.text.toString(),
                    macroMonitroBean.pkiaa,
                    if (tv_latitude.text != "经度：获取定位失败！") tv_latitude.text.toString().substring(3).toDouble() else 0.0,
                    macroMonitroBean.pkiaaname,
                    if (tv_longitude.text != "纬度：获取定位失败！") tv_longitude.text.toString().substring(3).toDouble() else 0.0,
                    legal,
                    "有效",
                    "正常",
                    "无异常"),
                    dataList)

            mSharedPrefUtil.putObject("macroMonitroBean-${macroMonitroBean.name}${macroMonitroBean.pkiaa}", bean)
            if (addFileFragment.fileBeans.isNotEmpty()) {
                mSharedPrefUtil.putObject("macroMonitroBean-vedioPath-${macroMonitroBean.name}${macroMonitroBean.pkiaa}", addFileFragment.fileBeans.first())
            }

            showToast("保存成功")
            finish()
        }



        btn_macro_patrol_upload.setOnClickListener {

            if (!ZXNetworkUtil.isConnected()) {
                ZXDialogUtil.showYesNoDialog(this, "提示", "网络连接失败，是否保存信息？") { dialog, which ->
                    btn_macro_patrol_save.performClick()
                }
                return@setOnClickListener
            }

            ZXDialogUtil.showYesNoDialog(mContext, "提示", "是否确认提交？") { dialog, which ->

                var centerLatitude = intent.getDoubleExtra("latitude", 0.0)
                var centerLongitude = intent.getDoubleExtra("longitude", 0.0)

                var latitude = if (tv_latitude.text != "经度：获取定位失败！") tv_latitude.text.toString().substring(3).toDouble() else 0.0
                var longitude = if (tv_longitude.text != "纬度：获取定位失败！") tv_longitude.text.toString().substring(3).toDouble() else 0.0

                var legal: Int
                if (DistanceUtils.getDistance(centerLatitude, centerLongitude, latitude, longitude) < macroMonitroBean.legalradius) {
                    legal = 1
                } else {
                    legal = -1
                }

                var macropatrolphenomenas = arrayListOf<MacroMonitroFillBean.Macropatrolphenomenas>()

                dataList.forEach {
                    if (it.exist == 1) {
                        macropatrolphenomenas.add(MacroMonitroFillBean.Macropatrolphenomenas(it.exist, it.typeid))
                    }
                }

                var bean = MacroMonitroFillBean(MacroMonitroFillBean.Macropatroldata(
                        macroMonitroBean.setid,
                        et_macro_patrol_note.text.toString(),
                        macroMonitroBean.pkiaa,
                        latitude,
                        macroMonitroBean.pkiaaname,
                        longitude,
                        legal,
                        "有效",
                        "正常",
                        "无异常"),
                        macropatrolphenomenas)

                mPresenter.addMacropatroldata(bean)
            }


        }
    }


}
