package com.zx.landdisaster.module.groupdefense.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.frame.zxmvp.http.upload.UploadRequestBody
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.base.other.UserManager
import com.zx.landdisaster.module.groupdefense.bean.MacroMonitroCurrentTypeBean
import com.zx.landdisaster.module.groupdefense.bean.MacroMonitroFillBean
import com.zx.landdisaster.module.groupdefense.mvp.contract.MacroMonitroFillContract
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class MacroMonitroFillPresenter : MacroMonitroFillContract.Presenter() {
    override fun findCurrentType(setid: String) {
        mModel.findCurrentType(setid)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<List<MacroMonitroCurrentTypeBean>>(mView) {
                    override fun _onNext(t: List<MacroMonitroCurrentTypeBean>?) {

                        if (t != null) {
                            mView.findCurrentTypeResult(t)
                        }

                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.dismissLoading()
                        mView.handleError(code, message)
                    }

                })
    }


    override fun addMacropatroldata(fillBean: MacroMonitroFillBean) {

        mModel.addMacropatroldata(ApiParamUtil.addMacroMonitrodata(fillBean))
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<String>(mView) {
                    override fun _onNext(t: String?) {

                        if (t != null) {
                            mView.addMacropatroldataResult(t)
                        }

                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.dismissLoading()
                        mView.handleError(code, message)
                        mView.onSubmitErrorResult()
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
        val uploadRequestBody = UploadRequestBody(builder.build()) { progress, _ ->
            if (progress < 100) mView.onAddMacropatroldataFileAddPregress(progress)
        }

        mModel.uploadDisasterpoint(uploadRequestBody)
                .compose(RxHelper.bindToLifecycle<String>(mView))
                .subscribe(object : RxSubscriber<String>() {

                    override fun _onNext(s: String?) {
                        mView.onAddMacropatroldataFileAddResult()
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.dismissLoading()
                        mView.handleError(code, message)
                    }
                })
    }
}