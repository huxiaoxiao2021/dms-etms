package com.jd.bluedragon.distribution.print.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Pack;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.base.service.AirTransportService;
import com.jd.bluedragon.distribution.fastRefund.service.WaybillCancelClient;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.popPrint.domain.PopPrint;
import com.jd.bluedragon.distribution.popPrint.service.PopPrintService;
import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintContext;
import com.jd.bluedragon.distribution.waybill.domain.BaseResponseIncidental;
import com.jd.bluedragon.distribution.waybill.domain.LabelPrintingRequest;
import com.jd.bluedragon.distribution.waybill.domain.LabelPrintingResponse;
import com.jd.bluedragon.distribution.waybill.service.LabelPrinting;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.LableType;
import com.jd.bluedragon.utils.OriginalType;
import com.jd.etms.waybill.dto.PackOpeFlowDto;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 面单打印冗余服务
 * Created by shipeilin on 2018/1/31.
 */
@Service("wayBillPrintRedundanceService")
public class WayBillPrintRedundanceServiceImpl implements WayBillPrintRedundanceService {

    private static final Log logger= LogFactory.getLog(WayBillPrintRedundanceServiceImpl.class);

    @Autowired
    private WaybillCommonService waybillCommonService;

    @Autowired
    private PopPrintService popPrintService;

    @Autowired
    private AirTransportService airTransportService;

    @Autowired
    private LabelPrinting labelPrinting;


