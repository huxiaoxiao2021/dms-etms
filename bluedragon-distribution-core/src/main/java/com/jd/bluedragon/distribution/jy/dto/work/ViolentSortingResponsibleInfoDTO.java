package com.jd.bluedragon.distribution.jy.dto.work;

import lombok.Data;

import java.io.Serializable;

@Data
public class ViolentSortingResponsibleInfoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    //暴力分拣id ，与 fdm_jdl_exp_wl_ai_cv_pro_gai_fj_violent_chain 对应
    private Long id;
    //责任人类型：1自有员工 2外包工 3临时工
    private Integer responsibleType;
    
    //责任人code： erp/外包商编码
    private String responsibleCode;
    //异常跟进人erp 
    private String advanceErp;
    //场地id
    private Integer siteCode;
    //任务状态 0 未找到责任人 1正常
    private Integer status;
    //判责流程编号
    private String processInstanceId;
}
