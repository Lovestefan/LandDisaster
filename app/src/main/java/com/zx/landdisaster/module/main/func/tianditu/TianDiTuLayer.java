package com.zx.landdisaster.module.main.func.tianditu;

import android.util.Log;

import com.esri.android.map.TiledServiceLayer;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.SpatialReference;

import java.util.Map;
import java.util.concurrent.RejectedExecutionException;

public class TianDiTuLayer extends TiledServiceLayer {

    private TianDiTuLayerInfo layerInfo;

    public TianDiTuLayer(int layerType) {
        super(true);
        this.layerInfo = LayerInfoFactory.getLayerInfo(layerType);
        this.init();
    }

    private void init() {
        try {
            getServiceExecutor().submit(new Runnable() {
                public void run() {
                    TianDiTuLayer.this.initLayer();
                }
            });
        } catch (RejectedExecutionException rejectedexecutionexception) {
            Log.e("ArcGIS", "initialization of the layer failed.",
                    rejectedexecutionexception);
        }
    }

    protected byte[] getTile(int level, int col, int row) throws Exception {
        String url = "";
        if (layerInfo.getType() == TianDiTuLayerTypes.JXDX_VEC || layerInfo.getType() == TianDiTuLayerTypes.JXDX_IMG || layerInfo.getType() == TianDiTuLayerTypes.JXDX_CVA
                || layerInfo.getType() == TianDiTuLayerTypes.JXRC_VEC || layerInfo.getType() == TianDiTuLayerTypes.JXRC_IMG || layerInfo.getType() == TianDiTuLayerTypes.JXRC_CVA
                || layerInfo.getType() == TianDiTuLayerTypes.JXRC_GRID || layerInfo.getType() == TianDiTuLayerTypes.JXDX_GRID) {
            if (level > layerInfo.getMaxZoomLevel()
                    || level < layerInfo.getMinZoomLevel())
                return new byte[0];
            url = layerInfo.getUrl() + "?"
                    + "layer=" + layerInfo.getLayerName()
                    + "&style="
                    + "&tilematrixset=" + layerInfo.getTileMatrixSet()
                    + "&Service=WMTS"
                    + "&Request=GetTile"
                    + "&Version=1.0.0"
                    + "&Format=image/png"
                    + "&TileMatrix=" + layerInfo.getTileMatrixSet() + ":" + (level)
                    + "&TileCol=" + col
                    + "&TileRow=" + row
                    + "&TileatrixSet="
                    + "&tdsourcetag=s_pcqq_aiomsg";
        } else if (layerInfo.getType() == TianDiTuLayerTypes.TDT_VEC_NEW || layerInfo.getType() == TianDiTuLayerTypes.TDT_IMG_NEW || layerInfo.getType() == TianDiTuLayerTypes.TDT_IMG_NEW ||
                layerInfo.getType() == TianDiTuLayerTypes.TDT_CVA_NEW || layerInfo.getType() == TianDiTuLayerTypes.TDT_CIA_NEW || layerInfo.getType() == TianDiTuLayerTypes.TDT_CTA_NEW) {
            url = layerInfo.getUrl() +
                    "&x=" + col +
                    "&y=" + row +
                    "&l=" + (level + 1);
        } else {
            if (level > layerInfo.getMaxZoomLevel()
                    || level < layerInfo.getMinZoomLevel())
                return new byte[0];
            url = layerInfo.getUrl() +
                    "&service=wmts" +
                    "&request=gettile" +
                    "&version=1.0.0" +
                    "&layer=" + layerInfo.getLayerName() +
                    "&format=tiles" +
                    "&style=default" +
                    "&format=tiles" +
                    "&tilematrixset=" + layerInfo.getTileMatrixSet() +
                    "&tilecol=" + col +
                    "&tilerow=" + row +
                    "&tilematrix=" + (level + 1);
        }
        Map<String, String> map = null;
        return com.esri.core.internal.io.handler.a.a(url, map);
    }

    protected void initLayer() {
        if (getID() == 0L) {
            nativeHandle = create();
            changeStatus(com.esri.android.map.event.OnStatusChangedListener.STATUS
                    .fromInt(-1000));
        } else {
            this.setDefaultSpatialReference(SpatialReference.create(layerInfo
                    .getSrid()));
            this.setFullExtent(new Envelope(layerInfo.getxMin(), layerInfo
                    .getyMin(), layerInfo.getxMax(), layerInfo.getyMax()));
            this.setTileInfo(new TileInfo(layerInfo.getOrigin(), layerInfo
                    .getScales(), layerInfo.getResolutions(), layerInfo
                    .getScales().length, layerInfo.getDpi(), layerInfo
                    .getTileWidth(), layerInfo.getTileHeight()));
            super.initLayer();
        }
    }


}
