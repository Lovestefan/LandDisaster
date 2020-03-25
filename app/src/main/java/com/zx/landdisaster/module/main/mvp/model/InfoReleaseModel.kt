package com.zx.landdisaster.module.main.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.main.bean.InfoDeliveryBean

import com.zx.landdisaster.module.main.mvp.contract.InfoReleaseContract
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class InfoReleaseModel : BaseModel(), InfoReleaseContract.Model {
    override fun findWeatherdecision(map: Map<String, String>): Observable<NormalList<InfoDeliveryBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .findWeatherdecision(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }


}