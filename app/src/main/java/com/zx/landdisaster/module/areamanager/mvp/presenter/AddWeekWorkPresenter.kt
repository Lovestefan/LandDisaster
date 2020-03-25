package com.zx.landdisaster.module.areamanager.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.frame.zxmvp.http.upload.UploadRequestBody
import com.zx.landdisaster.base.other.UserManager
import com.zx.landdisaster.module.areamanager.mvp.contract.AddWeekWorkContract
import com.zx.landdisaster.module.disaster.bean.AuditReportFileBean
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class AddWeekWorkPresenter : AddWeekWorkContract.Presenter() {
    override fun getWeekWorkFile(map: Map<String, String>) {
        mModel.getWeekWorkFileData(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<List<AuditReportFileBean>>() {
                    override fun _onNext(t: List<AuditReportFileBean>?) {
                        if (t != null) {
                            mView.getWeekWorkFileResult(t)
                        }else{
                            mView.getWeekWorkFileResult()
                        }
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.handleError(code, message)
                    }

                })
    }

    override fun uploadWeekWork(info: RequestBody) {
        mModel.uploadData(info)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<String>(mView) {
                    override fun _onNext(t: String?) {
                        if (t != null) {
                            mView.onUploadResult(t)
                        }
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.handleError(code, message)
                    }

                })
    }

    override fun uploadWeekWorkFile(files: List<File>, logid: String, reportType: String) {
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        for (file in files) {
            builder.addFormDataPart("files", file.name, RequestBody.create(MediaType.parse("multipart/form-data"), file))
        }
        builder.addFormDataPart("logid", logid)
        builder.addFormDataPart("reportType", reportType)
        builder.addFormDataPart("token", UserManager.getUser().token)
        val uploadRequestBody = UploadRequestBody(builder.build()) { progress, done ->
            if (progress < 100) mView.onUploadFilePregress(progress)
        }
        mModel.uploadFileData(uploadRequestBody)
                .compose(RxHelper.bindToLifecycle<String>(mView))
                .subscribe(object : RxSubscriber<String>() {

                    override fun _onNext(s: String?) {
                        mView.onUploadFileResult()
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.dismissLoading()
                        mView.handleError(code, message)
                    }
                })
    }
}