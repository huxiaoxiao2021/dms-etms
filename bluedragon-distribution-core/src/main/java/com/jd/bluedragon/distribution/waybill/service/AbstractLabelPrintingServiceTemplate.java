package com.jd.bluedragon.distribution.waybill.service;

import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.ldop.basic.dto.BasicTraderInfoDTO;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintContext;
import com.jd.bluedragon.distribution.waybill.domain.BaseResponseIncidental;
import com.jd.bluedragon.distribution.waybill.domain.LabelPrintingRequest;
import com.jd.bluedragon.distribution.waybill.domain.LabelPrintingResponse;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ql.basic.domain.BaseDmsStore;
import com.jd.ql.basic.domain.CrossPackageTagNew;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.ws.BasicSecondaryWS;


/**
 * Created by yanghongqiang on 2015/11/30.
 */
public abstract class AbstractLabelPrintingServiceTemplate implements LabelPrintingService {

    private static final Logger log = LoggerFactory.getLogger(AbstractLabelPrintingServiceTemplate.class);

    public static final String LOG_PREFIX="包裹标签打印模板[AbstractLabelPrintingServiceTemplate] ";

    @Autowired
	WaybillQueryManager waybillQueryManager;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private BaseMinorManager baseMinorManager;

    @Autowired
    private BasicSecondaryWS basicSecondaryWS;
    
    @Autowired
    private WaybillCommonService waybillCommonService;
    
    /**
     * 收件人联系方式需要突出显示的位数
     */
    private static final int PHONE_HIGHLIGHT_NUMBER = 4;

