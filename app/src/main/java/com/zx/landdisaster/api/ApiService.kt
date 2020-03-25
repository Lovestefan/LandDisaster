package com.zx.landdisaster.api

import com.frame.zxmvp.basebean.BaseRespose
import com.google.gson.JsonObject
import com.zx.landdisaster.base.bean.NormalList
import com.zx.landdisaster.base.bean.UserBean
import com.zx.landdisaster.module.areamanager.bean.AreaManagerBean
import com.zx.landdisaster.module.areamanager.bean.PatrolListBean
import com.zx.landdisaster.module.areamanager.bean.WeekWorkListBean
import com.zx.landdisaster.module.dailymanage.bean.ExpertDailyBean
import com.zx.landdisaster.module.disaster.bean.*
import com.zx.landdisaster.module.groupdefense.bean.GroupDefenceBean
import com.zx.landdisaster.module.groupdefense.bean.MacroMonitroBean
import com.zx.landdisaster.module.groupdefense.bean.MacroMonitroCurrentTypeBean
import com.zx.landdisaster.module.groupdefense.bean.MonitorPatrolBean
import com.zx.landdisaster.module.main.bean.*
import com.zx.landdisaster.module.other.bean.CountDisasterBean
import com.zx.landdisaster.module.system.bean.AreaBean
import com.zx.landdisaster.module.system.bean.MessageRecordBean
import com.zx.landdisaster.module.system.bean.VersionRecordBean
import com.zx.landdisaster.module.worklog.bean.DailyAuditListBean
import com.zx.landdisaster.module.worklog.bean.DailyListBean
import com.zx.landdisaster.module.worklog.bean.DailyRemindBean
import com.zx.landdisaster.module.worklog.bean.WorkLogListBean
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*
import rx.Observable

/**
 * Created by Xiangb on 2019/2/26.
 * 功能：
 */
interface ApiService {

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST(ApiConfigModule.URL + "loginApp.do")
    fun doLogin(@Body info: RequestBody): Observable<BaseRespose<UserBean>>

    @POST(ApiConfigModule.URL + "logout.do")
    fun loginOut(): Observable<BaseRespose<String>>

    @POST(ApiConfigModule.URL_FILE + "apk/findUpdateinfo.do")
    fun getVersion(): Observable<BaseRespose<VersionBean>>

    @POST(ApiConfigModule.URL + "user/updatePassword.do")
    fun changePwd(@QueryMap map: Map<String, String>): Observable<BaseRespose<String>>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST(ApiConfigModule.URL + "report/reportDisasterDanger.do")
    fun submitReport(@Body info: RequestBody): Observable<BaseRespose<ReportResultbean>>

    @POST(ApiConfigModule.URL + "report/pageAuditTask.do")
    fun getAuditList(@QueryMap map: Map<String, String>): Observable<BaseRespose<NormalList<AuditListBean>>>

    @POST(ApiConfigModule.URL + "report/pageLeaderReview.do")
    fun getAuditLeaderList(@QueryMap map: Map<String, String>): Observable<BaseRespose<NormalList<AuditListBean>>>

    @POST(ApiConfigModule.URL + "report/pageDispatchReview.do")
    fun getAuditDispatchList(@QueryMap map: Map<String, String>): Observable<BaseRespose<NormalList<AuditListBean>>>

    @POST(ApiConfigModule.URL + "report/findAuditDetail/{url}")
    fun getAuditDetail(@Path("url") path: String): Observable<BaseRespose<ReportDetailBean>>

    @POST(ApiConfigModule.URL_FILE + "report/upload.do")
    fun uploadFile(@Body body: RequestBody): Observable<BaseRespose<String>>

    @POST(ApiConfigModule.URL_FILE + "report/listFiles.do")
    fun getFileList(@QueryMap map: Map<String, String>): Observable<BaseRespose<List<AuditReportFileBean>>>

    @POST(ApiConfigModule.URL_FILE + "directreport/listFiles.do")
    fun getDisasterFileList(@QueryMap map: Map<String, String>): Observable<BaseRespose<List<AuditReportFileBean>>>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST(ApiConfigModule.URL + "report/auditReport.do")
    fun doAuditReport(@Body info: RequestBody): Observable<BaseRespose<Boolean>>

    @POST(ApiConfigModule.URL + "report/findFlowLogs/{url}")
    fun getAuditLogs(@Path("url") path: String): Observable<BaseRespose<List<AuditLogsBean>>>

