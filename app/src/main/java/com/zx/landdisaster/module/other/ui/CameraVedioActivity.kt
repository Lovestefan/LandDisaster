package com.zx.landdisaster.module.other.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import com.zx.landdisaster.R
import com.zx.landdisaster.app.ConstStrings
import com.zx.landdisaster.base.BaseActivity
import com.zx.landdisaster.module.other.mvp.contract.CameraVedioContract
import com.zx.landdisaster.module.other.mvp.model.CameraVedioModel
import com.zx.landdisaster.module.other.mvp.presenter.CameraVedioPresenter
import com.zx.zxutils.util.ZXBitmapUtil
import com.zx.zxutils.util.ZXTimeUtil
import com.zx.zxutils.views.CameraView.ZXCameraView
import com.zx.zxutils.views.CameraView.listener.CameraListener
import com.zx.zxutils.views.CameraView.listener.CameraListener.CameraType
import com.zx.zxutils.views.ZXStatusBarCompat
import kotlinx.android.synthetic.main.activity_camera_vedio.*
import java.io.File
import java.text.SimpleDateFormat


/**
 * Create By admin On 2017/7/11
 * 功能：图片、录像
 */
class CameraVedioActivity : BaseActivity<CameraVedioPresenter, CameraVedioModel>(), CameraVedioContract.View {

    companion object {
        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean, cameraType: Int = 0) {
            val intent = Intent(activity, CameraVedioActivity::class.java)
            intent.putExtra("cameraType", cameraType)
            activity.startActivityForResult(intent, 0x02)
            if (isFinish) activity.finish()
        }

        /**
         * 启动器
         */
        fun startAction(activity: Activity, isFinish: Boolean, cameraType: Int = 0, requestCode: Int) {
            val intent = Intent(activity, CameraVedioActivity::class.java)
            intent.putExtra("cameraType", cameraType)
            activity.startActivityForResult(intent, requestCode)
            if (isFinish) activity.finish()
        }
    }

    /**
     * layout配置
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_camera_vedio
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ZXStatusBarCompat.translucent(this);
    }

    /**
     * 初始化
     */
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        val cameraType = intent.getIntExtra("cameraType", 0)
        //设置视频保存路径
        camera_view.setSaveVideoPath(ConstStrings.getLocalPath() + "ReportFile")
                .setCameraMode(if (cameraType == 1) ZXCameraView.BUTTON_STATE_ONLY_CAPTURE else if (cameraType == 2) ZXCameraView.BUTTON_STATE_ONLY_RECORDER else ZXCameraView.BUTTON_STATE_BOTH)
                .setMediaQuality(ZXCameraView.MEDIA_QUALITY_MIDDLE)
                .setMaxVedioDuration(30)
                .showAlbumView(true)
                .setCameraLisenter(object : CameraListener {
                    override fun onCaptureCommit(bitmap: Bitmap) {
                        val time = ZXTimeUtil.getTime(System.currentTimeMillis(), SimpleDateFormat("yyyyMMdd_HHmmss"))
                        val name = time
                        val path = ConstStrings.getLocalPath() + "ReportFile/" + time + ".jpg"
                        ZXBitmapUtil.bitmapToFile(bitmap, File(path))
                        val intent = Intent()
                        intent.putExtra("type", 1)
                        intent.putExtra("path", path)
                        intent.putExtra("name", name)
                        intent.putExtra("vedioPath", "")
                        setResult(0x02, intent)
                        finish()
                    }

                    override fun onRecordCommit(url: String, firstFrame: Bitmap) {
                        val time = ZXTimeUtil.getTime(System.currentTimeMillis(), SimpleDateFormat("yyyyMMdd_HHmmss"))
                        val name = time + ".mp4"
                        val path = ConstStrings.getLocalPath() + "ReportFile/" + time + ".jpg"
                        ZXBitmapUtil.bitmapToFile(firstFrame, File(path))
                        val intent = Intent()
                        intent.putExtra("type", 2)
                        intent.putExtra("path", path)
                        intent.putExtra("vedioPath", url)
                        intent.putExtra("name", name)
                        setResult(0x02, intent)
                        finish()
                    }

                    override fun onActionSuccess(type: CameraType) {

                    }

                    override fun onError(errorType: CameraListener.ErrorType) {
                        //打开Camera失败回调
                    }
                })
        if (cameraType == 1){
            camera_view.setTip("轻触拍照")
        }else if (cameraType == 2){
            camera_view.setTip("长按摄像")
        }
    }

    /**
     * View事件设置
     */
    override fun onViewListener() {

    }

    override fun onPause() {
        super.onPause()
        camera_view.onPause()
    }

    override fun onResume() {
        super.onResume()
        camera_view.onResume()
    }

}
