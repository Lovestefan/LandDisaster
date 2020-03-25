package com.zx.landdisaster.module.disaster.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.disaster.bean.ReportStatisGroupdeBean
import com.zx.landdisaster.module.disaster.bean.ReportstatisBean

import com.zx.landdisaster.module.disaster.mvp.contract.ReportStatisContract
import com.zx.landdisaster.module.system.bean.AreaBean
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class ReportStatisModel : BaseModel(), ReportStatisContract.Model {

    override fun countReports(map: Map<String, String>): Observable<NormalList<ReportstatisBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .countReports(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }
    override fun pageGarrisonreportrate(map: Map<String, String>): Observable<NormalList<ReportstatisBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .pageGarrisonreportrate(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }
    override fun pageAreaManagerreportrate(map: Map<String, String>): Observable<NormalList<ReportstatisBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .pageAreaManagerreportrate(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }
    override fun pageGroupdefensesreportrate(map: Map<String, String>): Observable<NormalList<ReportStatisGroupdeBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .pageGroupdefensesreportrate(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }
    override fun findByParent(map: Map<String, String>): Observable<List<AreaBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .findByParent(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }
    override fun findNoReportTime(map: Map<String, String>): Observable<List<String>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .findNoReportTime(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

}