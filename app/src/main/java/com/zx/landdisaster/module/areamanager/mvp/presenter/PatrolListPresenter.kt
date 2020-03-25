package com.zx.landdisaster.module.areamanager.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.zx.landdisaster.module.areamanager.bean.PatrolListBean
import com.zx.landdisaster.module.areamanager.mvp.contract.PatrolListContract


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class PatrolListPresenter : PatrolListContract.Presenter() {

    override fun getPatrolListData(map: Map<String, String>) {
        mModel.patrolListData(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<List<PatrolListBean>>() {
                    override fun _onNext(t: List<PatrolListBean>?) {
                        mView.onPatrolListResult(t)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onPatrolListResult(null)
                        mView.handleError(code, message)
                    }

                })
    }
}