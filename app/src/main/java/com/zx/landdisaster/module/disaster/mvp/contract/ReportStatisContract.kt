package com.zx.landdisaster.module.disaster.mvp.contract

import com.frame.zxmvp.base.BasePresenter
import com.frame.zxmvp.base.IView
import com.frame.zxmvp.base.IModel
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.disaster.bean.ReportStatisGroupdeBean
import com.zx.landdisaster.module.disaster.bean.ReportstatisBean
import com.zx.landdisaster.module.system.bean.AreaBean
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
interface ReportStatisContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View : IView {
        fun onReportStatisResult(list: NormalList<ReportstatisBean>?)
        fun onFindByParentResult(areaBean: List<AreaBean>?)
        fun onPageGroupdefensesreportrate(list: NormalList<ReportStatisGroupdeBean>?)
        fun onFindNoReportTime(list: List<String>?)
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model : IModel {
        fun countReports(map: Map<String, String>): Observable<NormalList<ReportstatisBean>>
        fun pageGarrisonreportrate(map: Map<String, String>): Observable<NormalList<ReportstatisBean>>
        fun pageAreaManagerreportrate(map: Map<String, String>): Observable<NormalList<ReportstatisBean>>
        fun pageGroupdefensesreportrate(map: Map<String, String>): Observable<NormalList<ReportStatisGroupdeBean>>

        fun findByParent(map: Map<String, String>): Observable<List<AreaBean>>
        fun findNoReportTime(map: Map<String, String>): Observable<List<String>>

    }

    //方法
    abstract class Presenter : BasePresenter<View, Model>() {
        abstract fun countReports(map: Map<String, String>)
        abstract fun pageGarrisonreportrate(map: Map<String, String>)
        abstract fun pageAreaManagerreportrate(map: Map<String, String>)
        abstract fun pageGroupdefensesreportrate(map: Map<String, String>)
        abstract fun getFindByParent(map: Map<String, String>)
        abstract fun findNoReportTime(map: Map<String, String>)

    }
}
