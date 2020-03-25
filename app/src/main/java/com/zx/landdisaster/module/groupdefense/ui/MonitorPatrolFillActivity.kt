package com.zx.landdisaster.module.groupdefense.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.zx.landdisaster.R
import com.zx.landdisaster.app.ConstStrings
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.base.tool.LocationTool
import com.zx.landdisaster.module.disaster.bean.AddFileBean
import com.zx.landdisaster.module.groupdefense.bean.GroupDefenceBean
import com.zx.landdisaster.module.groupdefense.bean.MonitorPatrolFillBean
import com.zx.landdisaster.module.groupdefense.func.utils.DistanceUtils
import com.zx.landdisaster.module.groupdefense.mvp.contract.MonitorPatrolFillContract
import com.zx.landdisaster.module.groupdefense.mvp.model.MonitorPatrolFillModel
import com.zx.landdisaster.module.groupdefense.mvp.presenter.MonitorPatrolFillPresenter
import com.zx.landdisaster.module.other.ui.CameraVedioActivity
import com.zx.landdisaster.module.other.ui.FilePreviewActivity
import com.zx.zxutils.util.ZXDialogUtil
import com.zx.zxutils.util.ZXFileUtil
import com.zx.zxutils.util.ZXNetworkUtil
import com.zx.zxutils.util.ZXTimeUtil
import kotlinx.android.synthetic.main.activity_monitor_patrol_fill.*
import java.io.File
import java.text.SimpleDateFormat


/**
 * Create By admin On 2017/7/11
 * 功能：定量监测
 */
class MonitorPatrolFillActivity : BaseActivity<MonitorPatrolFillPresenter, MonitorPatrolFillModel>(), MonitorPatrolFillContract.View {


    private lateinit var monitorpatroldatas: GroupDefenceBean.Monitorpointinfo

