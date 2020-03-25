package com.zx.landdisaster.module.dailymanage.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.dailymanage.bean.ExpertDailyBean
import com.zx.landdisaster.module.dailymanage.mvp.contract.ExpertDailyListContract


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class ExpertDailyListPresenter : ExpertDailyListContract.Presenter() {

    override fun findMyReport(map: Map<String, String>) {
        mModel.findMyReport(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<NormalList<ExpertDailyBean>>() {
                    override fun _onNext(t: NormalList<ExpertDailyBean>?) {
                        mView.onFindMyReportResult(t)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onFindMyReportResult(null)
                        mView.handleError(code, message)
                    }

                })
    }

}