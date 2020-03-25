package com.zx.landdisaster.module.worklog.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.worklog.bean.DailyAuditListBean
import com.zx.landdisaster.module.worklog.mvp.contract.DailyAuditListContract


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class DailyAuditListPresenter : DailyAuditListContract.Presenter() {
    override fun getPageAuditReport(map: Map<String, String>) {
        mModel.pageAuditReport(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<NormalList<DailyAuditListBean>>() {
                    override fun _onNext(t: NormalList<DailyAuditListBean>?) {
                        if (t == null) {
                            mView.onPageAuditReportResult(NormalList())
                            return
                        }
                        mView.onPageAuditReportResult(t)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onPageAuditReportResult(NormalList())
                        mView.handleError(code, message)
                    }

                })
    }
}