    @POST(ApiConfigModule.URL + "report/pageReport.do")
    fun getReportList(@QueryMap map: Map<String, String>): Observable<BaseRespose<NormalList<ReportListBean>>>

    @POST(ApiConfigModule.URL + "report/findReportDetail/{url}")
    fun getReportDetail(@Path("url") path: String): Observable<BaseRespose<ReportDetailBean.Reportdata>>

    @POST(ApiConfigModule.URL_FILE + "report/transfer.do")
    fun transferFile(@QueryMap map: Map<String, String>): Observable<BaseRespose<String>>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST(ApiConfigModule.URL + "report/editReport.do")
    fun editReport(@Body info: RequestBody): Observable<BaseRespose<String>>

    @POST(ApiConfigModule.URL_FILE + "report/deleteFiles.do")
    fun deleteFile(@QueryMap map: Map<String, String>): Observable<BaseRespose<Any>>

    @POST(ApiConfigModule.URL + "log/gps/{x}/{y}/")
    fun updateGps(@Path("x") x: String, @Path("y") y: String): Observable<Any>

    @POST(ApiConfigModule.URL + "reportsafety/addReportsafety.do")
    fun submitDailyInfo(@Body info: RequestBody): Observable<BaseRespose<String>>

    @POST(ApiConfigModule.URL + "reportsafety/findReportsafeties.do")
    fun getDailyList(@QueryMap map: Map<String, String>): Observable<BaseRespose<NormalList<DailyListBean>>>

    @POST(ApiConfigModule.URL + "reportsafety/reminding.do")
    fun getDailyRemind(): Observable<BaseRespose<DailyRemindBean>>

    @GET(ApiConfigModule.URL + "report/currentPersonRole.do")
    fun getPersonRole(): Observable<BaseRespose<PersonRoleBean>>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST(ApiConfigModule.URL + "report/leaderReview.do")
    fun doAuditLeader(@QueryMap map: Map<String, String>): Observable<BaseRespose<String>>

    @GET(ApiConfigModule.URL + "log/query.do")
    fun doSearch(@QueryMap map: Map<String, String>): Observable<BaseRespose<NormalList<SearchBean>>>

    @POST(ApiConfigModule.URL + "disasterdanger/pageDisasterDanger.do")
    fun getDisasterList(@QueryMap map: Map<String, String>): Observable<BaseRespose<NormalList<DisasterPointBean>>>

    @POST(ApiConfigModule.URL + "basedisaster/queryBaseDisasterByPage.do")
    fun getHiddenList(@Body info: RequestBody, @Query("pageNo") pageNo: Int = 1, @Query("pageSize") pageSize: Int = 999999): Observable<BaseRespose<NormalList<HiddenPointBean>>>

    @POST(ApiConfigModule.URL + "disasterdanger/findDDdetals.do")
    fun getDisasterDetail(@QueryMap map: Map<String, String>): Observable<BaseRespose<ReportDetailBean.Reportdata>>

    @POST(ApiConfigModule.URL + "basedisaster/queryBaseDisasterDim.do")
    fun getDisasterPkiaaList(@QueryMap map: Map<String, String>): Observable<BaseRespose<NormalList<DisasterPkiaaBean>>>

    @POST(ApiConfigModule.URL + "report/leaderreviewDetail/{auditid}")
    fun getAuditReview(@Path("auditid") auditid: String): Observable<BaseRespose<AuditReviewBean>>

    @POST(ApiConfigModule.URL + "basedisaster/findDetail/{pkiaa}")
    fun getHiddenDetail(@Path("pkiaa") pkiaa: String): Observable<BaseRespose<HiddenDetailBean>>

    @GET
    fun getMapExtent(@Url url: String): Observable<JsonObject>

    @POST(ApiConfigModule.URL_FILE + "rainfall/findRainfall.do")
    fun getRainList(@QueryMap map: Map<String, String>): Observable<BaseRespose<RainDataBean>>

    @GET(ApiConfigModule.URL_FILE + "rainfall_data/{type}/{filename}")
    fun getRainJson(@Path("type") type: String, @Path("filename") filename: String): Observable<JsonObject>

    @POST(ApiConfigModule.URL + "disasterdanger/countDisasterDanger.do")
    fun countDisaster(@QueryMap map: Map<String, String>): Observable<BaseRespose<List<CountDisasterBean>>>

    @POST(ApiConfigModule.URL + "report/countReportTask.do")
    fun getTaskNum(): Observable<BaseRespose<TaskNumBean>>

