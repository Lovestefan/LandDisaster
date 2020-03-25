package com.zx.landdisaster.module.areamanager.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.module.areamanager.bean.AreaManagerBean

import com.zx.landdisaster.module.areamanager.mvp.contract.AreaManagerContract
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class AreaManagerModel : BaseModel(), AreaManagerContract.Model {
    override fun areaData(map: Map<String, String>): Observable<AreaManagerBean> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getArea(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }


}