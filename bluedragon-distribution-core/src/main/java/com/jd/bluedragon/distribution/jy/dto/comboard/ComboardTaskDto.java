package com.jd.bluedragon.distribution.jy.dto.comboard;

import lombok.Data;

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
}
