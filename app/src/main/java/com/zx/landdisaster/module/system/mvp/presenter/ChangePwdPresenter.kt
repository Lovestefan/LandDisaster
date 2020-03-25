package com.zx.landdisaster.module.system.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.zx.landdisaster.module.system.mvp.contract.ChangePwdContract


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class ChangePwdPresenter : ChangePwdContract.Presenter() {
    override fun changePwd(map: Map<String, String>) {
        mModel.changePwdData(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<String>(mView) {
                    override fun _onNext(t: String?) {
                        mView.onChangeResult()
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.handleError(code, message)
                    }

                })
    }

    override fun checkPassword(map: Map<String, String>) {
        mModel.checkPassword(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<String>() {
                    override fun _onNext(t: String) {
                        mView.onCheckResult(t)
                    }

                    override fun _onError(code: String?, message: String?) {
//                        mView.handleError(code, message)
                    }

                })
    }

    override fun sendUpdatePwd(map: Map<String, String>) {
        mModel.sendUpdatePwd(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<String?>() {
                    override fun _onNext(t: String?) {
                        mView.onSendUpdatePwdResult("")
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.handleError(code, message)
                        mView.onSendUpdatePwdResult("error")
                    }

                })
    }

    override fun updatePasswordByCode(map: Map<String, String>) {
        mModel.updatePasswordByCode(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<String?>() {
                    override fun _onNext(t: String?) {
                        mView.onUpdatePwdResult(t ?: "")
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.handleError(code, message)
                    }

                })
    }
}