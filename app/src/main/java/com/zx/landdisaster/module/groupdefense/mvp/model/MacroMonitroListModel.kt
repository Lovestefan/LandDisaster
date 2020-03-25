package com.zx.landdisaster.module.groupdefense.mvp.model

import com.frame.zxmvp.base.BaseModel
import com.frame.zxmvp.baserx.RxHelper
import com.frame.zxmvp.baserx.RxSchedulers
import com.zx.landdisaster.api.ApiService
import com.zx.landdisaster.module.groupdefense.bean.MacroMonitroBean

import com.zx.landdisaster.module.groupdefense.mvp.contract.MacroMonitroListContract
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
class MacroMonitroListModel : BaseModel(), MacroMonitroListContract.Model {
    override fun findMacroMonitroList(setid: String, map: Map<String, String>): Observable<List<MacroMonitroBean>> {
        return mRepositoryManager.obtainRetrofitService(ApiService::class.java)
                .findMacroMonitroList(setid, map)
                .compose(RxHelper.handleResult())
                .compose(RxSchedulers.io_main())
    }


}