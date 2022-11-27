package com.jd.bluedragon.common.dto.photo;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

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

    //照片地址一
    private String url1;

    //照片地址二
    private String url2;

    //照片地址三
    private String url3;

    //照片地址四
    private String url4;

    //照片地址五
    private String url5;

    //照片地址六
    private String url6;

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

    public String getUrl1() {
        return url1;
    }

    public void setUrl1(String url1) {
        this.url1 = url1;
    }

    public String getUrl2() {
        return url2;
    }

    public void setUrl2(String url2) {
        this.url2 = url2;
    }

    public String getUrl3() {
        return url3;
    }

    public void setUrl3(String url3) {
        this.url3 = url3;
    }

    public String getUrl4() {
        return url4;
    }

    public void setUrl4(String url4) {
        this.url4 = url4;
    }

    public String getUrl5() {
        return url5;
    }

    public void setUrl5(String url5) {
        this.url5 = url5;
    }

    public String getUrl6() {
        return url6;
    }

    public void setUrl6(String url6) {
        this.url6 = url6;
    }
}
