package com.jd.ql.dms.common.domain;

/**
 * 城市信息
 * Created by xumei3 on 2017/12/28.
 */
public class City {
    /** 城市id **/
    private int cityId;

    /** 城市名称 **/
    private String cityName;

    public City(int cityId,String cityName){
        this.cityId = cityId;
        this.cityName = cityName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof City))
            return false;
        City city = (City) o;
        return this.cityId == city.cityId && this.cityName.equals(city.cityName);
    }

    @Override
    public int hashCode() {
        return cityId * cityName.hashCode();
    }

    @Override
    public String toString(){
        return "城市信息:" + this.cityId + ":" + this.cityName;
    }
}
