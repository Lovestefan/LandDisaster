package com.zx.landdisaster.module.disaster.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.module.disaster.bean.HiddenDetailBean

import com.zx.landdisaster.module.disaster.mvp.contract.HiddenPointContract
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class HiddenPointModel : BaseModel(), HiddenPointContract.Model {
    override fun hiddenDetailData(pkiaa: String): Observable<HiddenDetailBean> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getHiddenDetail(pkiaa)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }


}