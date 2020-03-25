package com.zx.landdisaster.module.main.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.frame.zxmvp.http.download.DownInfo
import com.frame.zxmvp.http.download.listener.DownloadOnNextListener
import com.frame.zxmvp.http.download.manager.HttpDownManager
import com.zx.landdisaster.api.ApiConfigModule
import com.zx.landdisaster.app.ConstStrings
import com.zx.landdisaster.module.main.bean.InfoReleaseBean
import com.zx.landdisaster.module.main.mvp.contract.InfoReleaseInfoContract
import com.zx.zxutils.util.ZXDialogUtil
import com.zx.zxutils.util.ZXFileUtil
import java.io.File
import java.net.URLDecoder


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class InfoReleaseInfoPresenter : InfoReleaseInfoContract.Presenter() {
    override fun getFileData(serviceid: String, map: Map<String, String>) {
        mModel.readFile(serviceid, map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<InfoReleaseBean>(mView) {
                    override fun _onNext(t: InfoReleaseBean?) {
                        mView.onReadFileResult(t)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onReadFileResult(null)
                    }

                })
    }

    override fun downloadFile(downUrl: String) {
        val fileName = URLDecoder.decode(downUrl.substring(downUrl.lastIndexOf("/") + 1), "UTF-8")
        val downInfo = DownInfo(ApiConfigModule.URL_FILE + downUrl.replace(ApiConfigModule.BASE_IP + ApiConfigModule.URL_FILE, ""))
        downInfo.baseUrl = ApiConfigModule.BASE_IP
        downInfo.savePath = ConstStrings.getLocalPath() + "Pdf_file/" + fileName
        downInfo.listener = object : DownloadOnNextListener<Any>() {
            override fun onNext(o: Any) {

            }

            override fun onStart() {
                ZXDialogUtil.showLoadingDialog(mContext, "正在下载中，请稍后...", 0)
            }

            override fun onComplete(file: File) {
                mView.onFileDownloadResult(file)
                ZXDialogUtil.dismissLoadingDialog()
            }

            override fun updateProgress(progress: Int) {
                ZXDialogUtil.showLoadingDialog(mContext, "正在下载中，请稍后...", progress)
            }
        }
        if (ZXFileUtil.isFileExists(ConstStrings.getLocalPath() + "Pdf_file/" + fileName)) {
            mView.onFileDownloadResult(File(ConstStrings.getLocalPath() + "Pdf_file/" + fileName))
        } else {
            HttpDownManager.getInstance().startDown(downInfo)
        }
    }
}