    /** 运单号突出显示的位数 **/
    private static final int WAYBILL_CODE_HIGHLIGHT_NUMBER = 4;
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
            Waybill waybill,boolean isNew);

    /**
     * 打印主要方法
     * @param request
     * @return
     */
    public BaseResponseIncidental<LabelPrintingResponse> packageLabelPrint(LabelPrintingRequest request){

        BaseResponseIncidental<LabelPrintingResponse> response = new BaseResponseIncidental<LabelPrintingResponse>();

        //初始化运单数据
        LabelPrintingResponse labelPrinting = initWaybillInfo(request, null);
        
        //运单没有数据，不用打印包裹标签
        if(labelPrinting==null){
            return new BaseResponseIncidental<LabelPrintingResponse>(LabelPrintingResponse.CODE_EMPTY_WAYBILL,LabelPrintingResponse.MESSAGE_EMPTY_WAYBILL+"(运单)");
        }

        //现场调度标识
        if(LabelPrintingService.LOCAL_SCHEDULE.equals(request.getLocalSchedule())){
            //特殊标识 追加"调"字
        	labelPrinting.appendSpecialMark(LabelPrintingService.SPECIAL_MARK_LOCAL_SCHEDULE);
            //反调度设置路区为0
            labelPrinting.setRoad("0");
            labelPrinting.setRoadCode("0");
        }

        if(labelPrinting.getPrepareSiteCode()!=null && labelPrinting.getPrepareSiteCode().equals(-1)){
            return new BaseResponseIncidental<LabelPrintingResponse>(LabelPrintingResponse.CODE_EMPTY_SITE,LabelPrintingResponse.MESSAGE_EMPTY_SITE,labelPrinting,JsonHelper.toJson(labelPrinting));
        }

        response = processByBase(request,labelPrinting,response);


        return response;
    }

    /**
     * 打印主要方法，有二次预分拣逻辑（旧的保留不变）
     * @param request
     * @return
     */
    public BaseResponseIncidental<LabelPrintingResponse> packageLabelPrint(LabelPrintingRequest request, WaybillPrintContext context){

        BaseResponseIncidental<LabelPrintingResponse> response = new BaseResponseIncidental<LabelPrintingResponse>();

        //初始化运单数据
        LabelPrintingResponse labelPrinting = initWaybillInfo(request, context);

        //运单没有数据，不用打印包裹标签
        if(labelPrinting==null){
            return new BaseResponseIncidental<LabelPrintingResponse>(LabelPrintingResponse.CODE_EMPTY_WAYBILL,LabelPrintingResponse.MESSAGE_EMPTY_WAYBILL+"(运单)");
        }

        //现场调度标识
        if(LabelPrintingService.LOCAL_SCHEDULE.equals(request.getLocalSchedule())){
            //特殊标识 追加"调"字
        	labelPrinting.appendSpecialMark(LabelPrintingService.SPECIAL_MARK_LOCAL_SCHEDULE);
            //反调度设置路区为0
            labelPrinting.setRoad("0");
            labelPrinting.setRoadCode("0");
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
        	JdResult<CrossPackageTagNew> jdResult = baseMinorManager.queryCrossPackageTagForPrint(baseDmsStore,labelPrinting.getPrepareSiteCode(),request.getDmsCode(),labelPrinting.getOriginalCrossType());
            if(jdResult.isSucceed()) {
            	crossPackageTag = jdResult.getData();
            }else{
            	log.warn("打印业务：获取滑道号笼车号信息为空:{}", jdResult.getMessage());
            }
        }

        if(crossPackageTag==null){
            log.warn(LOG_PREFIX+" 无法获取包裹打印数据{}", request.getWaybillCode());
            if(StringHelper.isEmpty(labelPrinting.getPrepareSiteName())){
                labelPrinting.setPrepareSiteName(getBaseSite(labelPrinting.getPrepareSiteCode()));
                labelPrinting.setPrintSiteName(getBaseSite(labelPrinting.getPrepareSiteCode()));
            }
            return new BaseResponseIncidental<LabelPrintingResponse>(LabelPrintingResponse.CODE_EMPTY_BASE,LabelPrintingResponse.MESSAGE_EMPTY_BASE+"(crossPackageTag打印数据)"
                    ,labelPrinting,JsonHelper.toJson(labelPrinting));
        }
        log.info(LOG_PREFIX + "基础资料crossPackageTag:{}",crossPackageTag.toString());

        //如果是自提柜，则打印的是自提柜的地址(基础资料大全表)，而非客户地址(运单系统)
        if(LabelPrintingService.ARAYACAK_CABINET.equals(crossPackageTag.getIsZiTi())){
        	labelPrinting.appendSpecialMark(LabelPrintingService.SPECIAL_MARK_ARAYACAK_CABINET);
            labelPrinting.setPrintAddress(crossPackageTag.getPrintAddress());
        }

        //目的站点
        if(request.getPreSeparateName()==null){
            labelPrinting.setPrepareSiteName(
                    StringHelper.isEmpty(crossPackageTag.getPrintSiteName())?this.getBaseSite(labelPrinting.getPrepareSiteCode()):crossPackageTag.getPrintSiteName()
            );
            labelPrinting.setPrintSiteName(StringHelper.isEmpty(crossPackageTag.getPrintSiteName())?this.getBaseSite(labelPrinting.getPrepareSiteCode()):crossPackageTag.getPrintSiteName());
        }

        //起始分拣中心
        labelPrinting.setOriginalDmsCode(crossPackageTag.getOriginalDmsId());
        labelPrinting.setOriginalDmsName(crossPackageTag.getOriginalDmsName());
        labelPrinting.setPurposefulDmsCode(crossPackageTag.getDestinationDmsId());
        labelPrinting.setPurposefulDmsName(crossPackageTag.getDestinationDmsName());
        labelPrinting.setDestinationDmsName(crossPackageTag.getDestinationDmsName());
        //笼车号
        labelPrinting.setOriginalTabletrolley(crossPackageTag.getOriginalTabletrolleyCode());
        labelPrinting.setOriginalTabletrolleyCode(crossPackageTag.getOriginalTabletrolleyCode());
        labelPrinting.setPurposefulTableTrolley(crossPackageTag.getDestinationTabletrolleyCode());
        labelPrinting.setDestinationTabletrolleyCode(crossPackageTag.getDestinationTabletrolleyCode());
        //道口号
        labelPrinting.setOriginalCrossCode(crossPackageTag.getOriginalCrossCode());
        labelPrinting.setPurposefulCrossCode(crossPackageTag.getDestinationCrossCode());
        labelPrinting.setDestinationCrossCode(crossPackageTag.getDestinationCrossCode());

        com.jd.bluedragon.common.domain.Waybill waybill = waybillCommonService.findByWaybillCode(request.getWaybillCode());
        if(waybill!=null&&waybill.getWaybillSign()!=null){
            if(BusinessUtil.isSignChar(waybill.getWaybillSign(),31,'3')){
                labelPrinting.setOriginalDmsCode(null);
                labelPrinting.setOriginalDmsName("");
                labelPrinting.setPurposefulDmsCode(null);
                labelPrinting.setPurposefulDmsName("");
                labelPrinting.setDestinationDmsName("");
                //笼车号
                labelPrinting.setOriginalTabletrolley("");
                labelPrinting.setOriginalTabletrolleyCode("");
                labelPrinting.setPurposefulTableTrolley("");
                labelPrinting.setDestinationTabletrolleyCode("");
                //道口号
                labelPrinting.setOriginalCrossCode("");
                labelPrinting.setPurposefulCrossCode("");
                labelPrinting.setDestinationCrossCode("");
            }
        }

        response.setData(labelPrinting);
        response.setJsonData(JsonHelper.toJson(labelPrinting));

        response.setCode(JdResponse.CODE_OK);
        response.setMessage(JdResponse.MESSAGE_OK);

        return response;
    }

    /**
     * 查询运单接口
     * @param request
     * @return
     */
    public LabelPrintingResponse initWaybillInfo(LabelPrintingRequest request, WaybillPrintContext context){
    	BigWaybillDto bigWaybillDto = null;
    	boolean isNew = false;
    	//旧打印逻辑，context为null
    	//先从context不为空，先context中获取原运单数据，否则调取运单接口
        if(context != null){
        	bigWaybillDto = context.getBigWaybillDto();
        	isNew = true;
        }else{
	        /**查询运单*/
            BaseEntity<BigWaybillDto> entity = waybillQueryManager.getWaybillDataForPrint(request.getWaybillCode());
	        if(entity==null || entity.getData()==null){
	            log.warn(LOG_PREFIX+" 没有获取运单数据(BaseEntity<BigWaybillDto>):{}",request.getWaybillCode());
	            return null;
	        }
            bigWaybillDto = entity.getData();
        }
        if(bigWaybillDto==null){
        	log.warn(LOG_PREFIX+" 没有获取运单数据(BaseEntity<BigWaybillDto>):{}",request.getWaybillCode());
        	return null;
        }
        Waybill waybill = bigWaybillDto.getWaybill();
        if(waybill==null){
            log.warn(LOG_PREFIX+" 没有获取运单数据(waybill):{}",request.getWaybillCode());
            return null;
        }
        if(context != null && context.getWaybill() != null && InterceptResult.STATUS_WEAK_PASSED.equals(context.getStatus())){//二次预分拣时重置目的站点和路区
            waybill.setOldSiteId(context.getWaybill().getSiteCode());
            waybill.setSiteName(context.getWaybill().getSiteName());
            waybill.setRoadCode(context.getWaybill().getRoad());
        }

        BasicTraderInfoDTO dto = baseMinorManager.getBaseTraderById(waybill.getBusiId());

        LabelPrintingResponse labelPrinting = new LabelPrintingResponse(request.getWaybillCode());
        if(context != null){
        	context.setBasePrintWaybill(labelPrinting);
        	context.setLabelPrintingResponse(labelPrinting);
        }
        //B网面单要求将运单号后四位突出显示
        String waybillCode = request.getWaybillCode();
        if(StringUtils.isNotBlank(waybillCode) && waybillCode.length()>=WAYBILL_CODE_HIGHLIGHT_NUMBER) {
            labelPrinting.setWaybillCodeFirst(waybillCode.substring(0,waybillCode.length()-WAYBILL_CODE_HIGHLIGHT_NUMBER));
            labelPrinting.setWaybillCodeLast(waybillCode.substring(waybillCode.length()-WAYBILL_CODE_HIGHLIGHT_NUMBER));
        }
        //订单号
        labelPrinting.setOrderCode(waybill.getVendorId());
        //商家编码
        labelPrinting.setBusiCode(dto!=null?dto.getTraderCode():null);
        labelPrinting.setOriginalDmsCode(request.getDmsCode());
        labelPrinting.setOriginalDmsName(request.getDmsName());
        labelPrinting.setOriginalCrossType(BusinessUtil.getOriginalCrossType(waybill.getWaybillSign(), waybill.getSendPay()));
        //扩展追加字段方法
        labelPrinting = waybillExtensional(request,labelPrinting,waybill,isNew);

        //超区
        if (LabelPrintingService.PREPARE_SITE_CODE_OVER_AREA.equals(labelPrinting.getPrepareSiteCode())) {
            labelPrinting.setPrepareSiteCode(LabelPrintingService.PREPARE_SITE_CODE_OVER_AREA);
            labelPrinting.setPrepareSiteName(LabelPrintingService.PREPARE_SITE_NAME_OVER_AREA);
            labelPrinting.setPrintSiteName(LabelPrintingService.PREPARE_SITE_NAME_OVER_AREA);
            log.warn(LOG_PREFIX+" 没有获取预分拣站点(-2超区):{}",request.getWaybillCode());
            //未定位门店
        } else if(labelPrinting.getPrepareSiteCode()==null || (labelPrinting.getPrepareSiteCode()<=LabelPrintingService.PREPARE_SITE_CODE_NOTHING && labelPrinting.getPrepareSiteCode() > LabelPrintingService.PREPARE_SITE_CODE_OVER_LINE)){
            labelPrinting.setPrepareSiteCode(LabelPrintingService.PREPARE_SITE_CODE_NOTHING);
            labelPrinting.setPrepareSiteName(LabelPrintingService.PREPARE_SITE_NAME_NOTHING);
            labelPrinting.setPrintSiteName(LabelPrintingService.PREPARE_SITE_NAME_NOTHING);
            log.warn(LOG_PREFIX+" 没有获取预分拣站点(未定位门店):{}",request.getWaybillCode());
        } else if(labelPrinting.getPrepareSiteCode() !=null && labelPrinting.getPrepareSiteCode().intValue() < LabelPrintingService.PREPARE_SITE_CODE_OVER_LINE){
            //新细分超区
            labelPrinting.setPrepareSiteName(LabelPrintingService.PREPARE_SITE_NAME_OVER_AREA);
            labelPrinting.setPrintSiteName(LabelPrintingService.PREPARE_SITE_NAME_OVER_AREA);
            log.warn(LOG_PREFIX+" 没有获取预分拣站点(细分超区):{},{}",labelPrinting.getPrepareSiteCode(),request.getWaybillCode());
        }

        //EMS全国直发
        if(labelPrinting.getPrepareSiteCode()!=null && labelPrinting.getPrepareSiteCode().equals(LabelPrintingService.PREPARE_SITE_CODE_EMS_DIRECT)){
            labelPrinting.setPrepareSiteName(LabelPrintingService.PREPARE_SITE_NAME_EMS_DIRECT);
            labelPrinting.setPrintSiteName(LabelPrintingService.PREPARE_SITE_NAME_EMS_DIRECT);
        }

        labelPrinting.setCustomerName(waybill.getReceiverName());

        String contactTelephone = StringHelper.isEmpty(waybill.getReceiverTel())?null:waybill.getReceiverTel();
        String contactMobilePhone = StringHelper.isEmpty(waybill.getReceiverMobile())?"":waybill.getReceiverMobile();

        labelPrinting.setCustomerContacts( StringHelper.isEmpty(contactTelephone)?contactMobilePhone:(contactTelephone+","+contactMobilePhone));

        //分段设置收件人联系方式，手机号和座机号的后四位加大、标红显示
        String receiverMobile = waybill.getReceiverMobile();
        String receiverTel = waybill.getReceiverTel();
        if (StringHelper.isNotEmpty(receiverMobile) && receiverMobile.length() >= PHONE_HIGHLIGHT_NUMBER) {
            labelPrinting.setMobileFirst(receiverMobile.substring(0, receiverMobile.length() - PHONE_HIGHLIGHT_NUMBER));
            labelPrinting.setMobileLast(receiverMobile.substring(receiverMobile.length() - PHONE_HIGHLIGHT_NUMBER));
        }
        if (StringHelper.isNotEmpty(receiverTel) && receiverTel.length() >= PHONE_HIGHLIGHT_NUMBER) {
            labelPrinting.setTelFirst(receiverTel.substring(0, receiverTel.length() - PHONE_HIGHLIGHT_NUMBER));
            labelPrinting.setTelLast(receiverTel.substring(receiverTel.length() - PHONE_HIGHLIGHT_NUMBER));
        }

        //支付方式为在线支付，金额显示在线支付；货到付款，金额显示具体金额
        labelPrinting.setPackagePrice(waybill.getCodMoney());
        if(waybill.getPayment()!=null){
            if(waybill.getPayment()==LabelPrintingService.ONLINE_PAYMENT_SIGN){
                labelPrinting.setPackagePrice(LabelPrintingService.ONLINE_PAYMENT);
            }
        }

        //路区
        labelPrinting.setRoad(StringHelper.isEmpty(waybill.getRoadCode())?"0":waybill.getRoadCode());
        labelPrinting.setRoadCode(StringHelper.isEmpty(waybill.getRoadCode())?"0":waybill.getRoadCode());

        // labelPrinting.setBusiOrderCode(waybill.getBusiOrderCode());
        waybillCommonService.setBasePrintInfoByWaybill(labelPrinting,waybill);
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
