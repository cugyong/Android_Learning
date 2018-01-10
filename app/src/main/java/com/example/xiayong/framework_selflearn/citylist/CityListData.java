package com.example.xiayong.framework_selflearn.citylist;

/**
 * Created by cugyong on 2018/1/8.
 */

public class CityListData {

    private String city;
    private String id;

    public CityListData(String city, String id) {
        this.city = city;
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
