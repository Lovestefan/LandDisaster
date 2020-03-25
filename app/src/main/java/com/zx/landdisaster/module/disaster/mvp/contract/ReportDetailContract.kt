package com.zx.landdisaster.module.disaster.mvp.contract

import com.frame.zxmvp.base.BasePresenter
import com.frame.zxmvp.base.IModel
import com.frame.zxmvp.base.IView
import com.zx.landdisaster.module.disaster.bean.AuditReportFileBean
import com.zx.landdisaster.module.disaster.bean.ReportDetailBean
import com.zx.landdisaster.module.disaster.bean.AuditReviewBean
import okhttp3.RequestBody
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
interface ReportDetailContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View : IView {
        fun onAuditResult(status: Boolean?)

        fun onTransferFileResult()

        fun onReportInfoResult(reportInfoBean: ReportDetailBean)

        fun onReviewDetailResult(reviewBean: AuditReviewBean)
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model : IModel {
        fun auditReportData(info: RequestBody): Observable<Boolean>

        fun auditLeaderData(map: Map<String, String>): Observable<String>

        fun transferFileData(map: Map<String, String>): Observable<String>

        fun reportInfoData(path: String): Observable<ReportDetailBean.Reportdata>

        fun auditDetailData(path: String): Observable<ReportDetailBean>

        fun disasterDetailData(map: Map<String, String>): Observable<ReportDetailBean.Reportdata>

        fun fileListData(map: Map<String, String>): Observable<List<AuditReportFileBean>>

        fun disasterFileListData(map: Map<String, String>): Observable<List<AuditReportFileBean>>

        fun leaderReviewData(auditid: String): Observable<AuditReviewBean>
    }

    //方法
    abstract class Presenter : BasePresenter<View, Model>() {

        abstract fun doAuditReport(info: RequestBody)

        abstract fun transferFile(map: Map<String, String>)

        abstract fun doAuditLeader(map: Map<String, String>)

        abstract fun getReportInfo(reportdataid: String)

        abstract fun getAuditDetail(path: String)

        abstract fun getDisasterDetail(pkidd: String)

        abstract fun getAuditReview(auditid: String)
    }
}
