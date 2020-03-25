package com.zx.landdisaster.module.other.mvp.presenter

import com.frame.zxmvp.http.download.DownInfo
import com.frame.zxmvp.http.download.listener.DownloadOnNextListener
import com.frame.zxmvp.http.download.manager.HttpDownManager
import com.zx.landdisaster.api.ApiConfigModule
import com.zx.landdisaster.app.ConstStrings
import com.zx.landdisaster.module.other.mvp.contract.AddFileContract
import com.zx.zxutils.util.ZXDialogUtil
import com.zx.zxutils.util.ZXFileUtil
import java.io.File


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class AddFilePresenter : AddFileContract.Presenter() {

    override fun downloadFile(downUrl: String) {
        val fileName = downUrl.substring(downUrl.lastIndexOf("/") + 1)
        val downInfo = DownInfo( ApiConfigModule.URL_FILE+downUrl.replace(ApiConfigModule.BASE_IP + ApiConfigModule.URL_FILE,""))
        downInfo.baseUrl = ApiConfigModule.BASE_IP
        downInfo.savePath = ConstStrings.getLocalPath()  + "ReportFile/"+ fileName
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
        if (ZXFileUtil.isFileExists(ConstStrings.getLocalPath() + "ReportFile/" + fileName)) {
            mView.onFileDownloadResult(File(ConstStrings.getLocalPath() + "ReportFile/" + fileName))
        } else {
            HttpDownManager.getInstance().startDown(downInfo)
        }
    }
}