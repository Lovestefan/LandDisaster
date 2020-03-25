package com.zx.landdisaster.module.disaster.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.disaster.bean.RainPointBean
import com.zx.landdisaster.module.disaster.mvp.contract.RainPointContract


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class RainPointPresenter : RainPointContract.Presenter() {
    override fun getRainPointList(map: Map<String, String>) {
        mModel.rainListData(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<NormalList<RainPointBean>>(mView) {
                    override fun _onNext(t: NormalList<RainPointBean>?) {
                        if (t == null) {
                            mView.onRainResult(NormalList())
                            return
                        }
                        mView.onRainResult(t)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onRainResult(NormalList())
                        mView.handleError(code, message)
                    }
                })
    }

}