package com.jd.bluedragon.distribution.print.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.TextConstants;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.api.response.WaybillPrintResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.AirTransportService;
import com.jd.bluedragon.distribution.base.service.DmsBaseDictService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.popPrint.domain.PopPrint;
import com.jd.bluedragon.distribution.popPrint.service.PopPrintService;
import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import com.jd.bluedragon.distribution.print.domain.PrintPackage;
import com.jd.bluedragon.distribution.print.domain.PrintWaybill;
import com.jd.bluedragon.distribution.print.domain.SignConfig;
import com.jd.bluedragon.distribution.urban.domain.TransbillM;
import com.jd.bluedragon.distribution.urban.service.TransbillMService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.WaybillManageDomain;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ldop.basic.dto.BasicTraderInfoDTO;
import com.jd.ql.basic.domain.BaseDmsStore;
import com.jd.ql.basic.domain.CrossPackageTagNew;
import com.jd.ql.basic.ws.BasicSecondaryWS;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by wangtingwei on 2015/12/23.
 */

public class SimpleWaybillPrintServiceImpl implements WaybillPrintService {

    private static final Logger log = LoggerFactory.getLogger(SimpleWaybillPrintServiceImpl.class);

    @Autowired
    private PopPrintService popPrintService;

    @Autowired
    private BaseMinorManager baseMinorManager;

    @Autowired
    private BasicSecondaryWS basicSecondaryWS;

    @Autowired
    private AirTransportService airTransportService;

    @Autowired
    private TransbillMService transbillMService;

    @Autowired
    private WaybillCommonService waybillCommonService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private PreSortingSecondService preSortingSecondService;
    
    @Autowired
    private DmsBaseDictService dmsBaseDictService;

