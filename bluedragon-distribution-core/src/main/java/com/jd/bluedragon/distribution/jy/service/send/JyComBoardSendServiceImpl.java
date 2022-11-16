package com.jd.bluedragon.distribution.jy.service.send;

import static com.jd.bluedragon.distribution.base.domain.InvokeResult.RESULT_SUCCESS_CODE;
import static com.jd.bluedragon.distribution.base.domain.InvokeResult.RESULT_SUCCESS_MESSAGE;

import com.jd.bluedragon.common.dto.comboard.request.*;
import com.jd.bluedragon.common.dto.comboard.response.*;
import com.jd.bluedragon.core.jsf.cross.SortCrossJsfManager;
import com.jd.bluedragon.common.dto.operation.workbench.enums.SendVehicleScanTypeEnum;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.businessIntercept.enums.BusinessInterceptOnlineStatusEnum;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.jsf.domain.BoardCombinationJsfResponse;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.ver.service.SortingCheckService;
import com.jd.bluedragon.dms.utils.BarCodeType;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jdl.basic.api.domain.cross.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import com.jd.dms.workbench.utils.sdk.base.Result;
import com.jd.etms.waybill.domain.Waybill;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import static com.jd.bluedragon.distribution.base.domain.InvokeResult.NO_OPERATE_SITE_CODE;
import static com.jd.bluedragon.distribution.base.domain.InvokeResult.NO_OPERATE_SITE_MESSAGE;

@Service
@Slf4j
public class JyComBoardSendServiceImpl implements JyComBoardSendService {

  @Autowired
  private SortCrossJsfManager sortCrossJsfManager;
  
  @Autowired
  private WaybillQueryManager waybillQueryManager;
  @Autowired
  private SortingCheckService sortingCheckService;
  @Autowired
  private BoxService boxService;


  @Override
  public InvokeResult<CrossDataResp> listCrossData(CrossDataReq request) {
    log.info("开始获取场地滑道信息：{}", JsonHelper.toJson(request));
    CrossPageQuery query = new CrossPageQuery();
    if (request == null || request.getCurrentOperate() == null ){
      return new InvokeResult<>(NO_OPERATE_SITE_CODE,NO_OPERATE_SITE_MESSAGE);
    }
    InvokeResult<CrossDataResp> result = new InvokeResult<>();
    CrossDataResp crossDataResp = new CrossDataResp();
    result.setData(crossDataResp);
    query.setDmsId(request.getCurrentOperate().getSiteCode());
    query.setPageNumber(request.getPageNo());
    query.setLimit(request.getPageSize());
    CrossDataJsfResp crossDataJsfResp = sortCrossJsfManager.queryCrossDataByDmsCode(query);
    if (crossDataJsfResp != null ) {
      crossDataResp.setCrossCodeList(crossDataJsfResp.getCrossCodeList());
      crossDataResp.setTotalPage(crossDataJsfResp.getTotalPage());
    }
    return result;
  }

  @Override
  public InvokeResult<TableTrolleyResp> listTableTrolleyUnderCross(TableTrolleyReq request) {
    log.info("开始获取笼车营业部信息：{}", JsonHelper.toJson(request));
    TableTrolleyQuery query = new TableTrolleyQuery();
    if (request == null || request.getCurrentOperate() == null ){
      return new InvokeResult<>(NO_OPERATE_SITE_CODE,NO_OPERATE_SITE_MESSAGE);
    }
    InvokeResult<TableTrolleyResp> result = new InvokeResult<>();
    TableTrolleyResp tableTrolleyResp = new TableTrolleyResp();
    result.setData(tableTrolleyResp);
    query.setDmsId(request.getCurrentOperate().getSiteCode());
    query.setPageNumber(request.getPageNo());
    query.setLimit(request.getPageSize());
    TableTrolleyJsfResp tableTrolleyJsfResp;
    if (!StringUtils.isEmpty(request.getCrossCode())) {
      // 根据滑道查询笼车信息
      tableTrolleyJsfResp = sortCrossJsfManager.queryTableTrolleyListByCrossCode(query);
    }else {
      // 根据场地查询笼车信息
      tableTrolleyJsfResp = sortCrossJsfManager.queryTableTrolleyListByDmsId(query);
    }
    if (tableTrolleyJsfResp != null ) {
      tableTrolleyResp.setTableTrolleyDtoList(getTableTrolleyDto(tableTrolleyJsfResp.getTableTrolleyDtoJsfList()));
      tableTrolleyResp.setTotalPage(tableTrolleyJsfResp.getTotalPage());
    }
    return result;  }

