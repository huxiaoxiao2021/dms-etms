package com.jd.bluedragon.common.dto.inventory;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;
import java.util.List;


/**
 * 找货任务
 */
public class InventoryTaskPhotographReq extends BaseReq implements Serializable {

    private static final long serialVersionUID = -6051001368608203945L;

    private String bizId;

    /**
     * 照片url
     */
    private List<String> photoUrl;
    /**
     * 照片方位
     * com.jd.bluedragon.common.dto.inventory.enums.PhotoPositionEnum
     */
    private Integer photoPosition;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public List<String> getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(List<String> photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Integer getPhotoPosition() {
        return photoPosition;
    }

    public void setPhotoPosition(Integer photoPosition) {
        this.photoPosition = photoPosition;
    }
}
