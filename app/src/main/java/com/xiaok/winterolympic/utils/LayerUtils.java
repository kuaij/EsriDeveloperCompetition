package com.xiaok.winterolympic.utils;

import com.esri.arcgisruntime.arcgisservices.TileInfo;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.xiaok.winterolympic.utils.tdt.LayerInfoFactory;
import com.xiaok.winterolympic.utils.tdt.TianDiTuLayer;
import com.xiaok.winterolympic.utils.tdt.TianDiTuLayerInfo;
import com.xiaok.winterolympic.utils.tdt.TianDiTuLayerTypes;

public class LayerUtils {
    /*
    * @function: 主要涉及地图底图加载、图层的可见性设置
    * @author: zzu_kuaijian
    * @date: 2019/08/02
     */

    //地图中心点
    public static final double CENTRAL_POSITION_LATITUDE = 40.732;
    public static final double CENTRAL_POSITION_LONGITUDE = 115.81;
    public static final double INIT_SCALE = 5900000f;

    public static void addTDT(MapView mapView){
        TianDiTuLayerInfo layerInfo=
                LayerInfoFactory.getLayerInfo(TianDiTuLayerTypes.TIANDITU_VECTOR_2000);
        TileInfo info=layerInfo.getTileInfo();
        Envelope fullExtent=layerInfo.getFullExtent();
        TianDiTuLayer layer=
                new TianDiTuLayer(info,fullExtent);
        layer.setLayerInfo(layerInfo);

        TianDiTuLayerInfo layerInfo_cva=
                LayerInfoFactory.getLayerInfo(TianDiTuLayerTypes.TIANDITU_VECTOR_ANNOTATION_CHINESE_2000);
        TileInfo info_cva =layerInfo_cva.getTileInfo();
        Envelope fullExtent_cva =layerInfo_cva.getFullExtent();
        TianDiTuLayer layer_cva =
                new TianDiTuLayer(info_cva,fullExtent_cva);
        layer_cva.setLayerInfo(layerInfo_cva);

        ArcGISMap map =new ArcGISMap();
        map.getBasemap().getBaseLayers().add(layer);
        map.getBasemap().getBaseLayers().add(layer_cva);
        mapView.setMap(map);
    }


    public static void addTDT_IMA(MapView mapView){
        TianDiTuLayerInfo layerInfo=
                LayerInfoFactory.getLayerInfo(TianDiTuLayerTypes.TIANDITU_IMAGE_2000);
        TileInfo info=layerInfo.getTileInfo();
        Envelope fullExtent=layerInfo.getFullExtent();
        TianDiTuLayer layer=
                new TianDiTuLayer(info,fullExtent);
        layer.setLayerInfo(layerInfo);

        TianDiTuLayerInfo layerInfo_anno=
                LayerInfoFactory.getLayerInfo(TianDiTuLayerTypes.TIANDITU_IMAGE_ANNOTATION_CHINESE_2000);
        TileInfo info_anno =layerInfo_anno.getTileInfo();
        Envelope fullExtent_anno =layerInfo_anno.getFullExtent();
        TianDiTuLayer layer_anno =
                new TianDiTuLayer(info_anno,fullExtent_anno);
        layer_anno.setLayerInfo(layerInfo_anno);

        ArcGISMap map =new ArcGISMap();
        map.getBasemap().getBaseLayers().add(layer);
        map.getBasemap().getBaseLayers().add(layer_anno);
        mapView.setMap(map);
    }


    //设置图层可见性
    public static void changeLayerVisibity(FeatureLayer featureLayer){
        if (!featureLayer.isVisible()){
            featureLayer.setVisible(true);
        }else {
            featureLayer.setVisible(false);
        }
    }
}
