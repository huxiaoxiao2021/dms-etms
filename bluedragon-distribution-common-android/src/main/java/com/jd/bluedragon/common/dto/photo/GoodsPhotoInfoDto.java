package com.jd.bluedragon.common.dto.photo;

import java.io.Serializable;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/11/24 14:13
 * @Description:  货物拍照信息
 */
public class GoodsPhotoInfoDto implements Serializable {


    //拍照人用户编码
    private Integer userCode;

    //拍照人erp
    private String userErp;

    //拍照场地编码
    private Integer siteCode;
    //拍照场地名称
    private String siteName;

    //货物单号
    private String barCode;

    //拍照环节
    private int operateNode;

    //照片地址一
    private String url1;

    //照片地址一
    private String url2;

    //照片地址一
    private String url3;

    //照片地址一
    private String url4;

    //照片地址一
    private String url5;



}
