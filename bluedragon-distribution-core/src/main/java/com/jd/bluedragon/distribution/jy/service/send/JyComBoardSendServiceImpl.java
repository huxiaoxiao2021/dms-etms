package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.common.dto.comboard.request.*;
import com.jd.bluedragon.common.dto.comboard.response.*;
import com.jd.bluedragon.common.dto.operation.workbench.enums.SendVehicleScanTypeEnum;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.dms.utils.BarCodeType;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public class JyComBoardSendServiceImpl implements JyComBoardSendService {

  @Override
  public InvokeResult<CrossDataResp> listCrossData(CrossDataReq request) {
    return null;
  }

  @Override
  public InvokeResult<TableTrolleyResp> listTableTrolleyUnderCross(TableTrolleyReq request) {
    return null;
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
    if (!BusinessHelper.isBoxcode(barCode)
        && !WaybillUtil.isWaybillCode(barCode)
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