    @POST(ApiConfigModule.URL + "user/queryPerson/{pkiaa}/{areaCode}")
    fun queryPerson(@Path("pkiaa") pkiaa: String, @Path("areaCode") areaCode: String): Observable<BaseRespose<QueryPersonBean>>

    @POST(ApiConfigModule.URL + "monitorRepor/addWeeklyreport")
    fun uploadWeekWork(@Body info: RequestBody): Observable<BaseRespose<String>>

    @POST(ApiConfigModule.URL + "report/countReportTask.do")
    fun getArea(@QueryMap map: Map<String, String>): Observable<BaseRespose<AreaManagerBean>>

    @POST(ApiConfigModule.URL_FILE + "dailyreport/uploadDailyreport.do")
    fun dailyUploadFile(@Body info: RequestBody): Observable<BaseRespose<String>>

    @POST(ApiConfigModule.URL_FILE + "dailyreport/listFiles.do")
    fun getDailyFile(@QueryMap map: Map<String, String>): Observable<BaseRespose<List<AuditReportFileBean>>>

    @POST(ApiConfigModule.URL + "basedisaster/findDisasterPoint")
    fun findDisasterPoint(): Observable<BaseRespose<List<GroupDefenceBean>>>

    @POST(ApiConfigModule.URL + "basedisaster/findFixMonitroList/{mpid}")
    fun findFixMonitroList(@Path("mpid") mpid: String, @QueryMap map: Map<String, String>): Observable<BaseRespose<List<MonitorPatrolBean>>>

    @POST(ApiConfigModule.URL + "basedisaster/findMacroMonitroList/{setid}")
    fun findMacroMonitroList(@Path("setid") setid: String, @QueryMap map: Map<String, String>): Observable<BaseRespose<List<MacroMonitroBean>>>

    @POST(ApiConfigModule.URL + "basedisaster/addMacropatroldata")
    fun addMacropatroldata(@Body body: RequestBody): Observable<BaseRespose<String>>

    @GET(ApiConfigModule.URL + "basedisaster/findCurrentType/{setid}")
    fun findCurrentType(@Path("setid") setid: String): Observable<BaseRespose<List<MacroMonitroCurrentTypeBean>>>

    @POST(ApiConfigModule.URL + "basedisaster/addMonitorpatroldata")
    fun addMonitorpatroldata(@Body body: RequestBody): Observable<BaseRespose<String>>

    @POST(ApiConfigModule.URL_FILE + "disaterpoint/uploadDisasterpoint.do")
    fun uploadDisasterpoint(@Body info: RequestBody): Observable<BaseRespose<String>>

    @POST(ApiConfigModule.URL_FILE + "disaterpoint/listFiles.do")
    fun getMonitorPatrolDetailsFileList(@QueryMap map: Map<String, String>): Observable<BaseRespose<List<AuditReportFileBean>>>

    @POST(ApiConfigModule.URL_FILE + "workreport/uploadWorkreport")
    fun uploadWeekWorkFile(@Body body: RequestBody): Observable<BaseRespose<String>>

    @POST(ApiConfigModule.URL_FILE + "workreport/listFiles")
    fun getWeekWorkFile(@QueryMap map: Map<String, String>): Observable<BaseRespose<List<AuditReportFileBean>>>

    @POST(ApiConfigModule.URL + "monitorRepor/findWeeklyreport")
    fun getWeekWorkList(@QueryMap map: Map<String, String>): Observable<BaseRespose<NormalList<WeekWorkListBean>>>

    @POST(ApiConfigModule.URL + "monitorRepor/addWorkdairy")
    fun addWorkLog(@Body info: RequestBody): Observable<BaseRespose<String>>

    @GET(ApiConfigModule.URL + "monitorRepor/findWorkdairy")
    fun getWorkLogList(@QueryMap map: Map<String, String>): Observable<BaseRespose<NormalList<WorkLogListBean>>>

    @POST(ApiConfigModule.URL + "monitorRepor/addPatrolrecord")
    fun submit(@Body info: RequestBody): Observable<BaseRespose<String>>

    @POST(ApiConfigModule.URL_FILE + "patrolrecord/uploadPatrolrecord")
    fun uploadPatvolFile(@Body body: RequestBody): Observable<BaseRespose<String>>

    @POST(ApiConfigModule.URL + "monitorRepor/findPatrolrecord")
    fun getPatrolList(@QueryMap map: Map<String, String>): Observable<BaseRespose<List<PatrolListBean>>>

