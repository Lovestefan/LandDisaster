package com.zx.landdisaster.module.system.mvp.model;

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.base.bean.UserBean
import com.zx.landdisaster.module.disaster.bean.PersonRoleBean
import com.zx.landdisaster.module.system.mvp.contract.SplashContract
import okhttp3.RequestBody
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class SplashModel : BaseModel(), SplashContract.Model {
    override fun doLogin(info: RequestBody): Observable<UserBean> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .doLogin(info)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }


    override fun personRoleData(): Observable<PersonRoleBean> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getPersonRole()
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun checkPassword(map: Map<String, String>): Observable<String> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .checkPassword(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }
    override fun currentAuthMenu(map: Map<String, String>): Observable<List<String>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .currentAuthMenu(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

}