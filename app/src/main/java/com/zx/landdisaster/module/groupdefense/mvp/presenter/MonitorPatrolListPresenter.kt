package com.zx.landdisaster.module.groupdefense.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.zx.landdisaster.api.ApiParamUtil
import com.zx.landdisaster.module.groupdefense.bean.MonitorPatrolBean
import com.zx.landdisaster.module.groupdefense.mvp.contract.MonitorPatrolListContract


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class MonitorPatrolListPresenter : MonitorPatrolListContract.Presenter() {
    override fun findFixMonitroList(mpid: String, name: String, startTime: String, endTime: String) {
        mModel.findFixMonitroList(mpid, ApiParamUtil.findFixMonitroList(name, startTime, endTime))
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<List<MonitorPatrolBean>>(mView) {

                    override fun _onNext(t: List<MonitorPatrolBean>?) {
                        mView.onfindFixMonitroListResult(t)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onfindFixMonitroListResult(null)

                    }

                })
    }

}