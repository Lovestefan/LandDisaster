package com.zx.landdisaster.module.disaster.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.zx.landdisaster.module.disaster.bean.AuditLogsBean
import com.zx.landdisaster.module.disaster.mvp.contract.AuditHandleLogsContract


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class AuditHandleLogsPresenter : AuditHandleLogsContract.Presenter() {
    override fun getAuditLogs(path: String) {
        mModel.auditLogsData(path)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<List<AuditLogsBean>>(mView){
                    override fun _onNext(t: List<AuditLogsBean>?) {
                        if (t != null) {
                            mView.onAuditLogsResult(t)
                        }
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onAuditLogsResult(null)
                        mView.handleError(code, message)
                    }

                })
    }


}