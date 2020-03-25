package com.zx.landdisaster.module.other.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.module.other.bean.CountDisasterBean

import com.zx.landdisaster.module.other.mvp.contract.CountDisasterContract
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class CountDisasterModel : BaseModel(), CountDisasterContract.Model {
    override fun countDisasterData(map: Map<String, String>): Observable<List<CountDisasterBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .countDisaster(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }


}