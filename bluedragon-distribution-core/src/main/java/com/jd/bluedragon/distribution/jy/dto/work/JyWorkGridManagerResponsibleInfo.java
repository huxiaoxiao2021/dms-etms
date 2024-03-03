package com.jd.bluedragon.distribution.jy.dto.work;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class JyWorkGridManagerResponsibleInfo implements Serializable{
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private Integer id;

    /**
     * 任务业务id
     */
    private String bizId;

    /**
     * 工种：1-正式工 2-外包工 3-临时工
     */
    private Integer workType;

    /**
     * 责任人姓名
     */
    private String name;

    /**
     * 责任人身份证号，work_type为2或3时
     */
    private String idCard;

    /**
     * erp,work_type为1时
     */
    private String erp;

    /**
     * 外包商编码
     */
    private String supplierId;

    /**
     * 外包商名称
     */
    private String supplierName;
    /**
     * 网格组长
     */
    private String gridOwnerErp;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 0无效 1有效
     */
    private Integer yn;
}
