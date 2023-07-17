package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.UnifiedExceptionProcess;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.inventory.FindGoodsReq;
import com.jd.bluedragon.common.dto.inventory.FindGoodsResp;
import com.jd.bluedragon.common.dto.inventory.*;
import com.jd.bluedragon.common.dto.inventory.enums.InventoryDetailTypeEnum;
import com.jd.bluedragon.common.dto.inventory.enums.InventoryListTypeEnum;
import com.jd.bluedragon.common.dto.inventory.enums.PhotoPositionEnum;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.findgoods.constants.FindGoodsConstants;
import com.jd.bluedragon.distribution.jy.service.findgoods.JyFindGoodsService;
import com.jd.bluedragon.external.gateway.service.JyFindGoodsGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@UnifiedExceptionProcess
@Service
public class JyFindGoodsGatewayServiceImpl implements JyFindGoodsGatewayService {

  @Autowired
  private JyFindGoodsService jyFindGoodsService;


  private void checkBaseParam(User user, CurrentOperate currentOperate, String groupCode, String positionCode) {
    if(Objects.isNull(user) || StringUtils.isBlank(user.getUserErp())) {
      throw new JyBizException("操作人erp为空");
    }
    if(Objects.isNull(currentOperate) || Objects.isNull(currentOperate.getSiteCode())) {
      throw new JyBizException("操作场地编码为空");
    }
    if(Objects.isNull(currentOperate.getOperateTime())) {
      throw new JyBizException("操作时间为空");
    }
    if(StringUtils.isBlank(groupCode)) {
      throw new JyBizException("岗位组为空");
    }
    if(StringUtils.isBlank(positionCode)) {
      throw new JyBizException("岗位码为空");
    }
  }

