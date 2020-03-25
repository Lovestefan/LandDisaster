package com.zx.landdisaster.module.system.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.module.main.bean.VersionBean

import com.zx.landdisaster.module.system.mvp.contract.UserContract
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class UserModel : BaseModel(), UserContract.Model {

    override fun loginOutData(): Observable<String> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .loginOut()
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