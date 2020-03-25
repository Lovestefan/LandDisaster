package com.zx.landdisaster.module.system.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.main.bean.VersionBean
import com.zx.landdisaster.module.system.bean.MessageRecordBean

import com.zx.landdisaster.module.system.mvp.contract.MessageRecordContract
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class MessageRecordModel : BaseModel(), MessageRecordContract.Model {

    override fun getPushList(map: Map<String, String>): Observable<NormalList<MessageRecordBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getOwnPushList(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }
    override fun versionData(): Observable<VersionBean> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getVersion()
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

}