package com.jd.bluedragon.common.dto.photo;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/11/24 14:13
 * @Description:  货物拍照信息
 */
public class GoodsPhotoInfoDto implements Serializable {

    private CurrentOperate currentOperate;

    private User user;

    //货物单号
    private String barCode;

    //拍照环节
    private int operateNode;

    //照片地址
    private List<String> photoUrls;



    public CurrentOperate getCurrentOperate() {
        return currentOperate;
    }

    public void setCurrentOperate(CurrentOperate currentOperate) {
        this.currentOperate = currentOperate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public int getOperateNode() {
        return operateNode;
    }

    public void setOperateNode(int operateNode) {
        this.operateNode = operateNode;
    }

    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
    }
}
