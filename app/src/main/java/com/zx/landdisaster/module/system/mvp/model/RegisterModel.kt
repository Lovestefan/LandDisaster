package com.zx.landdisaster.module.system.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.module.system.bean.AreaBean

import com.zx.landdisaster.module.system.mvp.contract.RegisterContract
import okhttp3.RequestBody
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class RegisterModel : BaseModel(), RegisterContract.Model {
    override fun getHearUpload(map: RequestBody): Observable<String> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .hearUpload(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun getRegister(map: Map<String, String>): Observable<String> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .regist(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun findByParent(map: Map<String, String>): Observable<List<AreaBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .findByParent(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun getCheckAccount(map: Map<String, String>): Observable<String> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .checkAccount(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun checkPassword(map: Map<String, String>): Observable<String> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .checkPassword(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun sendRegister(map: Map<String, String>): Observable<String?> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .sendRegister(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }
}