    val fileBeans = arrayListOf<AddFileBean>()


    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean, monitorpatroldatas: GroupDefenceBean.Monitorpointinfo) {
            val intent = Intent(activity, MonitorPatrolFillActivity::class.java)
            intent.putExtra("Monitorpointinfo", monitorpatroldatas)
            activity.startActivity(intent)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_monitor_patrol_fill
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        monitorpatroldatas = intent.getSerializableExtra("Monitorpointinfo") as GroupDefenceBean.Monitorpointinfo


        tv_latitude.text = "经度：正在获取..."
        tv_longitude.text = "纬度：正在获取..."

        getMyLocation()

        tv_monitor_patrol_fill_pkiaaname.text = monitorpatroldatas.pkiaaname
        tv_monitor_patrol_fill_name.text = monitorpatroldatas.name

        val time = System.currentTimeMillis()
        tv_monitor_patrol_fill_monitortime.text = ZXTimeUtil.getTime(time, SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))

        if (mSharedPrefUtil.contains("monitorPatrolBean-${monitorpatroldatas.name}${monitorpatroldatas.name}${monitorpatroldatas.name}${monitorpatroldatas.name}") && mSharedPrefUtil.getObject<MonitorPatrolFillBean>("monitorPatrolBean-${monitorpatroldatas.name}${monitorpatroldatas.name}") != null) {
            ZXDialogUtil.showYesNoDialog(this, "提示", "存在未上传的信息，是否恢复？", "恢复", "取消", { dialog, which ->
                val fileBean = mSharedPrefUtil.getObject<MonitorPatrolFillBean>("monitorPatrolBean-${monitorpatroldatas.name}${monitorpatroldatas.name}")
                fileBean.apply {
                    tv_monitor_patrol_fill_actualdata.setText(if (actualdata == null) "" else actualdata.toString())
                    if (files.isNotEmpty()) {
                        fileBeans.addAll(files)
                        Glide.with(mContext)
                                .load(files[0].path)
                                .into(iv_image)
                        iv_file_delete.visibility = View.VISIBLE
                    } else {
                        Glide.with(mContext)
                                .load(R.drawable.ic_group_defence_image)
                                .into(iv_image)
                        iv_file_delete.visibility = View.GONE
                    }
//                    addFileFragment.setData(files)
                }
            }, { dialog, which ->
                mSharedPrefUtil.remove("monitorPatrolBean-${monitorpatroldatas.name}${monitorpatroldatas.name}")
                ZXFileUtil.deleteFiles(ConstStrings.getLocalPath() + "ReportFile/")
            })
        }


        iv_image.setOnClickListener {
            if (fileBeans.isEmpty()) {
                getPermission(arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)) {
                    CameraVedioActivity.startAction(this, false, 1, 0x10)
                }

            } else {
                var addFileBean = fileBeans[0]
                FilePreviewActivity.startAction(this, false, addFileBean.name, addFileBean.path)
            }

        }

        Glide.with(mContext)
                .load(R.drawable.ic_group_defence_image)
                .into(iv_image)

        iv_file_delete.setOnClickListener {

            Glide.with(mContext)
                    .load(R.drawable.ic_group_defence_image)
                    .into(iv_image)
            iv_file_delete.visibility = View.GONE
            fileBeans.clear()
        }
    }

    private fun getMyLocation() {
        getPermission(arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)) {
            LocationTool.getLocation(this@MonitorPatrolFillActivity) {
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
                fileBeans.clear()
                val fileBean = AddFileBean(data.getStringExtra("name"), data.getIntExtra("type", 1), data.getStringExtra("path"), data.getStringExtra("vedioPath"))
                fileBeans.add(fileBean)
                Glide.with(mContext)
                        .load(fileBean.path)
                        .into(iv_image)
                iv_file_delete.visibility = View.VISIBLE
            }
        }
    }


    /**
     * 提交回调
     */
    override fun addMonitorpatroldataResult(data: String) {
        val files = arrayListOf<File>()
        if (fileBeans.isNotEmpty()) {
            fileBeans.forEach {
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
            mPresenter.addFile(files, data)
        } else {
            onAddMonitorpatrolFileAddResult()
        }
    }

    override fun onAddMonitorpatrolFileAddPregress(progress: Int) {
        handler.post {
            showLoading("正在上传中...", progress)
        }
    }

    override fun onAddMonitorpatrolFileAddResult() {
        ZXFileUtil.deleteFiles(ConstStrings.getLocalPath() + "ReportFile/")
        showToast("提交成功！")
        finish()
    }


    override fun onSubmitErrorResult() {
        ZXDialogUtil.showYesNoDialog(this, "提示", "提交失败，是否保存信息？") { dialog, which ->
            btn_monitor_patrol_save.performClick()
        }
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {
        btn_monitor_patrol_save.setOnClickListener {

            var centerLatitude = intent.getDoubleExtra("latitude", 0.0)
            var centerLongitude = intent.getDoubleExtra("longitude", 0.0)

            var latitude = if (tv_latitude.text != "经度：获取定位失败！") tv_latitude.text.toString().substring(3).toDouble() else 0.0
            var longitude = if (tv_longitude.text != "纬度：获取定位失败！") tv_longitude.text.toString().substring(3).toDouble() else 0.0

            var legal: Int
            if (DistanceUtils.getDistance(centerLatitude, centerLongitude, latitude, longitude) < monitorpatroldatas.legalredius) {
                legal = 1
            } else {
                legal = -1
            }

            val fillBean = MonitorPatrolFillBean(
                    monitorpatroldatas.mpid,
                    et_monitor_patrol_note.text.toString(),
                    monitorpatroldatas.pkiaaname,
                    monitorpatroldatas.name,
                    tv_monitor_patrol_fill_actualdata.text.toString(),
                    ZXTimeUtil.getTime(System.currentTimeMillis(), SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+SSSS")).replace("+0000", "+0800"),
                    monitorpatroldatas.pkiaa,
                    latitude,
                    longitude,
                    "",
                    "",
                    legal,
                    fileBeans)
            mSharedPrefUtil.putObject("monitorPatrolBean-${fillBean.name}${fillBean.pkiaa}", fillBean)
            showToast("保存成功")
            finish()
        }

        btn_monitor_patrol_upload.setOnClickListener {
            if (!ZXNetworkUtil.isConnected()) {
                ZXDialogUtil.showYesNoDialog(this, "提示", "网络连接失败，是否保存信息？") { dialog, which ->
                    btn_monitor_patrol_save.performClick()
                }
                return@setOnClickListener
            }
            ZXDialogUtil.showYesNoDialog(mContext, "提示", "是否确认提交？") { dialog, which ->
                if (tv_monitor_patrol_fill_actualdata.text.isEmpty()) {
                    showToast("请填写地裂信息")
                    return@showYesNoDialog
                }

                var centerLatitude = intent.getDoubleExtra("latitude", 0.0)
                var centerLongitude = intent.getDoubleExtra("longitude", 0.0)

                var latitude = if (tv_latitude.text != "经度：获取定位失败！") tv_latitude.text.toString().substring(3).toDouble() else 0.0
                var longitude = if (tv_longitude.text != "纬度：获取定位失败！") tv_longitude.text.toString().substring(3).toDouble() else 0.0


                var legal: Int
                if (DistanceUtils.getDistance(centerLatitude, centerLongitude, latitude, longitude) < monitorpatroldatas.legalredius) {
                    legal = 1
                } else {
                    legal = -1
                }


                val fillBean = MonitorPatrolFillBean(
                        monitorpatroldatas.mpid,
                        et_monitor_patrol_note.text.toString(),
                        monitorpatroldatas.pkiaaname,
                        monitorpatroldatas.name,
                        tv_monitor_patrol_fill_actualdata.text.toString(),
                        ZXTimeUtil.getTime(System.currentTimeMillis(), SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+SSSS")).replace("+0000", "+0800"),
                        monitorpatroldatas.pkiaa,
                        latitude,
                        longitude,
                        "正常",
                        "有效",
                        legal)

                mPresenter.addMonitorpatroldata(fillBean)
                mSharedPrefUtil.remove("monitorPatrolBean-${fillBean.name}${fillBean.pkiaa}")
            }
        }

        iv_macro_patrol_location.setOnClickListener {
            getMyLocation()
        }
    }

}
