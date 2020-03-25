package com.zx.landdisaster.module.areamanager.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.module.areamanager.bean.PatrolListBean

import com.zx.landdisaster.module.areamanager.mvp.contract.PatrolListContract
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：巡查上报列表
 */
class PatrolListModel : BaseModel(), PatrolListContract.Model {

    override fun patrolListData(map: Map<String, String>): Observable<List<PatrolListBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getPatrolList(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }

}