package com.xiaok.winterolympic.base;

public interface NameObserable {
    void add(NameObserver observer);//添加观察者
    void remove(NameObserver observer);//删除观察者
    void notify(String message);//通知观察者
}
