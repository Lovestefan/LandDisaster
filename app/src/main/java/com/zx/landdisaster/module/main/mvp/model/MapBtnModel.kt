package com.zx.landdisaster.module.main.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.disaster.bean.LiveInfoMationBean
import com.zx.landdisaster.module.disaster.bean.RainPointBean
import com.zx.landdisaster.module.main.bean.*
import com.zx.landdisaster.module.main.mvp.contract.MapBtnContract
import okhttp3.RequestBody
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class MapBtnModel : BaseModel(), MapBtnContract.Model {

    override fun disasterListData(map: Map<String, String>): Observable<NormalList<DisasterPointBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getDisasterList(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun hiddenListData(info: RequestBody): Observable<NormalList<HiddenPointBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getHiddenList(info)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun rainListData(map: Map<String, String>): Observable<RainDataBean> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getRainList(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun taskNumData(): Observable<TaskNumBean> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getTaskNum()
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun getUpdateTime(map: Map<String, String>): Observable<String> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getUpdateTime(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }
    override fun getMaxRainstation(map: Map<String, String>): Observable<List<RainPointBean>?> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getMaxRainstation(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }
    override fun getLiveInfomation(map: Map<String, String>): Observable<LiveInfoMationBean> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getLiveInfomation(map)
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