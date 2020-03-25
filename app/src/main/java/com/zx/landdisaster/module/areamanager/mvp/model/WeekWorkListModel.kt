package com.zx.landdisaster.module.areamanager.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.areamanager.bean.WeekWorkListBean
import rx.Observable

import com.zx.landdisaster.module.areamanager.mvp.contract.WeekWorkListContract

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class WeekWorkListModel : BaseModel(), WeekWorkListContract.Model {
    override fun weekWorkListData(map: Map<String, String>): Observable<NormalList<WeekWorkListBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .getWeekWorkList(map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }


}