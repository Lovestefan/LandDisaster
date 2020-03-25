package com.zx.landdisaster.module.main.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.main.bean.InfoDeliveryBean
import com.zx.landdisaster.module.main.bean.VersionBean
import com.zx.landdisaster.module.main.bean.WeatherBean

import com.zx.landdisaster.module.main.mvp.contract.HomeContract
import com.zx.landdisaster.module.worklog.bean.DailyRemindBean
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class HomeModel : BaseModel(), HomeContract.Model {
    override fun findWeatherdecision(map: Map<String, String>): Observable<NormalList<InfoDeliveryBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .findWeatherdecision(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun getWeatherdescription(): Observable<String?> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getWeatherdescription()
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun getDegree(url: String): Observable<WeatherBean> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getDegree(url)
                .compose(RxSchedulers.io_main())
    }

    override fun versionData(): Observable<VersionBean> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getVersion()
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun updateLocation(x: String, y: String): Observable<Any> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .updateGps(x, y)
                .compose(RxSchedulers.io_main())
    }

    override fun dailyRemindData(): Observable<DailyRemindBean> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getDailyRemind()
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

}