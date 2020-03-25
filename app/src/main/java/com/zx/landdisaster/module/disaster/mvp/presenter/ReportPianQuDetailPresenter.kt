package com.zx.landdisaster.module.disaster.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.disaster.bean.ReportPianQuDetailBean
import com.zx.landdisaster.module.disaster.mvp.contract.ReportPianQuDetailContract


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class ReportPianQuDetailPresenter : ReportPianQuDetailContract.Presenter() {

    override fun pageAreaManagerreportrateDetail(map: Map<String, String>) {
        mModel.pageAreaManagerreportrateDetail(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<NormalList<ReportPianQuDetailBean>>() {
                    override fun _onNext(t: NormalList<ReportPianQuDetailBean>?) {
                        if (t == null) {
                            mView.onPageAreaManagerreportrateDetailResult(NormalList())
                            return
                        }
                        mView.onPageAreaManagerreportrateDetailResult(t)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onPageAreaManagerreportrateDetailResult(NormalList())
                        mView.handleError(code, message)
                    }
                })
    }

}