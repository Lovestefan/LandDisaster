package com.zx.landdisaster.module.main.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.module.main.bean.InfoReleaseBean

import com.zx.landdisaster.module.main.mvp.contract.InfoReleaseInfoContract
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class InfoReleaseInfoModel : BaseModel(), InfoReleaseInfoContract.Model {

    override fun readFile(serviceid: String, map: Map<String, String>): Observable<InfoReleaseBean> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getInfoReleaseInfoFile(serviceid, map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }
}