package com.zx.landdisaster.module.disaster.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.disaster.bean.ReportStatisGroupDetailBean
import com.zx.landdisaster.module.disaster.mvp.contract.ReportstatisDetailContract


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class ReportstatisDetailPresenter : ReportstatisDetailContract.Presenter() {

    override fun pageGroupdefensesreportrateDetail(map: Map<String, String>) {
        mModel.pageGroupdefensesreportrateDetail(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<NormalList<ReportStatisGroupDetailBean>>() {
                    override fun _onNext(t: NormalList<ReportStatisGroupDetailBean>?) {
                        if (t == null) {
                            mView.onPageGroupdefensesReportRateResult(NormalList())
                            return
                        }
                        mView.onPageGroupdefensesReportRateResult(t)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onPageGroupdefensesReportRateResult(NormalList())
                        mView.handleError(code, message)
                    }
                })
    }

}