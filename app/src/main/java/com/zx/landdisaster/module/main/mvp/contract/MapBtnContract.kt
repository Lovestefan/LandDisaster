package com.zx.landdisaster.module.main.mvp.contract

import com.frame.zxmvp.base.BasePresenter
import com.frame.zxmvp.base.IModel
import com.frame.zxmvp.base.IView
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.module.disaster.bean.LiveInfoMationBean
import com.zx.landdisaster.module.disaster.bean.RainPointBean
import com.zx.landdisaster.module.main.bean.*
import okhttp3.RequestBody
import rx.Observable

/**
 * Create By admin On 2017/7/11
 * 功能：
 */
interface MapBtnContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View : IView {
        fun onDisasterResult(disasterList: List<DisasterPointBean>)

        fun onHiddenResult(disasterList: List<HiddenPointBean>)

        fun onRainListResult(rainDataBean: RainDataBean?)

        fun onTaskNumResult(numBean: TaskNumBean)
        fun onUpdateTimeResult(data: String)
        fun onMaxRainstationResult(rainPointList: List<RainPointBean>?)

        fun onLiveInfomationResult(rainDataBean: LiveInfoMationBean?)
        fun onDisasterFromGisResult(data: List<DisasterFromGisBean>?)
        fun onRainStationFromGisResult(data: List<RainStationFromGisBean>?)
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model : IModel {
        fun disasterListData(map: Map<String, String>): Observable<NormalList<DisasterPointBean>>

        fun hiddenListData(info: RequestBody): Observable<NormalList<HiddenPointBean>>

        fun rainListData(map: Map<String, String>): Observable<RainDataBean>

        fun taskNumData(): Observable<TaskNumBean>
        fun getUpdateTime(map: Map<String, String>): Observable<String>

        fun getMaxRainstation(map: Map<String, String>): Observable<List<RainPointBean>?>
        fun getLiveInfomation(map: Map<String, String>): Observable<LiveInfoMationBean>
        fun getDisasterFromGis(map: Map<String, String>): Observable<List<DisasterFromGisBean>>
        fun getRainStationFromGis(map: Map<String, String>): Observable<List<RainStationFromGisBean>>
    }

    //方法
    abstract class Presenter : BasePresenter<View, Model>() {
        abstract fun getDisasterList(map: Map<String, String>)

        abstract fun getHiddenList(info: RequestBody)

        abstract fun getRainList(map: Map<String, String>)

        abstract fun getTaskNum()
        abstract fun getUpdateTime(map: Map<String, String>)
        abstract fun getMaxRainstation(map: Map<String, String>)
        //获取实况天气预警隐患点、雨量站、乡镇数，最大雨量站信息
        abstract fun getLiveInfomation(map: Map<String, String>)
        abstract fun getDisasterFromGis(map: Map<String, String>)
        abstract fun getRainStationFromGis(map: Map<String, String>)
    }
}
