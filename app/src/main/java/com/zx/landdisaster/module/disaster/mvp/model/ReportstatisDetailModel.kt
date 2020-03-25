package com.zx.landdisaster.module.disaster.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.disaster.bean.ReportStatisGroupDetailBean

import com.zx.landdisaster.module.disaster.mvp.contract.ReportstatisDetailContract
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class ReportstatisDetailModel : BaseModel(), ReportstatisDetailContract.Model {

    override fun pageGroupdefensesreportrateDetail(map: Map<String, String>): Observable<NormalList<ReportStatisGroupDetailBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .pageGroupdefensesreportrateDetail(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

}