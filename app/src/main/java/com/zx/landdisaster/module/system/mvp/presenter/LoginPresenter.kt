package com.zx.landdisaster.module.system.mvp.presenter

import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSubscriber
import com.zx.landdisaster.base.bean.UserBean
import com.zx.landdisaster.module.disaster.bean.PersonRoleBean
import com.zx.landdisaster.module.system.mvp.contract.LoginContract
import okhttp3.RequestBody
import rx.Observable
import rx.functions.Func1


/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class LoginPresenter : LoginContract.Presenter() {
    override fun doLogin(info: RequestBody) {
        mModel.doLogin(info)
                .compose(RxHelper.bindToLifecycle(mView))
                .flatMap(Func1<UserBean?, Observable<PersonRoleBean>> {
                    if (it != null) {
                        mView.onLoginResult(it)
                    }
                    return@Func1 mModel.personRoleData()
                })
                .subscribe(object : RxSubscriber<PersonRoleBean>(mView) {
                    override fun _onNext(t: PersonRoleBean?) {
                        if (t != null) {
                            mView.onPersonRoleResult(t)
                        }
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.showToast(message)
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
                        mView.handleError(code, message)
//                        mView.onLoginError()
                        mView.onCheckResult("1")
                    }

                })
    }
    override fun currentAuthMenu(map: Map<String, String>) {
        mModel.currentAuthMenu(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<List<String>>() {
                    override fun _onNext(t: List<String>) {
                        mView.onCurrentAuthMenuResult(t)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.handleError(code, message)
                    }
                })
    }
    override fun loginOut() {
        mModel.loginOutData()
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<String>(mView) {
                    override fun _onNext(t: String?) {
                        mView.onLoginOutResult()
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.onLoginOutResult()
                    }

                })
    }

    override fun sendLoginCheck(map: Map<String, String>) {
        mModel.sendLoginCheck(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .subscribe(object : RxSubscriber<String?>() {
                    override fun _onNext(t: String?) {
                        mView.onSendLoginCheckResult(t ?: "")
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.handleError(code, message)
                        mView.onSendLoginCheckResult("error")
                    }

                })
    }
    override fun loginAppBySms(map: RequestBody) {
        mModel.loginAppBySms(map)
                .compose(RxHelper.bindToLifecycle(mView))
                .flatMap(Func1<UserBean?, Observable<PersonRoleBean>> {
                    if (it != null) {
                        mView.onLoginResult(it)
                    }
                    return@Func1 mModel.personRoleData()
                })
                .subscribe(object : RxSubscriber<PersonRoleBean>(mView) {
                    override fun _onNext(t: PersonRoleBean?) {
                        if (t != null) {
                            mView.onPersonRoleResult(t)
                        }
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.showToast(message)
                        mView.onLoginError()
                    }
                })
    }

}