package com.zx.landdisaster.module.groupdefense.mvp.contract

import com.frame.zxmvp.base.BasePresenter
import com.frame.zxmvp.base.IModel
import com.frame.zxmvp.base.IView
import com.zx.landdisaster.module.groupdefense.bean.MonitorPatrolFillBean
import okhttp3.RequestBody
import rx.Observable
import java.io.File

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
interface MonitorPatrolFillContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View : IView {

        fun addMonitorpatroldataResult(data:String)

        fun onAddMonitorpatrolFileAddPregress(progress: Int)

        fun onAddMonitorpatrolFileAddResult()

        fun onSubmitErrorResult()


    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model : IModel {
        fun addMonitorpatroldata(info: RequestBody): Observable<String>

        fun uploadDisasterpoint(info: RequestBody) : Observable<String>

    }

    //方法
    abstract class Presenter : BasePresenter<View, Model>() {
        abstract fun addMonitorpatroldata(fillBean: MonitorPatrolFillBean)

        abstract fun addFile(files: List<File>, logid: String)

    }
}
