package com.jd.bluedragon.distribution.print.waybill.handler;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.TextConstants;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.api.response.WaybillPrintResponse;
import com.jd.bluedragon.distribution.base.service.AirTransportService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.domain.PrintPackage;
import com.jd.bluedragon.distribution.print.domain.PrintWaybill;
import com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum;
import com.jd.bluedragon.distribution.print.service.ComposeService;
import com.jd.bluedragon.distribution.print.service.PreSortingSecondService;
import com.jd.bluedragon.distribution.urban.domain.TransbillM;
import com.jd.bluedragon.distribution.urban.service.TransbillMService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.dms.utils.WaybillVasUtil;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.WaybillManageDomain;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WaybillVasDto;
import com.jd.ldop.basic.dto.BasicTraderInfoDTO;
import com.jd.pfinder.profiler.sdk.trace.PFTracing;
import com.jd.ql.basic.domain.BaseDmsStore;
import com.jd.ql.basic.domain.CrossPackageTagNew;
import com.jd.ql.basic.domain.SortCrossDetail;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum.SITE_MASTER_REVERSE_CHANGE_PRINT;
import static com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum.SWITCH_BILL_PRINT;

@Service
public class BasicWaybillPrintHandler implements InterceptHandler<WaybillPrintContext,String>{
	private static final Logger log = LoggerFactory.getLogger(BasicWaybillPrintHandler.class);

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private WaybillCommonService waybillCommonService;

    @Autowired
    private AirTransportService airTransportService;
    
    @Autowired
    private PreSortingSecondService preSortingSecondService;

    @Autowired
    private TransbillMService transbillMService;

    @Autowired
    private BaseMinorManager baseMinorManager;
    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    /**
     * 奢侈品订单打标位起始值
     */
    private static final char LUXURY_SIGN_START='1';

    /**
     * 奢侈品订单打标终值
     */
    private static final char LUXURY_SIGN_END='4';

    /**
     * 奢侈品订单标签显示值
     */
    private static final String LUXURY_SIGN_TEXT="奢";

    /**
     * 奢侈器订单打标位
     */
    private static final int LUXURY_SIGN_BIT=19;

    /**
     * 闪购订单标签显示值
     */
    private static final String QUICK_SIGN_TEXT="闪";

    /**
     * 闪购订单打标位1
     */
    private static final int QUICK_SIGN_BIT_ONE=55;

    /**
     * 闪购订单打标位2
     */
    private static final int QUICK_SIGN_BIT_TWO=51;
    /**
     * 闪购订单打标值
     */
    private static final char QUICK_SIGN_VALUE='1';

    /**
     * 普通发货
     */
    private static final String INVOICE_TYPE_COMMON_TEXT ="普";

    /**
     * 电子发票
     */
    private static final String INVOICE_TYPE_ELECTRONIC_TEXT="电";

    /**
     * 无发票
     */
    private static final String INVOICE_TYPE_NULL_TEXT="无";

    private static  final String USER_LEVEL_VIP="V";

    private static  final String USER_LEVEL_COMPANY="企";

    private static final String USER_PLUS_FLAG_A="101";
    private static final String USER_PLUS_FLAG_B="201";

    /**
     * 收件人联系方式需要突出显示的位数
     */
    private static final int PHONE_HIGHLIGHT_NUMBER = 4;

    /** 运单号突出显示的位数 **/
    private static final int WAYBILL_CODE_HIGHLIGHT_NUMBER = 4;

    /**
     * 半收的运单状态
     */
    private static final Integer WAYBILL_STATE_HALF_RECEIVE = 600;



