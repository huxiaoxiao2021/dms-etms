package com.jd.bluedragon.distribution.waybill.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.waybill.domain.BaseResponseIncidental;
import com.jd.bluedragon.distribution.waybill.domain.LabelPrintingRequest;
import com.jd.bluedragon.distribution.waybill.domain.LabelPrintingResponse;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.api.WaybillQueryApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.ql.basic.domain.BaseDmsStore;
import com.jd.ql.basic.domain.BaseResult;
import com.jd.ql.basic.domain.CrossPackageTagNew;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.fce.dos.service.contract.OrderMarkingService;
import com.jd.fce.dos.service.domain.OrderMarkingForeignRequest;
import com.jd.fce.dos.service.domain.OrderMarkingForeignResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * Created by yanghongqiang on 2015/11/30.
 */
public abstract class AbstractLabelPrintingServiceTemplate implements LabelPrintingService {

    private static final Logger log = Logger.getLogger(AbstractLabelPrintingServiceTemplate.class);

    public static final String LOG_PREFIX="包裹标签打印模板[AbstractLabelPrintingServiceTemplate] ";

    @Autowired
	WaybillQueryApi waybillQueryApi;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private BaseMinorManager baseMinorManager;

    @Autowired
    private OrderMarkingService orderMarkingService;

    /**
     * 初始化基础资料对象
     */
    public abstract BaseDmsStore initBaseVo(LabelPrintingRequest request);

    /**
     * 包裹标签在运单范围内的扩展
     * @param request
     * @param labelPrinting
     * @param waybill
     * @return
     */
    public abstract LabelPrintingResponse waybillExtensional(
            LabelPrintingRequest request, LabelPrintingResponse labelPrinting,
            Waybill waybill);

    /**
     * 打印主要方法
     * @param request
     * @return
     */
    public BaseResponseIncidental<LabelPrintingResponse> packageLabelPrint(LabelPrintingRequest request){

        BaseResponseIncidental<LabelPrintingResponse> response = new BaseResponseIncidental<LabelPrintingResponse>();

        //初始化运单数据
        LabelPrintingResponse labelPrinting = initWaybillInfo(request);

        //运单没有数据，不用打印包裹标签
        if(labelPrinting==null){
            return new BaseResponseIncidental<LabelPrintingResponse>(LabelPrintingResponse.CODE_EMPTY_WAYBILL,LabelPrintingResponse.MESSAGE_EMPTY_WAYBILL+"(运单)");
        }

        //现场调度标识
        if(request.getLocalSchedule()!=null && request.getLocalSchedule()==LabelPrintingService.LOCAL_SCHEDULE){
            //特殊标识 追加"调"字
            if (!StringHelper.isEmpty(labelPrinting.getSpecialMark())) {
                StringBuffer sb = new StringBuffer(labelPrinting.getSpecialMark());
                sb.append(LabelPrintingService.SPECIAL_MARK_LOCAL_SCHEDULE);
                labelPrinting.setSpecialMark(sb.toString());
            }else{
                labelPrinting.setSpecialMark(LabelPrintingService.SPECIAL_MARK_LOCAL_SCHEDULE);
            }
            //反调度设置路区为0
            labelPrinting.setRoad("0");
        }

        if(labelPrinting.getPrepareSiteCode()!=null && labelPrinting.getPrepareSiteCode().equals(-1)){
            return new BaseResponseIncidental<LabelPrintingResponse>(LabelPrintingResponse.CODE_EMPTY_SITE,LabelPrintingResponse.MESSAGE_EMPTY_SITE,labelPrinting,JsonHelper.toJson(labelPrinting));
        }

        response = processByBase(request,labelPrinting,response);


        return response;
    }

