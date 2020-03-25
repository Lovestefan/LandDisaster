package com.zx.landdisaster.module.disaster.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.zx.landdisaster.module.disaster.bean.QueryPersonBean
import com.zx.landdisaster.module.disaster.mvp.contract.HiddenPointPersonContract


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class HiddenPointPersonPresenter : HiddenPointPersonContract.Presenter() {
    override fun getPersonInfo(pkiaa: String, areaCode: String) {
        mModel.personData(pkiaa, areaCode)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<QueryPersonBean>() {
                    override fun _onNext(t: QueryPersonBean?) {
                        if (t != null) {
                            mView.onPersonResult(t)
                        }
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.handleError(code, message)
                    }

                })
    }


}