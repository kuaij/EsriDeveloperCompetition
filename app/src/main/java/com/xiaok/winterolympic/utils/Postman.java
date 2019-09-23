package com.xiaok.winterolympic.utils;

import com.xiaok.winterolympic.base.NameObserable;
import com.xiaok.winterolympic.base.NameObserver;

import java.util.ArrayList;
import java.util.List;

public class Postman implements NameObserable {
    /*
    * @function: 用户信息界面和主界面侧栏用户名的同步（观察者模式）
    * @author: zzu_kuaijian
    * @date: 2019/07/13
    */


    private List<NameObserver> personList = new ArrayList<NameObserver>();//保存收件人（观察者）的信息
    @Override
    public void add(NameObserver observer) {//添加收件人
        personList.add(observer);
    }
    @Override
    public void remove(NameObserver observer) {//移除收件人
        personList.remove(observer);

    }

    @Override
    public void notify(String message) {//逐一通知收件人（观察者）
        for (NameObserver observer : personList) {
            observer.syncUsename(message);
        }
    }
}
