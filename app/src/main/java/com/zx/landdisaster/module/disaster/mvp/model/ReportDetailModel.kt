package com.zx.landdisaster.module.disaster.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.module.disaster.bean.AuditReportFileBean
import com.zx.landdisaster.module.disaster.bean.ReportDetailBean
import com.zx.landdisaster.module.disaster.bean.AuditReviewBean

import com.zx.landdisaster.module.disaster.mvp.contract.ReportDetailContract
import okhttp3.RequestBody
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class ReportDetailModel : BaseModel(), ReportDetailContract.Model {


    override fun auditReportData(info: RequestBody): Observable<Boolean> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .doAuditReport(info)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun transferFileData(map: Map<String, String>): Observable<String> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .transferFile(map = map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }


    override fun auditLeaderData(map: Map<String, String>): Observable<String> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .doAuditLeader(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun reportInfoData(path: String): Observable<ReportDetailBean.Reportdata> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getReportDetail(path)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun fileListData(map: Map<String, String>): Observable<List<AuditReportFileBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getFileList(map = map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun auditDetailData(path: String): Observable<ReportDetailBean> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getAuditDetail(path)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun disasterDetailData(map: Map<String, String>): Observable<ReportDetailBean.Reportdata> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getDisasterDetail(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun disasterFileListData(map: Map<String, String>): Observable<List<AuditReportFileBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getDisasterFileList(map = map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun leaderReviewData(auditid: String): Observable<AuditReviewBean> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getAuditReview(auditid)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }


}