package com.zx.landdisaster.module.disaster.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.disaster.bean.AuditListBean
import com.zx.landdisaster.module.disaster.mvp.contract.AuditListContract
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class AuditListModel : BaseModel(), AuditListContract.Model {

    override fun auditListData(map: Map<String, String>): Observable<NormalList<AuditListBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getAuditList(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun auditListLeaderData(map: Map<String, String>): Observable<NormalList<AuditListBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getAuditLeaderList(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun auditListDispatchData(map: Map<String, String>): Observable<NormalList<AuditListBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getAuditDispatchList(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

}