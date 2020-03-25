package com.zx.landdisaster.module.main.bean

/**
 * Updated by dell on 2019-04-04
 */
data class MapExtentBean(var count: Int, var extent: Extent) {
    data class Extent(var xmin: Double, var ymin: Double, var xmax: Double, var ymax: Double, var spatialReference: SpatialReference) {
        data class SpatialReference(var wkid: Int, var latestWkid: Int)
    }
}