package com.zx.landdisaster.module.main.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.main.bean.SearchBean

import com.zx.landdisaster.module.main.mvp.contract.MapSearchContract
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class MapSearchModel : BaseModel(), MapSearchContract.Model {
    override fun searchData(map: Map<String, String>): Observable<NormalList<SearchBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .doSearch(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }


}