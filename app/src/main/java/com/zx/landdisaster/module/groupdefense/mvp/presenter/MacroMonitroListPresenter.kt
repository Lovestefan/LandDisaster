package com.zx.landdisaster.module.groupdefense.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.module.groupdefense.bean.MacroMonitroBean
import com.zx.landdisaster.module.groupdefense.mvp.contract.MacroMonitroListContract


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class MacroMonitroListPresenter : MacroMonitroListContract.Presenter() {
    override fun findMacroMonitroList(setid: String, startTime: String, endTime: String) {
        mModel.findMacroMonitroList(setid, ApiParamUtil.findMacroMonitroList(startTime, endTime))
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<List<MacroMonitroBean>>() {

                    override fun _onNext(t: List<MacroMonitroBean>?) {
                        mView.onfindMacroMonitroListResult(t)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onfindMacroMonitroListResult(null)
                    }

                })
    }


}