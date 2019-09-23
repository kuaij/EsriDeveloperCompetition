package com.xiaok.winterolympic.model;

public class UserProfile {

    private  String aName;
    private  String aValue;

    public UserProfile(){}

    public UserProfile(String aName,String aValue){

        this.aName = aName;
        this.aValue = aValue;
    }
    public String getaName(){
        return aName;
    }
    public String getaValue(){
        return aValue;
    }
    public void setaName(String aName) {
        this.aName = aName;
    }

    public void setaSpeak(String aValue) {
        this.aValue= aValue;
    }

}
