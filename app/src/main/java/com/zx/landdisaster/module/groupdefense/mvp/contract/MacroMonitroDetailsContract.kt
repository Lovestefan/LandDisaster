package com.zx.landdisaster.module.groupdefense.mvp.contract

import com.frame.zxmvp.base.BasePresenter
import com.frame.zxmvp.base.IModel
import com.frame.zxmvp.base.IView
import com.zx.landdisaster.module.disaster.bean.AuditReportFileBean
import com.zx.landdisaster.module.groupdefense.bean.MacroMonitroCurrentTypeBean
import retrofit2.http.QueryMap
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
interface MacroMonitroDetailsContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View : IView {
        fun findCurrentTypeResult(list: List<MacroMonitroCurrentTypeBean>)

        fun getMacroMonitrolDetailsFileResult(disasterList: List<AuditReportFileBean>)

        fun getMacroMonitrolDetailsFileResult()

        fun getMacroMonitrolDetailsFileError()
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model : IModel {
        fun findCurrentType(setid: String): Observable<List<MacroMonitroCurrentTypeBean>>

        fun getMacroMonitrolDetailsFileList(@QueryMap map: Map<String, String>): Observable<List<AuditReportFileBean>>

    }

    //方法
    abstract class Presenter : BasePresenter<View, Model>() {
        abstract fun findCurrentType(setid: String)

        abstract fun getMacroMonitrolDetailsFileList(logid: String)
    }
}
