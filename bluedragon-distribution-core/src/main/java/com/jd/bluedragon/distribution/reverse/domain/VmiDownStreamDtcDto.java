package com.jd.bluedragon.distribution.reverse.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 出大库向DTC发送数据参数
 *
 * @author litonggang
 */
@Data
public class VmiDownStreamDtcDto implements Serializable {

    /**
     * 目标库房信息，机构编号+配送中心编号+仓库编号, 格式：6,6,0
     */
    private String target;
    /**
     * 调用目标方法名
     */
    private String methodName;
    /**
     * 订单类型
     */
    private String outboundType;
    /**
     *
     */
    private Integer priority;
    /**
     * 发送对象集合
     */
    private VmiDownStreamDtcMessage messageValue;
    /**
     * 报文md5值
     */
    private String messageMd5Value;
    /**
     * 数据来源
     */
    private String source;
    /**
     * 运单号
     */
    private String outboundNo;
    /**
     * 批次号
     */
    private String batchNo;
}