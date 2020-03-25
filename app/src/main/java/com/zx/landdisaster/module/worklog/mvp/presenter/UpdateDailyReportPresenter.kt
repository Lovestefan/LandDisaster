package com.zx.landdisaster.module.worklog.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.frame.zxmvp.http.upload.UploadRequestBody
import com.zx.landdisaster.base.other.UserManager
import com.zx.landdisaster.module.worklog.mvp.contract.UpdateDailyReportContract
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class UpdateDailyReportPresenter : UpdateDailyReportContract.Presenter() {

    override fun submitDaily(info : RequestBody) {
        mModel.dailySubmitData(info)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object  : RxSubscriber<String>(mView){
                    override fun _onNext(t: String?) {
                        if (t != null) {
                            mView.onDailySubmitResult(t)
                        }
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.handleError(code, message)
                    }

                })
    }

    override fun addFile(files: List<File>, logid: String) {
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        for (file in files) {
            builder.addFormDataPart("files", file.name, RequestBody.create(MediaType.parse("multipart/form-data"), file))
        }
        builder.addFormDataPart("logid", logid)
        builder.addFormDataPart("token", UserManager.getUser().token)
        val uploadRequestBody = UploadRequestBody(builder.build()) { progress, done ->
            if (progress < 100) mView.onDailyFileAddPregress(progress)
        }
        mModel.dailyFileAddData(uploadRequestBody)
                .compose(RxHelper.bindToLifecycle<String>(mView))
                .subscribe(object : RxSubscriber<String>() {

                    override fun _onNext(s: String?) {
                        mView.onDailyFileAddResult()
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.dismissLoading()
                        mView.handleError(code, message)
                    }
                })
    }

}