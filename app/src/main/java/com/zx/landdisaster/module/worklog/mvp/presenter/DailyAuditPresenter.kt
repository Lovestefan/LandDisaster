package com.zx.landdisaster.module.worklog.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.zx.landdisaster.module.disaster.bean.AuditReportFileBean
import com.zx.landdisaster.module.worklog.mvp.contract.DailyAuditContract
import okhttp3.RequestBody


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class DailyAuditPresenter : DailyAuditContract.Presenter() {
    override fun getAuditReport(map: RequestBody) {
        mModel.auditReport(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<String>() {
                    override fun _onNext(t: String) {
                        mView.onAuditReportResult(t)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.handleError(code, message)
                    }

                })
    }
    override fun getDailyFile(map: Map<String, String>) {
        mModel.dailyFileData(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<List<AuditReportFileBean>>(mView){
                    override fun _onNext(t: List<AuditReportFileBean>?) {
                        if (t != null) {
                            mView.onDailyFileResult(t)
                        }
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.handleError(code, message)
                    }

                })
    }
}