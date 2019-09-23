package com.xiaok.winterolympic.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class LocalDataModel {
    /*
    * @function: 本地数据操纵类，包含字符串转化，账号类序列化并存储到sp中
    * @author: zzu_kuaijian
    * @date: 2019/08/10
     */

    private static SharedPreferences sp;



    /**
     * @param context:上下文环境
     * @param Rid :R中字符串资源唯一id
     * @return id对应的字符串对象
     */
    public static String RidToString(Context context, int Rid){
        String result = context.getResources().getString(Rid);
        return result;
    }
}
