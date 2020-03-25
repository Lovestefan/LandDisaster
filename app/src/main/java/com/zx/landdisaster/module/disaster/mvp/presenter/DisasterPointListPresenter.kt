package com.zx.landdisaster.module.disaster.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.disaster.mvp.contract.DisasterPointListContract
import com.zx.landdisaster.module.main.bean.DisasterPointBean


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class DisasterPointListPresenter : DisasterPointListContract.Presenter() {

    override fun getDisasterList(map: Map<String, String>) {
        mModel.disasterListData(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<NormalList<DisasterPointBean>>() {
                    override fun _onNext(t: NormalList<DisasterPointBean>?) {
                        if (t == null) {
                            mView.onDisasterResult(NormalList())
                            return
                        }
                        mView.onDisasterResult(t)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onDisasterResult(NormalList())
                        mView.handleError(code, message)
                    }

                })
    }
}