package com.jd.bluedragon.distribution.consumable.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author shipeilin
 * @Description: 类描述信息
 * @date 2018年08月22日 16时:06分
 */
public class WaybillConsumableExportDto  implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 分拣中心编号 */
    private Integer dmsId;

    /** 分拣中心名称 */
    private String dmsName;

    /** 运单号 */
    private String waybillCode;

    /** 确认状态（0：未确认 1：已确认） */
    private Integer confirmStatus;

    /** 修改状态（0：未修改 1：已修改） */
    private Integer modifyStatus;

    /** 揽收人编号 */
    private String receiveUserCode;

    /** 揽收人erp */
    private String receiveUserErp;

    /** 揽收人Name */
    private String receiveUserName;

    /** 确认人编号 */
    private String confirmUserName;

    /** 确认人erp */
    private String confirmUserErp;

    /** 揽收时间 */
    private Date receiveTime;

    /** 确认时间 */
    private Date confirmTime;

    /** 揽收数量 */
    private Integer receiveQuantity;

    /** 确认数量 */
    private Integer confirmQuantity;

    /** 操作人编号 */
    private String operateUserCode;

    /** 操作人erp */
    private String operateUserErp;

    /** 耗材编号 */
    private String code;

    /** 耗材名称 */
    private String name;

    /** 类型 */
    private String type;

    /** 体积 */
    private BigDecimal volume;

    /** 体积系数 */
    private BigDecimal volumeCoefficient;

    /** 规格 */
    private String specification;

    /** 单位 */
    private String unit;

}
