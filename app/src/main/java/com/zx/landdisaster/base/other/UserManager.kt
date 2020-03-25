package com.zx.landdisaster.base.other

import com.zx.landdisaster.base.bean.UserBean
import com.zx.zxutils.util.ZXSharedPrefUtil

/**
 * Created by Xiangb on 2019/3/5.
 * 功能：用户管理器
 */
object UserManager {

    private var user: UserBean? = null

    var quanxian = listOf<String>()
        set(value) {
            val sharedPref = ZXSharedPrefUtil()
            sharedPref.putList("m_quanxian", value)
            field = value
        }
        get() {
            val sharedPref = ZXSharedPrefUtil()
            if (field.isEmpty() && sharedPref.contains("m_quanxian")) {
                return sharedPref.getList("m_quanxian")
            } else {
                return field
            }
        }

    var userName: String = ""
        set(value) {
            val sharedPref = ZXSharedPrefUtil()
            val sharedPrefTemp = ZXSharedPrefUtil("landdisater_temp")
            sharedPref.putString("landdisaster_username", value)
            sharedPrefTemp.putString("username", value)
            field = value
        }
        get() {
            val sharedPref = ZXSharedPrefUtil()
            val sharedPrefTemp = ZXSharedPrefUtil("landdisater_temp")
            if (field.isEmpty() && (sharedPref.contains("landdisaster_username") || sharedPrefTemp.contains("username"))) {
                return if (sharedPrefTemp.contains("username")) {
                    sharedPrefTemp.getString("username")
                } else {
                    sharedPref.getString("landdisaster_username")
                }
            } else {
                return field
            }
        }

    var passWord: String = ""
        set(value) {
            val sharedPref = ZXSharedPrefUtil()
            val sharedPrefTemp = ZXSharedPrefUtil("landdisater_temp")
            sharedPref.putString("landdisaster_userpwd", value)
            sharedPrefTemp.putString("password", value)
            field = value
        }
        get() {
            val sharedPref = ZXSharedPrefUtil()
            val sharedPrefTemp = ZXSharedPrefUtil("landdisater_temp")
            if (field.isEmpty() && (sharedPref.contains("landdisaster_userpwd") || sharedPrefTemp.contains("password"))) {
                return if (sharedPrefTemp.contains("password")) {
                    sharedPrefTemp.getString("password")
                } else {
                    sharedPref.getString("landdisaster_userpwd")
                }
            } else {
                return field
            }
        }


    fun getUser(): UserBean {
        if (user == null) {
            val sharedPref = ZXSharedPrefUtil()
            user = sharedPref.getObject("userBean")
            if (user == null) {
                user = UserBean()
            }
        }
        return user!!
    }

    fun setUser(userBean: UserBean) {
        user = userBean
        saveUser()
    }

    fun saveUser() {
        val sharedPref = ZXSharedPrefUtil()
        sharedPref.putObject("userBean", user)
    }

    fun loginOut() {
        passWord = ""
        saveUser()
    }

    //可以上报
    fun canReport(): Boolean {
        val role = user!!.personRole
        return (role.areaManager || role.ringStand || role.ringStandAudit || role.groupDefense || role.garrison || role.dutyPerson)
    }

    //可以审核
    fun canAudit(): Boolean {
        val role = user!!.personRole
        return (role.ringStandAudit || role.dutyPerson || role.elPerson)
    }

    //可以审阅
    fun canPreview(): Boolean {
        val role = user!!.personRole
        return role.leader || role.dispatch
    }

    fun getUserRoleName(role: String = ""): String {
        val roleString: String
        if (role.isEmpty()) {
            roleString = user!!.currentUser!!.identities
        } else {
            roleString = role
        }
        val roleArray = roleString.split(",")

        if (roleArray.isNotEmpty()) {
            var roleInfo = ""
            roleArray.forEach {
                roleInfo += when (it) {
                    "1" -> "片区负责人 "
                    "2" -> "地环站人员 "
                    "3" -> "群测群防员 "
                    "4" -> "驻守地质人员 "
                    "5" -> "专家 "
                    "6" -> "工作人员 "
                    "7" -> "系统管理员 "
                    else -> ""
                }
            }
            return roleInfo
        } else {
            return ""
        }
    }

}