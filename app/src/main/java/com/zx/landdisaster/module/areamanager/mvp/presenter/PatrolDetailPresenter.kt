package com.zx.landdisaster.module.areamanager.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.zx.landdisaster.module.areamanager.mvp.contract.PatrolDetailContract
import com.zx.landdisaster.module.disaster.bean.AuditReportFileBean


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class PatrolDetailPresenter : PatrolDetailContract.Presenter() {
    override fun getPatrolFile(map: Map<String, String>) {
        mModel.getPatrolFileData(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<List<AuditReportFileBean>>() {
                    override fun _onNext(t: List<AuditReportFileBean>?) {
                        if (t != null) {
                            mView.getPatrolFileResult(t)
                        } else {
                            mView.getPatrolFileResult()
                        }
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.handleError(code, message)
                    }

                })
    }


}