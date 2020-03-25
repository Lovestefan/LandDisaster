package com.zx.landdisaster.module.main.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.module.main.bean.VersionBean

import com.zx.landdisaster.module.main.mvp.contract.MainOtherContract
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class MainOtherModel : BaseModel(), MainOtherContract.Model {
    override fun versionData(): Observable<VersionBean> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getVersion()
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun updateLocation(x: String, y: String) : Observable<Any> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .updateGps(x,y)
                .compose(RxSchedulers.io_main())
    }

}