    @POST(ApiConfigModule.URL_FILE + "patrolrecord/listFiles")
    fun getPatrolFile(@QueryMap map: Map<String, String>): Observable<BaseRespose<List<AuditReportFileBean>>>

    @POST(ApiConfigModule.URL + "log/action")
    fun operationLog(@QueryMap info: Map<String, String>): Observable<ResponseBody>

    @POST(ApiConfigModule.URL_FILE + "rainfall/getRainstationList")
    fun getRainPointList(@QueryMap map: Map<String, String>): Observable<BaseRespose<NormalList<RainPointBean>>>

    @GET(ApiConfigModule.URL + "gis/isExistInRegion")
    fun getExistInRegion(@QueryMap map: Map<String, String>): Observable<BaseRespose<String>>

    @POST(ApiConfigModule.URL + "checkPassword")
    fun checkPassword(@QueryMap map: Map<String, String>): Observable<BaseRespose<String>>

    @POST(ApiConfigModule.URL_FILE + "apk/findUpdateinfoList")
    fun findUpdateinfoList(): Observable<BaseRespose<List<VersionRecordBean>>>

    @POST(ApiConfigModule.URL + "reportsafety/pageAuditReport.do")
    fun pageAuditReport(@QueryMap map: Map<String, String>): Observable<BaseRespose<NormalList<DailyAuditListBean>>>

    @POST(ApiConfigModule.URL + "reportsafety/auditReport.do")
    fun auditReport(@Body info: RequestBody): Observable<BaseRespose<String>>

    @POST(ApiConfigModule.URL + "weatherdecision/findWeatherdecision")
    fun findWeatherdecision(@QueryMap map: Map<String, String>): Observable<BaseRespose<NormalList<InfoDeliveryBean>>>

    @GET(ApiConfigModule.URL_FILE + "weatherdecision/read/{serviceid}")
    fun getInfoReleaseInfoFile(@Path("serviceid") serviceid: String, @QueryMap info: Map<String, String>): Observable<BaseRespose<InfoReleaseBean>>

    @POST("aggregation/fileManager/registHeadUp")
    fun hearUpload(@Body map: RequestBody): Observable<BaseRespose<String>>

    @POST(ApiConfigModule.URL + "regist.do")
    fun regist(@QueryMap info: Map<String, String>): Observable<BaseRespose<String>>

    @GET("aggregation/areapandect/findByParent")
    fun findByParent(@QueryMap info: Map<String, String>): Observable<BaseRespose<List<AreaBean>>>

    @GET("aggregation/user/checkAccount")
    fun checkAccount(@QueryMap info: Map<String, String>): Observable<BaseRespose<String>>

    @GET(ApiConfigModule.URL + "menu/currentAuthMenu.do")
    fun currentAuthMenu(@QueryMap map: Map<String, String>): Observable<BaseRespose<List<String>>>

    @GET(ApiConfigModule.URL + "weatherdecision/getWeatherdescription")
    fun getWeatherdescription(): Observable<BaseRespose<String>>

    @GET
    fun getDegree(@Url url: String): Observable<WeatherBean>

    @POST(ApiConfigModule.URL + "gis/getUpdateTime")
    fun getUpdateTime(@QueryMap info: Map<String, String>): Observable<BaseRespose<String>>

    @POST(ApiConfigModule.URL + "weatherdecision/readed/{serviceid}")
    fun readed(@Path("serviceid") serviceid: String): Observable<BaseRespose<String>>

    @GET(ApiConfigModule.URL + "log/getOwnPushList")
    fun getOwnPushList(@QueryMap map: Map<String, String>): Observable<BaseRespose<NormalList<MessageRecordBean>>>

    //区县日报
    @POST(ApiConfigModule.URL + "reportsafety/countReports.do")
    fun countReports(@QueryMap map: Map<String, String>): Observable<BaseRespose<NormalList<ReportstatisBean>>>

    //区县日报
    @POST(ApiConfigModule.URL + "reportsafety/findNoReportTime.do")
    fun findNoReportTime(@QueryMap map: Map<String, String>): Observable<BaseRespose<List<String>>>

    //驻守日志
    @POST(ApiConfigModule.URL + "reportrate/pageGarrisonreportrate")
    fun pageGarrisonreportrate(@QueryMap map: Map<String, String>): Observable<BaseRespose<NormalList<ReportstatisBean>>>

