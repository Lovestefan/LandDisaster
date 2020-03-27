package com.zx.landdisaster.api

import android.util.Log
import com.google.gson.Gson
import com.zx.landdisaster.base.other.UserManager
import com.zx.landdisaster.base.tool.RSADataUtil
import com.zx.landdisaster.module.disaster.bean.ReportDetailBean
import com.zx.landdisaster.module.groupdefense.bean.MacroMonitroFillBean
import com.zx.landdisaster.module.groupdefense.bean.MonitorPatrolFillBean
import com.zx.zxutils.util.*
import okhttp3.MediaType
import okhttp3.RequestBody
import java.text.SimpleDateFormat


/**
 * Created by Xiangb on 2019/2/26.
 * 功能：
 */
object ApiParamUtil {

    private fun toJson(map: HashMap<String, String>): RequestBody {
//        if (UserManager.getUser().token.isNotEmpty()) map["token"] = UserManager.getUser().token
        val json = Gson().toJson(map)
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
    }

    private fun toJsonAny(map: HashMap<String, Any>): RequestBody {
//        if (UserManager.getUser().token.isNotEmpty()) map["token"] = UserManager.getUser().token
        val json = Gson().toJson(map)
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
    }

    //登录
    fun loginParam(account: String, password: String, imei: String): RequestBody {
        ApiConfigModule.COOKIE = ""
        val map = hashMapOf<String, String>()
        map["account"] = account
        map["password"] = getPwd(password)
        map["ims"] = ZXUniqueIdUtil.getUniqueId()
        map["imei"] = imei
        map["appVersion"] = ZXSystemUtil.getVersionCode().toString()
        Log.e("map=", map.toString())
        return toJson(map)
    }

    //验证码登录
    fun loginAppBySms(account: String, code: String, imei: String): RequestBody {
        ApiConfigModule.COOKIE = ""
        val map = hashMapOf<String, String>()
        map["phone"] = account
        map["code"] = code
        map["ims"] = ZXUniqueIdUtil.getUniqueId()
        map["imei"] = imei
        map["appVersion"] = ZXSystemUtil.getVersionCode().toString()
        Log.e("map=", map.toString())
        return toJson(map)
    }

    //修改密码
    fun changePwdParam(oldPwd: String, newPwd: String): Map<String, String> {
        val map = hashMapOf<String, String>()
        map["oldPwd"] = getPwd(ZXMD5Util.getMD5(oldPwd))
        map["newPwd"] = getPwd(ZXMD5Util.getMD5(newPwd))
        return map
    }

    //测试
    var publicKey_230 = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBALkjyeUyEuGeyHZ69CB264BQImvy9Kdm9v//Ygq7mEgn/qLZqKcvxscPCjFeEKh+VG5d2rNvBXKlRbQ5b0fCAhcCAwEAAQ=="
    //正式
    var publicKey_183 = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKBt/+JnECk0rZH9JbDgJ693be8z7noIxiQ92WhG1nHKm4VsaZtU4qKzeu+KF4uMiUP6rY2lb2hiIWg1d7GuRa8CAwEAAQ=="

    fun getPwd(pwd: String): String {
        var key = publicKey_230
        if (ZXSharedPrefUtil().getString("base_ip", ApiConfigModule.ip_1) == ApiConfigModule.ip_1)
            key = publicKey_183
        if (ZXSharedPrefUtil().getString("base_ip", ApiConfigModule.ip_1) == ApiConfigModule.ip_2)
            key = publicKey_183

        val decrypt = RSADataUtil.byte2Base64(
                RSADataUtil.publicEncrypt(pwd.toUpperCase(), RSADataUtil.string2PublicKey(key)))
        return decrypt.replace("+", "-").replace("/", "_")
    }


