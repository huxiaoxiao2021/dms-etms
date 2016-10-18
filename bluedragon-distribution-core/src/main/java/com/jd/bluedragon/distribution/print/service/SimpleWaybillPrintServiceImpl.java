package com.jd.bluedragon.distribution.print.service;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.AirTransportService;
import com.jd.bluedragon.distribution.popPrint.domain.PopPrint;
import com.jd.bluedragon.distribution.popPrint.service.PopPrintService;
import com.jd.bluedragon.distribution.print.domain.PrintPackage;
import com.jd.bluedragon.distribution.print.domain.PrintWaybill;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.basic.domain.BaseDmsStore;
import com.jd.ql.basic.domain.BaseResult;
import com.jd.ql.basic.domain.CrossPackageTagNew;
import com.jd.etms.waybill.api.WaybillQueryApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.WaybillManageDomain;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.ql.basic.domain.ReverseCrossPackageTag;
import com.jd.ql.basic.ws.BasicSecondaryWS;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangtingwei on 2015/12/23.
 */

public class SimpleWaybillPrintServiceImpl implements WaybillPrintService {

    private static final Log logger= LogFactory.getLog(SimpleWaybillPrintServiceImpl.class);

    @Autowired
    private WaybillQueryApi waybillQueryApi;

    @Autowired
    private PopPrintService popPrintService;

    @Autowired
    private BaseMinorManager baseMinorManager;

    @Autowired
    private BasicSecondaryWS basicSecondaryWS;

    @Autowired
    private AirTransportService airTransportService;


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

    @Override
    public InvokeResult<PrintWaybill> getPrintWaybill(Integer dmsCode, String waybillCode, Integer targetSiteCode) {

        InvokeResult<PrintWaybill> result=new InvokeResult<PrintWaybill>();
        try {
            loadWaybillInfo(result, dmsCode, waybillCode, targetSiteCode);
            if (null != result.getData()) {
                loadPrintedData(result.getData());
                loadBasicData(result.getData());
                for (ComposeService service : composeServiceList) {
                    service.handle(result.getData(), dmsCode, targetSiteCode);
                }
            }
        }catch (Exception ex){
            logger.error("标签打印接口异常",ex);
            result.error(ex);
        }
        return result;
    }

    /**
     * 加载运单基础数据
     * @param result
     * @param dmsCode
     * @param waybillCode
     * @param targetSiteCode
     */
    private final void loadWaybillInfo(final InvokeResult<PrintWaybill> result,Integer dmsCode,String waybillCode,Integer targetSiteCode){
        WChoice wChoice = new WChoice();
        wChoice.setQueryWaybillC(true);
        wChoice.setQueryWaybillE(true);
        wChoice.setQueryWaybillM(true);
        wChoice.setQueryPackList(true);
        BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryApi.getDataByChoice(waybillCode, wChoice);
        if (baseEntity != null
                && baseEntity.getData() != null
                &&null!=baseEntity.getData().getWaybill()) {
            if(null==result.getData()){
                result.setData(new PrintWaybill());
            }
            PrintWaybill commonWaybill=result.getData();
            com.jd.etms.waybill.domain.Waybill tmsWaybill=baseEntity.getData().getWaybill();
            WaybillManageDomain tmsWaybillManageDomain=baseEntity.getData().getWaybillState();
            commonWaybill.setWaybillCode(tmsWaybill.getWaybillCode());
            commonWaybill.setPopSupId(tmsWaybill.getConsignerId());
            commonWaybill.setPopSupName(tmsWaybill.getConsigner());
            commonWaybill.setBusiId(tmsWaybill.getBusiId());
            commonWaybill.setBusiName(tmsWaybill.getBusiName());
            commonWaybill.setQuantity(tmsWaybill.getGoodNumber());
            commonWaybill.setOrderCode(tmsWaybill.getVendorId());
            commonWaybill.setBusiOrderCode(tmsWaybill.getBusiOrderCode());//增加商家订单号字段
            commonWaybill.setOriginalDmsCode(dmsCode);
            commonWaybill.setPrepareSiteCode(tmsWaybill.getOldSiteId());
            commonWaybill.setPrintAddress(tmsWaybill.getReceiverAddress());
            commonWaybill.setNewAddress(tmsWaybill.getNewRecAddr());
            commonWaybill.setRoad(tmsWaybill.getRoadCode());
            commonWaybill.setPackagePrice(tmsWaybill.getCodMoney());
            commonWaybill.setWaybillSign(tmsWaybill.getWaybillSign());
            commonWaybill.setSendPay(tmsWaybill.getSendPay());
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
                Integer value= NumberUtils.createInteger(tmsWaybill.getSpareColumn1().trim());
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
            commonWaybill.setType(tmsWaybill.getWaybillType());
            commonWaybill.setRemark(tmsWaybill.getImportantHint());
            if(tmsWaybill.getPayment()!=null){
                if(tmsWaybill.getPayment()==ComposeService.ONLINE_PAYMENT_SIGN){
                    commonWaybill.setPackagePrice(ComposeService.ONLINE_PAYMENT);
                }
            }
            commonWaybill.setCustomerName(tmsWaybill.getReceiverName());
            commonWaybill.setCustomerContacts(concatPhone(tmsWaybill.getReceiverMobile(),tmsWaybill.getReceiverTel()));
            if(null!=tmsWaybillManageDomain){
                commonWaybill.setStoreId(tmsWaybillManageDomain.getStoreId());
                //commonWaybill.setStoreName(tmsWaybillManageDomain);
            }
            if(null!=baseEntity.getData().getPackageList()){
                List<PrintPackage> packageList=new ArrayList<PrintPackage>(baseEntity.getData().getPackageList().size());{
                    for (DeliveryPackageD item:baseEntity.getData().getPackageList()){
                        PrintPackage pack=new PrintPackage();
                        pack.setPackageCode(item.getPackageBarcode());
                        pack.setWeight(item.getGoodWeight());
                        packageList.add(pack);
                    }
                }
                commonWaybill.setPackList(packageList);
            }
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
            BaseResult<CrossPackageTagNew> baseResult = baseMinorManager.getCrossPackageTagByPara(baseDmsStore, waybill.getPrepareSiteCode(), waybill.getOriginalDmsCode());
            if(BaseResult.SUCCESS==baseResult.getResultCode()&&null!=baseResult.getData()) {
                tag=baseResult.getData();
            }else{
                com.jd.ql.basic.domain.BaseResult<ReverseCrossPackageTag> reverseResult= basicSecondaryWS.getReverseCrossPackageTag(waybill.getOriginalDmsCode(),waybill.getPrepareSiteCode());
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
                }
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
            waybill.setOriginalDmsCode(tag.getOriginalDmsId());
            waybill.setOriginalDmsName(tag.getOriginalDmsName());
            waybill.setPurposefulDmsCode(tag.getDestinationDmsId());
            waybill.setPurposefulDmsName(tag.getDestinationDmsName());
            //笼车号
            waybill.setOriginalTabletrolley(tag.getOriginalTabletrolleyCode());
            waybill.setPurposefulTableTrolley(tag.getDestinationTabletrolleyCode());
            //道口号
            waybill.setOriginalCrossCode(tag.getOriginalCrossCode());
            waybill.setPurposefulCrossCode(tag.getDestinationCrossCode());
        }
    }

    public List<ComposeService> getComposeServiceList() {
        return composeServiceList;
    }

    public void setComposeServiceList(List<ComposeService> composeServiceList) {
        this.composeServiceList = composeServiceList;
    }

    
}
