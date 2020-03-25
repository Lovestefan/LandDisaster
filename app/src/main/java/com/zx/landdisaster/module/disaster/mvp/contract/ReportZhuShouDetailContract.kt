package com.zx.landdisaster.module.disaster.mvp.contract

import com.frame.zxmvp.base.BasePresenter
import com.frame.zxmvp.base.IView
import com.frame.zxmvp.base.IModel
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.disaster.bean.ReportZhuShouDetailBean
import com.zx.landdisaster.module.worklog.bean.WorkLogListBean
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
interface ReportZhuShouDetailContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View : IView {
        fun onPageGarrisonreportrateDetailResult(list: NormalList<ReportZhuShouDetailBean>?)
        fun onFindWorkdairyResult(list: NormalList<WorkLogListBean>?)
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model : IModel {
        fun pageGarrisonreportrateDetail(map: Map<String, String>): Observable<NormalList<ReportZhuShouDetailBean>>
        fun findWorkdairy(map: Map<String, String>): Observable<NormalList<WorkLogListBean>>
    }

    //方法
    abstract class Presenter : BasePresenter<View, Model>() {
        abstract fun pageGarrisonreportrateDetail(map: Map<String, String>)
        abstract fun findWorkdairy(map: Map<String, String>)

    }
}