    //驻守日志-详情
    @POST(ApiConfigModule.URL + "reportrate/pageGarrisonreportrateDetail.do")
    fun pageGarrisonreportrateDetail(@QueryMap map: Map<String, String>): Observable<BaseRespose<NormalList<ReportZhuShouDetailBean>>>

    //驻守日志-详情-实际上报列表
    @POST(ApiConfigModule.URL + "monitorRepor/findWorkdairy.do")
    fun findWorkdairy(@QueryMap map: Map<String, String>): Observable<BaseRespose<NormalList<WorkLogListBean>>>

    //片区周报
    @POST(ApiConfigModule.URL + "reportrate/pageAreaManagerreportrate")
    fun pageAreaManagerreportrate(@QueryMap map: Map<String, String>): Observable<BaseRespose<NormalList<ReportstatisBean>>>

    //片区周报-详情
    @POST(ApiConfigModule.URL + "reportrate/pageAreaManagerreportrateDetail.do")
    fun pageAreaManagerreportrateDetail(@QueryMap map: Map<String, String>): Observable<BaseRespose<NormalList<ReportPianQuDetailBean>>>

    //监测数据
    @POST(ApiConfigModule.URL + "reportrate/pageGroupdefensesreportrate.do")
    fun pageGroupdefensesreportrate(@QueryMap map: Map<String, String>): Observable<BaseRespose<NormalList<ReportStatisGroupdeBean>>>

    //监测数据-详情
    @POST(ApiConfigModule.URL + "reportrate/pageGroupdefensesreportrateDetail.do")
    fun pageGroupdefensesreportrateDetail(@QueryMap map: Map<String, String>): Observable<BaseRespose<NormalList<ReportStatisGroupDetailBean>>>

    @POST(ApiConfigModule.URL + "professorreport/findMyReport")
    fun findMyReport(@QueryMap map: Map<String, String>): Observable<BaseRespose<NormalList<ExpertDailyBean>>>

    @POST(ApiConfigModule.URL_FILE + "rainfall/getMaxRainstation")
    fun getMaxRainstation(@QueryMap map: Map<String, String>): Observable<BaseRespose<List<RainPointBean>>>

    //收到下线消息后调的接口
    @POST(ApiConfigModule.URL + "contacts/getBatch.do")
    fun getBatch(): Observable<BaseRespose<String>>

    //注册验证码
    @POST(ApiConfigModule.URL + "sendRegister.do")
    fun sendRegister(@QueryMap map: Map<String, String>): Observable<BaseRespose<String?>>

    //修改密码 验证码
    @POST(ApiConfigModule.URL + "user/sendUpdatePwd.do")
    fun sendUpdatePwd(@QueryMap map: Map<String, String>): Observable<BaseRespose<String?>>

    @POST(ApiConfigModule.URL + "user/updatePasswordByCode.do")
    fun updatePassword(@QueryMap map: Map<String, String>): Observable<BaseRespose<String>>

    //获取登录 验证码
    @POST(ApiConfigModule.URL + "sendLoginCheck.do")
    fun sendLoginCheck(@QueryMap map: Map<String, String>): Observable<BaseRespose<String?>>

    // 验证码登录
    @POST(ApiConfigModule.URL + "loginAppBySms.do")
    fun loginAppBySms(@Body info: RequestBody): Observable<BaseRespose<UserBean>>

    // 雨量分析
    @GET(ApiConfigModule.URL + "liveweatherwarn/getLiveInfomation")
    fun getLiveInfomation(@QueryMap map: Map<String, String>): Observable<BaseRespose<LiveInfoMationBean>>

    // 获取实况天气预警隐患点
    @GET(ApiConfigModule.URL + "liveweatherwarn/getDisasterFromGis")
    fun getDisasterFromGis(@QueryMap map: Map<String, String>): Observable<BaseRespose<List<DisasterFromGisBean>>>

    // 获取实况天气预警乡镇
    @GET(ApiConfigModule.URL + "liveweatherwarn/getAreaFromGis")
    fun getAreaFromGis(@QueryMap map: Map<String, String>): Observable<BaseRespose<List<AreaFromGisBean>>>

    // 获取实况天气预警雨量站
    @GET(ApiConfigModule.URL + "liveweatherwarn/getRainStationFromGis")
    fun getRainStationFromGis(@QueryMap map: Map<String, String>): Observable<BaseRespose<List<RainStationFromGisBean>>>
}