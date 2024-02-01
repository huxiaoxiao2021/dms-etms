package com.jd.bluedragon.distribution.jy.dto.send;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 * 批次号报读已封
 * @Author zhengchengfa
 * @Date 2024/2/1 14:12
 * @Description
 */
@Data
public class BatchCodeShuttleSealDto implements Serializable {

    private static final long serialVersionUID = -9147679847630229665L;

    private String operatorErp;

    private Integer operateSiteId;

    private List<String> successSealBatchCodeList;

    private Date operateTime;


}
