package com.jd.bluedragon.core.jsf.adapter;

import lombok.Data;

/**
 * 字节 解密服务 入参
 */
@Data
public class AdapterRequestStandardJsonObj {
    /**
     * 运单号
     */
    String waybillCode;
    /**
     * 请求用户类型，1-系统；2-业务员（快递员）；3-客服；4-总部业务人员；5-网点业务人员。decryType为1时必填
     */
    Integer userType;
    /**
     * 若userType≠1时，则必须传入具体用户的系统ID，可以结合systemSource中系统的用户ID。decryType为1时必填
     */
    String userId;
    /**
     * 系统来源，根据物流公司内部系统的代号传值，比如CRM系统，网点操作系统、小件员APP等，区分系统唯一性。decryType为1时必填
     */
    String systemSource;
    /**
     * 查询原因，根据实际用户查询的情况传值，如派件员联系收件人；处理客诉问题等。decryType为1时必填
     */
    String queryReason;
    /**
     * IP地址，调用该接口数据的用户的IP地址
     */
    String ipAddress;
    /**
     * 物流商编码（接入连接平台的物流商必填）
     */
    String company;
    /**
     * 所属平台：同运单增值服务中保持一致，10:字节
     */
    String platform;
    /**
     * 解密类型：1、查询收件人明文 2、查询收件人虚拟号
     */
    Integer decryType;

}