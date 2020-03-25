package com.zx.landdisaster.module.groupdefense.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.zx.landdisaster.module.groupdefense.bean.GroupDefenceBean
import com.zx.landdisaster.module.groupdefense.mvp.contract.GroupDefenceContract


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class GroupDefencePresenter : GroupDefenceContract.Presenter() {

    override fun findDisasterPoint() {
        mModel.findDisasterPoint()
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<List<GroupDefenceBean>>() {

                    override fun _onNext(t: List<GroupDefenceBean>?) {
                        mView.onfindDisasterPointResult(t)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onfindDisasterPointResult(null)
                        mView.handleError(code, message)
                    }

                })
    }

}