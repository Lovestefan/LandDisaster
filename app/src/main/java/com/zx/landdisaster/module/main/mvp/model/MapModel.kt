package com.zx.landdisaster.module.main.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxSchedulers
import com.google.gson.JsonObject
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.module.main.mvp.contract.MapContract
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class MapModel : BaseModel(), MapContract.Model {
    override fun mapExtentData(esriString : String): Observable<JsonObject> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getMapExtent(esriString)
                .compose(RxSchedulers.io_main())
    }


}