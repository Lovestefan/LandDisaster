package com.zx.landdisaster.module.disaster.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.module.disaster.bean.AuditLogsBean
import com.zx.landdisaster.module.disaster.mvp.contract.AuditHandleLogsContract
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class AuditHandleLogsModel : BaseModel(), AuditHandleLogsContract.Model {

    override fun auditLogsData(path: String): Observable<List<AuditLogsBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getAuditLogs(path)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }


}