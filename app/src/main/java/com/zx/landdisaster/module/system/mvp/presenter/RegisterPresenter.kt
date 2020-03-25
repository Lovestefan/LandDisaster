package com.zx.landdisaster.module.system.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.frame.zxmvp.http.upload.UploadRequestBody
import com.zx.landdisaster.module.system.bean.AreaBean
import com.zx.landdisaster.module.system.mvp.contract.RegisterContract
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class RegisterPresenter : RegisterContract.Presenter() {

    override fun hearUpload(file: File, personid: String) {
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        builder.addFormDataPart("file", file.name, RequestBody.create(MediaType.parse("multipart/form-data"), file))
        builder.addFormDataPart("personid", personid)
        val uploadRequestBody = UploadRequestBody(builder.build()) { progress, done ->
            if (progress < 100) mView.onUploadFilePregress(progress)
        }
        mModel.getHearUpload(uploadRequestBody)
                .compose(RxHelper.bindToLifecycle<String>(mView))
                .subscribe(object : RxSubscriber<String>() {

                    override fun _onNext(s: String?) {
                        mView.dismissLoading()
                        mView.onHearUploadResult()
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.dismissLoading()
                        mView.handleError(code, message)
                    }
                })
    }

    override fun register(map: Map<String, String>) {
        mModel.getRegister(map)
                .compose(RxHelper.bindToLifecycle<String>(mView))
                .subscribe(object : RxSubscriber<String>() {
                    override fun _onNext(t: String?) {
                        mView.onRegisterResult(t!!)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.handleError(code, message)
                    }

                })
    }

    override fun getFindByParent(map: Map<String, String>) {
        mModel.findByParent(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<List<AreaBean>>() {
                    override fun _onNext(t: List<AreaBean>?) {
                        mView.onFindByParentResult(t)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.handleError(code, message)
                    }

                })
    }

    override fun getCheckAccountData(map: Map<String, String>) {
        mModel.getCheckAccount(map)
                .compose(RxHelper.bindToLifecycle<String>(mView))
                .subscribe(object : RxSubscriber<String>() {
                    override fun _onNext(t: String) {
                        mView.onCheckAccountResult(t)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.handleError(code, message)
                    }

                })
    }

    override fun checkPassword(map: Map<String, String>) {
        mModel.checkPassword(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<String>() {
                    override fun _onNext(t: String) {
                        mView.onCheckResult(t)
                    }

                    override fun _onError(code: String?, message: String?) {
//                        mView.handleError(code, message)
                    }

                })
    }

    override fun sendRegister(map: Map<String, String>) {
        mModel.sendRegister(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<String?>() {
                    override fun _onNext(t: String?) {
                        mView.onSendRegisterResult("")
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.handleError(code, message)
                        mView.onSendRegisterResult("error")
                    }

                })
    }
}
