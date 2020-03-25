package com.zx.landdisaster.module.disaster.bean

import java.io.Serializable

/**
 * Updated by dell on 2019-03-25
 */
data class PersonRoleBean(var areaManager: Boolean = false,//是否是片区负责人
                          var groupDefense: Boolean = false,//群测群防人员
                          var ringStand: Boolean = false,//地环站人员
                          var ringStandAudit: Boolean = false,//地环站人员有审核权限的
                          var garrison: Boolean = false,//驻守地质队员
                          var expert: Boolean = false,//是否是专家
                          var dutyPerson: Boolean = false,//是否是值班人员
                          var elPerson: Boolean = false,//是否是应急值守科
                          var leader: Boolean = false,//是否是领导
                          var sysadmin: Boolean = false,//是否是系统管理员
                          var areaAdmin: Boolean = false,//是否是曲线管理员
                          var sysGuest: Boolean = false,//是否是
                          var dispatch: Boolean = false//是否是会商调度科
) : Serializable

