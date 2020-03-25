package com.zx.landdisaster.module.groupdefense.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.module.groupdefense.bean.MacroMonitroCurrentTypeBean

import com.zx.landdisaster.module.groupdefense.mvp.contract.MacroMonitroFillContract
import okhttp3.RequestBody
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class MacroMonitroFillModel : BaseModel(), MacroMonitroFillContract.Model {
    override fun findCurrentType(setid: String): Observable<List<MacroMonitroCurrentTypeBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .findCurrentType(setid)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }


    override fun addMacropatroldata(info: RequestBody): Observable<String> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .addMacropatroldata(info)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun uploadDisasterpoint(info: RequestBody): Observable<String> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .uploadDisasterpoint(info)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }


}