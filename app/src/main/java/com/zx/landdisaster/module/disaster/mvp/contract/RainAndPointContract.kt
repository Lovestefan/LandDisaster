package com.zx.landdisaster.module.disaster.mvp.contract

import com.frame.zxmvp.base.BasePresenter
import com.frame.zxmvp.base.IView
import com.frame.zxmvp.base.IModel
import com.zx.landdisaster.module.main.bean.AreaFromGisBean
import com.zx.landdisaster.module.main.bean.DisasterFromGisBean
import com.zx.landdisaster.module.main.bean.RainStationFromGisBean
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
interface RainAndPointContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View : IView {

        fun onDisasterFromGisResult(data: List<DisasterFromGisBean>?)
        fun onRainStationFromGisResult(data: List<RainStationFromGisBean>?)
        fun onAreaFromGisResult(data: List<AreaFromGisBean>?)
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model : IModel {
        fun getAreaFromGis(map: Map<String, String>): Observable<List<AreaFromGisBean>>
        fun getDisasterFromGis(map: Map<String, String>): Observable<List<DisasterFromGisBean>>
        fun getRainStationFromGis(map: Map<String, String>): Observable<List<RainStationFromGisBean>>

    }

    //方法
    abstract class Presenter : BasePresenter<View, Model>() {
        abstract fun getAreaFromGis(map: Map<String, String>)
        abstract fun getDisasterFromGis(map: Map<String, String>)
        abstract fun getRainStationFromGis(map: Map<String, String>)

    }
}
