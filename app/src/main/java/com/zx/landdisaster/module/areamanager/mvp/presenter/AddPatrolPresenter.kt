package com.zx.landdisaster.module.areamanager.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.frame.zxmvp.http.upload.UploadRequestBody
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.base.other.UserManager
import com.zx.landdisaster.module.areamanager.mvp.contract.AddPatrolContract
import com.zx.landdisaster.module.disaster.bean.DisasterPkiaaBean
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class AddPatrolPresenter : AddPatrolContract.Presenter() {

    override fun uploadFile(files: List<File>, logid: String) {
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        for (file in files) {
            builder.addFormDataPart("files", file.name, RequestBody.create(MediaType.parse("multipart/form-data"), file))
        }
        builder.addFormDataPart("recordid", logid)
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

    override fun submit(info: RequestBody) {
        mModel.submitData(info)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<String>(mView) {
                    override fun _onNext(t: String?) {
                        if (t != null) {
                            mView.onSubmitResult(t)
                        }
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.handleError(code, message)
                    }

                })
    }

    override fun getPkiaaList(map: Map<String, String>) {
        mModel.pkiaaListData(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<NormalList<DisasterPkiaaBean>>() {
                    override fun _onNext(t: NormalList<DisasterPkiaaBean>?) {
                        if (t == null) {
                            mView.onPkiaaListResult(NormalList())
                            return
                        }
                        mView.onPkiaaListResult(t)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onPkiaaListResult(NormalList())
                        mView.handleError(code, message)
                    }

                })
    }
}