  private List<TableTrolleyDto> getTableTrolleyDto(List<TableTrolleyJsfDto> tableTrolleyDtoJsfList) {
    List<TableTrolleyDto> tableTrolleyDtoList = new ArrayList<>();
    for (TableTrolleyJsfDto tableTrolleyJsfDto : tableTrolleyDtoJsfList) {
      TableTrolleyDto tableTrolleyDto = new TableTrolleyDto();
      tableTrolleyDto.setTableTrolleyCode(tableTrolleyJsfDto.getTableTrolleyCode());
      tableTrolleyDto.setCrossCode(tableTrolleyJsfDto.getCrossCode());
      tableTrolleyDto.setEndSiteId(tableTrolleyJsfDto.getEndSiteId());
      tableTrolleyDto.setEndSiteName(tableTrolleyJsfDto.getEndSiteName());
      tableTrolleyDtoList.add(tableTrolleyDto);
    }
    return tableTrolleyDtoList;
  }


  @Override
  public InvokeResult<CreateGroupCTTResp> createGroupCTTData(CreateGroupCTTReq request) {
    return null;
  }

  @Override
  public InvokeResult addCTT2Group(AddCTTReq request) {
    return null;
  }

  @Override
  public InvokeResult removeCTTFromGroup(RemoveCTTReq request) {
    return null;
  }

  @Override
  public InvokeResult<CTTGroupDataResp> listCTTGroupData(CTTGroupDataReq request) {
    return null;
  }

  @Override
  public InvokeResult<SendFlowDataResp> listSendFlowUnderCTTGroup(SendFlowDataReq request) {
    return null;
  }

  @Override
  public InvokeResult<SendFlowDetailResp> querySendFlowDetail(SendFlowDetailReq request) {
    return null;
  }

  @Override
  public InvokeResult<BoardResp> queryBoardDetail(BoardReq request) {
    return null;
  }

  @Override
  public InvokeResult finishBoard(BoardReq request) {
    return null;
  }

  @Override
  public InvokeResult finishBoardsUnderCTTGroup(CTTGroupReq request) {
    return null;
  }

