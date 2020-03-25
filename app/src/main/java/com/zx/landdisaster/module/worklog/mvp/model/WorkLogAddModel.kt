package com.zx.landdisaster.module.worklog.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService

import com.zx.landdisaster.module.worklog.mvp.contract.WorkLogAddContract
import okhttp3.RequestBody
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class WorkLogAddModel : BaseModel(), WorkLogAddContract.Model {
    override fun workLogData(info: RequestBody): Observable<String> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .addWorkLog(info)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun workLogFileAddData(info: RequestBody): Observable<String> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .uploadWeekWorkFile(info)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }


}