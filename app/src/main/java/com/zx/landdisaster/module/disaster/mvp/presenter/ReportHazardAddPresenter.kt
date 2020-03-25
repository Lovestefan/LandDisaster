package com.zx.landdisaster.module.disaster.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.frame.zxmvp.http.upload.UploadRequestBody
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.base.other.UserManager
import com.zx.landdisaster.module.disaster.bean.DisasterPkiaaBean
import com.zx.landdisaster.module.disaster.bean.ReportDetailBean
import com.zx.landdisaster.module.disaster.bean.ReportResultbean
import com.zx.landdisaster.module.disaster.mvp.contract.ReportHazardAddContract
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class ReportHazardAddPresenter : ReportHazardAddContract.Presenter() {
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
                        mView.handleError(code, message)
                    }

                })
    }

    override fun submitReport(reportBean: ReportDetailBean) {
        if (reportBean.reportdata!!.editAllAble) {//新增 或重新提交
            mModel.reportSubmitData(ApiParamUtil.reortSubmitParam(reportBean))
                    .compose(RxHelper.bindToLifecycle(mView))
                    .subscribe(object : RxSubscriber<ReportResultbean>(mView) {
                        override fun _onNext(t: ReportResultbean?) {
                            if (t != null) {
                                mView.onReportSubmitResult(t)
                            }
                        }

                        override fun _onError(code: String?, message: String?) {
                            mView.handleError(code, message)
                            mView.onSubmitErrorResult()
                        }
                    })
        } else {
            mModel.reportEditData(ApiParamUtil.reortSubmitParam(reportBean))
                    .compose(RxHelper.bindToLifecycle(mView))
                    .subscribe(object : RxSubscriber<String>(mView) {
                        override fun _onNext(t: String?) {
                            mView.onReportSubmitResult(ReportResultbean(flowNum = reportBean.reportdata!!.flownum!!))
                        }

                        override fun _onError(code: String?, message: String?) {
                            mView.handleError(code, message)
                        }
                    })
        }
    }

    override fun uploadFile(files: List<File>, flowNum: String) {
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        for (file in files) {
            builder.addFormDataPart("files", file.name, RequestBody.create(MediaType.parse("multipart/form-data"), file))
        }
        builder.addFormDataPart("flowNum", flowNum)
        builder.addFormDataPart("token", UserManager.getUser().token)
        val uploadRequestBody = UploadRequestBody(builder.build()) { progress, done ->
            if (progress < 100) mView.onFileUploadPregress(progress)
        }
        mModel.fileUploadData(uploadRequestBody)
                .compose(RxHelper.bindToLifecycle<String>(mView))
                .subscribe(object : RxSubscriber<String>() {

                    override fun _onNext(s: String?) {
                        mView.onFileUploadResult(flowNum)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onFileUploadResult(flowNum)
//                        mView.dismissLoading()
//                        mView.handleError(code, message)
                    }
                })
    }

    override fun deleteFile(map: Map<String, String>) {
        mModel.deleteFileData(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<Any>(mView) {
                    override fun _onNext(t: Any?) {
                        mView.onFileDeleteResult()
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.handleError(code, message)
                    }

                })
    }

    override fun getIsExistInRegion(map: Map<String, String>) {
        mModel.isExistInRegion(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<String>() {
                    override fun _onNext(t: String) {
                        mView.onExistInRegionResult(t)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onExistInRegionResult("2")
                    }
                })
    }

}