  @Override
  public InvokeResult<ComboardScanResp> comboardScan(ComboardScanReq request) {
    baseCheck(request);
    bizCheck(request);
    getOrCreateBoardCode(request);
    comboard(request);
    send(request);

    ComboardScanResp resp = new ComboardScanResp();
    resp.setEndSiteId(request.getEndSiteId());
    return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, resp);
  }

  private void send(ComboardScanReq request) {
  }

  private void comboard(ComboardScanReq request) {
  }

  private String getOrCreateBoardCode(ComboardScanReq request) {
    return null;
  }

  private void baseCheck(ComboardScanReq request) {
    /**
     * 参数校验
     */
    if (!ObjectHelper.isNotNull(request.getTemplateCode())) {
      throw new JyBizException("参数错误：缺少混扫任务编号！");
    }
    if (!ObjectHelper.isNotNull(request.getScanType())) {
      throw new JyBizException("参数错误：请选择扫货方式！");
    }

    String barCode = request.getBarCode();
    if (!BusinessHelper.isBoxcode(barCode) && !WaybillUtil.isWaybillCode(barCode)
        && !WaybillUtil.isPackageCode(barCode)) {
      throw new JyBizException("请扫描正确的条形码：只支持扫描包裹、运单、箱号！");
    }
    int siteCode = request.getCurrentOperate().getSiteCode();
    if (!NumberHelper.gt0(siteCode)) {
      throw new JyBizException("缺少操作场地！");
    }

    if (StringUtils.isBlank(request.getGroupCode())) {
      throw new JyBizException("扫描前请绑定小组！");
    }
    // 扫描方式与扫描条码是否匹配校验
    if (request.getScanType() == null) {
      request.setScanType(SendVehicleScanTypeEnum.SCAN_ONE.getCode());
    }
    final BarCodeType barCodeType = BusinessUtil.getBarCodeType(request.getBarCode());
    if (barCodeType == null) {
      throw new JyBizException("请扫描正确的条码！");
    }
    if (Objects.equals(SendVehicleScanTypeEnum.SCAN_ONE.getCode(), request.getScanType()) &&
        (!Objects.equals(BarCodeType.PACKAGE_CODE.getCode(), barCodeType.getCode()) && !Objects
            .equals(BarCodeType.BOX_CODE.getCode(), barCodeType.getCode()))) {
      throw new JyBizException("请扫描包裹号或箱号！");
    }
    if (Objects.equals(SendVehicleScanTypeEnum.SCAN_WAYBILL.getCode(), request.getScanType())) {
      if (!Objects.equals(BarCodeType.PACKAGE_CODE.getCode(), barCodeType.getCode()) && !Objects
          .equals(BarCodeType.WAYBILL_CODE.getCode(), barCodeType.getCode())) {
        throw new JyBizException("请扫描包裹号或运单号！");
      }
      request.setBarCode(WaybillUtil.getWaybillCode(request.getBarCode()));
    }
  }

  private void bizCheck(ComboardScanReq request) {
    String barCode = request.getBarCode();
    if (WaybillUtil.isPackageCode(barCode)) {
      final Waybill waybill = waybillQueryManager
          .getOnlyWaybillByWaybillCode(WaybillUtil.getWaybillCodeByPackCode(barCode));
      if (waybill == null) {
        throw new JyBizException("未查找到运单数据");
      }
      if (waybill.getOldSiteId() == null) {
        throw new JyBizException("运单对应的预分拣站点为空");
      }
      request.setEndSiteId(waybill.getOldSiteId());
      //匹配流向
      checkAndMatchDestination(request);
      combinationCheck(request);

    } else if (BusinessHelper.isBoxcode(barCode)) {
      final Box boxExist = boxService.findBoxByCode(barCode);
      if (boxExist == null) {
        throw new JyBizException("未找到对应箱号，请检查");
      }
      request.setEndSiteId(boxExist.getReceiveSiteCode());
      //匹配流向
      checkAndMatchDestination(request);

    } else {

    }

  }

  private void combinationCheck(ComboardScanReq request) {
    final PdaOperateRequest pdaOperateRequest = new PdaOperateRequest();
    pdaOperateRequest.setPackageCode(request.getBarCode());
    pdaOperateRequest.setBoxCode(request.getBarCode());
    pdaOperateRequest.setReceiveSiteCode(request.getEndSiteId());
    pdaOperateRequest.setCreateSiteCode(request.getCurrentOperate().getSiteCode());
    pdaOperateRequest.setCreateSiteName(request.getCurrentOperate().getSiteName());
    pdaOperateRequest.setOperateUserCode(request.getUser().getUserCode());
    pdaOperateRequest.setOperateUserName(request.getUser().getUserName());
    pdaOperateRequest.setOnlineStatus(BusinessInterceptOnlineStatusEnum.ONLINE.getCode());
    BoardCombinationJsfResponse interceptResult = sortingCheckService
        .virtualBoardCombinationCheck(pdaOperateRequest);
    if (!interceptResult.getCode().equals(200)) {
      throw new JyBizException(interceptResult.getMessage());
    }

  }

  private void checkAndMatchDestination(ComboardScanReq request) {
  }


  @Override
  public InvokeResult<BoardStatisticsResp> queryBoardStatisticsUnderSendFlow(
      BoardStatisticsReq request) {
    return null;
  }

  @Override
  public InvokeResult<HaveScanStatisticsResp> queryHaveScanStatisticsUnderBoard(
      HaveScanStatisticsReq request) {
    return null;
  }

  @Override
  public InvokeResult<PackageDetailResp> listPackageDetailRespUnderBox(BoxQueryReq request) {
    return null;
  }

  @Override
  public InvokeResult<WaitScanStatisticsResp> queryWaitScanStatisticsUnderSendFlow(
      WaitScanStatisticsReq request) {
    return null;
  }

  @Override
  public InvokeResult<PackageDetailResp> listPackageDetailRespUnderSendFlow(
      SendFlowQueryReq request) {
    return null;
  }

  @Override
  public InvokeResult<BoardExcepStatisticsResp> queryExcepScanStatisticsUnderBoard(
      BoardExcepStatisticsReq request) {
    return null;
  }

  @Override
  public InvokeResult<SendFlowExcepStatisticsResp> queryExcepScanStatisticsUnderCTTGroup(
      SendFlowExcepStatisticsReq request) {
    return null;
  }
}
