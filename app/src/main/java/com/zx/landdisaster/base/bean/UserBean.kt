package com.zx.landdisaster.base.bean

import com.zx.landdisaster.module.disaster.bean.PersonRoleBean
import java.io.Serializable

/**
 * Updated by dell on 2019-03-16
 */
data class UserBean(var loginStatus: String = "",
                    var currentUser: CurrentUser? = null,
                    var token: String = "",
                    var gpsupfreq: Int = 0,
                    var password: String = "",
                    var dailySafeScope: String = "",
                    var mapSl: String = "",
                    var mapYx: String = "",
                    var mapYxzj: String = "",
                    var personRole: PersonRoleBean = PersonRoleBean()) : Serializable {

    data class CurrentUser(var account: String = "",
                           var address: String = "",
                           var birthday: String = "",
                           var createtime: Long = 0,
                           var culture: String = "",
                           var email: String = "",
                           var fax: String = "",
                           var gender: String = "",
                           var headurl: String = "",
                           var idcardnumber: String = "",
                           var identities: String = "",
                           var lastmodytime: String = "",
                           var name: String = "",
                           var nation: String = "",
                           var orgid: String = "",
                           var personid: String = "",
                           var orgName: String? = "",
                           var polics: String = "",
                           var pwd: String = "",
                           var areaCode: String? = "",
                           var areaName: String? = "",
                           var telephone: String? = "") : Serializable
}
