package com.zx.landdisaster.module.system.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.zx.landdisaster.module.system.bean.VersionRecordBean
import com.zx.landdisaster.module.system.mvp.contract.VersionRecordContract


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class VersionRecordPresenter : VersionRecordContract.Presenter() {
    override fun findUpdateinfoList() {
        mModel.findUpdateinfoList()
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<List<VersionRecordBean>>() {
                    override fun _onNext(t: List<VersionRecordBean>?) {
                        mView.onVersionRecordResult(t)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onVersionRecordResult(null)
                        mView.handleError(code, message)
                    }
                })
    }
}