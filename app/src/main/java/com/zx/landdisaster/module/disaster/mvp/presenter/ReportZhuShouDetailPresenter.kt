package com.zx.landdisaster.module.disaster.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.disaster.bean.ReportZhuShouDetailBean
import com.zx.landdisaster.module.disaster.mvp.contract.ReportZhuShouDetailContract
import com.zx.landdisaster.module.worklog.bean.WorkLogListBean


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class ReportZhuShouDetailPresenter : ReportZhuShouDetailContract.Presenter() {

    override fun pageGarrisonreportrateDetail(map: Map<String, String>) {
        mModel.pageGarrisonreportrateDetail(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<NormalList<ReportZhuShouDetailBean>>() {
                    override fun _onNext(t: NormalList<ReportZhuShouDetailBean>?) {
                        if (t == null) {
                            mView.onPageGarrisonreportrateDetailResult(NormalList())
                            return
                        }
                        mView.onPageGarrisonreportrateDetailResult(t)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onPageGarrisonreportrateDetailResult(NormalList())
                        mView.handleError(code, message)
                    }
                })
    }
    override fun findWorkdairy(map: Map<String, String>) {
        mModel.findWorkdairy(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<NormalList<WorkLogListBean>>() {
                    override fun _onNext(t: NormalList<WorkLogListBean>?) {
                        if (t == null) {
                            mView.onFindWorkdairyResult(NormalList())
                            return
                        }
                        mView.onFindWorkdairyResult(t)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onFindWorkdairyResult(NormalList())
                        mView.handleError(code, message)
                    }
                })
    }

}