package com.jd.bluedragon.distribution.jy.dto.collect;

import lombok.Builder;
import lombok.Data;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.jy.dto.collect
 * @ClassName: CollectScanDto
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2023/3/14 11:05
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
@Data
@Builder
public class CollectScanDto {

    private String waybillCode;

    private String packageCode;

    private Integer nextSiteCode;

    private Integer collectNodeSiteCode;

    private String operatorErp;

    private String BizId;
}
