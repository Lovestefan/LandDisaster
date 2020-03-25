package com.zx.landdisaster.module.disaster.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.disaster.mvp.contract.HiddenPointListContract
import com.zx.landdisaster.module.main.bean.HiddenPointBean
import okhttp3.RequestBody


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class HiddenPointListPresenter : HiddenPointListContract.Presenter() {
    override fun getHiddenList(info: RequestBody, pageNo : Int, pageSize : Int) {
        mModel.hiddenListData(info, pageNo, pageSize)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<NormalList<HiddenPointBean>>() {
                    override fun _onNext(t: NormalList<HiddenPointBean>?) {
                        if (t == null) {
                            mView.onHiddenResult(NormalList())
                            return
                        }
                        mView.onHiddenResult(t)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onHiddenResult(NormalList())
                        mView.handleError(code, message)
                    }

                })
    }

}