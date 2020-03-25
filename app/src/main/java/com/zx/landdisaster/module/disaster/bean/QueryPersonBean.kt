package com.zx.landdisaster.module.disaster.bean

/**
 * Updated by dell on 2019-04-15
 */
data class QueryPersonBean(var personRingStandDetail: List<PersonBean>,
                           var personAreaManagerDetail: List<PersonBean>,
                           var personGarrisonDetail: List<PersonBean>,
                           var personGroupDefenseDetail: List<PersonBean>) {
    data class PersonBean(var account: String,
                          var address: String,
                          var birthday: String,
                          var createtime: String,
                          var culture: String,
                          var email: String,
                          var fax: String,
                          var gender: String,
                          var headurl: String,
                          var idcardnumber: String,
                          var identities: String?,
                          var lastmodytime: String,
                          var name: String,
                          var nation: String,
                          var orgid: String,
                          var personid: String,
                          var polics: String,
                          var pwd: String,
                          var telephone: String,
                          var imei: String,
                          var realmobile: String,
                          var workunit: String,
                          var location: String,
                          var longitude: String,
                          var latitude: String,
                          var areacode: String,
                          var role: String,
                          var disasterpointnumsname: String,
                          var imsi: String,
                          var ismonitor: Int,
                          var managepkiaas: String,
                          var operation: String,
                          var village: String,
                          var work: String)

}