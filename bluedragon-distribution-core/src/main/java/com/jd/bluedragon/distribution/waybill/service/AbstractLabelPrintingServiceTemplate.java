package com.jd.bluedragon.distribution.waybill.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.service.WaybillPrintService;
import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.waybill.domain.BaseResponseIncidental;
import com.jd.bluedragon.distribution.waybill.domain.LabelPrintingRequest;
import com.jd.bluedragon.distribution.waybill.domain.LabelPrintingResponse;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.api.WaybillQueryApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.ql.basic.domain.BaseDmsStore;
import com.jd.ql.basic.domain.BaseResult;
import com.jd.ql.basic.domain.CrossPackageTagNew;
import com.jd.ql.basic.domain.ReverseCrossPackageTag;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.ws.BasicSecondaryWS;


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
    private BasicSecondaryWS basicSecondaryWS;
    
    @Autowired
    private WaybillCommonService waybillCommonService;
    
    @Autowired
    private WaybillPrintService waybillPrintService;
    /**
     * 收件人联系方式需要突出显示的位数
     */
    private static final int PHONE_HIGHLIGHT_NUMBER = 4;
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
        LabelPrintingResponse labelPrinting = initWaybillInfo(request, null);
        
        labelPrinting.setBrandImageKey(request.getBrandImageKey());

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

        labelPrinting.setBrandImageKey(request.getBrandImageKey());

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
            crossPackageTag = getCrossPackageTagByPara(baseDmsStore,labelPrinting.getPrepareSiteCode(),request.getDmsCode());
        }



        if(crossPackageTag==null){
            log.warn(LOG_PREFIX+" 无法获取包裹打印数据"+request.getWaybillCode());
            if(StringHelper.isEmpty(labelPrinting.getPrepareSiteName())){
                labelPrinting.setPrepareSiteName(getBaseSite(labelPrinting.getPrepareSiteCode()));
            }
            return new BaseResponseIncidental<LabelPrintingResponse>(LabelPrintingResponse.CODE_EMPTY_BASE,LabelPrintingResponse.MESSAGE_EMPTY_BASE+"(crossPackageTag打印数据)"
                    ,labelPrinting,JsonHelper.toJson(labelPrinting));
        }
        log.info(new StringBuilder(LOG_PREFIX).append("基础资料crossPackageTag").append(crossPackageTag.toString()));
        //航空标识
        if(LabelPrintingService.AIR_TRANSPORT.equals(crossPackageTag.getIsAirTransport()) && request.isAirTransport()){
        	labelPrinting.appendSpecialMark(LabelPrintingService.SPECIAL_MARK_AIRTRANSPORT);
        }
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
        }

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

        response.setCode(JdResponse.CODE_OK);
        response.setMessage(JdResponse.MESSAGE_OK);

        return response;
    }

    /**
    * 查询基础资料大全表信息
    * @param baseDmsStore
    * @param prepareSiteCode
    * @param dmsCode
    * @return
    */
    public CrossPackageTagNew getCrossPackageTagByPara(BaseDmsStore baseDmsStore,Integer prepareSiteCode,Integer dmsCode){

        CrossPackageTagNew tag = null;
            BaseResult<CrossPackageTagNew> baseResult = baseMinorManager.getCrossPackageTagByPara(baseDmsStore, prepareSiteCode, dmsCode);
            if(BaseResult.SUCCESS==baseResult.getResultCode()&&null!=baseResult.getData()) {
                tag=baseResult.getData();
            }else{
                com.jd.ql.basic.domain.BaseResult<ReverseCrossPackageTag> reverseResult= basicSecondaryWS.getReverseCrossPackageTag(dmsCode,prepareSiteCode);
                if(null!=reverseResult&&com.jd.ql.basic.domain.BaseResult.RESULT_SUCCESS==reverseResult.getResultCode()){
                    tag=new CrossPackageTagNew();
                    tag.setTargetSiteName(reverseResult.getData().getTargetStoreName());
                    tag.setTargetSiteId(reverseResult.getData().getTargetStoreId());
                    tag.setOriginalCrossCode(reverseResult.getData().getOriginalCrossCode());
                    tag.setOriginalDmsName(reverseResult.getData().getOriginalDmsName());
                    tag.setOriginalTabletrolleyCode(reverseResult.getData().getOriginalTabletrolleyCode());
                    tag.setOriginalDmsId(reverseResult.getData().getOriginalDmsId());
                    tag.setDestinationCrossCode(reverseResult.getData().getDestinationCrossCode());
                    tag.setDestinationDmsName(reverseResult.getData().getDestinationDmsName());
                    tag.setDestinationTabletrolleyCode(reverseResult.getData().getDestinationTabletrolleyCode());
                    tag.setDestinationDmsId(reverseResult.getData().getDestinationDmsId());
                }else {
                    log.warn("获取基础资料正向及逆向道口信息为失败");
                }
            }
        return tag;
    }

    /**
     * 查询运单接口
     * @param request
     * @return
     */
    public LabelPrintingResponse initWaybillInfo(LabelPrintingRequest request, WaybillPrintContext context){
    	BigWaybillDto bigWaybillDto = null;
    	//先从context不为空，先context中获取原运单数据，否则调取运单接口
        if(context != null){
        	bigWaybillDto = context.getBigWaybillDto();
        }else{
        /**查询运单*/
        WChoice wchoice = new WChoice();
        wchoice.setQueryWaybillC(true);
        wchoice.setQueryWaybillE(true);
        BaseEntity<BigWaybillDto> entity = waybillQueryApi.getDataByChoice(request.getWaybillCode(), wchoice);
        if(entity==null || entity.getData()==null){
            log.warn(LOG_PREFIX+" 没有获取运单数据(BaseEntity<BigWaybillDto>)"+request.getWaybillCode());
            return null;
        }
            bigWaybillDto = entity.getData();
        }
        if(bigWaybillDto==null){
        	log.warn(LOG_PREFIX+" 没有获取运单数据(BaseEntity<BigWaybillDto>)"+request.getWaybillCode());
        	return null;
        }
        Waybill waybill = bigWaybillDto.getWaybill();
        if(waybill==null){
            log.warn(LOG_PREFIX+" 没有获取运单数据(waybill)"+request.getWaybillCode());
            return null;
        }
        if(context != null && context.getWaybill() != null && InterceptResult.STATUS_WEAK_PASSED == context.getStatus()){//二次预分拣时重置目的站点和路区
            waybill.setOldSiteId(context.getWaybill().getSiteCode());
            waybill.setSiteName(context.getWaybill().getSiteName());
            waybill.setRoadCode(context.getWaybill().getRoad());
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
            log.warn(LOG_PREFIX+" 没有获取预分拣站点(-2超区),"+request.getWaybillCode());
            //未定位门店
        } else if(labelPrinting.getPrepareSiteCode()==null || (labelPrinting.getPrepareSiteCode()<=LabelPrintingService.PREPARE_SITE_CODE_NOTHING && labelPrinting.getPrepareSiteCode() > LabelPrintingService.PREPARE_SITE_CODE_OVER_LINE)){
            labelPrinting.setPrepareSiteCode(LabelPrintingService.PREPARE_SITE_CODE_NOTHING);
            labelPrinting.setPrepareSiteName(LabelPrintingService.PREPARE_SITE_NAME_NOTHING);
            log.warn(LOG_PREFIX+" 没有获取预分拣站点(未定位门店),"+request.getWaybillCode());
        } else if(labelPrinting.getPrepareSiteCode() !=null && labelPrinting.getPrepareSiteCode().intValue() < LabelPrintingService.PREPARE_SITE_CODE_OVER_LINE){
            //新细分超区
            labelPrinting.setPrepareSiteCode(labelPrinting.getPrepareSiteCode());
            labelPrinting.setPrepareSiteName(LabelPrintingService.PREPARE_SITE_NAME_OVER_AREA);
            log.warn(LOG_PREFIX+" 没有获取预分拣站点(细分超区)," + labelPrinting.getPrepareSiteCode() + ","+request.getWaybillCode());
        }

        //EMS全国直发
        if(labelPrinting.getPrepareSiteCode()!=null && labelPrinting.getPrepareSiteCode().equals(LabelPrintingService.PREPARE_SITE_CODE_EMS_DIRECT)){
            labelPrinting.setPrepareSiteName(LabelPrintingService.PREPARE_SITE_NAME_EMS_DIRECT);
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
