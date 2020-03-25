package com.zx.landdisaster.module.disaster.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.zx.landdisaster.module.disaster.mvp.contract.AuditHomeContract
import com.zx.landdisaster.module.main.bean.TaskNumBean


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class AuditHomePresenter : AuditHomeContract.Presenter() {

    override fun getTaskNum() {
        mModel.taskNumData()
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<TaskNumBean>() {
                    override fun _onNext(t: TaskNumBean?) {
                        if (t != null) {
                            mView.onTaskNumResult(t)
                        }
                    }

                    override fun _onError(code: String?, message: String?) {
                    }

                })
    }
}