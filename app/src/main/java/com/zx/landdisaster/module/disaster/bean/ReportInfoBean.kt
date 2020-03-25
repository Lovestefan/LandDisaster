package com.zx.landdisaster.module.disaster.bean

import java.io.Serializable

/**
 * Created by Xiangb on 2019/3/16.
 * 功能：
 */
data class ReportInfoBean(var oneBean: AddOneBean, var twoBean: AddTwoBean, var threeBean: AddThreeBean, var detailBean: DetailBean? = null) : Serializable {

    data class AddOneBean(var hazardName: String = "",
                          var happenTime: String = "",
                          var hazardType: Int = 1,
                          var isArea: Int = 0,
                          var disasterType: String = "",
                          var scaleLevel: String = "",
                          var disvolume: String = "",
                          var areaCode: String = "",
                          var areaName: String = "",
                          var longitude: Double = 0.0,
                          var latitude: Double = 0.0,
                          var address: String = "") : Serializable {

    }

    data class AddTwoBean(var pkiaa: String = "",
                          var threatObj: String = "",
                          var threatNum: String = "",
                          var leaveNum: String = "",
                          var injureNum: String = "",
                          var dieNum: String = "",
                          var missingNum: String = "",
                          var economicLoss: String = "",
                          var cause: String = "",
                          var causeNote: String = "",
                          var dealidea: String = "") : Serializable

    data class AddThreeBean(var reportPhone: String = "",
                            var reportDept: String = "",
                            var reportdeptDesc: String = "",
                            var reportNote: String = "",
                            var files: ArrayList<AddFileBean> = arrayListOf()) : Serializable

    data class DetailBean(var flowNum: String,
                          var reportdataid: String,
                          var editAble: Boolean,
                          var editAllAble: Boolean,
                          var auditKind: Int = 0,
                          var auditid: String = "",
                          var elPerson: Boolean = false,
                          var pkidd: String = "") : Serializable
}