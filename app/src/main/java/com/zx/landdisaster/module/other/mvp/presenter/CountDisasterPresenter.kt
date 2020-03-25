package com.zx.landdisaster.module.other.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.zx.landdisaster.module.other.bean.CountDisasterBean
import com.zx.landdisaster.module.other.mvp.contract.CountDisasterContract


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class CountDisasterPresenter : CountDisasterContract.Presenter() {
    override fun countDisaster(map: Map<String, String>) {
        mModel.countDisasterData(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<List<CountDisasterBean>>(mView) {
                    override fun _onNext(t: List<CountDisasterBean>?) {
                        if (t != null) {
                            mView.onCountDisasterResult(t)
                        }
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.handleError(code, message)
                    }

                })
    }


}