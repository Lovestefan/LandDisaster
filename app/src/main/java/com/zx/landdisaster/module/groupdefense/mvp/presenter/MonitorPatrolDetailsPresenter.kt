package com.zx.landdisaster.module.groupdefense.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.module.disaster.bean.AuditReportFileBean
import com.zx.landdisaster.module.groupdefense.mvp.contract.MonitorPatrolDetailsContract


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class MonitorPatrolDetailsPresenter : MonitorPatrolDetailsContract.Presenter() {
    override fun getMonitorPatrolDetailsFileList(logid: String) {
        mModel.getMonitorPatrolDetailsFileList(ApiParamUtil.getMonitorFileList(logid))
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<List<AuditReportFileBean>>() {
                    override fun _onNext(t: List<AuditReportFileBean>?) {
                        if (t != null) {
                            mView.getMonitorPatrolDetailsFileResult(t)
                        }else{
                            mView.getMonitorPatrolDetailsFileResult()
                        }
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.handleError(code, message)
                        mView.getMonitorPatrolDetailsFileError()

                    }

                })
    }


}