package com.zx.landdisaster.module.disaster.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.disaster.bean.ReportZhuShouDetailBean

import com.zx.landdisaster.module.disaster.mvp.contract.ReportZhuShouDetailContract
import com.zx.landdisaster.module.worklog.bean.WorkLogListBean
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：驻守日志-详情
 */
class ReportZhuShouDetailModel : BaseModel(), ReportZhuShouDetailContract.Model {

    override fun pageGarrisonreportrateDetail(map: Map<String, String>): Observable<NormalList<ReportZhuShouDetailBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .pageGarrisonreportrateDetail(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }
    override fun findWorkdairy(map: Map<String, String>): Observable<NormalList<WorkLogListBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .findWorkdairy(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

}