  private void checkPage(Integer pageNo, Integer pageSize) {
    if(Objects.isNull(pageNo) || Objects.isNull(pageSize) || pageNo < 1 || pageSize < 1) {
      throw new JyBizException("页码参数错误");
    }
    if(pageSize > Constants.PDA_DEFAULT_PAGE_MAXSIZE) {
      throw new JyBizException("每页查询数量超过最大值" + Constants.PDA_DEFAULT_PAGE_MAXSIZE);
    }
  }


  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyFindGoodsGatewayServiceImpl.findGoodsScan", mState = {JProEnum.TP})
  public JdCResponse<FindGoodsResp> findGoodsScan(FindGoodsReq request) {
    return retJdCResponse(jyFindGoodsService.findGoodsScan(request));
  }

  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyFindGoodsGatewayServiceImpl.findCurrentInventoryTask", mState = {JProEnum.TP})
  public JdCResponse<InventoryTaskDto> findCurrentInventoryTask(InventoryTaskQueryReq request) {
    String methodDesc = "JyFindGoodsGatewayServiceImpl.findCurrentInventoryTask:获取当前时刻找货任务服务:";
    JdCResponse<InventoryTaskDto> res = new JdCResponse<>();
    res.toSucceed();
    try{
      if(Objects.isNull(request)) {
        res.toFail("请求为空");
        return res;
      }
      if(log.isInfoEnabled()) {
        log.info("{}start-request={}", methodDesc, JsonHelper.toJson(request));
      }
      checkBaseParam(request.getUser(), request.getCurrentOperate(), request.getGroupCode(), request.getPositionCode());

      return retJdCResponse(jyFindGoodsService.findCurrentInventoryTask(request));
    }catch (JyBizException ex) {
      log.error("{}服务失败-request={}，errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage());
      res.toError("获取当前时刻找货任务服务失败");
      return res;
    }catch (Exception ex) {
      log.error("{}服务异常-request={}，errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage(), ex);
      res.toError("获取当前时刻找货任务服务异常");
      return res;
    }
  }

  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyFindGoodsGatewayServiceImpl.findInventoryTaskByBizId", mState = {JProEnum.TP})
  public JdCResponse<InventoryTaskDto> findInventoryTaskByBizId(InventoryTaskQueryReq request) {
    String methodDesc = "JyFindGoodsGatewayServiceImpl.findInventoryTaskByBizId:查询找货任务服务：";
    JdCResponse<InventoryTaskDto> res = new JdCResponse<>();
    res.toSucceed();
    try{
      if(Objects.isNull(request)) {
        res.toFail("请求为空");
        return res;
      }
      if(log.isInfoEnabled()) {
        log.info("{}start-request={}", methodDesc, JsonHelper.toJson(request));
      }
      checkBaseParam(request.getUser(), request.getCurrentOperate(), request.getGroupCode(), request.getPositionCode());


      return retJdCResponse(jyFindGoodsService.findInventoryTaskByBizId(request));

    }catch (JyBizException ex) {
      log.error("{}服务失败-request={}，errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage());
      res.toError("查询找货任务服务失败");
      return res;
    }catch (Exception ex) {
      log.error("{}服务异常-request={}，errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage(), ex);
      res.toError("查询找货任务服务异常");
      return res;
    }
  }

  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyFindGoodsGatewayServiceImpl.findInventoryTaskListPage", mState = {JProEnum.TP})
  public JdCResponse<InventoryTaskListQueryRes> findInventoryTaskListPage(InventoryTaskListQueryReq request) {
    String methodDesc = "JyFindGoodsGatewayServiceImpl.findInventoryTaskListPage：查询找货任务列表服务：";
    JdCResponse<InventoryTaskListQueryRes> res = new JdCResponse<>();
    res.toSucceed();
    try{
      if(Objects.isNull(request)) {
        res.toFail("请求为空");
        return res;
      }
      if(log.isInfoEnabled()) {
        log.info("{}start-request={}", methodDesc, JsonHelper.toJson(request));
      }
      checkBaseParam(request.getUser(), request.getCurrentOperate(), request.getGroupCode(), request.getPositionCode());
      checkPage(request.getPageNo(), request.getPageSize());

      //默认查当天列表
      if(Objects.isNull(request.getQueryDays()) || request.getQueryDays() < 0) {
        request.setQueryDays(0);
      }
      if(request.getQueryDays() > 180) {
        res.toFail("暂不支持查询180天之前数据");
        return res;
      }

      return retJdCResponse(jyFindGoodsService.findInventoryTaskListPage(request));

    }catch (JyBizException ex) {
      log.error("{}服务失败-request={}，errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage());
      res.toError("查询找货任务列表服务失败");
      return res;
    }catch (Exception ex) {
      log.error("{}服务异常-request={}，errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage(), ex);
      res.toError("查询找货任务列表服务异常");
      return res;
    }
  }

  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyFindGoodsGatewayServiceImpl.inventoryTaskStatistics", mState = {JProEnum.TP})
  public JdCResponse<InventoryTaskStatisticsRes> inventoryTaskStatistics(InventoryTaskStatisticsReq request) {
    String methodDesc = "JyFindGoodsGatewayServiceImpl.inventoryTaskStatistics：找货任务统计服务：";
    JdCResponse<InventoryTaskStatisticsRes> res = new JdCResponse<>();
    res.toSucceed();
    try{
      if(Objects.isNull(request)) {
        res.toFail("请求为空");
        return res;
      }
      if(log.isInfoEnabled()) {
        log.info("{}start-request={}", methodDesc, JsonHelper.toJson(request));
      }
      checkBaseParam(request.getUser(), request.getCurrentOperate(), request.getGroupCode(), request.getPositionCode());
      //默认查最近15天统计数据
      if(Objects.isNull(request.getStatisticsDays()) || request.getStatisticsDays() < 0) {
        request.setStatisticsDays(FindGoodsConstants.STATISTICS_DAYS);
      }

      return retJdCResponse(jyFindGoodsService.inventoryTaskStatistics(request));

    }catch (JyBizException ex) {
      log.error("{}服务失败-request={}，errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage());
      res.toError("找货任务统计服务失败");
      return res;
    }catch (Exception ex) {
      log.error("{}服务异常-request={}，errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage(), ex);
      res.toError("找货任务统计服务异常");
      return res;
    }
  }

  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyFindGoodsGatewayServiceImpl.inventoryTaskPhotograph", mState = {JProEnum.TP})
  public JdCResponse<Void> inventoryTaskPhotograph(InventoryTaskPhotographReq request) {
    String methodDesc = "JyFindGoodsGatewayServiceImpl.inventoryTaskPhotograph：找货任务上传照片服务：";
    JdCResponse<Void> res = new JdCResponse<>();
    res.toSucceed();
    try{
      if(Objects.isNull(request)) {
        res.toFail("请求为空");
        return res;
      }
      if(log.isInfoEnabled()) {
        log.info("{}start-request={}", methodDesc, JsonHelper.toJson(request));
      }
      checkBaseParam(request.getUser(), request.getCurrentOperate(), request.getGroupCode(), request.getPositionCode());

      if(StringUtils.isBlank(request.getBizId())) {
        res.toFail("请求参数bizId为空");
        return res;
      }
      if(Objects.isNull(request.getPhotoPosition())) {
        res.toFail("拍照方位为空");
        return res;
      }
      if(!PhotoPositionEnum.isLegal(request.getPhotoPosition())) {
        res.toFail("拍照方位不支持");
        return res;
      }
      if(CollectionUtils.isEmpty(request.getPhotoUrls())) {
        res.toFail("图片url为空");
        return res;
      }
      if(request.getPhotoUrls().size() > FindGoodsConstants.PHOTOGRAPH_MAX_NUM) {
        res.toFail("一次最多上传图片数量为" + FindGoodsConstants.PHOTOGRAPH_MAX_NUM);
        return res;
      }

      return retJdCResponse(jyFindGoodsService.inventoryTaskPhotograph(request));

    }catch (JyBizException ex) {
      log.error("{}服务失败-request={}，errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage());
      res.toError("找货任务上传照片服务失败");
      return res;
    }catch (Exception ex) {
      log.error("{}服务异常-request={}，errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage(), ex);
      res.toError("找货任务上传照片服务异常");
      return res;
    }
  }

  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyFindGoodsGatewayServiceImpl.findInventoryDetailPage", mState = {JProEnum.TP})
  public JdCResponse<InventoryDetailQueryRes> findInventoryDetailPage(InventoryDetailQueryReq request) {
    String methodDesc = "JyFindGoodsGatewayServiceImpl.findInventoryDetailPage：查询找货任务清单服务：";
    JdCResponse<InventoryDetailQueryRes> res = new JdCResponse<>();
    res.toSucceed();
    try{
      if(Objects.isNull(request)) {
        res.toFail("请求为空");
        return res;
      }
      if(log.isInfoEnabled()) {
        log.info("{}start-request={}", methodDesc, JsonHelper.toJson(request));
      }
      checkBaseParam(request.getUser(), request.getCurrentOperate(), request.getGroupCode(), request.getPositionCode());
      checkPage(request.getPageNo(), request.getPageSize());

      if(StringUtils.isBlank(request.getBizId())) {
        res.toFail("bizId为空");
        return res;
      }
      if(!InventoryListTypeEnum.isLegal(request.getInventoryListType())) {
        res.toFail("任务类型参数传值错误");
        return res;
      }
      if(!InventoryDetailTypeEnum.isLegal(request.getInventoryDetailType())) {
        res.toFail("任务明细类型参数传值错误");
        return res;
      }

      return retJdCResponse(jyFindGoodsService.findInventoryDetailPage(request));

    }catch (JyBizException ex) {
      log.error("{}服务失败-request={}，errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage());
      res.toError("查询找货任务清单服务失败");
      return res;
    }catch (Exception ex) {
      log.error("{}服务异常-request={}，errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage(), ex);
      res.toError("查询找货任务清单服务异常");
      return res;
    }
  }

  private <T> JdCResponse<T> retJdCResponse(InvokeResult<T> invokeResult) {
    return new JdCResponse<>(invokeResult.getCode(), invokeResult.getMessage(),
        invokeResult.getData());
  }
}
