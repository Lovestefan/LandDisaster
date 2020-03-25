package com.zx.landdisaster.module.main.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.main.bean.InfoDeliveryBean
import com.zx.landdisaster.module.main.mvp.contract.InfoReleaseContract


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class InfoReleasePresenter : InfoReleaseContract.Presenter() {
    override fun getFindWeatherdecisionData(map: Map<String, String>) {
        mModel.findWeatherdecision(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<NormalList<InfoDeliveryBean>>() {
                    override fun _onNext(t: NormalList<InfoDeliveryBean>?) {
                        if (t == null) {
                            mView.onFindWeatherdecisionResult(NormalList())
                            return
                        }
                        mView.onFindWeatherdecisionResult(t)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onFindWeatherdecisionResult(NormalList())
                        mView.handleError(code, message)
                    }

                })
    }

}