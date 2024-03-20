package com.jd.bluedragon.distribution.jy.dto.material;

import lombok.Data;

import java.io.Serializable;

/**
 * 循环物资操作记录
 */
@Data
public class RecycleMaterialOperateRecordDto implements Serializable {
    private static final long serialVersionUID = -777457122964711741L;

    /**
     * 物资编码
     */
    private String materialCode;

    /**
     * 芯片mac地址
     */
    private String chipMacAddress;

    /**
     * 芯片数据接收端mac地址
     */
    private String chipDataCollectorMacAddress;

    /**
     * 物资类型
     */
    private String materialType;

    /**
     * 箱号
     */
    private String boxCode;

    /**
     * 操作类型
     * 1-进；2-出；3-在线
     */
    private Integer operateType;

    /**
     * 操作节点编码
     */
    private Integer operateNodeCode;

    /**
     * 操作节点名称
     */
    private String operateNodeName;

    /**
     * 目的场地ID
     */
    private String receiveSiteId;

    /**
     * 目的场地名称
     */
    private String receiveSiteName;

    /**
     * 操作人erp
     */
    private String operateUserErp;

    /**
     * 操作人姓名
     */
    private String operateUserName;

    /**
     * 操作场地ID
     */
    private String operateSiteId;

    /**
     * 操作场地名称
     */
    private String operateSiteName;

    /**
     * 操作时间，毫秒级时间戳
     */
    private Long operateTime;

    /**
     * 系统实际发消息时间
     */
    private Long sendTime;

    private String bizSource;
}
