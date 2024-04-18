package com.jd.bluedragon.distribution.jy.dto.send;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 * 封车成功批次数据
 * @Author zhengchengfa
 * @Date 2024/2/1 14:12
 * @Description
 */
@Data
public class BatchCodeSealCarDto implements Serializable {

    private static final long serialVersionUID = -9147679847630229665L;

    private String operatorErp;
    //操作人
    private Integer operateSiteId;
    //封车批次
    private List<String> successSealBatchCodeList;
    //操作时间
    private Date operateTime;
    //运力编码
    private String transportCode;
    //操作岗位
    private String post;

}
