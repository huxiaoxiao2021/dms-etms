package com.jd.bluedragon.utils.location.dto;

import java.io.Serializable;

/**
 * 矩形
 *
 * @author fanggang7
 */
public class Rectangle implements Serializable {

    private static final long serialVersionUID = 1L;

    private LatLng northEast;//东北脚点

    private LatLng northWest;//西北脚点

    private LatLng southEast;//东南脚点

    private LatLng southWest;//西南脚点

    public LatLng getNorthEast() {
        return northEast;
    }

    public void setNorthEast(LatLng northEast) {
        this.northEast = northEast;
    }

    public LatLng getNorthWest() {
        return northWest;
    }

    public void setNorthWest(LatLng northWest) {
        this.northWest = northWest;
    }

    public LatLng getSouthEast() {
        return southEast;
    }

    public void setSouthEast(LatLng southEast) {
        this.southEast = southEast;
    }

    public LatLng getSouthWest() {
        return southWest;
    }

    public void setSouthWest(LatLng southWest) {
        this.southWest = southWest;
    }
}
