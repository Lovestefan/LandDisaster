package com.zx.landdisaster.module.worklog.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.worklog.bean.DailyListBean
import com.zx.landdisaster.module.worklog.mvp.contract.DailyReportListContract


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class DailyReportListPresenter : DailyReportListContract.Presenter() {
    override fun getDailyList(map: Map<String, String>) {
        mModel.dailyListData(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<NormalList<DailyListBean>>() {
                    override fun _onNext(t: NormalList<DailyListBean>?) {
                        mView.onDailyListResult(t)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onDailyListResult(null)
                        mView.handleError(code, message)
                    }

                })
    }


}