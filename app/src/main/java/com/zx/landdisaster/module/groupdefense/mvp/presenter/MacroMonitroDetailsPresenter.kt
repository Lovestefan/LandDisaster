package com.zx.landdisaster.module.groupdefense.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.module.disaster.bean.AuditReportFileBean
import com.zx.landdisaster.module.groupdefense.bean.MacroMonitroCurrentTypeBean
import com.zx.landdisaster.module.groupdefense.mvp.contract.MacroMonitroDetailsContract


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class MacroMonitroDetailsPresenter : MacroMonitroDetailsContract.Presenter() {
    override fun findCurrentType(setid: String) {
        mModel.findCurrentType(setid)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<List<MacroMonitroCurrentTypeBean>>(mView) {
                    override fun _onNext(t: List<MacroMonitroCurrentTypeBean>?) {

                        if (t != null) {
                            mView.findCurrentTypeResult(t)
                        }

                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.dismissLoading()
                        mView.handleError(code, message)
                    }

                })
    }

    override fun getMacroMonitrolDetailsFileList(logid: String) {
        mModel.getMacroMonitrolDetailsFileList(ApiParamUtil.getMonitorFileList(logid))
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<List<AuditReportFileBean>>() {
                    override fun _onNext(t: List<AuditReportFileBean>?) {
                        if (t != null) {
                            mView.getMacroMonitrolDetailsFileResult(t)
                        } else {
                            mView.getMacroMonitrolDetailsFileResult()
                        }
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.handleError(code, message)
                        mView.getMacroMonitrolDetailsFileError()

                    }

                })
    }


}