    //数据上报
    fun reortSubmitParam(reportDetailBean: ReportDetailBean): RequestBody {
        val map = hashMapOf<String, String>()
        reportDetailBean.reportdata!!.apply {
            if (!hazardname.isNullOrEmpty()) map["hazardname"] = hazardname!!
            if (!happentime.isNullOrEmpty()) map["happentime"] = ZXTimeUtil.getTime(happentime!!.toLong(), SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+SSSS")).replace("+0000", "+0800")
            if (!hazardtype.isNullOrEmpty()) map["hazardtype"] = hazardtype.toString()
            if (!isarea.isNullOrEmpty()) map["isarea"] = isarea!!
            if (!disastertype.isNullOrEmpty()) map["disastertype"] = disastertype!!
            if (!scalelevel.isNullOrEmpty()) map["scalelevel"] = scalelevel!!
            if (!disvolume.isNullOrEmpty()) map["disvolume"] = disvolume!!
//            if (!areacode.isNullOrEmpty()) map["areacode"] = areacode!!
            if (!longitude.isNullOrEmpty()) map["longitude"] = longitude!!
            if (!latitude.isNullOrEmpty()) map["latitude"] = latitude!!
            if (!address.isNullOrEmpty()) map["address"] = address!!
            if (!getThreatObject().isNullOrEmpty()) map["threatenobj"] = getThreatObject()!!
            if (!threatnum.isNullOrEmpty()) map["threatnum"] = threatnum!!
            if (!leavenum.isNullOrEmpty()) map["leavenum"] = leavenum!!
            if (!injuredperson.isNullOrEmpty()) map["injuredperson"] = injuredperson!!
            if (!dienum.isNullOrEmpty()) map["dienum"] = dienum!!
            if (!missingnum.isNullOrEmpty()) map["missingnum"] = missingnum!!
            if (!economicloss.isNullOrEmpty()) map["economicloss"] = economicloss!!
            if (!cause.isNullOrEmpty()) map["cause"] = cause!!
            if (!causenote.isNullOrEmpty()) map["causenote"] = causenote!!
            if (!dealidea.isNullOrEmpty()) map["dealidea"] = dealidea!!
            if (!nextjobplan.isNullOrEmpty()) map["nextjobplan"] = nextjobplan!!
            if (!pkiaa.isNullOrEmpty()) map["pkiaa"] = pkiaa!!
            if (!pkidd.isNullOrEmpty()) map["pkidd"] = pkidd!!
            if (!reportorphone.isNullOrEmpty()) map["reportorphone"] = reportorphone!!
            if (!reportdept.isNullOrEmpty()) map["reportdept"] = reportdept!!
            if (!reportdeptDesc.isNullOrEmpty()) map["reportdeptDesc"] = reportdeptDesc!!
            if (!reportnote.isNullOrEmpty()) map["reportnote"] = reportnote!!
            if (!flownum.isNullOrEmpty()) map["flownum"] = flownum!!
            map["reportdataid"] = reportdataid!!
        }
        return ApiParamUtil.toJson(map)
    }

    //审核列表
    fun auditListParam(pageNo: Int, pageSize: Int = 10, hazardtype: Int = 0): HashMap<String, String> {
        val map = hashMapOf<String, String>()
        map["pageNo"] = pageNo.toString()
        map["pageSize"] = pageSize.toString()
        map["hazardtype"] = if (hazardtype == 0) "" else hazardtype.toString()
        map["auditstatus"] = "0"
//        map["token"] = UserManager.getUser().token
        return map
    }

    //审核详情
    fun auditDetailParam(auditKind: Int, flownum: String, auditid: String, reportdataid: String): String {
        val path = "$auditKind/$flownum/$auditid/$reportdataid"
        return path
    }

    //列举文件
    fun fileListParam(flownum: String): Map<String, String> {
        val map = hashMapOf<String, String>()
        map["flowNum"] = flownum
        map["token"] = UserManager.getUser().token
        return map
    }

    //列举灾险情文件
    fun disasterFileListParam(pkidd: String): Map<String, String> {
        val map = hashMapOf<String, String>()
        map["types"] = "1,2,3,4,5"
        map["pkidd"] = pkidd
        map["token"] = UserManager.getUser().token
        return map
    }

    //灾险情审核
    fun auditReportParam(auditKind: Int, auditid: String, auditopinion: String, auditChoices: String): RequestBody {
        val map = hashMapOf<String, String>()
        map["auditKind"] = auditKind.toString()
        map["auditid"] = auditid
        map["auditopinion"] = auditopinion
        map["auditChoices"] = auditChoices.toString()
//        map["token"] = UserManager.getUser().token
        return toJson(map)
    }

    //灾险情审阅
    fun auditLeaderParam(auditid: String, reviewopinion: String): Map<String, String> {
        val map = hashMapOf<String, String>()
        map["auditid"] = auditid
        map["reviewopinion"] = reviewopinion
//        map["token"] = UserManager.getUser().token
        return map
    }

    //上报列表
    fun reportListParam(pageNo: Int, pageSize: Int = 10): Map<String, String> {
        val map = hashMapOf<String, String>()
        map["pageNo"] = pageNo.toString()
        map["pageSize"] = pageSize.toString()
//        map["excludeAudit"] = "1,-2"
        map["token"] = UserManager.getUser().token
        return map
    }

    //转移文件
    fun thansferFileParam(flowNum: String, pkidd: String): Map<String, String> {
        val map = hashMapOf<String, String>()
        map["flowNum"] = flowNum
        map["pkidd"] = pkidd
        map["token"] = UserManager.getUser().token
        return map
    }

    //删除文件
    fun deleteFilesParam(flowNum: String, fileNames: Array<String?>): Map<String, String> {
        val map = hashMapOf<String, String>()
        map["flowNum"] = flowNum
        var fileNameString = ""
        fileNames.forEach {
            fileNameString += "$it,"
        }
        map["fileNames"] = fileNameString
        map["token"] = UserManager.getUser().token
        return map
    }

    //新增日报
    fun dailySubmitParam(workcontent: String, longitude: String, latitude: String, prelogid: String): RequestBody {
        val map = hashMapOf<String, String>()
        map["workcontent"] = workcontent
        map["longitude"] = longitude
        map["latitude"] = latitude
        map["prelogid"] = prelogid
//        map["token"] = UserManager.getUser().token
        return toJson(map)
    }

    //日报列表
    fun dailyListParam(pageNo: Int, pageSize: Int = 10, personName: String = "", startTime: String = "", endTime: String = "", areacode: String = "", status: String = ""): Map<String, String> {
        val map = hashMapOf<String, String>()
        map["pageNo"] = pageNo.toString()
        map["pageSize"] = pageSize.toString()
        map["personName"] = personName
        map["startTime"] = startTime
        map["endTime"] = endTime
        map["areacode"] = areacode
        map["status"] = status
//        map["token"] = UserManager.getUser().token
        return map
    }

    //搜索列表
    fun searchListParam(pageNo: Int, pageSize: Int = 10, word: String): Map<String, String> {
        val map = hashMapOf<String, String>()
        map["pageNo"] = pageNo.toString()
        map["pageSize"] = pageSize.toString()
        map["word"] = word
//        map["token"] = UserManager.getUser().token
        return map
    }

    //灾险情点集
    fun disasterListParam(pageNo: Int = 1, pageSize: Int = 99999, type: String, hazardtype: String = "", scaleLevel: String = ""): Map<String, String> {
        val map = hashMapOf<String, String>()
        map["pageNo"] = pageNo.toString()
        map["pageSize"] = pageSize.toString()
        map["type"] = type
//        if (startTime.isNotEmpty()) {
//            map["startTime"] = "$startTime 00:00:00"
//        } else {
//            map["startTime"] = "2019-1-1 00:00:00"
//        }
//        if (endTime.isNotEmpty()) {
//            map["endTime"] = "$endTime 00:00:00"
//        }
        if (hazardtype.isNotEmpty()) {
            map["hazardtype"] = hazardtype
        }
        if (scaleLevel.isNotEmpty()) {
            map["scalelevel"] = scaleLevel
        }
//        map["token"] = UserManager.getUser().token
        return map
    }

    //隐患点点集
    fun hiddenListParam(disasterType: String = "", scaleLevel: String = ""): RequestBody {
        val map = hashMapOf<String, Any>()
        map["listWithDeviation"] = arrayListOf<QueryRegionBean>()
        val outDevList = arrayListOf<QueryRegionBean>()
        if (disasterType.isNotEmpty()) {
            outDevList.add(QueryRegionBean("type", disasterType))
        }
        if (scaleLevel.isNotEmpty()) {
            outDevList.add(QueryRegionBean("scalelevel", scaleLevel, "2"))
        }
        map["listWithOutdev"] = outDevList
        return toJsonAny(map)
    }

    data class QueryRegionBean(var key: String, var value: String, var sign: String = "6")

    //灾险情详情
    fun disasterDetailParam(pkidd: String): Map<String, String> {
        val map = hashMapOf<String, String>()
        map["pkidd"] = pkidd
//        map["token"] = UserManager.getUser().token
        return map
    }

    //隐患点编号列表
    fun disasterPkiaaParam(pageNo: Int, searchText: String): Map<String, String> {
        val map = hashMapOf<String, String>()
        map["pageNo"] = pageNo.toString()
        map["pageSize"] = 10.toString()
        map["keyword"] = searchText
//        map["token"] = UserManager.getUser().token
        return map
    }

    //ersi 区域查询
    fun esriQueryArea(areacode: String): String {
        var featureNum = "0"
        var where = ""
        if (areacode.length == 6) {
            featureNum = "0"
            where = "areacode='$areacode'"
        } else if (areacode.length == 9) {
            featureNum = "1"
            where = "areacode='$areacode'"
        } else if (areacode.length == 12) {
            featureNum = "2"
            where = "areacode='$areacode'"
        }
        return "http://183.230.8.9:4000/arcgis/rest/services/xzqh/MapServer/$featureNum/query?" +
                "f=json&" +
                "ourSR=102100&" +
                "spatialRel=esriSpatialRelIntersects&" +
                "where=$where"
    }

    //查询雨量列表
    fun rainListParam(type: String): Map<String, String> {
        val map = hashMapOf<String, String>()
//        map["time"] = "2019-4-19 18:00:00"
        map["type"] = type
//        map["token"] = UserManager.getUser().token
        return map
    }

    //统计灾险情
    fun countDisasterParam(happentime: String = "", county: String = ""): Map<String, String> {
        val map = hashMapOf<String, String>()
        map["happentime"] = happentime
        map["county"] = county
//        map["token"] = UserManager.getUser().token
        return map
    }

    //上传周报
    fun uploadWeekWork(townsname: String, reporttime: String, areamanager: String, content: String): RequestBody {
        val map = hashMapOf<String, String>()
        map["townsname"] = townsname
        val time = ZXTimeUtil.getTime(ZXTimeUtil.string2Millis(reporttime), SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+SSSS")).replace("+0000", "+0800")
        map["reporttime"] = time
        map["areamanager"] = areamanager
        map["content"] = content
//        map["token"] = UserManager.getUser().token
        return toJson(map)
    }


    //获取日报文件
    fun getDailyFile(logid: String): Map<String, String> {
        val map = hashMapOf<String, String>()
        map["logid"] = logid
        map["token"] = UserManager.getUser().token
        return map
    }

    //查询当前隐患点对应的宏观观测记录
    fun findFixMonitroList(name: String, startTime: String, endTime: String): Map<String, String> {
        val map = hashMapOf<String, String>()
        map["name"] = name
        map["startTime"] = startTime
        map["endTime"] = endTime
        return map

    }

    //查询当前隐患点对应的宏观观测记录
    fun findMacroMonitroList(startTime: String, endTime: String): Map<String, String> {
        val map = hashMapOf<String, String>()
        map["startTime"] = startTime
        map["endTime"] = endTime
        return map
    }

    //工作日志列表
    fun workLogListParams(pageNo: Int, pageSize: Int = 10, username: String, startTime: String = "", endTime: String = ""): Map<String, String> {
        val map = hashMapOf<String, String>()
        map["pageNo"] = pageNo.toString()
        map["pageSize"] = pageSize.toString()
        map["username"] = username
        map["startTime"] = startTime
        map["endTime"] = endTime
//        map["token"] = UserManager.getUser().token
        return map
    }

    //新增工作日志
    fun addWorkLogParams(recorder: String, worktype: String, onduty: String, content: String, note: String): RequestBody {
        val map = hashMapOf<String, String>()
        map["recorder"] = recorder
        map["worktype"] = worktype
        map["onduty"] = onduty
        map["content"] = content
        map["note"] = note
//        map["token"] = UserManager.getUser().token
        return toJson(map)
    }

    //获取周报情况列表
    fun getWeekWorkList(pageNo: Int, pageSize: Int = 10, username: String, startTime: String = "", endTime: String = ""): Map<String, String> {
        val map = hashMapOf<String, String>()
        map["pageNo"] = pageNo.toString()
        map["pageSize"] = pageSize.toString()
        map["username"] = username
        map["startTime"] = startTime
        map["endTime"] = endTime
//        map["token"] = UserManager.getUser().token
        return map
    }

    //获取单个周报文件列表
    fun getWeekWorkFileList(logid: String, reportType: Int): Map<String, String> {
        val map = hashMapOf<String, String>()
        map["logid"] = logid
        map["reportType"] = reportType.toString()
        map["token"] = UserManager.getUser().token
        return map
    }

    //新增定量观测记录
    fun addMonitorpatroldata(fillBean: MonitorPatrolFillBean): RequestBody {
        val map = hashMapOf<String, String>()

        map["mpid"] = if (fillBean.mpid == null) "" else fillBean.mpid!!
        map["note"] = if (fillBean.note == null) "" else fillBean.note!!
        map["pkiaaname"] = if (fillBean.pkiaaname == null) "" else fillBean.pkiaaname!!
        map["name"] = if (fillBean.name == null) "" else fillBean.name!!
        map["actualdata"] = if (fillBean.actualdata == null) "" else fillBean.actualdata!!
        map["monitortime"] = if (fillBean.monitortime == null) "" else fillBean.monitortime!!
        map["pkiaa"] = if (fillBean.pkiaa == null) "" else fillBean.pkiaa!!
        map["latitude"] = if (fillBean.latitude == null) "" else fillBean.latitude.toString()
        map["longitude"] = if (fillBean.longitude == null) "" else fillBean.longitude.toString()
        map["alarmlevel"] = if (fillBean.alarmlevel == null) "" else fillBean.alarmlevel!!
        map["effectivity"] = if (fillBean.effectivity == null) "" else fillBean.effectivity!!
        map["legal"] = if (fillBean.legal == null) "" else fillBean.legal.toString()

        return toJson(map)
    }

    //新增宏观观测记录
    fun addMacroMonitrodata(fillBean: MacroMonitroFillBean): RequestBody {
        val map = hashMapOf<String, Any>()

        map["macropatroldata"] = fillBean.macropatroldata
        map["macropatrolphenomenas"] = fillBean.macropatrolphenomenas


        val json = Gson().toJson(map)
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
    }

    //获取观测文件列表
    fun getMonitorFileList(logid: String): Map<String, String> {
        val map = hashMapOf<String, String>()
        map["logid"] = logid
        map["token"] = UserManager.getUser().token
        return map
    }

    //提交巡查报告
    fun submitPatrol(pkiaa: String = "", phase: String, longitude: Double, latitude: Double, content: String = ""): RequestBody {
        val map = hashMapOf<String, String>()
        map["pkiaa"] = pkiaa
        map["phase"] = phase
        map["longitude"] = longitude.toString()
        map["latitude"] = latitude.toString()
        map["content"] = content
//        map["token"] = UserManager.getUser().token
        return toJson(map)
    }

    //获取巡查上报列表
    fun getPatrolList(pkiaa: String, startTime: String = "", endTime: String = ""): Map<String, String> {
        val map = hashMapOf<String, String>()
        map["pkiaa"] = pkiaa
        map["startTime"] = startTime
        map["endTime"] = endTime
//        map["token"] = UserManager.getUser().token
        return map
    }

    //获取单个巡查文件列表
    fun getPatrolFileList(recordid: String): Map<String, String> {
        val map = hashMapOf<String, String>()
        map["recordid"] = recordid
        map["token"] = UserManager.getUser().token
        return map
    }

    //操作日志
    fun operationLog(model: String, modelName: String, type: String, typeName: String, action: String): Map<String, String> {
        val map = hashMapOf<String, String>()
        map["model"] = model
        map["modelName"] = modelName
        map["type"] = type
        map["typeName"] = typeName
        map["action"] = action
        map["imei"] = action
        return map
    }


    //查询雨量列表
    fun rainList(pageNo: Int = 1, type: Int = 3, areacode: String = ""): Map<String, String> {
        val map = hashMapOf<String, String>()
        map["type"] = type.toString()
        map["areacode"] = areacode
        map["pageNo"] = pageNo.toString()
        map["pageSize"] = "99999"
//        map["token"] = UserManager.getUser().token
        return map
    }

    //判断点坐标是否在指定行政区内
    fun getExistInRegion(x: String, y: String, xzqdm: String): Map<String, String> {
        val map = hashMapOf<String, String>()
        map["x"] = x
        map["y"] = y
        map["xzqdm"] = xzqdm
//        map["token"] = UserManager.getUser().token
        return map
    }

    //返回密码强度的百分数
    fun checkPassword(newPwd: String): Map<String, String> {
        val map = hashMapOf<String, String>()
        map["newPwd"] = newPwd
//        map["token"] = UserManager.getUser().token
        return map
    }

    //分页查询平安上报审核
    fun pageAuditReport(status: String = "", startTime: String = "", endTime: String = "", pageNo: Int, pageSize: Int = 10): Map<String, String> {
        val map = hashMapOf<String, String>()
        map["status"] = status
        map["startTime"] = startTime
        map["endTime"] = endTime
        map["pageSize"] = pageSize.toString()
        map["pageNo"] = pageNo.toString()
//        map["token"] = UserManager.getUser().token
        return map
    }

    //审核平安上报记录
    fun auditReport(auditid: String = "", auditopinion: String = "", auditChoices: String = ""): RequestBody {
        val map = hashMapOf<String, String>()
        map["auditid"] = auditid
        map["auditopinion"] = auditopinion
        map["auditChoices"] = auditChoices
//        map["token"] = UserManager.getUser().token
        return toJson(map)
    }

    //分页查询列表
    fun getFindWeatherdecisionData(pageNo: Int = 1, pageSize: Int = 3, servicetitle: String = "", servicetype: String = "", startTime: String = "", endTime: String = ""): Map<String, String> {
        val map = hashMapOf<String, String>()
        map["pageNo"] = pageNo.toString()
        map["pageSize"] = pageSize.toString()
        map["servicetitle"] = servicetitle
        map["servicetype"] = servicetype
        map["startTime"] = startTime
        map["endTime"] = endTime
//        map["token"] = UserManager.getUser().token
        return map
    }

    //分页查询列表
    fun getInfoReleaseFile(): Map<String, String> {
        val map = hashMapOf<String, String>()
//        map["token"] = UserManager.getUser().token
        return map
    }

    //头像上传
    fun hearUpload(personid: String = "", file: String): Map<String, String> {
        val map = hashMapOf<String, String>()
        map["personid"] = personid
        map["file"] = file
        return map
    }

    //注册
    fun regist(map: HashMap<String, String>): RequestBody {
        return toJson(map)
    }

    //根据上级行政区划查询行政区划
    fun findByParent(code: String = "", name: String = ""): Map<String, String> {
        val map = hashMapOf<String, String>()
        map["code"] = code
        map["name"] = name
        return map
    }

    //检查账号是否存在
    fun checkAccount(account: String): Map<String, String> {
        val map = hashMapOf<String, String>()
        map["account"] = account
        return map
    }

    //获取观测文件列表
    fun currentAuthMenu(superid: String = "", supercode: String = ""): Map<String, String> {
        val map = hashMapOf<String, String>()
        map["superid"] = superid
        map["supercode"] = supercode
        map["token"] = UserManager.getUser().token
        return map
    }

    //查询预警更新时间
    fun getUpdateTime(type: String): Map<String, String> {
        val map = hashMapOf<String, String>()
        map["type"] = type
//        map["token"] = UserManager.getUser().token
        return map
    }

    //app推送历史
    fun getPushList(pageNo: Int = 1, pageSize: Int = 10, startTime: String = "", endTime: String = "", noticeType: String = ""): Map<String, String> {
        val map = hashMapOf<String, String>()
        map["pageNo"] = pageNo.toString()
        map["pageSize"] = pageSize.toString()
        map["noticeType"] = noticeType
        map["startTime"] = startTime
        map["endTime"] = endTime
//        map["token"] = UserManager.getUser().token
        return map
    }

    //驻守地工作日志上报率
    fun getReportstatis(pageNo: Int = 1, pageSize: Int = 10, startTime: String = "", recorder: String = "", areacode: String = "", endTime: String = ""): Map<String, String> {
        val map = hashMapOf<String, String>()
        map["pageNo"] = pageNo.toString()
        map["pageSize"] = pageSize.toString()
        map["recorder"] = recorder
        map["areacode"] = areacode
        map["startTime"] = startTime
        map["endTime"] = endTime
//        map["token"] = UserManager.getUser().token
        return map
    }

    //查询我的上报记录
    fun findMyReport(pageNo: Int = 1, pageSize: Int = 15, startTime: String = "", endTime: String = "", pkidd: String = "", pkiaa: String = ""): Map<String, String> {
        val map = hashMapOf<String, String>()
        map["pageNo"] = pageNo.toString()
        map["pageSize"] = pageSize.toString()
        map["pkidd"] = pkidd//灾险情编号
        map["pkiaa"] = pkiaa//地灾体编号
        map["startTime"] = startTime
        map["endTime"] = endTime
//        map["token"] = UserManager.getUser().token
        return map
    }

    //注册验证码
    fun sendRegister(name: String, phone: String, areacode: String, areaname: String): Map<String, String> {
        val map = hashMapOf<String, String>()
        map["name"] = name
        map["phone"] = phone
        map["areacode"] = areacode
        map["areaname"] = areaname
        return map
    }

    //改密码验证码
    fun sendUpdatePwd(phone: String): Map<String, String> {
        val map = hashMapOf<String, String>()
        map["phone"] = phone
        return map
    }

    //改密码验证码
    fun updatePasswordByCode(checkCode: String, phone: String, newPwd: String): Map<String, String> {
        val map = hashMapOf<String, String>()
        map["checkCode"] = checkCode
        map["phone"] = phone
        map["newPwd"] = getPwd(ZXMD5Util.getMD5(newPwd))
        return map
    }

    // 分析
    fun getFenXi(startTime: String = "", endTime: String = ""): Map<String, String> {
        val map = hashMapOf<String, String>()
        map["startTime"] = startTime
        map["endTime"] = endTime
        return map
    }
}