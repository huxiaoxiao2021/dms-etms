package com.jd.bluedragon.distribution.jy.api;


import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dto.findgoods.CreateFindGoodsTask;
import com.jd.bluedragon.distribution.jy.dto.findgoods.DistributPackageDto;
import com.jd.bluedragon.distribution.jy.dto.findgoods.FindGoodsTaskDto;
import com.jd.bluedragon.distribution.jy.dto.findgoods.UpdateWaitFindPackageStatusDto;
import com.jd.bluedragon.distribution.jy.dto.findgoods.WaitFindPackageDto;
import com.jd.bluedragon.distribution.jy.dto.findgoods.WaitFindPackageQueryDto;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import java.util.List;

public interface JyFindGoodsJsfService {

  //查询场地波次待找包裹
  InvokeResult<PagerResult<WaitFindPackageDto>> listWaitFindPackage(WaitFindPackageQueryDto dto);

  /**
   * 生成网格找货任务
   */
  InvokeResult<FindGoodsTaskDto> createFindGoodsTask(CreateFindGoodsTask dto);


  /**
   * 给任务分配待找获取明细
   */
  InvokeResult distributWaitFindPackage(DistributPackageDto dto);

  /**
   * 更新待找货物状态
   */
  InvokeResult updateWaitFindPackageStatus(UpdateWaitFindPackageStatusDto dto);





}
