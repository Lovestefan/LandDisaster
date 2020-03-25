package com.zx.landdisaster.module.groupdefense.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService

import com.zx.landdisaster.module.groupdefense.mvp.contract.MonitorPatrolFillContract
import okhttp3.RequestBody
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class MonitorPatrolFillModel : BaseModel(), MonitorPatrolFillContract.Model {


    override fun addMonitorpatroldata(info: RequestBody): Observable<String> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .addMonitorpatroldata(info)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun uploadDisasterpoint(info: RequestBody): Observable<String> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .uploadDisasterpoint(info)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }
}