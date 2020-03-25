package com.zx.landdisaster.module.disaster.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.disaster.bean.ReportListBean
import com.zx.landdisaster.module.disaster.mvp.contract.ReportHazardListContract


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class ReportHazardListPresenter : ReportHazardListContract.Presenter() {


    override fun getReportList(map: Map<String, String>) {
        mModel.reportListData(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<NormalList<ReportListBean>>() {
                    override fun _onNext(t: NormalList<ReportListBean>?) {
                        if (t == null) {
                            mView.onReportListResult(NormalList())
                            return
                        }
                        mView.onReportListResult(t)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onReportListResult(NormalList())
                        mView.handleError(code, message)
                    }

                })
    }
}