package com.zx.landdisaster.module.disaster.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.zx.landdisaster.module.disaster.bean.HiddenDetailBean
import com.zx.landdisaster.module.disaster.mvp.contract.HiddenPointContract


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class HiddenPointPresenter : HiddenPointContract.Presenter() {
    override fun getHiddenDetail(pkiaa: String) {
        mModel.hiddenDetailData(pkiaa)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<HiddenDetailBean>(mView){
                    override fun _onNext(t: HiddenDetailBean?) {
                        if (t != null) {
                            mView.onHiddenDetail(t)
                        }
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.handleError(code, message)
                    }

                })
    }


}