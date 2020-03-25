package com.zx.landdisaster.module.system.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.module.system.bean.VersionRecordBean

import com.zx.landdisaster.module.system.mvp.contract.VersionRecordContract
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class VersionRecordModel : BaseModel(), VersionRecordContract.Model {

    override fun findUpdateinfoList(): Observable<List<VersionRecordBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .findUpdateinfoList()
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

}