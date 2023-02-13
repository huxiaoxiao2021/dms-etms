package com.jd.bluedragon.distribution.jy.manager;

import com.jd.bluedragon.utils.JsonHelper;
import com.jdl.jy.realtime.api.comboard.IComboardJsfService;
import com.jdl.jy.realtime.base.Pager;
import com.jdl.jy.realtime.base.ServiceResult;
import com.jdl.jy.realtime.model.es.comboard.ComboardScanedDto;
import com.jdl.jy.realtime.model.es.comboard.JyComboardPackageDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class JyComboardJsfManagerImpl implements IJyComboardJsfManager {

  private static final Logger log = LoggerFactory.getLogger(JyComboardJsfManagerImpl.class);


  @Autowired
  @Qualifier("jyComboardJsfService")
  private IComboardJsfService comboardJsfService;

  @Override
  public Pager<ComboardScanedDto> queryScannedDetail(Pager<JyComboardPackageDetail> query) {
    try {
      ServiceResult<Pager<ComboardScanedDto>> serviceResult = comboardJsfService.queryScannedDetail(query);
      if (serviceResult.retSuccess()) {
        return serviceResult.getData();
      } else {
        log.warn("分页查询已扫包裹失败. {}. {}", JsonHelper.toJson(query), JsonHelper.toJson(serviceResult));
      }
    } catch (Exception ex) {
      log.error("查询发车包裹明细异常. {}", JsonHelper.toJson(query), ex);
    }
    return null;
  }

  @Override
  public Pager<ComboardScanedDto> queryInterceptDetail(Pager<JyComboardPackageDetail> query) {
    try {
      ServiceResult<Pager<ComboardScanedDto>> serviceResult = comboardJsfService.queryInterceptDetail(query);
      if (serviceResult.retSuccess()) {
        return serviceResult.getData();
      } else {
        log.warn("分页查询拦截包裹失败. {}. {}", JsonHelper.toJson(query), JsonHelper.toJson(serviceResult));
      }
    } catch (Exception ex) {
      log.error("查询拦截包裹明细异常. {}", JsonHelper.toJson(query), ex);
    }
    return null;
  }

  @Override
  public Pager<ComboardScanedDto> queryWaitScanDetail(Pager<JyComboardPackageDetail> query) {
    try {
      ServiceResult<Pager<ComboardScanedDto>> serviceResult = comboardJsfService.queryWaitScanDetail(query);
      if (serviceResult.retSuccess()) {
        return serviceResult.getData();
      } else {
        log.warn("分页查询待扫包裹失败. {}. {}", JsonHelper.toJson(query), JsonHelper.toJson(serviceResult));
      }
    } catch (Exception ex) {
      log.error("查询待扫包裹明细异常. {}", JsonHelper.toJson(query), ex);
    }
    return null;
  }
}
