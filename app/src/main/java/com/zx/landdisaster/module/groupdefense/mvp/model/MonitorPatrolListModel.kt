package com.zx.landdisaster.module.groupdefense.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.module.groupdefense.bean.MonitorPatrolBean

import com.zx.landdisaster.module.groupdefense.mvp.contract.MonitorPatrolListContract
import retrofit2.http.QueryMap
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class MonitorPatrolListModel : BaseModel(), MonitorPatrolListContract.Model {
    override fun findFixMonitroList(mpid: String, @QueryMap map: Map<String, String>): Observable<List<MonitorPatrolBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .findFixMonitroList(mpid, map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }
}