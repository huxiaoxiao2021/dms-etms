package com.jd.bluedragon.distribution.jy.manager;

import com.jd.bluedragon.distribution.jy.dto.findgoods.WaitFindPackageDto;
import com.jd.bluedragon.distribution.jy.dto.findgoods.WaitFindPackageQueryDto;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.springframework.stereotype.Service;

@Service("jyFindGoodsManager")
public class JyFindGoodsManagerImpl implements JyFindGoodsManager{

  @Override
  public PagerResult<WaitFindPackageDto> listWaitFindPackage(
      WaitFindPackageQueryDto waitFindPackageQueryDto) {
    //调用大数据团队服务获取
    return null;
  }
}
