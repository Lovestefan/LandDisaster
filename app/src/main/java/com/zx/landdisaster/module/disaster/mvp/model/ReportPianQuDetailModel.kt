package com.zx.landdisaster.module.disaster.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.disaster.bean.ReportPianQuDetailBean

import com.zx.landdisaster.module.disaster.mvp.contract.ReportPianQuDetailContract
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class ReportPianQuDetailModel : BaseModel(), ReportPianQuDetailContract.Model {

    override fun pageAreaManagerreportrateDetail(map: Map<String, String>): Observable<NormalList<ReportPianQuDetailBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .pageAreaManagerreportrateDetail(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

}