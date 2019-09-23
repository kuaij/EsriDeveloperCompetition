package com.xiaok.winterolympic.model;

import android.content.Context;
import android.content.Intent;

import java.util.HashMap;
import java.util.Map;

public class UIAyncManager {

    /***************************************************************************************************/
    /*********************************************回调更新***********************************************/
    /***************************************************************************************************/
    /**
     * InfoTransferModelInterface 不带tag的回调
     * InfoTransferTagModelInterface 带tag的回调
     * */
    public interface InfoTransferModelInterface<T>{
        void changeUIByModel(T model);
    }

//   *************  经过我的使用发现  想法有点错误，于是改了改，原因自己体会～～ **************
//    /**
//     * 上级页面绑定更新
//     * BindChangeModel 上级页面注册回调
//     * @param context 上级页面
//     * */
//    public static void BindChangeModel(Context context, InfoTransferModelInterface changeModel){
//        String key = keyByContext(context);
//        BindAction(key,changeModel);
//    }
//    public static void BindChangeModel(String activityName,InfoTransferModelInterface changeModel){
//        BindAction(activityName,changeModel);
//    }

    /**
     * 上级页面绑定更新
     *
     * @param activityClass 下级页面的activity Class
     * @param changeModel   回调更新注册
     */
    public static void BindChangeModel(Class activityClass, InfoTransferModelInterface changeModel){
        String key = keyByContext(activityClass);
        BindAction(key,changeModel);
    }
    public static void BindChangeModel(String activityName,InfoTransferModelInterface changeModel){
        BindAction(activityName,changeModel);
    }
    public static void BindChangeModel(Intent activityIntent, InfoTransferModelInterface changeModel){
        String key = activityIntent.getComponent().getClassName();
        if (key == null) return;
        BindAction("class " + key,changeModel);
    }

//   *************  经过我的使用发现  想法有点错误，于是改了改，原因自己体会～～ **************
//    /**
//     * 下一级页面更新上级页面的函数
//     *
//     * PostChangeByModel 下级页面发起回调
//     * @param activityClass 上级页面的class
//     * */
//    public static<T> void PostChangeByModel(Class activityClass,T model){
//        String key = keyByContext(activityClass);
//        PostAction(model,key);
//    }
//    public static<T> void PostChangeByModel(String activityName,T model) {
//        PostAction(model,activityName);
//    }

    /**
     * 下一级页面更新上级页面的函数
     *
     * @param <T>     model的范型
     * @param context 当前页面的context
     * @param model   回调更新的model
     */
    public static<T> void PostChangeByModel(Context context, T model){
        String key = keyByContext(context);
        PostAction(model,key);
    }
    public static<T> void PostChangeByModel(String activityName,T model) {
        PostAction(model,activityName);
    }



    /***************************************************************************************************/
    /*********************************************下面都是私有方法 不用管的***********************************************/
    /***************************************************************************************************/




    /**
     * 页面间消息传递
     * 采用匿名累，超微量级别的，用于页面间回调更新
     * 实时回调，startActivityForResult是等待上一个页面finish才会调用
     * */
    // 变量
    private static UIAyncManager share = new UIAyncManager();
    private UIAyncManager(){}//单利模式
    private Map<String,InfoTransferModelInterface> hashMap = new HashMap<>();
    /**
     * 下面都是私有方法了：：：：
     *
     * BindAction 上级页面注册回调
     * PostAction 下级页面发起回调
     * */
    private static void BindAction(String key,InfoTransferModelInterface changeModel){
        if (changeModel == null) return;
        if (key == null) return;
        share.hashMap.put(key,changeModel);
    }
    private static<T> void PostAction(T model,String key){
        if (key == null) return;
        InfoTransferModelInterface infoTransferModelInterface = share.hashMap.get(key);
        if (infoTransferModelInterface == null) return;// 不存在
        try {
            infoTransferModelInterface.changeUIByModel(model);
        }catch (Exception e){// 野指针
            share.hashMap.remove(infoTransferModelInterface);
        }
    }
    /**
     * keyByContext 辅助方法，获取key
     */
    private static String keyByContext(Context context){
        if (context == null) return null;
        return context.getClass().toString();
    }
    private static String keyByContext(Class activityClass){
        if (activityClass == null) return null;
        return activityClass.toString();
    }
}
