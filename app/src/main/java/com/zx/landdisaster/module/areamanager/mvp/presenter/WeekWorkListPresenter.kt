package com.zx.landdisaster.module.areamanager.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.areamanager.bean.WeekWorkListBean
import com.zx.landdisaster.module.areamanager.mvp.contract.WeekWorkListContract


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class WeekWorkListPresenter : WeekWorkListContract.Presenter() {

    override fun getWeekWorkListData(map: Map<String, String>) {
        mModel.weekWorkListData(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<NormalList<WeekWorkListBean>>() {
                    override fun _onNext(t: NormalList<WeekWorkListBean>?) {
                        if (t == null) {
                            mView.onWeekWorkListResult(NormalList())
                            return
                        }
                        mView.onWeekWorkListResult(t)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onWeekWorkListResult(NormalList())
                        mView.handleError(code, message)
                    }

                })
    }
}