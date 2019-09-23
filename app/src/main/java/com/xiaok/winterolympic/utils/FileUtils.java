package com.xiaok.winterolympic.utils;

public class FileUtils {
    /*
    * @function: 二维数据库和三维场景文件路径
    * @author: zzu_kuaijian
    * @date: 2019/07/25
     */

    public static String FILE_ROOT_PATH = android.os.Environment.getExternalStorageDirectory() + "/Android/data/com.xiaok.winterolympic";

    public static String ZZUPIPE_FILE_PATH = FILE_ROOT_PATH + "/files";
    public static String ZZU_GDB_CHECK= ZZUPIPE_FILE_PATH + "/gdb";
    public static String ZZU_SCENE_CHECK = ZZUPIPE_FILE_PATH + "/scene";


    public static final String MAIN_PAGE_DATEBASE = "/sdcard/Android/data/com.xiaok.winterolympic/files/gdb/DAMainPage7.geodatabase";
    public static final String MAIN_PAGE_COMPLACE = "/sdcard/Android/data/com.xiaok.winterolympic/files/gdb/ComplaceNew.geodatabase";
    //主界面场馆周围项目图层
    public static final String MAIN_PAGE_LARGE_COMPLACE = "/sdcard/Android/data/com.xiaok.winterolympic/files/gdb/DAMainComplace.geodatabase";

    //水立方室内地图
    public static final String INDOOR_WATER_CLUB_01 = "/sdcard/Android/data/com.xiaok.winterolympic/files/gdb/DAWaterClub1.geodatabase";
    public static final String INDOOR_WATER_CLUB_02 = "/sdcard/Android/data/com.xiaok.winterolympic/files/gdb/DAWaterClub2.geodatabase";

    //水立方一层室内导航地图
    public static final String INDOOR_NAVI_NETWORK = "/sdcard/Android/data/com.xiaok.winterolympic/files/gdb/indoor_network.geodatabase";

    //国家体育馆一二层室内地图
    public static final String INDOOR_NATIONAL_STATION_01 = "/sdcard/Android/data/com.xiaok.winterolympic/files/gdb/DANationalStation1.geodatabase";
    public static final String INDOOR_NATIONAL_STATION_02 = "/sdcard/Android/data/com.xiaok.winterolympic/files/gdb/DANationalStation2.geodatabase";

    //逃生路线、室内灭火器、医疗点图层
    public static final String ESCAPE_ROUTE_DATEBASE = "/sdcard/Android/data/com.xiaok.winterolympic/files/gdb/WCEscapeLine.geodatabase";

    //志愿者界面志愿者位置点图层
    public static final String VOLUNTEER_POSITION = "/sdcard/Android/data/com.xiaok.winterolympic/files/gdb/volunteer.geodatabase";
    public static final String VOLUNTEER_POSITION2 = "/sdcard/Android/data/com.xiaok.winterolympic/files/gdb/volunteer2.geodatabase";

    /*
    *三维场景路径
     */
    public static final String SCENE_PATH = "/sdcard/Android/data/com.xiaok.winterolympic/files/scene/gym_0911.slpk";
    public static final String SCENE_NIAOCHAO = "/sdcard/Android/data/com.xiaok.winterolympic/files/scene/3dgym_gai.mspk";


    public static final String VIDEO_01 = "/sdcard/Android/data/com.xiaok.winterolympic/files/scene/video_01.mp4";
    public static final String VIDEO_02 = "/sdcard/Android/data/com.xiaok.winterolympic/files/scene/video_02.mp4";
    public static final String VIDEO_03 = "/sdcard/Android/data/com.xiaok.winterolympic/files/scene/video_03.mp4";

}
