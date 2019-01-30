package com.jd.bluedragon.distribution.print.waybill.handler;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.*;

import com.jd.bluedragon.core.base.*;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.etms.api.common.enums.RouteProductEnum;
import com.jd.etms.waybill.domain.WaybillPickup;
import com.jd.preseparate.vo.external.AnalysisAddressResult;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.TextConstants;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.service.WaybillCommonService;
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
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.WaybillManageDomain;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ldop.basic.dto.BasicTraderInfoDTO;
import com.jd.ql.basic.domain.BaseDmsStore;
import com.jd.ql.basic.domain.CrossPackageTagNew;

@Service
public class BasicWaybillPrintHandler implements InterceptHandler<WaybillPrintContext,String>{
	private static final Log logger= LogFactory.getLog(BasicWaybillPrintHandler.class);

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
    private BasicSafInterfaceManager basicSafInterfaceManager;

    @Autowired
    private PreseparateWaybillManager preseparateWaybillManager;

    @Autowired
    private VrsRouteTransferRelationManager vrsRouteTransferRelationManager;
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
     * 换单打印的操作类型
     */
    private static final Integer OPERATE_TYPE_EXCHANGE_PRINT = 100104;

    /**
     * 半收的运单状态
     */
    private static final Integer WAYBILL_STATE_HALF_RECEIVE = 600;

    private static final String UNIT_WEIGHT_KG = "kg";

    private static final String SPECIAL_REQUIRMENT_SIGNBACK ="签单返还";
    private static final String SPECIAL_REQUIRMENT_PACK="包装";
    private static final String SPECIAL_REQUIRMENT_DELIVERY_UPSTAIRS="重货上楼";
    private static final String SPECIAL_REQUIRMENT_DELIVERY_WAREHOUSE="送货入仓";

