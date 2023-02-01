package com.jd.bluedragon.distribution.seal.domain;


import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CreateTransAbnormalAndUnsealJmqMsg {
    public static final Integer TYPE_CREATE_TRANS_ABNORMAL_AND_DE_SEAL_CODE = 1;//无到货解封签
    public static final Integer TYPE_CREATE_TRANS_ABNORMAL_AND_UNSEAL = 2;//提报异常并解封车

    String desealCarTime;// 解封车时间
    List<String> desealCodes;// 解封签号
    int desealSiteId;//解封车站点id
    String desealSiteName;//解封车站点名
    String desealUserCode;//解封车用户erp
    String desealUserName;// 解封车用户名
    String sealCarCode;// sc
    String vehicleNumber;//车牌
    String abnormalDesc;// 异常原因描述
    String abnormalTypeCode;// 异常类型
    String abnormalTypeName;// 异常类型名称
    List<String> photoUrlList;//照片list
    String referBillCode;// 运输billCode
    Byte referBillType;// 运输billType

    int source;//来源

    Date createTransAbnormalTime;//提报异常时间
}
