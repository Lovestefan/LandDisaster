package com.zx.landdisaster.module.areamanager.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService

import com.zx.landdisaster.module.areamanager.mvp.contract.PatrolDetailContract
import com.zx.landdisaster.module.disaster.bean.AuditReportFileBean
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class PatrolDetailModel : BaseModel(), PatrolDetailContract.Model {

    override fun getPatrolFileData(map: Map<String, String>): Observable<List<AuditReportFileBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getPatrolFile(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

}