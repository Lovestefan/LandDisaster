package com.zx.landdisaster.base.tool

import android.util.Log
import com.frame.zxmvp.base.IView
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.frame.zxmvp.baserx.RxSubscriber
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.app.MyApplication
import okhttp3.ResponseBody
import rx.Observable

/**
 * Created by admin123 on 2019/5/8.
 */
object UserActionTool {
    //行为日志
    fun updateUserAction(info: Map<String, String>): Observable<String>? {
        MyApplication.instance.component.repositoryManager()
                .obtainRetrofitService(ApiService::class.java)
                .operationLog(info)
                .compose(RxSchedulers.io_main())
                .subscribe(object : RxSubscriber<ResponseBody>() {
                    override fun _onNext(t: ResponseBody) {
                    }

                    override fun _onError(code: String?, message: String?) {
                        Log.e("logAction", "操作日志提交失败$message")
                    }
                })
        return null
    }

    //下线
    fun getBatch(mView: IView): Observable<String>? {
        MyApplication.instance.component.repositoryManager()
                .obtainRetrofitService(ApiService::class.java)
                .getBatch()
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
                .subscribe(object : RxSubscriber<String?>() {
                    override fun _onNext(t: String?) {
                        mView.handlerSuccess(t)
                    }

                    override fun _onError(code: String?, message: String?) {
                        mView.handleError(code, message)
                    }
                })
        return null
    }

}