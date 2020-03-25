package com.zx.landdisaster.module.areamanager.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.zx.landdisaster.module.areamanager.bean.AreaManagerBean
import com.zx.landdisaster.module.areamanager.mvp.contract.AreaManagerContract


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class AreaManagerPresenter : AreaManagerContract.Presenter() {
    override fun getAreaData(map: Map<String, String>) {
        mModel.areaData(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<AreaManagerBean>(mView){
                    override fun _onNext(t: AreaManagerBean?) {
                        if (t != null) {
                            mView.onAreaResult(t)
                        }
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.handleError(code, message)
                    }

                })
    }


}