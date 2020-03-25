package com.zx.landdisaster.module.disaster.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.module.disaster.bean.QueryPersonBean

import com.zx.landdisaster.module.disaster.mvp.contract.HiddenPointPersonContract
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class HiddenPointPersonModel : BaseModel(), HiddenPointPersonContract.Model {
    override fun personData(pkiaa: String, areaCode: String): Observable<QueryPersonBean> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .queryPerson(pkiaa, areaCode)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }


}