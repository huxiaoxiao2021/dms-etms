package com.jd.bluedragon.distribution.jy.dto.comboard;

import com.jd.bluedragon.distribution.api.domain.OperatorData;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class ComboardTaskDto {
  private String waybillCode;
  private Integer startSiteId;
  private String startSiteName;
  private Integer endSiteId;
  private String endSiteName;
  private String boardCode;
  private Integer pageNo;
  private Integer pageSize;
  private Integer totalPage;
  private String userErp;
  private String userName;
  private String barCode;
  private Integer userCode;
  private Date operateTime;
  private Long operateFlowId;
  /**
   *@see com.jd.bluedragon.distribution.api.enums.OperatorTypeEnum
   * 操作者类型编码
   */
	private Integer operatorTypeCode;
  /**
   * 操作者id
   */
	private String operatorId;
    /**
     * 操作信息对象
     */
	private OperatorData operatorData;
    /**
     * <包裹号, 操作流水表主键>
     */
    private Map<String, Long> operateFlowMap;
}
