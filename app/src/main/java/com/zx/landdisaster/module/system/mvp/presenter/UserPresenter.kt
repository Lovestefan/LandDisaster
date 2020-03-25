package com.zx.landdisaster.module.system.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.frame.zxmvp.http.download.DownInfo
import com.frame.zxmvp.http.download.listener.DownloadOnNextListener
import com.frame.zxmvp.http.download.manager.HttpDownManager
import com.zx.landdisaster.api.ApiConfigModule
import com.zx.landdisaster.app.ConstStrings
import com.zx.landdisaster.module.main.bean.VersionBean
import com.zx.landdisaster.module.system.mvp.contract.UserContract
import com.zx.zxutils.util.ZXDialogUtil
import com.zx.zxutils.util.ZXFileUtil
import java.io.File


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class UserPresenter : UserContract.Presenter() {
    override fun loginOut() {
        mModel.loginOutData()
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<String>(mView) {
                    override fun _onNext(t: String?) {
                        mView.onLoginOutResult()
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onLoginOutResult()
                    }

                })
    }

    override fun downloadApk(downUrl: String) {
        val fileName = downUrl.substring(downUrl.lastIndexOf("/") + 1)
        val downInfo = DownInfo( ApiConfigModule.URL_FILE+downUrl)
        downInfo.baseUrl = ApiConfigModule.BASE_IP
        downInfo.savePath = ConstStrings.getApkPath() + fileName
        downInfo.listener = object : DownloadOnNextListener<Any>() {
            override fun onNext(o: Any) {

            }

            override fun onStart() {
                ZXDialogUtil.showLoadingDialog(mContext, "正在下载中，请稍后...", 0)
            }

            override fun onComplete(file: File) {
                mView.onApkDownloadResult(file)
                ZXDialogUtil.dismissLoadingDialog()
            }

            override fun updateProgress(progress: Int) {
                ZXDialogUtil.showLoadingDialog(mContext, "正在下载中，请稍后...", progress)
            }
        }
        if (ZXFileUtil.isFileExists(ConstStrings.getApkPath() + fileName)) {
            mView.onApkDownloadResult(File(ConstStrings.getApkPath() + fileName))
        } else {
            HttpDownManager.getInstance().startDown(downInfo)
        }
    }

    override fun getVerson() {
        mModel.versionData()
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<VersionBean>(mView) {
                    override fun _onNext(t: VersionBean?) {
                        mView.onVersionResult(t)
                    }

                    override fun _onError(code: String?, message: String?) {
//                        mView.handleError(code, message)
                    }

                })
    }

}