	@Override
    @PFTracing(name = "DMSWEB.BasicWaybillPrintHandler.handle.Tracing")
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.BasicWaybillPrintHandler.handle",mState={JProEnum.TP,JProEnum.FunctionError})
	public InterceptResult<String> handle(WaybillPrintContext context) {
		InterceptResult<String> interceptResult = context.getResult();
        String waybillCode = WaybillUtil.getWaybillCode(context.getRequest().getBarCode());
        try {
            BaseEntity<BigWaybillDto> baseEntity =  waybillQueryManager.getWaybillDataForPrint(waybillCode);
            if (null == baseEntity){
                interceptResult.toError(InterceptResult.CODE_ERROR, "运单数据为空！");
                return interceptResult;
            }
            if (baseEntity != null && Constants.RESULT_SUCCESS != baseEntity.getResultCode()){
                interceptResult.toError(InterceptResult.CODE_ERROR, baseEntity.getMessage());
                return interceptResult;
            }
            //运单数据为空，直接返回运单数据为空异常
            if(null == baseEntity.getData() || null == baseEntity.getData().getWaybill()){
                interceptResult.toFail(WaybillPrintMessages.FAIL_MESSAGE_WAYBILL_NULL.getMsgCode(),
                        WaybillPrintMessages.FAIL_MESSAGE_WAYBILL_NULL.formatMsg());
                log.warn("调用运单接口获取运单数据为空，waybillCode：{}", waybillCode);
                return interceptResult;
            }
            //获取运单数据正常，设置打印基础信息
            context.setBigWaybillDto(baseEntity.getData());
            context.setWaybill(waybillCommonService.convWaybillWS(baseEntity.getData(), true, true,true,false));
            //加载运单基础数据
            loadWaybillInfo(context);
            interceptResult = preSortingSecondService.preSortingAgain(context);//处理是否触发2次预分拣
            //加载运单包裹重量
            loadWaybillPackageWeight(context, context.getResponse());
            //加载已打印记录【标签打印与发票打印】
            loadPrintedData(context);
            //根据预分拣站点加载始发及目的站点信息
            loadBasicData(context,context.getResponse());
            //加载路由信息
            waybillCommonService.loadWaybillRouter(context.getRequest(),context.getResponse(),context.getResponse().getOriginalDmsCode(),context.getResponse().getPurposefulDmsCode(),context.getWaybill().getWaybillSign());
        }catch (Exception ex){
            log.error("标签打印接口异常，运单号:{}", waybillCode,ex);
            interceptResult.toError();
        }
        return interceptResult;
	}
    /**
     * 加载运单基础数据
     * @param context
     */
    private final void loadWaybillInfo(WaybillPrintContext context){
        CallerInfo callerInfo = Profiler.registerInfo("dms.web.BasicWaybillPrintHandler.loadWaybillInfo",
                Constants.UMP_APP_NAME_DMSWEB, false, true);
        try {
            Integer dmsCode = context.getRequest().getDmsSiteCode();
            WaybillPrintResponse commonWaybill = new WaybillPrintResponse();
            context.setResponse(commonWaybill);
            context.setBasePrintWaybill(commonWaybill);
            BigWaybillDto bigWaybillDto = context.getBigWaybillDto();
            com.jd.etms.waybill.domain.Waybill tmsWaybill=bigWaybillDto.getWaybill();
            WaybillManageDomain tmsWaybillManageDomain=bigWaybillDto.getWaybillState();
            commonWaybill.setWaybillCode(tmsWaybill.getWaybillCode());
            //B网面单要求将运单号后四位突出显示
            String waybillCode = tmsWaybill.getWaybillCode();
            if(StringUtils.isNotBlank(waybillCode) && waybillCode.length()>=WAYBILL_CODE_HIGHLIGHT_NUMBER) {
                commonWaybill.setWaybillCodeFirst(waybillCode.substring(0,waybillCode.length()-WAYBILL_CODE_HIGHLIGHT_NUMBER));
                commonWaybill.setWaybillCodeLast(waybillCode.substring(waybillCode.length()-WAYBILL_CODE_HIGHLIGHT_NUMBER));
            }
            commonWaybill.setPopSupId(tmsWaybill.getConsignerId());
            commonWaybill.setPopSupName(tmsWaybill.getConsigner());
            commonWaybill.setBusiId(tmsWaybill.getBusiId());
            commonWaybill.setBusiName(tmsWaybill.getBusiName());

            //备注拼接服务单号-逆向打印调用 (自营售后取件快递业务 124 位等于4)
            if (BusinessUtil.isSignChar(tmsWaybill.getWaybillSign(), 124, '4')) {
                commonWaybill.appendRemark("运单号:"+tmsWaybill.getWaybillCode());
            }

            commonWaybill.setOriginalCrossType(BusinessUtil.getOriginalCrossType(tmsWaybill.getWaybillSign(), tmsWaybill.getSendPay()));
            //调用外单接口，根据商家id获取商家编码
            BasicTraderInfoDTO basicTraderInfoDTO = baseMinorManager.getBaseTraderById(tmsWaybill.getBusiId());
            if(basicTraderInfoDTO != null){
                commonWaybill.setBusiCode(basicTraderInfoDTO.getTraderCode());
                context.setBusiCode(basicTraderInfoDTO.getTraderCode());
                context.setTraderSign(basicTraderInfoDTO.getTraderSign());
            }
            commonWaybill.setQuantity(tmsWaybill.getGoodNumber());
            commonWaybill.setOrderCode(tmsWaybill.getVendorId());
            // commonWaybill.setBusiOrderCode(tmsWaybill.getBusiOrderCode());//增加商家订单号字段
            commonWaybill.setOriginalDmsCode(dmsCode);
            commonWaybill.setPrepareSiteCode(tmsWaybill.getOldSiteId());
            commonWaybill.setPrintAddress(tmsWaybill.getReceiverAddress());
            commonWaybill.setNewAddress(tmsWaybill.getNewRecAddr());
            commonWaybill.setPackagePrice(tmsWaybill.getCodMoney());
            commonWaybill.setWaybillSign(tmsWaybill.getWaybillSign());
            commonWaybill.setSendPay(tmsWaybill.getSendPay());
            commonWaybill.setDistributeType(tmsWaybill.getDistributeType());
            if(StringUtils.isNotBlank(tmsWaybill.getSendPay())&&tmsWaybill.getSendPay().length()>QUICK_SIGN_BIT_ONE) {
                char luxurySign = tmsWaybill.getSendPay().charAt(LUXURY_SIGN_BIT);
                commonWaybill.setLuxuryText(luxurySign <= LUXURY_SIGN_END && luxurySign >= LUXURY_SIGN_START ? LUXURY_SIGN_TEXT : StringUtils.EMPTY);
                commonWaybill.setLuxuryText(commonWaybill.getLuxuryText() + ((BusinessUtil.isSignY(tmsWaybill.getSendPay(),56) || BusinessUtil.isSignY(tmsWaybill.getSendPay(),52)) ? QUICK_SIGN_TEXT : StringUtils.EMPTY));
            }
            /*值=1，打“普”字，
			     值=3，打“电”，
			     值=2,4,8，或字段为空，打“无”
			*/
            commonWaybill.setNormalText(INVOICE_TYPE_NULL_TEXT);

            if(StringUtils.isNotBlank(tmsWaybill.getSpareColumn1())&& NumberUtils.isNumber(tmsWaybill.getSpareColumn1().trim())){
                Integer value=Integer.MIN_VALUE;
                try {
                    value= NumberUtils.createInteger(tmsWaybill.getSpareColumn1().trim());
                }catch (NumberFormatException exception){
                    value=Integer.MIN_VALUE;/*不符合integer*/
                }
                if(log.isInfoEnabled()){
                    log.info("原值：{}转换后:{}",tmsWaybill.getSpareColumn1(),value);
                }
                switch (value){
                    case 1:
                        commonWaybill.setNormalText(INVOICE_TYPE_COMMON_TEXT);
                        break;
                    case 3:
                        commonWaybill.setNormalText(INVOICE_TYPE_ELECTRONIC_TEXT);
                        break;
                    default:
                        break;
                }
            }
            if(log.isInfoEnabled()){
                log.info(commonWaybill.getNormalText());
            }
            /*，62=金牌用户，105=钻石会员，110=VIP会员，在面单上展示“V”。
				90=企业用户，面单上展示“企”。
			*/
            commonWaybill.setUserLevel(StringUtils.EMPTY);
            if(null!=tmsWaybill.getUserLevel()){
                switch (tmsWaybill.getUserLevel()){
                    case 62:
                    case 105:
                    case 110:
                        commonWaybill.setUserLevel(USER_LEVEL_VIP);
                        break;
                    case 90:
                        commonWaybill.setUserLevel(USER_LEVEL_COMPANY);
                        break;
                    default:
                        break;
                }
            }
            if(StringUtils.isNotBlank(tmsWaybill.getFlagInfo())&&tmsWaybill.getFlagInfo().length()>19){
                String plusFlag=tmsWaybill.getFlagInfo().substring(16,19);
                if(plusFlag.equals(USER_PLUS_FLAG_A)||plusFlag.equals(USER_PLUS_FLAG_B)){
                    commonWaybill.setUserLevel(USER_LEVEL_VIP);
                }
            }
            commonWaybill.setType(tmsWaybill.getWaybillType());
            commonWaybill.appendRemark(tmsWaybill.getImportantHint());
            String roadCode = "";
            if(BusinessUtil.isUrban(tmsWaybill.getWaybillSign(), tmsWaybill.getSendPay()) || BusinessUtil.isHeavyCargo(tmsWaybill.getWaybillSign())) {//城配的订单标识，remark打派车单号
                String scheduleCode = "";
                TransbillM transbillM = transbillMService.getByWaybillCode(tmsWaybill.getWaybillCode());
                if(transbillM != null){
                    if(StringHelper.isNotEmpty(transbillM.getScheduleBillCode())){
                        scheduleCode = transbillM.getScheduleBillCode();
                    }
                    //城配运单设置路区号-为卡位号
                    if(StringHelper.isNotEmpty(transbillM.getTruckSpot())){
                        roadCode = transbillM.getTruckSpot();
                    }
                }
                commonWaybill.appendRemark(scheduleCode);
            }

            //sendpay的第153位为“1”，remark追加【合并送】
            if(BusinessUtil.isSignY(commonWaybill.getSendPay(), 153)){
                commonWaybill.appendRemark(TextConstants.REMARK_SEND_GATHER_TOGETHER);
            }

            //路区-为空尝试从运单里获取
            if(StringHelper.isEmpty(roadCode)){
                if(StringHelper.isNotEmpty(tmsWaybill.getRoadCode())){
                    roadCode = tmsWaybill.getRoadCode();
                }else{
                    roadCode = "0";
                }
            }
            commonWaybill.setRoad(roadCode);
            commonWaybill.setRoadCode(roadCode);

            if(tmsWaybill.getPayment()!=null){
                if(tmsWaybill.getPayment()==ComposeService.ONLINE_PAYMENT_SIGN){
                    commonWaybill.setPackagePrice(ComposeService.ONLINE_PAYMENT);
                }
            }
            commonWaybill.setCustomerName(tmsWaybill.getReceiverName());
            commonWaybill.setCustomerContacts(concatPhone(tmsWaybill.getReceiverMobile(),tmsWaybill.getReceiverTel()));
            //因为要求手机号和座机号的后四位加大、标红显示，分成4段设置收件人联系方式
            String receiverMobile = tmsWaybill.getReceiverMobile();
            String receiverTel = tmsWaybill.getReceiverTel();
            if (StringHelper.isNotEmpty(receiverMobile) && receiverMobile.length() >= PHONE_HIGHLIGHT_NUMBER) {
                commonWaybill.setMobileFirst(receiverMobile.substring(0, receiverMobile.length() - PHONE_HIGHLIGHT_NUMBER));
                commonWaybill.setMobileLast(receiverMobile.substring(receiverMobile.length() - PHONE_HIGHLIGHT_NUMBER));
            }
            if (StringHelper.isNotEmpty(receiverTel) && receiverTel.length() >= PHONE_HIGHLIGHT_NUMBER) {
                commonWaybill.setTelFirst(receiverTel.substring(0, receiverTel.length() - PHONE_HIGHLIGHT_NUMBER));
                commonWaybill.setTelLast(receiverTel.substring(receiverTel.length() - PHONE_HIGHLIGHT_NUMBER));
            }
            if(null!=tmsWaybillManageDomain){
                commonWaybill.setStoreId(tmsWaybillManageDomain.getStoreId());
                //commonWaybill.setStoreName(tmsWaybillManageDomain);
            }
            // 什么类型的单子才打印包裹增值服务 所有的包裹
            //waybillsign86=3或2 表示包裹有增值服务
            Map<String,String> pickUpMap = new HashMap();
            boolean isPackageHavePickUpOrNo = BusinessUtil.isPackageHavePickUpOrNo(tmsWaybill.getWaybillSign());
            if(isPackageHavePickUpOrNo){
                pickUpMap = waybillQueryManager.doGetPackageVasInfo(waybillCode);
            }
            //包裹维度商品信息展示 waybillsign 66=3 展示;否则不展示
            Map<String,String> packageNameMap = new HashMap<>();
            boolean isPrintPackageNameOrNo = BusinessUtil.needPrintPackageName(tmsWaybill.getWaybillSign());
            if(isPrintPackageNameOrNo){
                packageNameMap = waybillQueryManager.doGetPackageGoodsVasInfo(waybillCode);
            }
            List<PrintPackage> packageList=new ArrayList<PrintPackage>();
            if(null!=bigWaybillDto.getPackageList()){
                for (DeliveryPackageD item:bigWaybillDto.getPackageList()){
                	PrintPackage pack=new PrintPackage();
                    pack.setPackageCode(item.getPackageBarcode());
                    //设置包裹增值服务信息
                    pack.setPackageSpecialRequirement(pickUpMap.get(item.getPackageBarcode()));
                    //设置包裹商品名称
                    pack.setPackageGoodsName(packageNameMap.get(item.getPackageBarcode()));
                    //设置包裹序号和包裹号后缀
                    pack.setPackageIndexNum(WaybillUtil.getCurrentPackageNum(item.getPackageBarcode()));
                    pack.setPackageIndex(WaybillUtil.getPackageIndex(item.getPackageBarcode()));
                    pack.setPackageSuffix(WaybillUtil.getPackageSuffix(item.getPackageBarcode()));
                    if(NumberHelper.gt0(item.getAgainWeight())){
                        pack.setWeightAndUnit(item.getAgainWeight(), Constants.MEASURE_UNIT_NAME_KG);
                    }

                    packageList.add(pack);
                }
            }

            commonWaybill.setPackList(packageList);

            //B网面单设置已称标识
            if(BusinessUtil.isB2b(tmsWaybill.getWaybillSign())){
                //如果waybillSign第25位等于3时，表示运费支付方式为寄付，打印【已称】
                //waybillSign第66位等于1时，为信任运单，打印【已称】
                if(BusinessUtil.isSignChar(tmsWaybill.getWaybillSign(), 25, '3') ||
                        BusinessUtil.isSignChar(tmsWaybill.getWaybillSign(), 66, '1') ||
                        SWITCH_BILL_PRINT.getType().equals(context.getRequest().getOperateType()) ||
                        SITE_MASTER_REVERSE_CHANGE_PRINT.getType().equals(context.getRequest().getOperateType())){
                    commonWaybill.setWeightFlagText(TextConstants.WEIGHT_FLAG_TRUE);
                }
                //半收的不打印【已称】，这里需要判断原单的状态
                if(SWITCH_BILL_PRINT.getType().equals(context.getRequest().getOperateType()) ||
                        SITE_MASTER_REVERSE_CHANGE_PRINT.getType().equals(context.getRequest().getOperateType())){
                    //获取原运单号
                    BaseEntity<com.jd.etms.waybill.domain.Waybill>  oldWaybill= waybillQueryManager.getWaybillByReturnWaybillCode(tmsWaybill.getWaybillCode());
                    if(oldWaybill != null && oldWaybill.getData()!=null){
                        String oldWaybillCode = oldWaybill.getData().getWaybillCode();
                        //查询原单号的状态
                        if(StringHelper.isNotEmpty(oldWaybillCode)){
                            context.getBasePrintWaybill().setOldWaybillCode(oldWaybillCode);/* 设置旧单号到返回值中 */
                            BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getWaybillDataForPrint(oldWaybillCode);
                            if(baseEntity!=null && baseEntity.getData()!=null && baseEntity.getData().getWaybillState()!=null){
                                context.setOldBigWaybillDto(baseEntity.getData());/* 设置旧单的运单对象到context中 */
                                if(WAYBILL_STATE_HALF_RECEIVE.equals(baseEntity.getData().getWaybillState().getWaybillState())){
                                    commonWaybill.setWeightFlagText("");
                                }
                            }
                        }
                    }
                }
            }
            //targetSiteCode>0时，设置返调度信息
            Integer targetSiteCode = context.getRequest().getTargetSiteCode();
            if(null!=targetSiteCode && targetSiteCode>0){
                commonWaybill.setPrepareSiteCode(targetSiteCode);
                if(StringHelper.isNotEmpty(commonWaybill.getNewAddress())){
                    commonWaybill.setPrintAddress(commonWaybill.getNewAddress());
                }
            }

            // 非冷链运单使用新的B网模板
            if(uccPropertyConfiguration.getBTemplateRouterExecuteNew()
                    && !(BusinessUtil.isColdChainWaybill(tmsWaybill.getWaybillSign()) || BusinessUtil.isBMedicine(tmsWaybill.getWaybillSign()))){
                commonWaybill.setExecuteNewRouterLogic(true);
            }

            //打标增值服务
            markingWaybillVasSign(context);
            //加载始发站点信息
            waybillCommonService.loadOriginalDmsInfo(context,commonWaybill,bigWaybillDto);
            waybillCommonService.setBasePrintInfoByWaybill(commonWaybill, tmsWaybill);
        } finally {
            Profiler.registerInfoEnd(callerInfo);
        }
    }
    private final String concatPhone(String mobile,String phone){
        StringBuilder sb=new StringBuilder();
        if(StringHelper.isNotEmpty(mobile)){
            sb.append(mobile);
        }

        if( StringHelper.isNotEmpty(phone)){
            if(StringHelper.isNotEmpty(mobile)) {
                sb.append(",");
            }
            sb.append(phone);
        }
        return sb.toString();
    }
    /**
     * 加载已打印记录【标签打印与发票打印】
     * @param context
     */
    private final void loadPrintedData(WaybillPrintContext context){
    	WaybillPrintResponse printWaybill = context.getResponse();
    	printWaybill.setPrintInvoice(context.getWaybill().getIsPrintInvoice() == Waybill.IS_PRINT_INVOICE);
        for (int i = 0; i < printWaybill.getPackList().size(); i++) {
        	printWaybill.getPackList().get(i).setIsPrintPack(
        			context.getWaybill().getPackList().get(i).getIsPrintPack() == Waybill.IS_PRINT_PACK);
        }
    }
    /**
     * 1、预分拣站点正常，查询大全表并设置滑道信息
     * 2、全量接单需求-预分拣站点为空或小于0，运单endDmsId大于0，查询基础资料滑道信息
     * @param waybill
     */
    private final void loadBasicData(WaybillPrintContext context,final PrintWaybill waybill){
        CallerInfo callerInfo = Profiler.registerInfo("dms.web.BasicWaybillPrintHandler.loadBasicData",
                Constants.UMP_APP_NAME_DMSWEB, false, true);
        try {
        	//返分拣报废-只设置默认目的分拣中心名称：返分拣报废
        	if(BusinessUtil.isScrapSortingSite(waybill.getWaybillSign())) {
                waybill.setPurposefulDmsName(DmsConstants.TEXT_SCRAP_DMS_NAME_MARK);
                waybill.setDestinationDmsName(DmsConstants.TEXT_SCRAP_DMS_NAME_MARK);
        		return;
        	}

            //如果预分拣站点为0超区或者999999999EMS全国直发，则不用查询大全表
            if(null!=waybill.getPrepareSiteCode()&&waybill.getPrepareSiteCode()>ComposeService.PREPARE_SITE_CODE_NOTHING
                    && !ComposeService.PREPARE_SITE_CODE_EMS_DIRECT.equals(waybill.getPrepareSiteCode())){
                /**
                 * 1、预分拣站点正常，查询大全表并设置滑道信息
                 */
                setCrossInfoByCrossPackageTag(waybill);
            }else if(null==waybill.getPrepareSiteCode()
            		|| waybill.getPrepareSiteCode() <= ComposeService.PREPARE_SITE_CODE_NOTHING) {
            	/**
            	 * 2、全量接单需求-预分拣站点为空或小于0，运单endDmsId大于0，查询基础资料滑道信息
            	 */
            	setCrossInfoByCrossDetail(context,waybill);
            }
        } finally {
            Profiler.registerInfoEnd(callerInfo);
        }
    }
    /**
     * 根据始发和目的分拣中心设置打印滑道信息
     * @param waybill
     * @param endDmsId 目的分拣中心
     */
    private void setCrossInfoByCrossDetail(WaybillPrintContext context,PrintWaybill waybill) {
    	SortCrossDetail crossDetail = null;
    	Integer endDmsId = null;
    	if(context.getBigWaybillDto() != null 
    			&& context.getBigWaybillDto().getWaybill()!= null
    			&& context.getBigWaybillDto().getWaybill().getWaybillExt()!= null) {
    		endDmsId = context.getBigWaybillDto().getWaybill().getWaybillExt().getEndDmsId();
    	}
    	BaseStaffSiteOrgDto originalDmsInfo =baseMajorManager.getBaseSiteBySiteId(waybill.getOriginalDmsCode());
    	if(originalDmsInfo != null) {
            waybill.setOriginalDmsCode(waybill.getOriginalDmsCode());
            waybill.setOriginalDmsName(originalDmsInfo.getSiteName());
    	}
    	if(NumberHelper.gt0(endDmsId)) {
    		context.setUseEndDmsId(true);
    		context.setWaybillEndDmsId(endDmsId);
    		JdResult<SortCrossDetail> remoteResult = baseMinorManager.queryCrossDetailByDmsIdAndSiteCode(waybill.getOriginalDmsCode(), endDmsId.toString(), waybill.getOriginalCrossType());
            if(remoteResult.isSucceed()) {
            	crossDetail=remoteResult.getData();
            }else{
                log.warn("打印业务：未获取到滑道号及笼车号信息:{}", remoteResult.getMessage());
            }
           	BaseStaffSiteOrgDto endDmsInfo =baseMajorManager.getBaseSiteBySiteId(endDmsId);
        	if(endDmsInfo != null) {
                waybill.setPurposefulDmsCode(endDmsId);
                waybill.setPurposefulDmsName(endDmsInfo.getSiteName());
                waybill.setDestinationDmsName(endDmsInfo.getSiteName());
        	}
    	}
    	if(crossDetail != null) {
            waybill.setPrepareSiteName("");
            waybill.setPrintSiteName("");

            //笼车号
            waybill.setOriginalTabletrolley(crossDetail.getTabletrolleyCode());
            waybill.setOriginalTabletrolleyCode(crossDetail.getTabletrolleyCode());
            //道口号
            waybill.setOriginalCrossCode(crossDetail.getCrossCode());
            hiddenCrossInfo(waybill);
    	}
	}
	/**
     * 根据大全表设置打印滑道信息
     * @param tag
     * @param waybill
     */
    private void setCrossInfoByCrossPackageTag(PrintWaybill waybill) {
    	CrossPackageTagNew tag = null;
            BaseDmsStore baseDmsStore = new BaseDmsStore();
            baseDmsStore.setStoreId(waybill.getStoreId());//库房编号
            baseDmsStore.setCky2(waybill.getCky2());//cky2
            baseDmsStore.setOrgId(waybill.getOrgId());//机构编号
            baseDmsStore.setDmsId(waybill.getOriginalDmsCode());//分拣中心编号
                JdResult<CrossPackageTagNew> jdResult = baseMinorManager.queryCrossPackageTagForPrint(baseDmsStore, waybill.getPrepareSiteCode(), waybill.getOriginalDmsCode(),waybill.getOriginalCrossType());
                if(jdResult.isSucceed()) {
                    tag=jdResult.getData();
                }else{
                    log.warn("打印业务：未获取到滑道号及笼车号信息:{}", jdResult.getMessage());
        }    	
            if(null!=tag){
                if(tag.getIsAirTransport()!=null
                        && tag.getIsAirTransport()== ComposeService.AIR_TRANSPORT
                        &&null!=waybill.getBusiId()&&waybill.getBusiId().compareTo(0)>0){
                    waybill.setIsAir(this.airTransportService.getAirSigns(waybill.getBusiId()));
                }
                //如果是自提柜，则打印的是自提柜的地址(基础资料大全表)，而非客户地址(运单系统)
                if(null!=tag.getIsZiTi()&&tag.getIsZiTi().equals(ComposeService.ARAYACAK_CABINET)){
                    waybill.setIsSelfService(true);
                    waybill.setPrintAddress(tag.getPrintAddress());
                }
                if (BusinessUtil.isZiTiByWaybillSign(waybill.getWaybillSign())
                        || BusinessUtil.isZiTiGuiByWaybillSign(waybill.getWaybillSign())
                        || BusinessUtil.isZiTiDianByWaybillSign(waybill.getWaybillSign())
                        || BusinessUtil.isWrcps(waybill.getSendPay())) {
                    if (StringHelper.isNotEmpty(tag.getPrintAddress()) && !BusinessUtil.isBusinessNet(waybill.getWaybillSign())) {
                        waybill.setPrintAddress(tag.getPrintAddress());
                    }
                }

                waybill.setPrepareSiteName(tag.getPrintSiteName());
                waybill.setPrintSiteName(tag.getPrintSiteName());
                waybill.setOriginalDmsCode(tag.getOriginalDmsId());
                waybill.setOriginalDmsName(tag.getOriginalDmsName());
                waybill.setPurposefulDmsCode(tag.getDestinationDmsId());
                waybill.setPurposefulDmsName(tag.getDestinationDmsName());
                waybill.setDestinationDmsName(tag.getDestinationDmsName());

                //笼车号
                waybill.setOriginalTabletrolley(tag.getOriginalTabletrolleyCode());
                waybill.setOriginalTabletrolleyCode(tag.getOriginalTabletrolleyCode());

                waybill.setPurposefulTableTrolley(tag.getDestinationTabletrolleyCode());
                waybill.setDestinationTabletrolleyCode(tag.getDestinationTabletrolleyCode());
                //道口号
                waybill.setOriginalCrossCode(tag.getOriginalCrossCode());
                waybill.setPurposefulCrossCode(tag.getDestinationCrossCode());
                waybill.setDestinationCrossCode(tag.getDestinationCrossCode());
            hiddenCrossInfo(waybill);
        }
	}
	/**
     * 隐藏滑道信息
     * @param waybill
     */
    private void hiddenCrossInfo(PrintWaybill waybill) {
                if(BusinessUtil.isSignChar(waybill.getWaybillSign(),31,'3')){
                    waybill.setOriginalDmsName("");
                    waybill.setPurposefulDmsName("");
                    waybill.setDestinationDmsName("");
                    waybill.setOriginalTabletrolley("");
            waybill.setOriginalTabletrolleyCode(""); 
                    waybill.setPurposefulTableTrolley("");
                    waybill.setDestinationTabletrolleyCode("");
                    waybill.setOriginalCrossCode("");
                    waybill.setPurposefulCrossCode("");
                    waybill.setDestinationCrossCode("");
                }
            }
    /**
     * 逆向换单设置终端重量
     * @param context
     * @param commonWaybill
     */
    private void loadWaybillPackageWeight(WaybillPrintContext context, PrintWaybill commonWaybill){
        //换单打印业务、或者毕业寄订单，取复重
        if(WaybillPrintOperateTypeEnum.isExchangePrint(context.getRequest().getOperateType())
				|| BusinessUtil.isGraduationExpress(commonWaybill.getWaybillSign())){
        	BigWaybillDto bigWaybillDto = context.getBigWaybillDto();
            if (bigWaybillDto != null && bigWaybillDto.getPackageList() != null && !bigWaybillDto.getPackageList().isEmpty()) {
                Map<String, DeliveryPackageD> againWeightMap = getAgainWeightMap(bigWaybillDto.getPackageList());
                for(PrintPackage pack : commonWaybill.getPackList()){
                    DeliveryPackageD deliveryPackageD = againWeightMap.get(pack.getPackageCode());
                    if(deliveryPackageD != null){
                    	//设置包裹重量，使用AgainWeight
                    	if(NumberHelper.gt0(deliveryPackageD.getAgainWeight())){
                            pack.setWeightAndUnit(deliveryPackageD.getAgainWeight(), Constants.MEASURE_UNIT_NAME_KG);
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取终端重量的map
     * @param packageDList
     * @return
     */
    private Map<String, DeliveryPackageD> getAgainWeightMap(List<DeliveryPackageD> packageDList) {
        Map<String, DeliveryPackageD> result = new HashMap<String, DeliveryPackageD>(packageDList.size());
        for (DeliveryPackageD deliveryPackageD : packageDList) {
            result.put(deliveryPackageD.getPackageBarcode(), deliveryPackageD);
        }
        return result;
    }


    /**
     * 打标运单的增值服务
     * 如果后期扩展，追加标位
     * @param context
     */
    private void markingWaybillVasSign(WaybillPrintContext context) {
        if (null == context.getBigWaybillDto() || CollectionUtils.isEmpty(context.getBigWaybillDto().getWaybillVasList())){
            return ;
        }
        String vasSign = WaybillVasUtil.DEFAULT_VAS_SIGN;
        /*增值服务打标-包裹有话说*/
        if (isHasSpecifiedValue(context,WaybillVasUtil.PACKAGE_SAY)){
            vasSign = WaybillVasUtil.markingPackageSaySign(vasSign);
        }
        // 精准送仓
        if(isHasSpecifiedValue(context,WaybillVasUtil.JZSC_VALUE)){
            vasSign = WaybillVasUtil.markingJZSCSign(vasSign);
        }

        context.getBasePrintWaybill().setWaybillVasSign(vasSign);
    }

    /**
     * 是否有 '包裹有话说'
     * @param context
     * @return
     */
    private boolean isHasSpecifiedValue(WaybillPrintContext context,String specifiedValue) {
        if (null == context.getBigWaybillDto() || CollectionUtils.isEmpty(context.getBigWaybillDto().getWaybillVasList())){
            return Boolean.FALSE;
        }
        List<WaybillVasDto> waybillVasDtos = context.getBigWaybillDto().getWaybillVasList();
        for (int i =0 ;i < waybillVasDtos.size();i++){
            WaybillVasDto tmp = waybillVasDtos.get(i);
            if (specifiedValue.equals(tmp.getVasNo())){
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

}