    /**
     * 查询基础资料完善数据
     * @param request
     * @param labelPrinting
     */
    private BaseResponseIncidental<LabelPrintingResponse> processByBase(LabelPrintingRequest request, LabelPrintingResponse labelPrinting,BaseResponseIncidental<LabelPrintingResponse> response) {

        //模板方法，加载基础资料所需的属性，各个打印源不同
        BaseDmsStore baseDmsStore = initBaseVo(request);

        CrossPackageTagNew crossPackageTag = null;

        //如果预分拣站点为0超区或者999999999EMS全国直发，则不用查询大全表
        if(labelPrinting.getPrepareSiteCode()>LabelPrintingService.PREPARE_SITE_CODE_NOTHING && !labelPrinting.getPrepareSiteCode().equals(LabelPrintingService.PREPARE_SITE_CODE_EMS_DIRECT)){
            BaseResult<CrossPackageTagNew> baseResult = baseMinorManager.getCrossPackageTagByPara(baseDmsStore, labelPrinting.getPrepareSiteCode(), request.getDmsCode());
            if(! (BaseResult.SUCCESS==baseResult.getResultCode()) ){
                log.error(" 获取基础资料包裹信息失败[getCrossPackageTagByPara],返回码 "+baseResult.getResultCode());
                return null;
            }

            crossPackageTag = baseResult.getData();
            if(crossPackageTag == null){
                log.error(" 获取基础资料包裹信息失败[getCrossPackageTagByPara],crossPackageTag为空");
                return null;
            }
        }

        if(crossPackageTag==null){
            log.error(LOG_PREFIX+" 无法获取包裹打印数据"+request.getWaybillCode());
            if(StringHelper.isEmpty(labelPrinting.getPrepareSiteName())){
                labelPrinting.setPrepareSiteName(getBaseSite(labelPrinting.getPrepareSiteCode()));
            }
            return new BaseResponseIncidental<LabelPrintingResponse>(LabelPrintingResponse.CODE_EMPTY_BASE,LabelPrintingResponse.MESSAGE_EMPTY_BASE+"(crossPackageTag打印数据)"
                    ,labelPrinting,JsonHelper.toJson(labelPrinting));
        }
        log.info(new StringBuilder(LOG_PREFIX).append("基础资料crossPackageTag").append(crossPackageTag.toString()));
        StringBuilder specialMark = new StringBuilder(StringHelper.isEmpty(labelPrinting.getSpecialMark())?"":labelPrinting.getSpecialMark());
        //航空标识
        if(crossPackageTag.getIsAirTransport()!=null && crossPackageTag.getIsAirTransport()==LabelPrintingService.AIR_TRANSPORT && request.isAirTransport()){
            specialMark.append(LabelPrintingService.SPECIAL_MARK_AIRTRANSPORT);
        }
        //如果是自提柜，则打印的是自提柜的地址(基础资料大全表)，而非客户地址(运单系统)
        if(crossPackageTag.getIsZiTi().equals(LabelPrintingService.ARAYACAK_CABINET)){
            specialMark.append(LabelPrintingService.SPECIAL_MARK_ARAYACAK_CABINET);
            labelPrinting.setPrintAddress(crossPackageTag.getPrintAddress());
        }

        //目的站点
        if(request.getPreSeparateName()==null){
            labelPrinting.setPrepareSiteName(
                    StringHelper.isEmpty(crossPackageTag.getPrintSiteName())?this.getBaseSite(labelPrinting.getPrepareSiteCode()):crossPackageTag.getPrintSiteName()
            );
        }

        labelPrinting.setSpecialMark(specialMark.toString());
        //起始分拣中心
        labelPrinting.setOriginalDmsCode(crossPackageTag.getOriginalDmsId());
        labelPrinting.setOriginalDmsName(crossPackageTag.getOriginalDmsName());
        labelPrinting.setPurposefulDmsCode(crossPackageTag.getDestinationDmsId());
        labelPrinting.setPurposefulDmsName(crossPackageTag.getDestinationDmsName());
        //笼车号
        labelPrinting.setOriginalTabletrolley(crossPackageTag.getOriginalTabletrolleyCode());
        labelPrinting.setPurposefulTableTrolley(crossPackageTag.getDestinationTabletrolleyCode());
        //道口号
        labelPrinting.setOriginalCrossCode(crossPackageTag.getOriginalCrossCode());
        labelPrinting.setPurposefulCrossCode(crossPackageTag.getDestinationCrossCode());

        response.setData(labelPrinting);
        response.setJsonData(JsonHelper.toJson(labelPrinting));



        log.info(LOG_PREFIX+" jsonData=="+response.getJsonData());
        response.setCode(JdResponse.CODE_OK);
        response.setMessage(JdResponse.MESSAGE_OK);

        return response;
    }

