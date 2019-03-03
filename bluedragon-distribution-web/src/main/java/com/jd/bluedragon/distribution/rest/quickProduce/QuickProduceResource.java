package com.jd.bluedragon.distribution.rest.quickProduce;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.distribution.base.service.AirTransportService;
import com.jd.bluedragon.distribution.quickProduce.domain.QuickProduceWabill;
import com.jd.bluedragon.distribution.quickProduce.service.QuickProduceService;
import com.jd.bluedragon.distribution.waybill.service.LabelPrinting;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.preseparate.util.LableType;
import com.jd.preseparate.util.OriginalType;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by yanghongqiang on 2015/9/10.
 * 快速生产服务类
 */
@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class QuickProduceResource {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    QuickProduceService quickProduceService;

    @Autowired
    private AirTransportService airTransportService;

    @Autowired
    private LabelPrinting labelPrinting;


    /**
     * 根据运单号或包裹号获取运单包裹信息接口
     * 从订单中间键，台帐，外单获取数据
     *
     * @param startDmsCode Or package
     * @return
     */
    @GET
    @Path("/quickProduce/getwaybillPrePack/{startDmsCode}/{waybillCodeOrPackage}/{localSchedule}/{paperless}/{waybillOrLabel}")
    public Waybill getwaybillPrePack(@PathParam("startDmsCode") Integer startDmsCode,
                                                      @PathParam("waybillCodeOrPackage") String waybillCodeOrPackage, @PathParam("localSchedule") Integer localSchedule
            , @PathParam("paperless") Integer paperless,@PathParam("waybillOrLabel") Integer waybillOrLabel) {


        // 转换运单号
        String waybillCode = WaybillUtil.getWaybillCode(waybillCodeOrPackage);
        // 调用服务
        try {

            QuickProduceWabill quickProduceWabill =quickProduceService.getQuickProduceWabill(waybillCode);
            if(quickProduceWabill==null){
                this.logger.info("运单号【" + waybillCode
                        + "】调用订单中间键获取运单包裹信息接口成功, 无数据");
                return new Waybill();
            }
            Waybill waybill = quickProduceWabill.getWaybill();
            if (waybill == null) {
                this.logger.info("运单号【" + waybillCode
                        + "】调用根据运单号获取运单包裹信息接口成功, 无数据");
                return new Waybill();
            }

            if (waybillOrLabel == 1) {
                // 判断传入参数
                if (startDmsCode == null || startDmsCode.equals(0)
                        || StringUtils.isEmpty(waybillCodeOrPackage)) {
                    this.logger.error("根据初始分拣中心-运单号/包裹号【" + startDmsCode + "-"
                            + waybillCodeOrPackage + "】获取运单包裹信息接口 --> 传入参数非法");
                    return new Waybill();
                }
                //调用预分拣接口获得基础资料信息
                this.setBasicMessageByDistribution(waybill, startDmsCode, localSchedule, paperless,1);//1：分拣中心； 2：站点；3：商家
            }

            this.logger.info("运单号【" + waybillCode + "】调用根据运单号获取运单包裹信息接口成功");
            return waybill;

        } catch (Exception e) {
            // 调用服务异常
            this.logger
                    .error("根据运单号【" + waybillCode + "】 获取运单包裹信息接口 --> 异常", e);
            return new Waybill();
        }
    }


    @SuppressWarnings("unused")
    private void setBasicMessageByDistribution(Waybill waybill, Integer startDmsCode ,Integer localSchedule,Integer paperless,Integer startSiteType) {
        try {
            com.jd.bluedragon.distribution.waybill.domain.LabelPrintingRequest request = new com.jd.bluedragon.distribution.waybill.domain.LabelPrintingRequest();
            com.jd.bluedragon.distribution.waybill.domain.BaseResponseIncidental<com.jd.bluedragon.distribution.waybill.domain.LabelPrintingResponse> response = new com.jd.bluedragon.distribution.waybill.domain.BaseResponseIncidental<com.jd.bluedragon.distribution.waybill.domain.LabelPrintingResponse>();
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
            if(paperless.equals(LableType.PAPER.getLabelPaper()))
                request.setLabelType(LableType.PAPER.getLabelPaper());
            else
                request.setLabelType(LableType.PAPERLESS.getLabelPaper());

            response = labelPrinting.dmsPrint(request);

            if(response==null || response.getData()==null){
                //
                this.logger.error("根据运单号【" + waybill.getWaybillCode()
                        + "】 获取预分拣的包裹打印信息为空response对象");
                return;
            }

            com.jd.bluedragon.distribution.waybill.domain.LabelPrintingResponse labelPrinting = response.getData();
            if(labelPrinting==null){
                this.logger.error("根据运单号【" + waybill.getWaybillCode()
                        + "】 获取预分拣的包裹打印信息为空labelPrinting对象");
                return;
            }

            waybill.setCrossCode(String.valueOf(labelPrinting
                    .getOriginalCrossCode()));
            waybill.setTrolleyCode(String.valueOf(labelPrinting
                    .getOriginalTabletrolley()));
            waybill.setTargetDmsCode(labelPrinting.getPurposefulDmsCode());
            waybill.setTargetDmsName(String.valueOf(labelPrinting
                    .getPurposefulDmsName()));
            waybill.setTargetDmsDkh(String.valueOf(labelPrinting
                    .getPurposefulCrossCode()));
            waybill.setTargetDmsLch(String.valueOf(labelPrinting
                    .getPurposefulTableTrolley()));
            waybill.setAddress(labelPrinting.getOrderAddress());
            waybill.setJsonData(response.getJsonData());
            waybill.setRoad(labelPrinting.getRoad());

            if(labelPrinting.getRoad()==null|| labelPrinting.getRoad().isEmpty()){
                this.logger.error("根据运单号【" + waybill.getWaybillCode()
                        + "】 获取预分拣的包裹打印路区信息为空");
            }
        } catch (Exception e) {
            this.logger.error("根据运单号【" + waybill.getWaybillCode()
                    + "】 获取预分拣的包裹打印信息接口 --> 异常", e);
        } catch(Throwable ee) {
            this.logger.error("根据运单号【" + waybill.getWaybillCode()
                    + "】 获取预分拣的包裹打印信息接口 --> 异常", ee);
        }
    }


    private boolean checkAireSigns(Waybill waybill) {
        // 设置航空标识
        boolean signs = false;
        if (waybill.getBusiId() != null && !waybill.getBusiId().equals(0)) {
            this.logger.info("B商家ID-初始分拣中心-目的站点【" + waybill.getBusiId() + "-"
                    + "-" + waybill.getSiteCode()
                    + "】根据基础资料调用设置航空标识开始");
            signs = this.airTransportService.getAirSigns(
                    waybill.getBusiId());
            this.logger.info("B商家ID-初始分拣中心-目的站点【" + waybill.getBusiId() + "-"
                    + "-" + waybill.getSiteCode()
                    + "】根据基础资料调用设置航空标识结束【" + waybill.getAirSigns() + "】");
        }
        return signs;
    }


}


