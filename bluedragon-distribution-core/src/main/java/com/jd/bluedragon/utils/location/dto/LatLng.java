package com.jd.bluedragon.utils.location.dto;

import java.io.Serializable;

/**
 * 经纬度
 *
 * @author fanggang7
 */
public class LatLng implements Serializable {

    private static final long serialVersionUID = 1L;

    private double lat;// 纬度值/Y

    private double lng;// 经度值/X

    public LatLng() {
    }

    public LatLng(LatLng latLng) {
        this.lat = latLng.getLat();
        this.lng = latLng.getLng();
    }

    public LatLng(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        LatLng other = (LatLng) obj;
        return (this.lat == other.getLat() && this.lng == other.getLng());
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

}