    /** 直辖市省id 1:北京市 2:上海市  3：天津市  4：重庆市 **/
    private static final List<Integer> municipalityList = Arrays.asList(1,2,3,4);
	@Override
	public InterceptResult<String> handle(WaybillPrintContext context) {
		InterceptResult<String> interceptResult = context.getResult();
        String waybillCode = WaybillUtil.getWaybillCode(context.getRequest().getBarCode());
        try {
            BaseEntity<BigWaybillDto> baseEntity =  waybillQueryManager.getWaybillDataForPrint(waybillCode);
            if(baseEntity != null && Constants.RESULT_SUCCESS == baseEntity.getResultCode()){
            	//运单数据为空，直接返回运单数据为空异常
            	if(baseEntity.getData() == null
            			||baseEntity.getData().getWaybill() == null){
            		interceptResult.toFail(WaybillPrintMessages.FAIL_MESSAGE_WAYBILL_NULL.getMsgCode(), WaybillPrintMessages.FAIL_MESSAGE_WAYBILL_NULL.formatMsg());
            		logger.warn("调用运单接口获取运单数据为空，waybillCode："+waybillCode);
            		return interceptResult;
            	}
            	//获取运单数据正常，设置打印基础信息
                context.setBigWaybillDto(baseEntity.getData());
                context.setWaybill(waybillCommonService.convWaybillWS(baseEntity.getData(), true, true,true,false));
                loadWaybillInfo(context);
                interceptResult = preSortingSecondService.preSortingAgain(context);//处理是否触发2次预分拣
                loadWaybillPackageWeight(context, context.getResponse());
                loadPrintedData(context);
                //根据预分拣站点加载始发及目的站点信息
                loadBasicData(context.getResponse());
                //加载路由信息
                loadWaybillRouter(context.getResponse());
            }else if(baseEntity != null && Constants.RESULT_SUCCESS != baseEntity.getResultCode()){
                interceptResult.toError(InterceptResult.CODE_ERROR, baseEntity.getMessage());
            }else{
                interceptResult.toError(InterceptResult.CODE_ERROR, "运单数据为空！");
            }
        }catch (Exception ex){
            logger.error("标签打印接口异常，运单号:"+waybillCode,ex);
            interceptResult.toError();
        }
        return interceptResult;
	}
    /**
     * 加载运单基础数据
     * @param context
     */
    private final void loadWaybillInfo(WaybillPrintContext context){
    		Integer dmsCode = context.getRequest().getDmsSiteCode();
    		WaybillPrintResponse commonWaybill = new WaybillPrintResponse();
            context.setResponse(commonWaybill);
    		BigWaybillDto bigWaybillDto = context.getBigWaybillDto();
            com.jd.etms.waybill.domain.Waybill tmsWaybill=bigWaybillDto.getWaybill();
            WaybillManageDomain tmsWaybillManageDomain=bigWaybillDto.getWaybillState();
            commonWaybill.setWaybillCode(tmsWaybill.getWaybillCode());
            //B网面单要求将运单号后四位突出显示
            String waybillCode = tmsWaybill.getWaybillCode();
            if(StringUtils.isNotBlank(waybillCode) && waybillCode.length()>=WAYBILL_CODE_HIGHLIGHT_NUMBER) {
                commonWaybill.setWaybillCodeFirst(waybillCode.substring(0,waybillCode.length()-4));
                commonWaybill.setWaybillCodeLast(waybillCode.substring(waybillCode.length()-4));
            }
            commonWaybill.setPopSupId(tmsWaybill.getConsignerId());
            commonWaybill.setPopSupName(tmsWaybill.getConsigner());
            commonWaybill.setBusiId(tmsWaybill.getBusiId());
            commonWaybill.setBusiName(tmsWaybill.getBusiName());
            commonWaybill.setOriginalCrossType(BusinessUtil.getOriginalCrossType(tmsWaybill.getWaybillSign(), tmsWaybill.getSendPay()));
            //调用外单接口，根据商家id获取商家编码
            BasicTraderInfoDTO basicTraderInfoDTO = baseMinorManager.getBaseTraderById(tmsWaybill.getBusiId());
            if(basicTraderInfoDTO != null){
                commonWaybill.setBusiCode(basicTraderInfoDTO.getTraderCode());
                context.setBusiCode(basicTraderInfoDTO.getTraderCode());
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
                if(logger.isInfoEnabled()){
                    logger.info(MessageFormat.format("原值：{0}转换后:{1}",tmsWaybill.getSpareColumn1(),value));
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
            if(logger.isInfoEnabled()){
                logger.info(commonWaybill.getNormalText());
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
            if(BusinessUtil.isUrban(tmsWaybill.getWaybillSign(), tmsWaybill.getSendPay())) {//城配的订单标识，remark打派车单号
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
//                String str = StringUtils.isNotBlank(tmsWaybill.getImportantHint())? tmsWaybill.getImportantHint():"";
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
            List<PrintPackage> packageList=new ArrayList<PrintPackage>();
            if(null!=bigWaybillDto.getPackageList()){
                for (DeliveryPackageD item:bigWaybillDto.getPackageList()){
                	PrintPackage pack=new PrintPackage();
                    pack.setPackageCode(item.getPackageBarcode());
                    pack.setWeight(item.getGoodWeight());
                    //设置包裹序号和包裹号后缀
                    pack.setPackageIndex(getPackageIndex(item.getPackageBarcode()));
                    pack.setPackageSuffix(getPackageSuffix(item.getPackageBarcode()));
                    pack.setPackageWeight(item.getGoodWeight() + UNIT_WEIGHT_KG);
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
                       OPERATE_TYPE_EXCHANGE_PRINT.equals(context.getRequest().getOperateType())){
                   commonWaybill.setWeightFlagText(TextConstants.WEIGHT_FLAG_TRUE);
               }
               //半收的不打印【已称】，这里需要判断原单的状态
               if(OPERATE_TYPE_EXCHANGE_PRINT.equals(context.getRequest().getOperateType())){
                   //获取原运单号
                   BaseEntity<com.jd.etms.waybill.domain.Waybill>  oldWaybill= waybillQueryManager.getWaybillByReturnWaybillCode(tmsWaybill.getWaybillCode());
                   if(oldWaybill != null && oldWaybill.getData()!=null){
                       String oldWaybillCode = oldWaybill.getData().getWaybillCode();
                       //查询原单号的状态
                       if(StringHelper.isNotEmpty(oldWaybillCode)){
                           BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getWaybillDataForPrint(oldWaybillCode);
                           if(baseEntity!=null && baseEntity.getData()!=null && baseEntity.getData().getWaybillState()!=null){
                               if(WAYBILL_STATE_HALF_RECEIVE.equals(baseEntity.getData().getWaybillState().getWaybillState())){
                                   commonWaybill.setWeightFlagText("");
                               }
                           }
                       }
                   }
               }
           }

        //设置特殊要求
        loadSpecialRequirement(commonWaybill);

        //加载始发站点信息
        loadOriginalDmsInfo(commonWaybill,bigWaybillDto);
        waybillCommonService.setBasePrintInfoByWaybill(commonWaybill, tmsWaybill);
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
     * 根据预分拣站点信息加载始发及目的站点、滑道号、笼车号
     * @param waybill
     */
    private final void loadBasicData(final PrintWaybill waybill){
        BaseDmsStore baseDmsStore = new BaseDmsStore();
        baseDmsStore.setStoreId(waybill.getStoreId());//库房编号
        baseDmsStore.setCky2(waybill.getCky2());//cky2
        baseDmsStore.setOrgId(waybill.getOrgId());//机构编号
        baseDmsStore.setDmsId(waybill.getOriginalDmsCode());//分拣中心编号
        CrossPackageTagNew tag = null;
        //如果预分拣站点为0超区或者999999999EMS全国直发，则不用查询大全表
        if(null!=waybill.getPrepareSiteCode()&&waybill.getPrepareSiteCode()>ComposeService.PREPARE_SITE_CODE_NOTHING
                && !ComposeService.PREPARE_SITE_CODE_EMS_DIRECT.equals(waybill.getPrepareSiteCode())){
        	JdResult<CrossPackageTagNew> jdResult = baseMinorManager.queryCrossPackageTagForPrint(baseDmsStore, waybill.getPrepareSiteCode(), waybill.getOriginalDmsCode(),waybill.getOriginalCrossType());
            if(jdResult.isSucceed()) {
                tag=jdResult.getData();
            }else{
            	logger.warn("打印业务：未获取到滑道号及笼车号信息！"+jdResult.getMessage());
            }
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
    }

    /**
     * 逆向换单设置终端重量
     * @param context
     * @param commonWaybill
     */
    private void loadWaybillPackageWeight(WaybillPrintContext context, PrintWaybill commonWaybill){
        if(WaybillPrintOperateTypeEnum.SWITCH_BILL_PRINT.getType().equals(context.getRequest().getOperateType())){
            BigWaybillDto bigWaybillDto = context.getBigWaybillDto();
            if (bigWaybillDto != null) {
                Map<String, DeliveryPackageD> againWeightMap = getAgainWeightMap(bigWaybillDto.getPackageList());
                for(PrintPackage pack : commonWaybill.getPackList()){
                    DeliveryPackageD deliveryPackageD = againWeightMap.get(pack.getPackageCode());
                    if(deliveryPackageD != null){
                    	//设置包裹重量，优先使用AgainWeight，前面已经默认设置为GoodWeight
                    	if(NumberHelper.gt0(deliveryPackageD.getAgainWeight())){
                            pack.setWeight(deliveryPackageD.getAgainWeight());
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
        Map<String, DeliveryPackageD> result = null;
        if (packageDList != null && packageDList.size() > 0) {
            result = new HashMap<String, DeliveryPackageD>(packageDList.size());
            for (DeliveryPackageD deliveryPackageD : packageDList) {
                result.put(deliveryPackageD.getPackageBarcode(), deliveryPackageD);
            }
        }
        return result;
    }

    /**
     * 获取包裹序列号
     */
    private String getPackageIndex(String packageCode) {
        if (WaybillUtil.isPackageCode(packageCode)) {
            int currentPackageNum = WaybillUtil.getCurrentPackageNum(packageCode);
            int totalPackageNum = WaybillUtil.getPackNumByPackCode(packageCode);
            return currentPackageNum + "/" + totalPackageNum;
        }
        return "0/0";
    }

    /**
     * 截取包裹号后缀
     * @param packageCode
     * @return
     */
    private String getPackageSuffix(String packageCode){
        int index = -1;
        if (WaybillUtil.isPackageCode(packageCode)) {
            if (packageCode.indexOf("N") > 0 && packageCode.indexOf("S") > 0) {
                index = packageCode.indexOf("N");
            } else if (packageCode.indexOf("-") > 0 && (packageCode.split("-").length == 3 || packageCode.split("-").length == 4)) {
                index = packageCode.indexOf("-");
            }
        }
        if(index < 0){
            return null;
        } else{
            return packageCode.substring(index);
        }

    }

    /**
     * 加载始发分拣中心信息
     *
     */
    private void loadOriginalDmsInfo(WaybillPrintResponse printWaybill,BigWaybillDto bigWaybillDto) {
        Integer dmsCode = printWaybill.getOriginalDmsCode();
        String waybillCode = printWaybill.getWaybillCode();
        if (dmsCode == null || dmsCode <= 0) {
            logger.warn("参数中无始发分拣中心编码，从外部系统获取.运单号:" + waybillCode);
            com.jd.etms.waybill.domain.Waybill etmsWaybill = bigWaybillDto.getWaybill();
            WaybillPickup waybillPickup = bigWaybillDto.getWaybillPickup();

            //判断有没有仓Id
            if (etmsWaybill != null && etmsWaybill.getDistributeStoreId() != null && etmsWaybill.getCky2() != null) {
                dmsCode = basicSafInterfaceManager.getStoreBindDms("wms", etmsWaybill.getCky2(), etmsWaybill.getDistributeStoreId());
                logger.info("运单号:" + waybillCode + ".库房类型:wms+cky2:" + etmsWaybill.getCky2() + "+库房号:" +
                        etmsWaybill.getDistributeStoreId() + "对应的分拣中心:" + dmsCode);
            }

            //判断有没有揽收站点
            if (dmsCode == null && waybillPickup != null && waybillPickup.getPickupSiteId() != null && waybillPickup.getPickupSiteId() > 0) {
                BaseStaffSiteOrgDto dto = baseMajorManager.getBaseSiteBySiteId(waybillPickup.getPickupSiteId());
                if (dto != null) {
                    dmsCode = dto.getDmsId();
                }
                logger.info("运单号:" + waybillCode + ".揽收站点:" + waybillPickup.getPickupSiteId() + "对应的分拣中心:" + dmsCode);
            }

            //判断有没有寄件省
            if (dmsCode == null) {
                Integer consignerCityId = null;
                if (waybillPickup != null && waybillPickup.getConsignerCityId() != null) {
                    consignerCityId = waybillPickup.getConsignerCityId();
                    logger.info("运单号：" + waybillCode + "在运单中获取的寄件城市为：" + consignerCityId);
                } else if (etmsWaybill != null && StringUtils.isNotBlank(etmsWaybill.getConsignerAddress())) {
                    //调预分拣接口
                    AnalysisAddressResult addressResult = preseparateWaybillManager.analysisAddress(etmsWaybill.getConsignerAddress());
                    if (addressResult != null) {
                        consignerCityId = addressResult.getCityId();
                    }
                    logger.info("运单号：" + waybillCode + "根据寄件人地址获取到的寄件城市为:" + consignerCityId);
                }
                if (consignerCityId != null && consignerCityId > 0) {
                    dmsCode = baseMajorManager.getCityBindDms(consignerCityId);
                    logger.info("运单号:" + waybillCode + "寄件城市对应的分拣中心为：" + dmsCode);
                }
            }
        }
        logger.info("组装包裹标签始发分拣中心信息，运单号：" + waybillCode + "对应的始发分拣中心为:" + dmsCode);
        printWaybill.setOriginalDmsCode(dmsCode);
    }

    /**
     * B网根据始发和目的获取路由信息
     * @param printWaybill
     */
    private void loadWaybillRouter(WaybillPrintResponse printWaybill){
        //非B网的不用查路由
        if(!BusinessUtil.isB2b(printWaybill.getWaybillSign())){
            return;
        }

        Integer originalDmsCode = printWaybill.getOriginalDmsCode();
        Integer destinationDmsCode = printWaybill.getPurposefulDmsCode();

        //获取始发和目的的七位编码
        BaseStaffSiteOrgDto originalDms=baseMajorManager.getBaseSiteBySiteId(originalDmsCode);
        if (originalDms==null){
            return ;
        }
        BaseStaffSiteOrgDto destinationDms=baseMajorManager.getBaseSiteBySiteId(destinationDmsCode);
        if (destinationDms==null){
            return ;
        }

        //调路由的接口获取路由节点
        Date predictSendTime = new Date();
        RouteProductEnum routeProduct = null;

        /**
         * 当waybill_sign第62位等于1时，确定为B网营业厅运单:
         * 1.waybill_sign第80位等于1时，产品类型为“特惠运”--TB1
         * 2.waybill_sign第80位等于2时，产品类型为“特准运”--TB2
         */
        String waybillSign = printWaybill.getWaybillSign();
        if(BusinessUtil.isSignChar(waybillSign,62,'1')){
            if(BusinessUtil.isSignChar(waybillSign,80,'1')){
                routeProduct = RouteProductEnum.TB1;
            }else if(BusinessUtil.isSignChar(waybillSign,80,'2')){
                routeProduct = RouteProductEnum.TB2;
            }
        }

        String router=vrsRouteTransferRelationManager.queryRecommendRoute(originalDms.getDmsSiteCode(),destinationDms.getDmsSiteCode(),predictSendTime,routeProduct);

        if (StringUtils.isEmpty(router)){
            return;
        }
        //拼接路由站点的名称
        String[] siteArr=router.split("\\|");
        //有路由节点的话，加上发出和接收节点，数量一定会>2个
        if (siteArr.length < 2){
            return ;
        }

        Integer startNodeIndex = 0;
        Integer endNodeIndex = 0;
        //定位始发网点和目的网点在整条路由中的定位
        for (int i = 0 ; i< siteArr.length; i++){
            if(siteArr.equals(originalDms.getDmsSiteCode())){
                startNodeIndex = i;
            }
            if(siteArr.equals(destinationDms.getDmsSiteCode())){
                endNodeIndex = i;
            }
        }

        for (int i = startNodeIndex ;i <= endNodeIndex; i++){
            //获取站点信息
            BaseStaffSiteOrgDto baseStaffSiteOrgDto= baseMajorManager.getBaseSiteByDmsCode(siteArr[i]);
            if (baseStaffSiteOrgDto!=null){
                Integer provinceId = baseStaffSiteOrgDto.getProvinceId();
                String cityName = baseStaffSiteOrgDto.getCityName();
                //直辖市的城市显示直辖市
                if(isMunicipality(provinceId)){
                    cityName = baseStaffSiteOrgDto.getProvinceName();
                }
                try {
                    Method setRouterNode = printWaybill.getClass().getMethod("setRouterNode" + (i - startNodeIndex + 1), String.class);
                    setRouterNode.invoke(printWaybill, cityName);
                }catch (Exception e){
                    logger.error("获取路由信息,设置路由节点失败.",e);
                }
            }
        }
    }

    /**
     * 加载特殊要求信息
     * @param printWaybill
     */
    private void loadSpecialRequirement(WaybillPrintResponse printWaybill){
        String waybillSign = printWaybill.getWaybillSign();
        String specialRequirement = "";
        if(StringUtils.isNotBlank(waybillSign)){
            //签单返还
            if(BusinessUtil.isSignChar(waybillSign,4,'1')){
                specialRequirement = specialRequirement + SPECIAL_REQUIRMENT_SIGNBACK + ",";
            }
            //包装服务
            if(BusinessUtil.isSignChar(waybillSign,72,'1')){
                specialRequirement = specialRequirement + SPECIAL_REQUIRMENT_PACK + ",";
            }
            //重货上楼
            if(BusinessUtil.isSignChar(waybillSign,49,'1')){
                specialRequirement = specialRequirement + SPECIAL_REQUIRMENT_DELIVERY_UPSTAIRS + ",";
            }
            //送货入仓
            if(BusinessUtil.isSignChar(waybillSign,42,'1')){
                specialRequirement = specialRequirement + SPECIAL_REQUIRMENT_DELIVERY_WAREHOUSE + ",";
            }
        }
        if(StringUtils.isNotBlank(specialRequirement)){
            printWaybill.setSpecialRequirement(specialRequirement.substring(0,specialRequirement.length()-1));
        }
    }

    /**
     * 判断是否是直辖市 1:北京市  2：上海市  3：天津市 4：重庆市
     * @param provinceId
     * @return
     */
    private boolean isMunicipality(Integer provinceId){
        return municipalityList.contains(provinceId);
    }
}
