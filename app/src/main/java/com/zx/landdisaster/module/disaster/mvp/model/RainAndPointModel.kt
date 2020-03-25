package com.zx.landdisaster.module.disaster.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService

import com.zx.landdisaster.module.disaster.mvp.contract.RainAndPointContract
import com.zx.landdisaster.module.main.bean.AreaFromGisBean
import com.zx.landdisaster.module.main.bean.DisasterFromGisBean
import com.zx.landdisaster.module.main.bean.RainStationFromGisBean
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class RainAndPointModel : BaseModel(), RainAndPointContract.Model {
    override fun getAreaFromGis(map: Map<String, String>): Observable<List<AreaFromGisBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getAreaFromGis(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun getDisasterFromGis(map: Map<String, String>): Observable<List<DisasterFromGisBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getDisasterFromGis(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }
    override fun getRainStationFromGis(map: Map<String, String>): Observable<List<RainStationFromGisBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getRainStationFromGis(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

}