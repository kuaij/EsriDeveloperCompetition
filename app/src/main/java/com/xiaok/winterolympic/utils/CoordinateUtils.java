package com.xiaok.winterolympic.utils;

import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.mapping.view.MapView;

public class CoordinateUtils {
    /*
    * @function: 实现不同坐标系之间的坐标转换，包括屏幕坐标转换为经纬度
    * @author: zzu_kuaijian
    * @date: 2019/08/02
     */

    public static SpatialReference mSR4326 = SpatialReference.create(4326);
    public static SpatialReference mSR3857 = SpatialReference.create(3857);

    public static Point screnToMapPoint(MapView mapView, float x, float y){
        //获取当前屏幕点击坐标
        android.graphics.Point point = new android.graphics.Point((int) x, (int) y);
        //转化为投影坐标
        Point sp = mapView.screenToLocation(point);
        //转化为经纬度
        Point resultPoint = (Point) GeometryEngine.project(sp, mSR4326);
        return resultPoint;
    }
}
