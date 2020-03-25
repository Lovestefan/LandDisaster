package com.zx.landdisaster.module.main.mvp.contract

import com.frame.zxmvp.base.BasePresenter
import com.frame.zxmvp.base.IModel
import com.frame.zxmvp.base.IView
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.main.bean.InfoDeliveryBean
import com.zx.landdisaster.module.main.bean.VersionBean
import com.zx.landdisaster.module.main.bean.WeatherBean
import com.zx.landdisaster.module.worklog.bean.DailyRemindBean
import rx.Observable
import java.io.File

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
interface HomeContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View : IView {
        fun onFindWeatherdecisionResult(list: NormalList<InfoDeliveryBean>?)
        fun onWeatherdescriptionResult(data: String?)
        fun onDegreeResult(data: WeatherBean?)
        fun onVersionResult(versionBean: VersionBean)

        fun onApkDownloadResult(file : File)
        fun onRemindResult(reminBean : DailyRemindBean)
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model : IModel {
        fun findWeatherdecision(map: Map<String, String>): Observable<NormalList<InfoDeliveryBean>>
        fun getWeatherdescription(): Observable<String?>
        fun getDegree(url: String): Observable<WeatherBean>
        fun versionData(): Observable<VersionBean>
        fun updateLocation(x : String, y : String) : Observable<Any>
        fun dailyRemindData() : Observable<DailyRemindBean>
    }

    //方法
    abstract class Presenter : BasePresenter<View, Model>() {
        abstract fun getFindWeatherdecisionData(map: Map<String, String>)
        abstract fun getWeatherdescription()
        abstract fun getDegree(url: String)
        abstract fun getVerson()

        abstract fun downloadApk(downUrl : String)
        abstract fun getDailyRemind()
        abstract fun updateLocation(x: String, y: String)
    }
}
