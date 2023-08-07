package com.jd.bluedragon.distribution.jy.manager;

import com.jd.bluedragon.distribution.jy.dto.findgoods.WaitFindPackageDto;
import com.jd.bluedragon.distribution.jy.dto.findgoods.WaitFindPackageQueryDto;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

public interface JyFindGoodsManager {

  PagerResult<WaitFindPackageDto> listWaitFindPackage(WaitFindPackageQueryDto waitFindPackageQueryDto);


}
