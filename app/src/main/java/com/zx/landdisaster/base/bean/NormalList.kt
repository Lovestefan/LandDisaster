package com.zx.landdisaster.base.bean

/**
 * Created by Xiangb on 2019/3/14.
 * 功能：列表基类
 */
data class NormalList<T>(var totalRecords: Int=0,
                         var pageNo: Int=1,
                         var pageSize: Int=0,
                         var perPage: Int=0,
                         var nextPage: Int=0,
                         var result: List<T>?= arrayListOf())