    /**
     * 查询运单接口
     * @param request
     * @return
     */
    public LabelPrintingResponse initWaybillInfo(LabelPrintingRequest request){
        /**查询运单*/
        WChoice wchoice = new WChoice();
        wchoice.setQueryWaybillC(true);
        wchoice.setQueryWaybillE(true);
        BaseEntity<BigWaybillDto> entity = waybillQueryApi.getDataByChoice(request.getWaybillCode(), wchoice);
        if(entity==null || entity.getData()==null){
            log.error(LOG_PREFIX+" 没有获取运单数据(BaseEntity<BigWaybillDto>)"+request.getWaybillCode());
            return null;
        }

        Waybill waybill = entity.getData().getWaybill();
        if(waybill==null){
            log.error(LOG_PREFIX+" 没有获取运单数据(waybill)"+request.getWaybillCode());
            return null;
        }

        LabelPrintingResponse labelPrinting = new LabelPrintingResponse(request.getWaybillCode());

        //订单号
        labelPrinting.setOrderCode(waybill.getVendorId());

        labelPrinting.setOriginalDmsCode(request.getDmsCode());
        labelPrinting.setOriginalDmsName(request.getDmsName());

        //扩展追加字段方法
        labelPrinting = waybillExtensional(request,labelPrinting,waybill);

        //超区
        if (LabelPrintingService.PREPARE_SITE_CODE_OVER_AREA.equals(labelPrinting.getPrepareSiteCode())) {
            labelPrinting.setPrepareSiteCode(LabelPrintingService.PREPARE_SITE_CODE_OVER_AREA);
            labelPrinting.setPrepareSiteName(LabelPrintingService.PREPARE_SITE_NAME_OVER_AREA);
            log.error(LOG_PREFIX+" 没有获取预分拣站点(-2超区),"+request.getWaybillCode());
            //未定位门店
        } else if(labelPrinting.getPrepareSiteCode()==null || (labelPrinting.getPrepareSiteCode()<=LabelPrintingService.PREPARE_SITE_CODE_NOTHING && labelPrinting.getPrepareSiteCode() > LabelPrintingService.PREPARE_SITE_CODE_OVER_LINE)){
            labelPrinting.setPrepareSiteCode(LabelPrintingService.PREPARE_SITE_CODE_NOTHING);
            labelPrinting.setPrepareSiteName(LabelPrintingService.PREPARE_SITE_NAME_NOTHING);
            log.error(LOG_PREFIX+" 没有获取预分拣站点(未定位门店),"+request.getWaybillCode());
        } else if(labelPrinting.getPrepareSiteCode() !=null && labelPrinting.getPrepareSiteCode().intValue() < LabelPrintingService.PREPARE_SITE_CODE_OVER_LINE){
            //新细分超区
            labelPrinting.setPrepareSiteCode(labelPrinting.getPrepareSiteCode());
            labelPrinting.setPrepareSiteName(LabelPrintingService.PREPARE_SITE_NAME_OVER_AREA);
            log.error(LOG_PREFIX+" 没有获取预分拣站点(细分超区)," + labelPrinting.getPrepareSiteCode() + ","+request.getWaybillCode());
        }

        //EMS全国直发
        if(labelPrinting.getPrepareSiteCode()!=null && labelPrinting.getPrepareSiteCode().equals(LabelPrintingService.PREPARE_SITE_CODE_EMS_DIRECT)){
            labelPrinting.setPrepareSiteName(LabelPrintingService.PREPARE_SITE_NAME_EMS_DIRECT);
        }

        labelPrinting.setCustomerName(waybill.getReceiverName());

        String contactTelephone = StringHelper.isEmpty(waybill.getReceiverTel())?null:waybill.getReceiverTel();
        String contactMobilePhone = StringHelper.isEmpty(waybill.getReceiverMobile())?"":waybill.getReceiverMobile();

        labelPrinting.setCustomerContacts( StringHelper.isEmpty(contactTelephone)?contactMobilePhone:(contactTelephone+","+contactMobilePhone));

        //支付方式为在线支付，金额显示在线支付；货到付款，金额显示具体金额
        labelPrinting.setPackagePrice(waybill.getCodMoney());
        if(waybill.getPayment()!=null){
            if(waybill.getPayment()==LabelPrintingService.ONLINE_PAYMENT_SIGN){
                labelPrinting.setPackagePrice(LabelPrintingService.ONLINE_PAYMENT);
            }
        }

        //路区
        labelPrinting.setRoad(StringHelper.isEmpty(waybill.getRoadCode())?"0":waybill.getRoadCode());

        try {
            if (request!=null&&request.getStartSiteType()!=null
                    &&waybill!=null&&StringHelper.isNotEmpty(request.getWaybillCode())
                    && SerialRuleUtil.isMatchReceiveWaybillNo(request.getWaybillCode())
                    &&(waybill.getWaybillType()!=Constants.ORDER_TYPE_B&& NumberHelper.isNumber(waybill.getVendorId()))) {

                log.warn("调用promise获取外单时效开始");

                OrderMarkingForeignRequest orderMarkingRequest = new OrderMarkingForeignRequest();
                if (waybill.getWaybillType() == Constants.ORDER_TYPE_B)
                    orderMarkingRequest.setOrderId(0);//纯外单订单号设置为0
                else
                    orderMarkingRequest.setOrderId(Long.parseLong(waybill.getVendorId()));//订单号
                orderMarkingRequest.setWaybillCode(waybill.getWaybillCode());//运单号
                orderMarkingRequest.setOpeSiteId(request.dmsCode.toString());//分拣中心ID
                orderMarkingRequest.setOpeSiteName(request.dmsName);//分拣中心名称

                orderMarkingRequest.setOpesiteType(request.getStartSiteType() == 64 ? 1 : request.getStartSiteType() == 4 ? 2 : 3); //1：分拣中心； 2：站点；3：商家
                orderMarkingRequest.setSource("11");//分拣中心环节 11
                orderMarkingRequest.setProvinceId(waybill.getProvinceId()==null?0:waybill.getProvinceId());//省
                orderMarkingRequest.setCityId(waybill.getCityId()==null?0:waybill.getCityId());//市
                orderMarkingRequest.setCountyId(waybill.getCountryId()==null?0:waybill.getCountryId());//县
                orderMarkingRequest.setTownId(waybill.getTownId()==null?0:waybill.getTownId());//镇
                orderMarkingRequest.setCurrentDate(new Date());//当前时间
//                }
                log.warn("调用promise获取外单时效传入参数" + orderMarkingRequest == null ? "" : JsonHelper.toJson(orderMarkingRequest));
                OrderMarkingForeignResponse orderMarkingForeignResponse = orderMarkingService.orderMarkingServiceForForeign(orderMarkingRequest);
                if (orderMarkingForeignResponse != null && orderMarkingForeignResponse.getResultCode() >= 1) {
                    labelPrinting.setPromiseText(orderMarkingForeignResponse.getPromiseMsg());
                    labelPrinting.setTimeCategory(orderMarkingForeignResponse.getSendpayDesc());
                } else {
                    log.error("调用promise接口获取外单时效失败：" + orderMarkingForeignResponse == null ? "" : JsonHelper.toJson(orderMarkingForeignResponse));
                }
                log.warn("调用promise获取外单时效返回数据" + orderMarkingForeignResponse == null ? "" : JsonHelper.toJson(orderMarkingForeignResponse));

            }//外单增加promise时效代码逻辑,包裹标签业务是核心业务，如果promise接口异常，仍要保证包裹标签业务。
        }catch (Exception e){
            log.error("外单调用promise接口异常" + e.toString() + (request == null ? "" : JsonHelper.toJson(request)));
        }
        return labelPrinting;
    }

    /**
     * 查询基础资料
     * @param prepareSiteCode
     * @return
     */
    private String getBaseSite(Integer prepareSiteCode) {
        log.info(LOG_PREFIX+"查询基础资料获取站点名称接口");

        BaseStaffSiteOrgDto baseStaffSite =baseMajorManager.getBaseSiteBySiteId(prepareSiteCode);
//        BaseStaffSiteOrgDto baseStaffSite = commonBasicFacade.getBaseSiteBySiteId(prepareSiteCode);
        if(baseStaffSite==null) return "";
        return baseStaffSite.getSiteName();
    }

}