    private List<ComposeService> composeServiceList;
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
    @Override
    public InvokeResult<WaybillPrintResponse> getPrintWaybill(Integer dmsCode, String waybillCode, Integer targetSiteCode) {

        InvokeResult<WaybillPrintResponse> result=new InvokeResult<WaybillPrintResponse>();
        CallerInfo info = Profiler.registerInfo("DMSWEB.SimpleWaybillPrintServiceImpl.getPrintWaybill", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getWaybillDataForPrint(waybillCode);
            if(baseEntity != null && Constants.RESULT_SUCCESS == baseEntity.getResultCode()){
                loadWaybillInfo(result,baseEntity.getData(), dmsCode, targetSiteCode);
                if (null != result.getData()) {
                    loadPrintedData(result.getData());
                    loadBasicData(result.getData());
                    for (ComposeService service : composeServiceList) {
                        service.handle(result.getData(), dmsCode, targetSiteCode);
                    }
                }
            }else if(baseEntity != null && Constants.RESULT_SUCCESS != baseEntity.getResultCode()){
                result.error(baseEntity.getMessage());
            }else{
                result.error("查询运单信息为空");
            }
        }catch (Exception ex){
            Profiler.functionError(info);
            log.error("标签打印接口异常，运单号:{}",waybillCode,ex);
            result.error(ex);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return result;
    }

    /**
     * 加载运单基础数据
     * @param result
     * @param dmsCode
     * @param bigWaybillDto
     * @param targetSiteCode
     */
    private final void loadWaybillInfo(final InvokeResult<WaybillPrintResponse> result,BigWaybillDto bigWaybillDto, Integer dmsCode,Integer targetSiteCode){
        if (null!=bigWaybillDto.getWaybill()) {
            if(null==result.getData()){
                result.setData(new WaybillPrintResponse());
            }
            PrintWaybill commonWaybill=result.getData();
            com.jd.etms.waybill.domain.Waybill tmsWaybill=bigWaybillDto.getWaybill();
            BasicTraderInfoDTO dto = baseMinorManager.getBaseTraderById(tmsWaybill.getBusiId());
            WaybillManageDomain tmsWaybillManageDomain=bigWaybillDto.getWaybillState();
            commonWaybill.setWaybillCode(tmsWaybill.getWaybillCode());
            commonWaybill.setPopSupId(tmsWaybill.getConsignerId());
            commonWaybill.setPopSupName(tmsWaybill.getConsigner());
            commonWaybill.setBusiId(tmsWaybill.getBusiId());
            commonWaybill.setBusiName(tmsWaybill.getBusiName());
            commonWaybill.setBusiCode(dto!=null?dto.getTraderCode():null);
            commonWaybill.setQuantity(tmsWaybill.getGoodNumber());
            commonWaybill.setOrderCode(tmsWaybill.getVendorId());
            commonWaybill.setOriginalDmsCode(dmsCode);
            commonWaybill.setPrepareSiteCode(tmsWaybill.getOldSiteId());
            commonWaybill.setPrintAddress(tmsWaybill.getReceiverAddress());
            commonWaybill.setNewAddress(tmsWaybill.getNewRecAddr());
            commonWaybill.setPackagePrice(tmsWaybill.getCodMoney());
            commonWaybill.setWaybillSign(tmsWaybill.getWaybillSign());
            commonWaybill.setSendPay(tmsWaybill.getSendPay());
            commonWaybill.setDistributeType(tmsWaybill.getDistributeType());
            commonWaybill.setOriginalCrossType(BusinessUtil.getOriginalCrossType(tmsWaybill.getWaybillSign(), tmsWaybill.getSendPay()));
            if(bigWaybillDto.getWaybillState()!=null){
                commonWaybill.setWaybillStatus(bigWaybillDto.getWaybillState().getWaybillState()); //增加返回运单状态
            }
            if(StringUtils.isNotBlank(tmsWaybill.getSendPay())&&tmsWaybill.getSendPay().length()>QUICK_SIGN_BIT_ONE) {
                char luxurySign = tmsWaybill.getSendPay().charAt(LUXURY_SIGN_BIT);
                commonWaybill.setLuxuryText(luxurySign <= LUXURY_SIGN_END && luxurySign >= LUXURY_SIGN_START ? LUXURY_SIGN_TEXT : StringUtils.EMPTY);
                commonWaybill.setLuxuryText(commonWaybill.getLuxuryText() + ((tmsWaybill.getSendPay().charAt(QUICK_SIGN_BIT_ONE) == QUICK_SIGN_VALUE || tmsWaybill.getSendPay().charAt(QUICK_SIGN_BIT_TWO) == QUICK_SIGN_VALUE) ? QUICK_SIGN_TEXT : StringUtils.EMPTY));
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
            List<PrintPackage> packageList=new ArrayList<PrintPackage>();
            if(null!=bigWaybillDto.getPackageList()){
                for (DeliveryPackageD item:bigWaybillDto.getPackageList()){
                	PrintPackage pack=new PrintPackage();
                    pack.setPackageCode(item.getPackageBarcode());
                    pack.setWeight(item.getGoodWeight());
                    packageList.add(pack);
                }
            }
            commonWaybill.setPackList(packageList);
           waybillCommonService.setBasePrintInfoByWaybill(commonWaybill, tmsWaybill);
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
     * @param printWaybill
     */
    private final void loadPrintedData(final PrintWaybill printWaybill){
        List<PopPrint> popPrintList = this.popPrintService.findAllByWaybillCode(printWaybill.getWaybillCode());
        if(null==popPrintList||popPrintList.size()==0){
            return;
        }
        for (PopPrint popPrint : popPrintList) {
            if (Constants.PRINT_PACK_TYPE.equals(popPrint.getOperateType())) {
                for (int i = 0; i < printWaybill.getPackList().size(); i++) {
                    if (popPrint.getPackageBarcode().equals(printWaybill.getPackList().get(i).getPackageCode())) {
                        printWaybill.getPackList().get(i).setIsPrintPack(Boolean.TRUE);
                    }
                }
            } else if (Constants.PRINT_INVOICE_TYPE.equals(popPrint.getOperateType())) {
                printWaybill.setPrintInvoice(Boolean.TRUE);
            }
        }
    }
    /**
     * 
     * @param printWaybill 源运单信息
     */
    private final void loadBasicData(final PrintWaybill printWaybill){
        BaseDmsStore baseDmsStore = new BaseDmsStore();
        baseDmsStore.setStoreId(printWaybill.getStoreId());//库房编号
        baseDmsStore.setCky2(printWaybill.getCky2());//cky2
        baseDmsStore.setOrgId(printWaybill.getOrgId());//机构编号
        baseDmsStore.setDmsId(printWaybill.getOriginalDmsCode());//分拣中心编号
        CrossPackageTagNew tag = null;
        //如果预分拣站点为0超区或者999999999EMS全国直发，则不用查询大全表
        if(null!=printWaybill.getPrepareSiteCode()&&printWaybill.getPrepareSiteCode()>ComposeService.PREPARE_SITE_CODE_NOTHING
                && !ComposeService.PREPARE_SITE_CODE_EMS_DIRECT.equals(printWaybill.getPrepareSiteCode())){
            JdResult<CrossPackageTagNew> jdResult = baseMinorManager.queryCrossPackageTagForPrint(baseDmsStore, printWaybill.getPrepareSiteCode(), printWaybill.getOriginalDmsCode(),printWaybill.getOriginalCrossType());
            if(jdResult.isSucceed()) {
                tag=jdResult.getData();
            }else{
            	log.warn("打印业务：未获取到滑道号及笼车号信息:{}",jdResult.getMessage());
            }
        }
        if(null!=tag){
            if(tag.getIsAirTransport()!=null
                    && tag.getIsAirTransport()== ComposeService.AIR_TRANSPORT
                    &&null!=printWaybill.getBusiId()&&printWaybill.getBusiId().compareTo(0)>0){
                printWaybill.setIsAir(this.airTransportService.getAirSigns(printWaybill.getBusiId()));
            }
            //如果是自提柜，则打印的是自提柜的地址(基础资料大全表)，而非客户地址(运单系统)
            if(null!=tag.getIsZiTi()&&tag.getIsZiTi().equals(ComposeService.ARAYACAK_CABINET)){
                printWaybill.setIsSelfService(true);
                printWaybill.setPrintAddress(tag.getPrintAddress());
            }
            printWaybill.setPrepareSiteName(tag.getPrintSiteName());
            printWaybill.setPrintSiteName(tag.getPrintSiteName());
            printWaybill.setOriginalDmsCode(tag.getOriginalDmsId());
            printWaybill.setOriginalDmsName(tag.getOriginalDmsName());
            printWaybill.setPurposefulDmsCode(tag.getDestinationDmsId());
            printWaybill.setPurposefulDmsName(tag.getDestinationDmsName());
            printWaybill.setDestinationDmsName(tag.getDestinationDmsName());
            //笼车号
            printWaybill.setOriginalTabletrolley(tag.getOriginalTabletrolleyCode());
            printWaybill.setOriginalTabletrolleyCode(tag.getOriginalTabletrolleyCode());
            printWaybill.setPurposefulTableTrolley(tag.getDestinationTabletrolleyCode());
            printWaybill.setDestinationTabletrolleyCode(tag.getDestinationTabletrolleyCode());
            //道口号
            printWaybill.setOriginalCrossCode(tag.getOriginalCrossCode());
            printWaybill.setPurposefulCrossCode(tag.getDestinationCrossCode());
            printWaybill.setDestinationCrossCode(tag.getDestinationCrossCode());
        }
    }

    public List<ComposeService> getComposeServiceList() {
        return composeServiceList;
    }

    public void setComposeServiceList(List<ComposeService> composeServiceList) {
        this.composeServiceList = composeServiceList;
    }

    /**
     * 处理打标信息（SendPay、WaybillSign单个标位字典处理）
     */
    @Override
	public void dealSignTexts(String signStr,BasePrintWaybill target,String signConfigName){
		if(StringHelper.isNotEmpty(signStr)){
			Map<Integer,SignConfig> signConfigs = dmsBaseDictService.getSignConfigsByConfigName(signConfigName);
			String signText = "";
			for(Integer position:signConfigs.keySet()){
				SignConfig signConfig = signConfigs.get(position);
				signText = null;
				if(position>0&&position<=signStr.length()){
					signText = signConfig.getSignTexts().get(signStr.subSequence(position - 1, position));
				}
				if(signText==null){
					signText = "";
				}
				Field field = ObjectHelper.getField(target, signConfig.getFieldName());
				try {
					ObjectHelper.setValue(target, field, signText);
				} catch (Exception e) {
					log.error("属性设置异常:{}",signConfig.getFieldName(), e);
				}
			}
		}
	}

	@Override
	public void dealDicTexts(String dicKey, Integer dicTypeCode,
			BasePrintWaybill target) {
		if(StringHelper.isNotEmpty(dicKey) && NumberHelper.gt0(dicTypeCode)){
			Map<Integer,SignConfig> signConfigs = dmsBaseDictService.getSignConfigsByConfigName(Constants.DIC_NAME_PACKAGE_PRINT_DIC_CONFIG);
			if(signConfigs.containsKey(dicTypeCode)){
				SignConfig signConfig = signConfigs.get(dicTypeCode);
				if(signConfig.getSignTexts() != null && signConfig.getSignTexts().containsKey(dicKey)){
					Field field = ObjectHelper.getField(target, signConfig.getFieldName());
					try {
						ObjectHelper.setValue(target, field, signConfig.getSignTexts().get(dicKey));
					} catch (Exception e) {
						log.error("属性设置异常:{}",signConfig.getFieldName(), e);
					}
				}
			}
		}
	}

    /**
     * COD_MONEY 大于0的运单，不允许发送给三方站点（第三方-》第三方快递）
     * @param siteType 站点类型
     * @param subType 站点子类型
     * @param codMoney 货款金额
     * @return true 不能发送第三方快递,false 可以发送
     */
    @Override
    public boolean isCodMoneyGtZeroAndSiteThird(Integer siteType,Integer subType,Double codMoney) {
        //三方站点（第三方-》第三方快递）
        return NumberHelper.gt0(codMoney) && Objects.equals(Constants.THIRD_SITE_TYPE, siteType)
                && Objects.equals(Constants.THIRD_SITE_SUB_TYPE, subType);
    }
}
