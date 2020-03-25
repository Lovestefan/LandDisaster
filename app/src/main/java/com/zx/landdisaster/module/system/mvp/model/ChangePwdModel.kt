package com.zx.landdisaster.module.system.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.module.system.mvp.contract.ChangePwdContract
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class ChangePwdModel : BaseModel(), ChangePwdContract.Model {
    override fun changePwdData(map: Map<String, String>): Observable<String> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .changePwd(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }
    override fun checkPassword(map: Map<String, String>): Observable<String> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .checkPassword(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun updatePasswordByCode(map: Map<String, String>): Observable<String?> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .updatePassword(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }
    override fun sendUpdatePwd(map: Map<String, String>): Observable<String?> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .sendUpdatePwd(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

}