package com.zx.landdisaster.module.worklog.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.disaster.bean.AuditReportFileBean
import com.zx.landdisaster.module.worklog.bean.WorkLogListBean
import com.zx.landdisaster.module.worklog.mvp.contract.WorkLogListContract


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class WorkLogListPresenter : WorkLogListContract.Presenter() {
    override fun getWorkLogList(map: Map<String, String>) {
        mModel.workLogData(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<NormalList<WorkLogListBean>>(mView) {
                    override fun _onNext(t: NormalList<WorkLogListBean>?) {
                        if (t == null) {
                            mView.onWorkLogListResult(NormalList())
                            return
                        }
                        mView.onWorkLogListResult(t)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onWorkLogListResult(NormalList())
                        mView.handleError(code, message)
                    }

                })
    }

    override fun getWorkLogFile(map: Map<String, String>, position: Int) {
        mModel.workLogFileData(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<List<AuditReportFileBean>>(mView) {
                    override fun _onNext(t: List<AuditReportFileBean>?) {
                        mView.onWorkLogFileResult(t, position)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.handleError(code, message)
                    }

                })
    }


}