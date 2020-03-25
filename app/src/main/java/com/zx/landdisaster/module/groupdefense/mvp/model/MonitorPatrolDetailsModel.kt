package com.zx.landdisaster.module.groupdefense.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.module.disaster.bean.AuditReportFileBean
import com.zx.landdisaster.module.groupdefense.mvp.contract.MonitorPatrolDetailsContract
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class MonitorPatrolDetailsModel : BaseModel(), MonitorPatrolDetailsContract.Model {
    override fun getMonitorPatrolDetailsFileList(map: Map<String, String>): Observable<List<AuditReportFileBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getMonitorPatrolDetailsFileList(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }


}