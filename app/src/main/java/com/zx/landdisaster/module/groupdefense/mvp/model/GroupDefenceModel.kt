package com.zx.landdisaster.module.groupdefense.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.module.groupdefense.bean.GroupDefenceBean

import com.zx.landdisaster.module.groupdefense.mvp.contract.GroupDefenceContract
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class GroupDefenceModel : BaseModel(), GroupDefenceContract.Model {
    override fun findDisasterPoint(): Observable<List<GroupDefenceBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .findDisasterPoint()
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }


}