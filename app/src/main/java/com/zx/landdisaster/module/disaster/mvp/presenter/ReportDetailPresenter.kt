package com.zx.landdisaster.module.disaster.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.module.disaster.bean.AddFileBean
import com.zx.landdisaster.module.disaster.bean.AuditReportFileBean
import com.zx.landdisaster.module.disaster.bean.AuditReviewBean
import com.zx.landdisaster.module.disaster.bean.ReportDetailBean
import com.zx.landdisaster.module.disaster.mvp.contract.ReportDetailContract
import okhttp3.RequestBody
import rx.Observable
import rx.functions.Func1


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class ReportDetailPresenter : ReportDetailContract.Presenter() {


    override fun doAuditReport(info: RequestBody) {
        mModel.auditReportData(info)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<Boolean>(mView) {
                    override fun _onNext(t: Boolean?) {
                        mView.onAuditResult(t)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.handleError(code, message)
                    }

                })
    }

    override fun doAuditLeader(map: Map<String, String>) {
        mModel.auditLeaderData(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<String>(mView) {
                    override fun _onNext(t: String?) {
                        mView.onAuditResult(null)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.handleError(code, message)
                    }

                })
    }

    override fun transferFile(map: Map<String, String>) {
        mModel.transferFileData(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<String>(mView) {
                    override fun _onNext(t: String?) {
                        mView.onAuditResult(null)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.handleError(code, message)
                    }

                })
    }

    override fun getReportInfo(reportdataid: String) {

        var reportInfoBean: ReportDetailBean? = null

        mModel.reportInfoData(reportdataid)
                .compose(RxHelper.bindToLifecycle(mView))
                .flatMap(Func1<ReportDetailBean.Reportdata?, Observable<List<AuditReportFileBean>>> {
                    if (it != null) {
                        reportInfoBean = ReportDetailBean(reportflow = ReportDetailBean.Reportflow(), auditDetail = ReportDetailBean.AuditDetail(), reportdata = it, leaderreview = ReportDetailBean.Leaderreview())
                        return@Func1 mModel.fileListData(ApiParamUtil.fileListParam(reportInfoBean!!.reportdata!!.flownum!!))
                    } else {
                        mView.showToast("未获取到信息，请重试")
                        mView.dismissLoading()
                        return@Func1 null
                    }
                })
                .subscribe(object : RxSubscriber<List<AuditReportFileBean>>(mView) {
                    override fun _onNext(t: List<AuditReportFileBean>?) {
                        val fileBeans = arrayListOf<AddFileBean>()
                        if (t != null) {
                            if (t.isNotEmpty()) {
                                t.forEach {
                                    fileBeans.add(AddFileBean.reSetType(it.type, it.name, it.path))
                                }
                            }
                        }
                        reportInfoBean!!.reportdata!!.files.addAll(fileBeans)
                        mView.onReportInfoResult(reportInfoBean!!)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.handleError(code, message)
                    }

                })
    }

    override fun getAuditDetail(path: String) {

        var reportInfoBean: ReportDetailBean? = null

        mModel.auditDetailData(path)
                .compose(RxHelper.bindToLifecycle(mView))
                .flatMap(Func1<ReportDetailBean, Observable<List<AuditReportFileBean>>> {
                    if (it != null) {
                        reportInfoBean = it
                        return@Func1 mModel.fileListData(ApiParamUtil.fileListParam(it.reportdata!!.flownum!!))
                    } else {
                        mView.showToast("未获取到信息，请重试")
                        mView.dismissLoading()
                        return@Func1 null
                    }
                })
                .subscribe(object : RxSubscriber<List<AuditReportFileBean>>(mView) {
                    override fun _onNext(t: List<AuditReportFileBean>?) {
                        val fileBeans = arrayListOf<AddFileBean>()
                        if (t != null) {
                            if (t.isNotEmpty()) {
                                t.forEach {
                                    fileBeans.add(AddFileBean.reSetType(it.type, it.name, it.path))
                                }
                            }
                        }
                        reportInfoBean!!.reportdata!!.files.addAll(fileBeans)
                        mView.onReportInfoResult(reportInfoBean!!)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.handleError(code, message)
                    }

                })
    }

    override fun getDisasterDetail(pkidd: String) {
        var reportInfoBean: ReportDetailBean? = null

        mModel.disasterDetailData(ApiParamUtil.disasterDetailParam(pkidd))
                .compose(RxHelper.bindToLifecycle(mView))
                .flatMap(Func1<ReportDetailBean.Reportdata, Observable<List<AuditReportFileBean>>> {
                    if (it != null) {
                        it.editAble = false
                        it.editAllAble = false
                        reportInfoBean = ReportDetailBean(reportflow = ReportDetailBean.Reportflow(), auditDetail = ReportDetailBean.AuditDetail(), reportdata = it, leaderreview = ReportDetailBean.Leaderreview())
                        return@Func1 mModel.disasterFileListData(ApiParamUtil.disasterFileListParam(pkidd))
                    } else {
                        mView.showToast("未获取到信息，请重试")
                        mView.dismissLoading()
                        return@Func1 null
                    }
                })
                .subscribe(object : RxSubscriber<List<AuditReportFileBean>>(mView) {
                    override fun _onNext(t: List<AuditReportFileBean>?) {
                        val fileBeans = arrayListOf<AddFileBean>()
                        if (t != null) {
                            if (t.isNotEmpty()) {
                                t.forEach {
                                    fileBeans.add(AddFileBean.reSetType(it.type, it.name, it.path))
                                }
                            }
                        }
                        reportInfoBean!!.reportdata!!.files.addAll(fileBeans)
                        mView.onReportInfoResult(reportInfoBean!!)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.handleError(code, message)
                    }

                })
    }

    override fun getAuditReview(auditid: String) {
        mModel.leaderReviewData(auditid)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<AuditReviewBean>(mView) {
                    override fun _onNext(t: AuditReviewBean?) {
                        if (t != null) {
                            mView.onReviewDetailResult(t)
                        }
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.handleError(code, message)
                    }

                })
    }
}