    public InterceptResult<String> getWaybillPack1(WaybillPrintContext context) {

        return null;
    }
    @Override
    public InterceptResult<String> getWaybillPack(WaybillPrintContext context) {
        InterceptResult<String> result = new InterceptResult<String>();
        Integer startDmsCode = context.getRequest().getDmsSiteCode();
        String waybillCodeOrPackage = context.getRequest().getBarCode();
        Integer localSchedule = context.getRequest().getTargetSiteCode();
        Boolean nopaperFlg = context.getRequest().getNopaperFlg();
        Integer startSiteType = context.getRequest().getStartSiteType();
        Integer packOpeFlowFlg = context.getRequest().getPackOpeFlowFlg();
        // 判断传入参数
        if (startDmsCode == null || startDmsCode.equals(0) || StringUtils.isEmpty(waybillCodeOrPackage)) {
            logger.error("根据初始分拣中心-运单号/包裹号【" + startDmsCode + "-" + waybillCodeOrPackage + "】获取运单包裹信息接口 --> 传入参数非法");
            result.toError(JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR);
            return result;
        }
        // 转换运单号
        String waybillCode = BusinessHelper.getWaybillCode(waybillCodeOrPackage);
        // 调用服务
        try {
            Waybill waybill = findWaybillMessage(waybillCode,packOpeFlowFlg);
            if (waybill == null) {
                logger.info("运单号【" + waybillCode + "】调用根据运单号获取运单包裹信息接口成功, 无数据");
                result.toError(JdResponse.CODE_OK_NULL, JdResponse.MESSAGE_OK_NULL);
            }else{
                //调用分拣接口获得基础资料信息
                setBasicMessageByDistribution(waybill, startDmsCode, localSchedule, nopaperFlg,startSiteType);
                logger.info("运单号【" + waybillCode + "】调用根据运单号获取运单包裹信息接口成功");
                result.toSuccess();
                context.setWaybill(waybill);
            }
        } catch (Exception e) {
            // 调用服务异常
            logger.error("根据运单号【" + waybillCode + "】 获取运单包裹信息接口 --> 异常", e);
            result.toError(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
        }
        return result;
    }
    /**
     * 获取运单信息
     * @param waybillCode 运单号
     * @param packOpeFlowFlg 是否获取称重信息
     * @return 运单实体
     */
    private Waybill findWaybillMessage(String waybillCode,Integer packOpeFlowFlg) {
        Waybill waybill = this.waybillCommonService.findWaybillAndPack(waybillCode);

        if (waybill == null) {
            return waybill;
        }

        // 获取该运单号的打印记录
        try {
            List<Pack> packs = waybill.getPackList();
            if (packs != null && !packs.isEmpty()) {
                if (BusinessHelper.checkIntNumRange(packs.size())) {
                    List<PopPrint> popPrintList = this.popPrintService.findAllByWaybillCode(waybillCode);
                    for (PopPrint popPrint : popPrintList) {
                        if (Constants.PRINT_PACK_TYPE.equals(popPrint.getOperateType())) {
                            for (int i = 0; i < waybill.getPackList().size(); i++) {
                                if (popPrint.getPackageBarcode().equals(packs.get(i).getPackCode())) {
                                    packs.get(i).setIsPrintPack(Waybill.IS_PRINT_PACK);
                                }
                            }
                        } else if (Constants.PRINT_INVOICE_TYPE.equals(popPrint.getOperateType())) {
                            waybill.setIsPrintInvoice(Waybill.IS_PRINT_INVOICE);
                        }
                    }
                    /**
                     * 获取称重流水，并设置包裹信息pWeight
                     */
                    if(Constants.INTEGER_FLG_TRUE.equals(packOpeFlowFlg)){
                        Map<String,PackOpeFlowDto> packOpeFlows = this.waybillCommonService.getPackOpeFlowsByOpeType(waybillCode,Constants.PACK_OPE_FLOW_TYPE_PSY_REC);
                        if(packOpeFlows!=null&&!packOpeFlows.isEmpty()){
                            for(Pack pack:packs){
                                PackOpeFlowDto packOpeFlow = packOpeFlows.get(pack.getPackCode());
                                if(packOpeFlow!=null&&packOpeFlow.getpWeight()!=null){
                                    pack.setpWeight(packOpeFlow.getpWeight().toString());
                                }
                            }
                        }
                    }
                    this.logger.info("根据运单号【" + waybillCode + "】获取运单包裹信息接口 --> 获取该运单号的打印记录，popPrintList：" + popPrintList);
                } else {
                    this.logger.error("根据运单号【" + waybillCode + "】获取运单包裹信息接口 --> 获取该运单号的打印记录 运单包裹数大于限定值");
                }
            }
        } catch (Exception e) {
            this.logger.error("根据运单号【" + waybillCode + "】获取运单包裹信息接口 --> 调用该运单号的打印记录(数据库)异常：", e);
        }

        // 增加SOP订单EMS全国直发
        if (Constants.POP_SOP_EMS_CODE.equals(waybill.getSiteCode())) {
            waybill.setSiteName(Constants.POP_SOP_EMS_NAME);
        }

        this.setWaybillStatus(waybill);
        return waybill;
    }

    /**
     * 设置打印数据基础信息
     * @param waybill 运单实体
     * @param startDmsCode 始发分拣中心code
     * @param localSchedule 目的站点code
     * @param nopaperFlg  是否无纸化
     * @param startSiteType 始发站点类型
     */
    private void setBasicMessageByDistribution(Waybill waybill, Integer startDmsCode ,Integer localSchedule,Boolean nopaperFlg,Integer startSiteType) {
        try {
            LabelPrintingRequest request = new LabelPrintingRequest();
            BaseResponseIncidental<LabelPrintingResponse> response = new BaseResponseIncidental<LabelPrintingResponse>();
            request.setWaybillCode(waybill.getWaybillCode());
            request.setDmsCode(startDmsCode);
            request.setStartSiteType(startSiteType);
            if (localSchedule!=null && !localSchedule.equals(0))
                request.setLocalSchedule(1);
            else
                request.setLocalSchedule(0);
            request.setCky2(waybill.getCky2());
            request.setOrgCode(waybill.getOrgId());

            //是否航空
            if(checkAireSigns(waybill)){
                //request
                request.setAirTransport(true);
            }

            request.setStoreCode(waybill.getStoreId());

            // 是否调度
            // request.setPreSeparateCode(waybill.getOldCode());
            if (localSchedule!=null && !localSchedule.equals(0))
                request.setPreSeparateCode(localSchedule);// 调度站点

            // 是否DMS调用
            request.setOriginalType(OriginalType.DMS.getValue());

            //是否有纸化
            if(nopaperFlg){
                request.setLabelType(LableType.PAPERLESS.getLabelPaper());
            }else {
                request.setLabelType(LableType.PAPER.getLabelPaper());
            }

            //如果是一号店,那么需要在标签上打出其标志,这里将标志图片名称发到打印端，打印端自行处理图片路径加载
            if(BusinessHelper.isYHD(waybill.getSendPay())){
                request.setBrandImageKey(Constants.BRAND_IMAGE_KEY_YHD);
            }

            response = labelPrinting.dmsPrint(request);

            if(response==null || response.getData()==null){
                this.logger.error("根据运单号【" + waybill.getWaybillCode() + "】 获取预分拣的包裹打印信息为空response对象");
                return;
            }

            LabelPrintingResponse labelPrinting = response.getData();
            if(labelPrinting==null){
                this.logger.error("根据运单号【" + waybill.getWaybillCode() + "】 获取预分拣的包裹打印信息为空labelPrinting对象");
                return;
            }

            if (response != null) {
                waybill.setCrossCode(String.valueOf(labelPrinting.getOriginalCrossCode()));
                waybill.setTrolleyCode(String.valueOf(labelPrinting.getOriginalTabletrolley()));
                waybill.setTargetDmsCode(labelPrinting.getPurposefulDmsCode());
                waybill.setTargetDmsName(String.valueOf(labelPrinting.getPurposefulDmsName()));
                waybill.setTargetDmsDkh(String.valueOf(labelPrinting.getPurposefulCrossCode()));
                waybill.setTargetDmsLch(String.valueOf(labelPrinting.getPurposefulTableTrolley()));
                waybill.setAddress(labelPrinting.getOrderAddress());
                waybill.setJsonData(response.getJsonData());
                waybill.setRoad(labelPrinting.getRoad());

                if(labelPrinting.getRoad()==null|| labelPrinting.getRoad().isEmpty()){
                    this.logger.error("根据运单号【" + waybill.getWaybillCode() + "】 获取预分拣的包裹打印路区信息为空");
                }
            } else {
                this.logger.error("根据运单号【" + waybill.getWaybillCode() + "】 获取预分拣的包裹打印信息为空");
            }
        } catch (Throwable e) {
            this.logger.error("根据运单号【" + waybill.getWaybillCode() + "】 获取预分拣的包裹打印信息接口 --> 异常", e);
        }
    }

    /**
     * 设置运单装态
     * @param waybill 运单实体
     */
    private void setWaybillStatus(Waybill waybill) {
        if (waybill == null || StringUtils.isBlank(waybill.getWaybillCode())) {
            return;
        }

        Boolean isDelivery = waybill.isDelivery();
        if (isDelivery) {
            waybill.setStatusAndMessage(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
        } else {
            waybill.setStatusAndMessage(SortingResponse.CODE_293040, SortingResponse.MESSAGE_293040);
        }

        // 验证运单号，是否锁定、删除等
        com.jd.bluedragon.distribution.fastRefund.domain.WaybillResponse cancelWaybill = null;
        try {
            cancelWaybill = WaybillCancelClient.getWaybillResponse(waybill.getWaybillCode());
        } catch (Exception e) {
            this.logger.error("WaybillResource --> setWaybillStatus get cancelWaybill Error:", e);
        }

        if (cancelWaybill != null) {
            if (SortingResponse.CODE_29300.equals(cancelWaybill.getCode())) {
                if (isDelivery) {
                    waybill.setStatusAndMessage(SortingResponse.CODE_29300, SortingResponse.MESSAGE_29300);
                } else {
                    waybill.setStatusAndMessage(SortingResponse.CODE_293000, SortingResponse.MESSAGE_293000);
                }
            } else if (SortingResponse.CODE_29302.equals(cancelWaybill.getCode())) {
                if (isDelivery) {
                    waybill.setStatusAndMessage(SortingResponse.CODE_29302, SortingResponse.MESSAGE_29302);
                } else {
                    waybill.setStatusAndMessage(SortingResponse.CODE_293020, SortingResponse.MESSAGE_293020);
                }
            } else if (SortingResponse.CODE_29301.equals(cancelWaybill.getCode())) {
                if (isDelivery) {
                    waybill.setStatusAndMessage(SortingResponse.CODE_29301, SortingResponse.MESSAGE_29301);
                } else {
                    waybill.setStatusAndMessage(SortingResponse.CODE_293010, SortingResponse.MESSAGE_293010);
                }
            } else if (SortingResponse.CODE_29303.equals(cancelWaybill.getCode())) {
                waybill.setStatusAndMessage(SortingResponse.CODE_29303, SortingResponse.MESSAGE_29303);
            }
        }
    }

    /**
     * 根据基础资料调用设置航空标识
     * @param waybill 运单数据
     * @return 布尔值
     */
    private boolean checkAireSigns(Waybill waybill) {
        // 设置航空标识
        boolean signs = false;
        if (waybill.getBusiId() != null && !waybill.getBusiId().equals(0)) {
            logger.info("B商家ID-初始分拣中心-目的站点【" + waybill.getBusiId() + "-" + "-" + waybill.getSiteCode() + "】根据基础资料调用设置航空标识");
            signs = this.airTransportService.getAirSigns(waybill.getBusiId());
        }
        return signs;
    }

}
