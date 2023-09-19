package com.jd.bluedragon.common.service.impl;

import com.alibaba.fastjson.JSON;
import com.jd.addresstranslation.api.base.ExternalAddressRequest;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.TextConstants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.domain.Pack;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.domain.WaybillErrorDomain;
import com.jd.bluedragon.common.domain.WaybillExtVO;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.base.*;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.core.jsf.address.DmsExternalJDAddressResponse;
import com.jd.bluedragon.core.jsf.address.ExternalAddressToJDAddressServiceManager;
import com.jd.bluedragon.core.jsf.presort.AoiBindRoadMappingData;
import com.jd.bluedragon.core.jsf.presort.AoiBindRoadMappingQuery;
import com.jd.bluedragon.core.jsf.presort.AoiServiceManager;
import com.jd.bluedragon.distribution.api.request.WaybillPrintRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.order.ws.OrderWebService;
import com.jd.bluedragon.distribution.popPrint.domain.PopPrint;
import com.jd.bluedragon.distribution.popPrint.service.PopPrintService;
import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import com.jd.bluedragon.distribution.print.domain.TemplateGroupEnum;
import com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum;
import com.jd.bluedragon.distribution.print.service.ComposeService;
import com.jd.bluedragon.distribution.print.service.HideInfoService;
import com.jd.bluedragon.distribution.print.service.WaybillPrintService;
import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintContext;
import com.jd.bluedragon.distribution.product.domain.Product;
import com.jd.bluedragon.distribution.product.service.ProductService;
import com.jd.bluedragon.distribution.reprint.service.ReprintRecordService;
import com.jd.bluedragon.distribution.waybill.enums.WaybillVasEnum;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.*;
import com.jd.bluedragon.utils.*;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.etms.api.common.enums.RouteProductEnum;
import com.jd.etms.cache.util.EnumBusiCode;
import com.jd.etms.waybill.api.WaybillPackageApi;
import com.jd.etms.waybill.api.WaybillPickupTaskApi;
import com.jd.etms.waybill.domain.*;
import com.jd.etms.waybill.dto.*;
import com.jd.preseparate.vo.external.AnalysisAddressResult;
import com.jd.ql.basic.dto.BaseSiteInfoDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Service("waybillCommonService")
public class WaybillCommonServiceImpl implements WaybillCommonService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${waybill.error.max.for.size:100}")
    private Integer maxForSize;

    private static final int REST_CODE_SUC = 1;
    /**
     * 需要显示特快送标识的产品名称
     */
	private static final Set<String> TKS_PRODUCT_NAMES = new HashSet<String>();
	static {
		TKS_PRODUCT_NAMES.add(TextConstants.PRODUCT_NAME_TKS);
		TKS_PRODUCT_NAMES.add(TextConstants.PRODUCT_NAME_TKSJR);
		TKS_PRODUCT_NAMES.add(TextConstants.PRODUCT_NAME_TKSCC);
		TKS_PRODUCT_NAMES.add(TextConstants.PRODUCT_NAME_SXTK);
	}
    @Autowired
    private ProductService productService;
    @Autowired
    private ReprintRecordService reprintRecordService;
    /**
     * 运单包裹查询
     */
    @Autowired
    WaybillPackageApi waybillPackageApi;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private OrderWebService orderWebService;
    @Autowired
    private BaseService baseService;
    @Autowired
    private SiteService siteService;
    @Autowired
    private WaybillQueryManager waybillQueryManager;
    @Autowired
    private PopPrintService popPrintService;
    @Autowired
    private WaybillPrintService waybillPrintService;
    @Autowired
    private WaybillPickupTaskApi waybillPickupTaskApi;

    @Autowired
    HideInfoService hideInfoService;

    @Autowired
    private BasicSafInterfaceManager basicSafInterfaceManager;

    @Autowired
    private PreseparateWaybillManager preseparateWaybillManager;

    @Autowired
    private VrsRouteTransferRelationManager vrsRouteTransferRelationManager;
    @Autowired
    SysConfigService sysConfigService;
    @Autowired
    private WaybillService waybillService;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    @Autowired
    @Qualifier("aoiServiceManager")
    private AoiServiceManager aoiServiceManager;

    @Autowired
    private BasicGoodsAreaManager basicGoodsAreaManager;

    @Autowired
    private ExternalAddressToJDAddressServiceManager externalAddressToJDAddressServiceManager;

    private static final String STORAGEWAYBILL_REDIS_KEY_PREFIX = "STORAGEWAY_KEY_";


    @Value("${WaybillCommonServiceImpl.additionalComment:http://www.jdwl.com   客服电话：950616}")
    private String additionalComment;

    @Value("${WaybillCommonServiceImpl.popularizeMatrixCode:https://logistics-mrd.jd.com/express/index.html?source=yingxiao_miantie}")
    private String popularizeMatrixCode;
    private String POPULARIZEMATRIXCODEDESC_DEFAULT = "扫码寄快递";
    private String POPULARIZEMATRIXCODEDESC_PACKAGE_SAY = "包裹有话说";
    /**
     * 惊喜打服务
     */
    private String POPULARIZEMATRIXCODEDESC_JXD = "扫码收祝福";
    /**
     * 京东logo的文件路径
     */
    private final String LOGO_IMAGE_KEY_JD="JDLogo.gif";


    /**
     * 验视
     */
    private final String EXAMINE_FLAG_COMMEN="[已验视]";
    private final String EXAMINE_FLAG_COMMEN_BJ="[北京已验视]";

    /**
     * 安检
     */
    private final String SECURITY_CHECK="[已安检]";

    /**
     * 包裹标签特殊要求
     */
    private static final String SPECIAL_REQUIRMENT_SIGNBACK ="签单返还";
    private static final String SPECIAL_REQUIRMENT_PACK="包装";
    private static final String SPECIAL_REQUIRMENT_DELIVERY_UPSTAIRS="重货上楼";
    private static final String SPECIAL_REQUIRMENT_JZSC="精准送仓";
    private static final String SPECIAL_REQUIRMENT_DELIVERY_WAREHOUSE="送货入仓";
    private static final String SPECIAL_REQUIRMENT_SPEED_DELIVERY_WAREHOUSE="极速到仓";
    private static final String SPECIAL_REQUIRMENT_LOAD_CAR = "装车";
    private static final String SPECIAL_REQUIRMENT_UNLOAD_CAR = "卸车";
    private static final String SPECIAL_REQUIRMENT_LOAD_UNLOAD_CAR = "装卸车";
    private static final String SPECIAL_REQUIREMENT_TE_AN_SONG = "特安";

    /**
     * B网医药冷链温层
     */
    private static final String DISTRIBUTE_TYPE_COLD = "冷藏";
    private static final String DISTRIBUTE_TYPE_COOL = "阴凉";
    private static final String DISTRIBUTE_TYPE_CONTROL_TEMP = "控温";
    private static final String DISTRIBUTE_TYPE_NORMAL = "常温";
    private static final String DISTRIBUTE_TYPE_FREEZING ="冷冻";
    private static final String DISTRIBUTE_TYPE_PRECISION_COLD = "精准冷藏";

    private static final String STORE_TYPE_WMS = "wms";

    private static final Integer CUSTOMER_VIDEO = 19;

    @Override
    public Waybill findByWaybillCode(String waybillCode) {
        Waybill waybill = null;

        try {
            WChoice wChoice = new WChoice();
            wChoice.setQueryWaybillC(true);
            wChoice.setQueryWaybillE(true);
            wChoice.setQueryWaybillM(true);
            BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryManager.getDataByChoice(waybillCode, wChoice);
            if (baseEntity != null && baseEntity.getData() != null) {
                waybill = this.convWaybillWS(baseEntity.getData(), false, false);
            }
            this.log.debug("运单号【{}】调用运单getDataByChoice()接口数据成功",waybillCode);
        } catch (Exception e) {
            this.log.error("运单号【{}】调用运单getDataByChoice()接口异常",waybillCode, e);
        }
        if (waybill == null) {
            // 无数据
            this.log.warn("运单号【{}】的调用运单getDataByChoice()接口数据为空",waybillCode);
        }
        return waybill;
    }

    /**
     * 根据运单号查询订单号 (仅限自营)
     *
     * @param waybillCode
     * @return
     */
    @Override
    public Long findOrderIdByWaybillCode(String waybillCode) {
        try {
            if(StringUtils.isBlank(waybillCode)){
                return null;
            }else if(SerialRuleUtil.isMatchNumeric(waybillCode)){
                return Long.valueOf(waybillCode);
            }else if(WaybillUtil.isWaybillCode(waybillCode)){
                Waybill waybill = findByWaybillCode(waybillCode);
                if(waybill!=null && StringUtils.isNotBlank(waybill.getOrderId()) && SerialRuleUtil.isMatchNumeric(waybill.getOrderId())){
                    return Long.valueOf(waybill.getOrderId());
                }else{
                    log.warn("获取订单号失败，入参:{}", waybillCode);
                    return null;
                }
            }else{
                log.warn("获取订单号错误，入参:{}",waybillCode);
                return null;
            }
        }catch (Exception e){
            log.error("获取订单号异常，入参:{}",waybillCode,e);
        }
        return null;
    }

    @Override
    public Waybill findWaybillAndPack(String waybillCode) {
        Waybill waybill = null;

        try {
            WChoice wChoice = new WChoice();
            wChoice.setQueryWaybillC(true);
            wChoice.setQueryWaybillE(true);
            wChoice.setQueryWaybillM(true);
            wChoice.setQueryPackList(true);
            BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryManager.getDataByChoice(
                    waybillCode, wChoice);
            if (baseEntity != null && baseEntity.getData() != null) {
                waybill = this.convWaybillWS(baseEntity.getData(), true, true);
                WaybillManageDomain waybillState = baseEntity.getData().getWaybillState();
                if (waybillState != null) {
                    waybill.setStoreId(waybillState.getStoreId());
                    waybill.setCky2(waybillState.getCky2());
                }
                if (Waybill.isInvalidWaybill(waybill)) {
                    this.log.warn("运单号【{}】验证运单数据缺少必要字段，运单【{}】",waybillCode,waybill);
                    return null;
                }
            }
            this.log.debug("运单号【{}】调用运单WSS数据成功", waybillCode);

        } catch (Exception e) {
            this.log.error("运单号【{}】调用运单WSS异常",waybillCode, e);
        }

        return waybill;
    }

    /**
     * 补全异常运单信息
     * @param waybillCodeC 正确运单号
     * @param waybillCodeE 错误运单号(运单生成的重复运单号)
     * @return
     */
    @Override
    public List<WaybillErrorDomain> complementWaybillError(String waybillCodeC, String waybillCodeE) {

        List<WaybillErrorDomain> waybillErrorDomains = new ArrayList<>();
        if(StringUtils.isBlank(waybillCodeC) || StringUtils.isBlank(waybillCodeE)){
            //缺少入参直接返回
            log.error("complementWaybillError 缺少参数 {},{}",waybillCodeC,waybillCodeE);
            return waybillErrorDomains;
        }

        //调用运单补全数据
        waybillErrorDomains.addAll(complementWaybillErrorOfWaybill(waybillCodeC));
        waybillErrorDomains.addAll(complementWaybillErrorOfWaybill(waybillCodeE));
        return waybillErrorDomains;
    }

    @Override
    public List<WaybillErrorDomain> complementWaybillError(String waybillCodeC) {

        List<WaybillErrorDomain> waybillErrorDomains = new ArrayList<>();
        if(StringUtils.isBlank(waybillCodeC)){
            //缺少入参直接返回
            log.error("complementWaybillError 缺少参数 {}", waybillCodeC);
            return waybillErrorDomains;
        }

        //调用运单补全数据
        waybillErrorDomains.addAll(complementWaybillErrorOfWaybill(waybillCodeC));
        return waybillErrorDomains;
    }

    /**
     * 补全异常运单信息
     * @param waybillCode
     * @return
     */
    private List<WaybillErrorDomain> complementWaybillErrorOfWaybill(String waybillCode){

        List<WaybillErrorDomain> waybillErrorDomains = new ArrayList<>();
        //调用运单补全数据
        WChoice wChoice = new WChoice();
        wChoice.setQueryWaybillC(true);
        wChoice.setQueryWaybillE(true);
        wChoice.setQueryWaybillM(true);
        wChoice.setQueryGoodList(true);
        wChoice.setQueryPackList(true);
        BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode,wChoice);

        if (baseEntity != null && baseEntity.getData() != null && baseEntity.getData().getWaybill() != null && !CollectionUtils.isEmpty(baseEntity.getData().getPackageList())) {
            int i = 0;
            com.jd.etms.waybill.domain.Waybill waybill = baseEntity.getData().getWaybill();
            for(DeliveryPackageD packageD : baseEntity.getData().getPackageList()){
                WaybillErrorDomain waybillErrorDomain = new WaybillErrorDomain();
                waybillErrorDomain.setPackageCode(packageD.getPackageBarcode());
                waybillErrorDomain.setWaybillCode(packageD.getWaybillCode());
                waybillErrorDomain.setOrderId(waybill.getVendorId());
                waybillErrorDomain.setReceiverName(waybill.getReceiverName());
                waybillErrorDomain.setReceiverMobile(waybill.getReceiverMobile());
                waybillErrorDomain.setReceiverAddress(waybill.getReceiverAddress());
                if(i++ < maxForSize ){
                    //最大判断已打印数据量 判断是否已补打
                    if(reprintRecordService.isBarCodeRePrinted(packageD.getPackageBarcode())){
                        waybillErrorDomain.setRemark("已补打");
                    }
                }
                waybillErrorDomains.add(waybillErrorDomain);
            }
        }
        return waybillErrorDomains;
    }

    @Override
    public Waybill findWaybillAndPack(String waybillCode, boolean isQueryWaybillC, boolean QueryWaybillE, boolean QueryWaybillM, boolean isQueryPackList) {
        Waybill waybill = null;

        try {
            WChoice wChoice = new WChoice();
            wChoice.setQueryWaybillC(isQueryWaybillC);
            wChoice.setQueryWaybillE(QueryWaybillE);
            wChoice.setQueryWaybillM(QueryWaybillM);
            wChoice.setQueryPackList(isQueryPackList);
            BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryManager.getDataByChoice(
                    waybillCode, wChoice);
            if (baseEntity != null && baseEntity.getData() != null) {
                waybill = this.convWaybillWS(baseEntity.getData(), true, true);
                if (Waybill.isInvalidWaybill(waybill)) {
                    this.log.warn("运单号【{}】验证运单数据缺少必要字段，运单【{}】",waybillCode,waybill);
                    return null;
                }
            }
            this.log.debug("运单号【{}】调用运单WSS数据成功",waybillCode);

        } catch (Exception e) {
            this.log.error("运单号【{}】调用运单WSS异常",waybillCode, e);
        }

        return waybill;
    }

    @Override
    public InvokeResult<Waybill> getReverseWaybill(String oldWaybillCode) {
        InvokeResult<Waybill> result = new InvokeResult<Waybill>();
        Waybill waybill = null;
        CallerInfo info = Profiler.registerInfo("DMSWEB.waybillCommonService.getReverseWaybill", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            WChoice wChoice = new WChoice();
            wChoice.setQueryWaybillC(true);
            wChoice.setQueryWaybillE(true);
            wChoice.setQueryWaybillM(true);
            wChoice.setQueryPackList(true);
            BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryManager.getReturnWaybillByOldWaybillCode(
                    oldWaybillCode, wChoice);
            log.info("WaybillQueryManager.getReturnWaybillByOldWaybillCode waybillCode: {} result: {}", JsonHelper.toJson(oldWaybillCode), JsonHelper.toJson(baseEntity));
            if (baseEntity != null && baseEntity.getData() != null) {
                waybill = this.convWaybillWS(baseEntity.getData(), true, true);
                /*if (Waybill.isInvalidWaybill(waybill)) {
                    this.log.error("运单号【 " + oldWaybillCode + "】验证运单数据缺少必要字段，运单【" + waybill + "】");
                    result.customMessage(-1,"运单缺少必要数据");
                    return result;
                }*/
            }
        } catch (Throwable e) {
            Profiler.functionError(info);
            this.log.error("运单号【{}】调用运单JSF异常",oldWaybillCode, e);
            result.error(e);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        result.setData(waybill);
        return result;
    }

    /**
     * 获取订单信息通过订单中间件
     *
     * @param waybillCode 运单号
     * @return
     */
    @JProfiler(jKey = "DMSWEB.WaybillCommonServiceImpl.getWaybillFromOrderService", mState = {JProEnum.TP})
    public Waybill getWaybillFromOrderService(String waybillCode) {
        if (StringUtils.isBlank(waybillCode)) {
            this.log.warn("通过运单号调用非运单接口获取运单数据，传入参数为空");
            return null;
        }
        Long orderId = findOrderIdByWaybillCode(waybillCode);
        if(orderId==null){
            this.log.warn("通过运单号调用非运单接口获取运单数据，运单号:{}未获取对应订单号,立即返回NULL",waybillCode);
            return null;
        }
        Waybill waybill = this.orderWebService.getWaybillByOrderId(orderId);
        List<Product> products = this.productService.getOrderProducts(orderId);

        if (waybill != null) {
            this.log.debug("通过运单号调用非运单接口获取运单数据，调用运单中间件结束，运单【{}】，非POP，运单类型【{}】",waybill,waybill.getType());
            waybill.setProList(products);
        } else {
            this.log.warn("通过运单号调用非运单接口获取运单数据，运单号:{}调用运单中间件结束，运单为空",waybillCode);
        }
        return waybill;
    }

    @JProfiler(jKey = "DMSWEB.WaybillCommonServiceImpl.getHisWaybillFromOrderService", mState = {JProEnum.TP})
    public Waybill getHisWaybillFromOrderService(String waybillCode) {
        if (StringUtils.isBlank(waybillCode)) {
            this.log.warn("通过运单号调用非运单接口获取运单数据，传入参数为空");
            return null;
        }


        Waybill waybill = this.findByWaybillCode(waybillCode);

        if (waybill == null) {
            waybill = this.orderWebService.getHisWaybillByOrderId(Long.valueOf(waybillCode));
        }

        List<Product> products = this.productService.getProductsByWaybillCode(waybillCode);
        if (waybill != null) {
            this.log.info("通过运单号调用非运单接口获取运单数据，调用运单中间件结束，运单【{}】，非POP，运单类型【{}】",waybill,waybill.getType());
            waybill.setProList(products);
        } else {
            this.log.warn("通过运单号调用非运单接口获取运单数据，运单号:{}调用运单中间件结束，运单为空",waybillCode);
        }
        return waybill;
    }
    /**
     * 转换运单基本信息
     *
     * @param
     * @return
     */
    public Waybill convWaybillWS(BigWaybillDto bigWaybillDto, boolean isSetName, boolean isSetPack){
    	return this.convWaybillWS(bigWaybillDto, isSetName, isSetPack,false,false);
    }
    /**
     * 转换运单基本信息
     *
     * @param
     * @return
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.WaybillCommonServiceImpl.convWaybillWS",mState={JProEnum.TP,JProEnum.FunctionError})
    public Waybill convWaybillWS(BigWaybillDto bigWaybillDto, boolean isSetName, boolean isSetPack,
			boolean loadPrintInfo,boolean loadPweight){
        if (bigWaybillDto == null || bigWaybillDto.getWaybill() == null) {
            return null;
        }
        com.jd.etms.waybill.domain.Waybill waybillWS = bigWaybillDto.getWaybill();
        String waybillCode = waybillWS.getWaybillCode();
        Waybill waybill = new Waybill();
        waybill.setWaybillCode(waybillWS.getWaybillCode());
        waybill.setPopSupId(waybillWS.getConsignerId());
        waybill.setPopSupName(waybillWS.getConsigner());
        waybill.setBusiId(waybillWS.getBusiId());
        waybill.setBusiName(waybillWS.getBusiName());
        waybill.setRoad(waybillWS.getRoadCode());
        // 设置站点
        waybill.setSiteCode(waybillWS.getOldSiteId());

        if (isSetName) {
            dealWaybillSiteName(waybill);
        }

        waybill.setPaymentType(waybillWS.getPayment());
        waybill.setQuantity(waybillWS.getGoodNumber());
        waybill.setWeight(waybillWS.getGoodWeight());
        waybill.setAddress(waybillWS.getReceiverAddress());
        waybill.setOrgId(waybillWS.getArriveAreaId());
        waybill.setSendPay(waybillWS.getSendPay());
        waybill.setType(waybillWS.getWaybillType());
        waybill.setTransferStationId(waybillWS.getTransferStationId());
        waybill.setCrossCode(waybillWS.getSlideCode());
        waybill.setDistributeStoreId(waybillWS.getDistributeStoreId());

        waybill.setWaybillSign(waybillWS.getWaybillSign());
        waybill.setImportantHint(waybillWS.getImportantHint());
        waybill.setOrderId(waybillWS.getVendorId());
        waybill.setBusiOrderCode(waybillWS.getBusiOrderCode());
        waybill.setCodMoney(NumberHelper.getDoubleValue(waybillWS.getCodMoney()));
        waybill.setSendPayMap(JsonHelper.json2MapByJSON(waybillWS.getWaybillExt() == null ? null : waybillWS.getWaybillExt().getSendPayMap()));
        waybill.setWaybillExtVO(convertToOwnWaybillExtVO(waybillWS.getWaybillExt()));
        if (isSetPack) {
        	//存放包裹的复重及打印信息
        	Map<String,PackOpeFlowDto> packOpeFlows = null;
        	Map<String,PopPrint> popPrints = null;
        	//获取该运单号的打印记录
        	if(loadPrintInfo){
        		popPrints = new HashMap<String,PopPrint>();
                try {
                	List<PopPrint> popPrintList = this.popPrintService.findAllByWaybillCode(waybillCode);
                    if(popPrintList != null){
                    	for (PopPrint popPrint : popPrintList) {
                    		popPrints.put(popPrint.getPackageBarcode(), popPrint);
                        }
                    }
                }catch(Exception ex){
                	log.error("加载运单包裹打印信息失败！waybillCode={}", waybillCode,ex);
                }
        	}
        	//获取该运单号的复重信息
        	if(loadPweight){
        		packOpeFlows = new HashMap<String,PackOpeFlowDto>();
                try {
                	packOpeFlows = this.getPackOpeFlowsByOpeType(waybillCode, Constants.PACK_OPE_FLOW_TYPE_PSY_REC);
                }catch(Exception ex){
                	log.error("加载运单包裹打印信息失败！waybillCode={} ", waybillCode,ex);
                }
        	}
            List<DeliveryPackageD> ds = bigWaybillDto.getPackageList();
            if (ds == null || ds.size() <= 0) {
                this.log.warn("转换包裹信息 --> 运单号【{}】,原始运单数据集bigWaybillDto为空或size为空",waybill.getWaybillCode());
            } else {
                // 转换包裹信息
                this.log.debug("转换包裹信息 --> 运单号：{}转换包裹信息, 包裹数量为:{}" ,waybill.getWaybillCode(), ds.size());
                if (BusinessHelper.checkIntNumRange(ds.size())) {
                	waybill.setPackageNum(ds.size());
                    List<Pack> packList = new ArrayList<Pack>();
                    for (DeliveryPackageD d : ds) {
                        Pack pack = new Pack();
                        pack.setWaybillCode(d.getWaybillCode());
                        pack.setPackCode(d.getPackageBarcode());
                        pack.setPackageCode(d.getPackageBarcode());
                        pack.setPackageIndex(WaybillUtil.getPackageIndex(d.getPackageBarcode()));
                        pack.setPackageSuffix(WaybillUtil.getPackageSuffix(d.getPackageBarcode()));
                        log.info("包裹：{}，复重：{}",pack.getPackageCode(),d.getAgainWeight());
                        if (d.getAgainWeight() != null) {
                            pack.setWeight(String.valueOf(d.getAgainWeight()));
                            pack.setPackageWeight(d.getAgainWeight() + "kg");
                        }
                        if (StringUtils.isNotEmpty(d.getPackageBarcode())) {
                            pack.setPackSerial(WaybillUtil.getPackIndexByPackCode(d.getPackageBarcode()));
                        } else {
                            this.log.warn("转换包裹信息 --> 运单号：{}生成包裹号,包裹号为空",waybill.getWaybillCode());
                        }
                        pack.setPackId(d.getPackageId());
                        //设置打印信息
                        if(loadPrintInfo && popPrints.containsKey(d.getPackageBarcode())){
                        	PopPrint popPrint = popPrints.get(d.getPackageBarcode());
                            if (Constants.PRINT_PACK_TYPE.equals(popPrint.getOperateType())) {
                            	pack.setIsPrintPack(Waybill.IS_PRINT_PACK);
                            } else if (Constants.PRINT_INVOICE_TYPE.equals(popPrint.getOperateType())) {
                                waybill.setIsPrintInvoice(Waybill.IS_PRINT_INVOICE);
                            }
                        }
                        //设置复重信息
                        if(loadPweight && packOpeFlows.containsKey(d.getPackageBarcode())){
                        	PackOpeFlowDto packOpeFlowDto = packOpeFlows.get(d.getPackageBarcode());
                            if(packOpeFlowDto.getpWeight()!=null){
                                pack.setpWeight(String.valueOf(packOpeFlowDto.getpWeight()));
                            }
                        }
                        packList.add(pack);
                    }
                    waybill.setPackList(packList);
                } else {
                    this.log.warn("转换包裹信息【运单返回】 --> 运单号：{}, 包裹数量为:{}" ,waybill.getWaybillCode() , ds.size());
                }
            }
        }

        return waybill;
    }

    private WaybillExtVO convertToOwnWaybillExtVO(WaybillExt waybillExt) {
        return waybillExt == null
                ? null : new WaybillExtVO()
                .clearanceType(waybillExt.getClearanceType())
                .startFlowDirection(waybillExt.getStartFlowDirection())
                .endFlowDirection(waybillExt.getEndFlowDirection());
    }

    private void dealWaybillSiteName(Waybill waybill) {
        if (waybill == null) {
            return;
        }
        // 设置站点
        Integer siteCode = waybill.getSiteCode();
        String waybillCode = waybill.getWaybillCode();
        // 调用获取站点接口
        if (siteCode != null) {
            waybill.setSiteCode(siteCode);
            // 根据站点ID获取站点Name
            BaseStaffSiteOrgDto baseStaffSiteOrgDto = this.baseMajorManager
                    .getBaseSiteBySiteId(siteCode);
            if (baseStaffSiteOrgDto != null) {
                waybill.setSiteName(baseStaffSiteOrgDto.getSiteName());
                this.log.debug("运单号为【{}】  调用接口设置站点名称成功【{}-{}】" ,waybillCode,siteCode ,waybill.getSiteName());
            } else {
                this.log.warn("运单号为【{}】  查找不到站点【{}】相关信息",waybillCode,siteCode);
            }
        }

        // 设置中转站站点名称
        Integer transferStationId = waybill.getTransferStationId();
        if (transferStationId != null) {
            waybill.setTransferStationId(transferStationId);
            // 根据中转站站点ID获取中转站站点Name
            BaseStaffSiteOrgDto baseStaffSiteOrgDto = this.baseMajorManager
                    .getBaseSiteBySiteId(transferStationId);
            if (baseStaffSiteOrgDto != null) {
                waybill.setTransferStationName(baseStaffSiteOrgDto.getSiteName());
            } else {
                this.log.warn("运单号为【{}】  查找不到中转站站点【{}】 相关信息" ,waybillCode, transferStationId );
            }
        }
    }


    @Override
    public Waybill findWaybillAndGoods(String waybillCode) {
        Waybill waybill = null;
        List<com.jd.bluedragon.distribution.product.domain.Product> products = new ArrayList<com.jd.bluedragon.distribution.product.domain.Product>();
        try {
            WChoice wChoice = new WChoice();
            wChoice.setQueryWaybillC(true);
            wChoice.setQueryWaybillE(true);
            wChoice.setQueryWaybillM(true);
            wChoice.setQueryGoodList(true);

            BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryManager.getDataByChoice(
                    waybillCode, wChoice);
            if (baseEntity != null && baseEntity.getData() != null) {
                waybill = this.convWaybillWS(baseEntity.getData(), false, false);

                if (waybill == null) {
                    this.log.warn("运单号【{}】调用运单接口成功，运单不存在",waybillCode);
                    return null;
                }

                if (baseEntity.getData().getWaybillState() != null && baseEntity.getData().getWaybillState().getStoreId() != null) {
                    this.log.warn("获取运单StoreId失败");
                    waybill.setStoreId(baseEntity.getData().getWaybillState().getStoreId());
                }
                List<Goods> goodses = baseEntity.getData().getGoodsList();
                if (goodses == null || goodses.size() == 0) {
                    this.log.warn("调用运单接口获得商品明细数据为空, waybill:{}", waybillCode);
                }
                if(goodses != null && !goodses.isEmpty()){
                    for (Goods goods : goodses) {
                        com.jd.bluedragon.distribution.product.domain.Product product = new com.jd.bluedragon.distribution.product.domain.Product();
                        product.setProductId(goods.getSku());
                        product.setName(goods.getGoodName());
                        product.setQuantity(goods.getGoodCount());
                        product.setPrice(BigDecimalHelper.toBigDecimal(goods.getGoodPrice()));
                        products.add(product);
                    }
                }

                waybill.setProList(products);
            }
        } catch (Exception e) {
            this.log.error("运单号【{}】调用运单异常",waybillCode, e);
        }

        return waybill;
    }

	@Override
	public Map<String,PackOpeFlowDto> getPackOpeFlowsByOpeType(String waybillCode,Integer opeType) {
        CallerInfo callerInfo = Profiler.registerInfo("dms.web.WaybillCommonServiceImpl.getPackOpeFlowsByOpeType",
                Constants.UMP_APP_NAME_DMSWEB, false, true);
        try {
            Map<String,PackOpeFlowDto> res = new TreeMap<String,PackOpeFlowDto>();
            //校验传入参数是否合法
            if(StringHelper.isEmpty(waybillCode)||opeType==null){
                return res;
            }
            CallerInfo callerInfoApi = Profiler.registerInfo("dms.web.waybillPackageApi.getPackOpeByWaybillCode",
                    Constants.UMP_APP_NAME_DMSWEB, false, true);
            //调用运单接口，获取称重流水信息
            BaseEntity<List<PackOpeFlowDto>> rest = this.waybillPackageApi.getPackOpeByWaybillCode(waybillCode);
            Profiler.registerInfoEnd(callerInfoApi);
            if(rest.getResultCode()==REST_CODE_SUC){
                List<PackOpeFlowDto> packOpeFlowList = rest.getData();
                PackOpeFlowDto oldData = null;
                if(packOpeFlowList!=null&&!packOpeFlowList.isEmpty()){
                    for(PackOpeFlowDto newData:packOpeFlowList){
                        String key = newData.getPackageCode();
                        Integer key0 = newData.getOpeType();
                        if(!opeType.equals(key0)){
                            continue;
                        }
                        oldData = res.get(key);
                        if(oldData==null){
                            res.put(key, newData);
                        }else{
                            if(oldData.getpWeight()==null||oldData.getpWeight()<=0){
//							newData.setpWeight(newData.getpWeight());
//							newData.setWeighTime(newData.getWeighTime());
                            }else if(newData.getpWeight()!=null&&newData.getpWeight()>0&&newData.getWeighTime().after(oldData.getWeighTime())){
                                oldData.setpWeight(newData.getpWeight());
                                oldData.setWeighTime(newData.getWeighTime());
                            }
                        }
                    }
                }
            }else{
                this.log.warn("没有获取到包裹称重信息，code:{},msg:{}", rest.getResultCode(),rest.getMessage());
            }
            return res;
        } finally {
            Profiler.registerInfoEnd(callerInfo);
        }
    }
    /**
     * 通过运单对象，设置基础打印信息
     * <p>设置商家id和name(busiId、busiName)
     * <p>设置配送方式deliveryMethod
     * <p>以始发分拣中心获取始发城市code和名称(originalCityCode、originalCityName)
     * <p>设置寄件人、电话、手机号、地址信息(consigner、consignerTel、consignerMobile、consignerAddress)
     * <p>设置设置价格保护标识和显示值：(priceProtectFlag、priceProtectText)
     * <p>设置打标信息：签单返还、配送类型、运输产品(signBackText、distributTypeText、transportMode)
     * <p>设置打标信息：运输产品类型、收件公司、寄件公司(signBackText、distributTypeText、transportMode)
     * <p>设置打标信息：B网订单的备用站点Id、已称标识、客户预约时间、派送时段、特殊要求</p>
     * <p>设置特殊要求：</p>
     * @param target 目标对象(BasePrintWaybill类型)
     * @param waybill 原始运单对象
     */
    @JProfiler(jKey = "DMS.BASE.WaybillCommonServiceImpl.setBasePrintInfoByWaybill", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public BasePrintWaybill setBasePrintInfoByWaybill(BasePrintWaybill target, com.jd.etms.waybill.domain.Waybill waybill){
    	if(target==null||waybill==null){
    		return target;
    	}
		waybillPrintService.dealSignTexts(waybill.getWaybillSign(), target, Constants.DIC_NAME_WAYBILL_SIGN_CONFIG);
		waybillPrintService.dealSignTexts(waybill.getSendPay(), target, Constants.DIC_NAME_SEND_PAY_CONFIG);

        // 设置打印模板分组编码
        setPrintTemplateGroup(waybill, target);

        //设置备用站点
        WaybillExt waybillExt = waybill.getWaybillExt();
        String productType = null;
        if(waybillExt != null){
            productType = waybillExt.getProductType();
            //从运单中取出备用站点id，转换成站点名称
            target.setBackupSiteId(waybillExt.getBackupSiteId());
            target.setBackupSiteName(siteService.getSiteNameByCode(waybillExt.getBackupSiteId()));
        }

        //判断是否为冷链卡班且外仓
        boolean isColdChainKBAndOuterWare = BusinessUtil.isColdChainKB(waybill.getWaybillSign(),productType)
                && BusinessUtil.isWareHouseNotJDWaybill(waybill.getWaybillSign());

    	//设置商家id和name
        target.setBusiId(waybill.getBusiId());
        target.setBusiName(waybill.getBusiName());
        //以始发分拣中心获取始发城市id和名称
        if(target.getOriginalDmsCode()!=null){
        	BaseStaffSiteOrgDto siteInfo = baseService.queryDmsBaseSiteByCode(target.getOriginalDmsCode().toString());
        	if(siteInfo!=null){
        		target.setOriginalCityCode(siteInfo.getCityId());
        		target.setOriginalCityName(siteInfo.getCityName());
        	}
        }
        //设置配送方式
        target.setDeliveryMethod(TextConstants.DELIVERY_METHOD_SEND);
        //联通华盛面单模板、小米运单、冷链卡班且外仓不显示京东字样 包括log，二维码，网址、电话
        if(!BusinessUtil.isSignChar(waybill.getWaybillSign(),69,'0')
                || BusinessUtil.isMillet(target.getBusiCode())
                || isColdChainKBAndOuterWare ){
            target.setJdLogoImageKey("");
            target.setPopularizeMatrixCode("");
            target.setAdditionalComment("");
            target.setPopularizeMatrixCodeDesc("");
        }else{
            target.setJdLogoImageKey(LOGO_IMAGE_KEY_JD);
            target.setAdditionalComment(additionalComment);
            //包裹二维码和描述
            target.setPopularizeMatrixCode(popularizeMatrixCode);
            target.setPopularizeMatrixCodeDesc(POPULARIZEMATRIXCODEDESC_DEFAULT);

            // 如果获取到收件人地址寄递码(receiveAdderesssCode存在)，则转换为二维码替换左下角二维码链接内容，同时将原二维码文本“扫码寄快递”设置为空。
            if (waybillExt != null) {
                String receiveAddressCode = waybillExt.getReceiveAddressCode();
                if (StringUtils.isNotBlank(receiveAddressCode)) {
                    target.setPopularizeMatrixCode(receiveAddressCode);
                    target.setPopularizeMatrixCodeDesc("");
                }
            }

            //包裹有话说
            if (WaybillVasUtil.isPackageSay(target.getWaybillVasSign())){
                BaseEntity<List<WaybillAttachmentDto>> waybillAttachments = waybillQueryManager.getWaybillAttachmentByWaybillCodeAndType(waybill.getWaybillCode(), CUSTOMER_VIDEO);
                if (CollectionUtils.isNotEmpty(waybillAttachments.getData())){
                    String attachmentUrl = this.getAttachmentUrl(waybillAttachments.getData());
                    if (StringUtils.isNotEmpty(attachmentUrl)){
                        target.setPopularizeMatrixCode(attachmentUrl);
                        target.setPopularizeMatrixCodeDesc(POPULARIZEMATRIXCODEDESC_PACKAGE_SAY);
                    }
                }
            }
            //判断是否有运单维度的增值服务
            if (BusinessUtil.hasWaybillVas(waybill.getWaybillSign())){
	            //增值服务，打印京喜送达服务url
	            BaseEntity<WaybillVasDto> waybillVasJXD = waybillQueryManager.getWaybillVasWithExtendInfoByWaybillCode(waybill.getWaybillCode(),DmsConstants.WAYBILL_VAS_JXD);
	            if (waybillVasJXD != null
	            		&& waybillVasJXD.getData() != null){
	                Map<String, String> extendMap = waybillVasJXD.getData().getExtendMap();
	            	String attachmentUrl = BusinessHelper.getAttachmentUrlForJxd(extendMap);
	                if(StringUtils.isNotBlank(attachmentUrl)) {
	                	target.setPopularizeMatrixCode(attachmentUrl);
	                    target.setPopularizeMatrixCodeDesc(POPULARIZEMATRIXCODEDESC_JXD);
	                }
	            }
            }
            String customerCode = waybill.getCustomerCode();
            List<String> qlListConfigList = sysConfigService.getStringListConfig(Constants.SYS_WAYBILL_PRINT_ADDIOWN_NUMBER_CONF);
            boolean signInChars = BusinessUtil.isSignInChars(waybill.getWaybillSign(), WaybillSignConstants.POSITION_61, WaybillSignConstants.CHAR_61_1,
                    WaybillSignConstants.CHAR_61_2, WaybillSignConstants.CHAR_61_3, WaybillSignConstants.CHAR_61_6);
            if(customerCode!=null&&!CollectionUtils.isEmpty(qlListConfigList)&&qlListConfigList.contains(customerCode)&&signInChars){
                //获取订单号

                BaseEntity<com.jd.etms.waybill.domain.Waybill> oldWaybill= waybillQueryManager.getWaybillByReturnWaybillCode(waybill.getWaybillCode());
                if(oldWaybill != null && oldWaybill.getData()!=null&&oldWaybill.getData().getWaybillCode()!=null) {
                    String oldWaybillCode = oldWaybill.getData().getWaybillCode();
                    target.setPopularizeMatrixCode(oldWaybillCode);
                    target.setPopularizeMatrixCodeDesc("原运单号");
                    target.appendRemark(Constants.PRINT_TITLES+oldWaybillCode);
                }else {
                    //二维码为扫码寄快递
                    target.appendRemark(Constants.PRINT_JD_TITLES);
                }
            }
        }
        target.setBusiOrderCode(waybill.getBusiOrderCode());

        //Waybillsign的15位打了3的取件单，并且订单号非“QWD”开头的单子getSpareColumn3  ----产品：luochengyi  2017年8月29日16:37:21
        //B网面单busiOrderCode字段调整为spareColumn3，若spareColumn3为null则该字段不显示
        if(BusinessUtil.isSignChar(waybill.getWaybillSign(),15,'3')
                || BusinessUtil.isB2b(waybill.getWaybillSign())){
            String spareColumn3 = waybill.getSpareColumn3();
            if(spareColumn3 !=null){
                target.setBusiOrderCode(waybill.getSpareColumn3());
            }else{
                target.setBusiOrderCode("");
            }
        }

        //面单打印新增寄件人、电话、手机号、地址信息
        target.setConsigner(waybill.getConsigner());
        target.setConsignerTel(waybill.getConsignerTel());
        target.setConsignerMobile(waybill.getConsignerMobile());
        target.setConsignerAddress(waybill.getConsignerAddress());
        //设置价格保护标识和显示值
        String priceProtectText = "";
        target.setPriceProtectFlag(waybill.getPriceProtectFlag());
        if(Constants.INTEGER_FLG_TRUE.equals(waybill.getPriceProtectFlag())){
            priceProtectText = Constants.TEXT_PRICE_PROTECT;
        }
        target.setPriceProtectText(priceProtectText);
        //收件公司名称
        target.setConsigneeCompany(waybill.getReceiveCompany());
        //寄件公司名称
        target.setSenderCompany(waybill.getSenderCompany());
        //根据waybillSign第一位判断是否SOP或纯外单（根据waybillSign第一位判断是否SOP或纯外单（标识为 2、3、6、K））
        target.setSopOrExternalFlg(BusinessUtil.isSopOrExternal(waybill.getWaybillSign()));
        //设置已验视已安检
        //判断始发分拣中心是否属于北京
        boolean bjCheckFlg = siteService.getBjDmsSiteCodes()
                .contains(target.getOriginalDmsCode());
        target.setBjCheckFlg(bjCheckFlg);
        if(bjCheckFlg){
            target.setExamineFlag(EXAMINE_FLAG_COMMEN_BJ);
        }else {
            target.setExamineFlag(EXAMINE_FLAG_COMMEN);
        }
        target.setSecurityCheck(SECURITY_CHECK);

        //打印时间,取后台服务器时间
        String printTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        target.setPrintTime(printTime);

        //设置运费及货款信息
        setFreightAndGoodsPayment(target,waybill);
        /**
         * B2B生鲜运输产品类型
         * waybill_sign36位=0 且waybill_sign40位=1 且 waybill_sign54位=2：冷链整车
         * waybill_sign36位=1 且waybill_sign40位=2 且 waybill_sign54位=2：快运冷链
         * 快运冷链下新增 原逻辑待业务确认是否变更
         * {
         *     1. Waybill_sign54=2生鲜行业 且Waybill_sign40=2 纯配快运零担 且Waybill_sign80=6 且118=2 城配整车，即为：“冷链城配整车”
         *     2. Waybill_sign54=2生鲜行业 且 Waybill_sign40=2纯配快运零担 且Waybill_sign80=6 且 118= 1或者0或者空 城配共配，即为：“冷链城配共配”
         *     3. Waybill_sign54=2 生鲜行业 且Waybill_sign40=2 纯配快运零担且Waybill_sign80=8 专车，代表：“冷链整车”
         * }
         * waybill_sign36位=1 且waybill_sign40位=3 且 waybill_sign54位=2：仓配冷链
         */
        if(BusinessUtil.isSignChar(waybill.getWaybillSign(),54,'2')){
            if (BusinessUtil.isSignChar(waybill.getWaybillSign(),40,'1')) {
                if (BusinessUtil.isSignChar(waybill.getWaybillSign(),36,'0')) {
                    //冷链整车
                    target.setjZDFlag(TextConstants.B2B_FRESH_WHOLE_VEHICLE);
                }
            } else if (BusinessUtil.isSignChar(waybill.getWaybillSign(),40,'2')) {
                if (BusinessUtil.isSignChar(waybill.getWaybillSign(),80,'8')) {
                    //冷链整车
                    target.setjZDFlag(TextConstants.B2B_FRESH_WHOLE_VEHICLE);
                } else if (BusinessUtil.isSignChar(waybill.getWaybillSign(),80,'6')) {
                    if (BusinessUtil.isSignChar(waybill.getWaybillSign(),118,'2')) {
                        //城配整车
                        target.setjZDFlag(TextConstants.B2B_FRESH_URBAN_WHOLE_VEHICLE);
                    } else if ( waybill.getWaybillSign().length() < 118
                            || BusinessUtil.isSignChar(waybill.getWaybillSign(),118,'1')
                            || BusinessUtil.isSignChar(waybill.getWaybillSign(),118,'0')) {
                        //城配共配
                        target.setjZDFlag(TextConstants.B2B_FRESH_URBAN_TOGETHER);
                    }
                } else if (BusinessUtil.isSignChar(waybill.getWaybillSign(),80,'7')) {
                    //冷链卡班
                    target.setjZDFlag(TextConstants.B2B_FRESH_EXPRESS);
                } else if (BusinessUtil.isSignChar(waybill.getWaybillSign(),36,'1')
                        && !isColdChainKBAndOuterWare) {
                    //冷链卡班
                    target.setjZDFlag(TextConstants.B2B_FRESH_EXPRESS);
                }
            } else if (BusinessUtil.isSignChar(waybill.getWaybillSign(),40,'3')) {
                if (BusinessUtil.isSignChar(waybill.getWaybillSign(),36,'1')) {
                    //仓配冷链
                    target.setjZDFlag(TextConstants.B2B_FRESH_WAREHOUSE);
                }
            }
        }

        //根据waybillExt.productType的值取，给jZDFlag赋值  ,若产品类型为铃连卡班且为非京仓时，jZDFlag不用赋值。
        if(!isColdChainKBAndOuterWare){
            waybillPrintService.dealDicTexts(productType, Constants.DIC_CODE_PACKAGE_PRINT_PRODUCT, target);
        }

        //B网冷链打印京仓/非京仓  waybill_sign 第89位等于3时，打印 【京仓】；第89位等于4时，打印 【非京仓】;B网冷链的冷链卡班且89位为4，打印"外仓"
        if(BusinessUtil.isColdChainWaybill(waybill.getWaybillSign())){
            if(BusinessUtil.isWareHouseJDWaybill(waybill.getWaybillSign())){
                target.appendSpecialMark1(DmsConstants.SPECIAL_MARK1_WAREHOUSE_JD);
            }else if(BusinessUtil.isWareHouseNotJDWaybill(waybill.getWaybillSign())){
                if(isColdChainKBAndOuterWare){
                    target.appendSpecialMark1(DmsConstants.SPECIAL_MARK1_WAREHOUSE_OUTER);
                }else{
                    target.appendSpecialMark1(DmsConstants.SPECIAL_MARK1_WAREHOUSE_NOT_JD);
                }

            }
        }

        /**
         * waybill_sign第54位等于4 且 第40位等于2或3时:医药零担
         */
        if(BusinessUtil.isBMedicine(waybill.getWaybillSign()) && BusinessUtil.isSignInChars(waybill.getWaybillSign(),WaybillSignConstants.POSITION_40, WaybillSignConstants.CHAR_40_2,WaybillSignConstants.CHAR_40_3)){
            target.setjZDFlag(TextConstants.COMMON_TEXT_MEDICINE_SCATTERED);
        }

        /**
         * B网面单识别“特准包裹”标识，在jZDFlag处打印“快”
         */
        if(BusinessUtil.isB2b(waybill.getWaybillSign())
                && BusinessUtil.isSignChar(waybill.getWaybillSign(),WaybillSignConstants.POSITION_80,WaybillSignConstants.CHAR_80_9)){
            target.setjZDFlag(TextConstants.PECIAL_TIMELY_MARK);
        }

        // 判断 特快送-次晨 或者 生鲜特快-次晨
        if(BusinessUtil.isTKSCC(waybill.getWaybillSign()) || BusinessUtil.isSXTKCC(waybill.getWaybillSign())){
            log.info("满足特快送-次晨 或者 生鲜特快-次晨-{}",waybill.getRequireTime());
            String requireTimeStr = DateHelper.formatDate(waybill.getRequireTime(), DateHelper.DATE_FORMAT_HHmm);
            String specialMark = target.getSpecialMark();
            target.appendSpecialMark(requireTimeStr+specialMark);
        }

        /* waybill_sign标识位，第七十九位为2，打提字标
           标位变更 ：2020-4-29
           详细见方法释义
           2020-7-23 补 众邮单不打印【提】【店】【柜】以及自提地址
         */
        if(BusinessUtil.isSignChar(waybill.getWaybillSign(), 79,'2') || BusinessUtil.isZiTiByWaybillSign(waybill.getWaybillSign())){
            if (!BusinessUtil.isBusinessNet(waybill.getWaybillSign())) {
                target.appendSpecialMark(ComposeService.SPECIAL_MARK_ARAYACAK_SITE);
            }
        }
        //waybill_sign标识位，第二十九位为8，打C字标
        if(BusinessUtil.isSignChar(waybill.getWaybillSign(),29,'8')){
            //一体化面单中商家ID，商家订单不显示
            target.setBusiCode("");
            target.setBusiOrderCode("");
        }
        //设置产品名称
        String transportMode=waybillQueryManager.getTransportMode(waybill);
        if(StringHelper.isNotEmpty(transportMode)){
            target.setTransportMode(transportMode);
            //判断是否特快送，打印特快速标识
            if(TKS_PRODUCT_NAMES.contains(transportMode)){
                target.setTransportModeFlag(TextConstants.PRODUCT_NAME_TKS_FLAG);
            }
        }
        /*** 产品类型为md-m-0005时:医药专送 */
        if(waybillExt != null && Constants.PRODUCT_TYPE_MEDICINE_SPECIAL_DELIVERY.equals(waybillExt.getProductType())){
            target.setTransportMode(TextConstants.COMMON_TEXT_MEDICINE_DELIVET);
        }
        /*** 产品类型为ed-m-0059时:电商特惠 */
        if(waybillExt != null && Constants.E_COMMERCE_SPECIAL_OFFER_SERVICE.equals(waybillExt.getProductType())){
            target.setTransportMode(TextConstants.PRODUCT_NAME_DSTH);
        }
        //添加抖音标识
        if(BusinessUtil.isDouyin(waybill.getWaybillCode(),waybill.getSourceCode(),waybill.getSendPay())) {
            if(StringHelper.isNotEmpty(transportMode)){
            	target.setTransportMode(StringHelper.append(TextConstants.PRODUCT_FLAG_DOUYIN_PRE, transportMode));
            }else {
            	target.setTransportMode(TextConstants.PRODUCT_FLAG_DOUYIN);
            }
        }

        /**
         * 1.waybill_sign第80位等于1时，面单打印“特惠运”
         * 2.waybill_sign第80位等于2时，面单打标“特准运”
         */
        if(BusinessUtil.isSignChar(waybill.getWaybillSign(),80,'1')){
            target.setjZDFlag(TextConstants.B2B_CHEAP_TRANSPORT);
        }else if(BusinessUtil.isSignChar(waybill.getWaybillSign(),80,'2')){
            target.setjZDFlag(TextConstants.B2B_TIMELY_TRANSPORT);
        }


        /**
         * 新增b网产品类型枚举，给为jZDFlag字段赋值
         *
         * 当wbs40=2 && 80=1时，赋值“特惠零担”
         *
         * 当wbs40=2 && 80=2时，赋值“特快零担”
         *
         * 当wbs40=2 && 80=0时，赋值“快运零担”
         *
         * 当wbs40=2 && 80=9时，赋值“特快重货”
         */
        if (BusinessUtil.isSignChar(waybill.getWaybillSign(),40,'2')) {
            if (BusinessUtil.isSignChar(waybill.getWaybillSign(),80,'1')) {
                //特惠零担
                target.setjZDFlag(TextConstants.B2B_THLD);
            }else if(BusinessUtil.isSignChar(waybill.getWaybillSign(),80,'2')) {
                //特快零担
                target.setjZDFlag(TextConstants.B2B_TKLD);
            }else if(BusinessUtil.isSignChar(waybill.getWaybillSign(),80,'0')) {
                //快运零担
                target.setjZDFlag(TextConstants.B2B_KYLD);
            }else if(BusinessUtil.isSignChar(waybill.getWaybillSign(),80,'9')) {
                //特快重货
                target.setjZDFlag(TextConstants.B2B_TKZH);
            }
        }

        //sendpay167位不等于0时，面单模板打印【京准达快递到车】
	    if(StringHelper.isNotEmpty(waybill.getSendPay())
	    		&& waybill.getSendPay().length() >= 167
	    		&& !BusinessUtil.isSignChar(waybill.getSendPay(), 167, '0')){
	    	target.setjZDFlag(TextConstants.TEXT_TRANSPORT_KDDC);
	    }
        //sendPay146位为4时，面单产品打印【冷链卡班】占位符 jzdflag
        if( BusinessUtil.isSignChar(waybill.getSendPay(),SendPayConstants.POSITION_146,SendPayConstants.CHAR_146_4)){
            target.setjZDFlag(TextConstants.B2B_FRESH_EXPRESS);
        }
        /*** 产品类型为ll-m-0020时:冷链小票*/
        if(Constants.PRODUCT_TYPE_COLD_CHAIN_XP.equals(productType)){
            target.setjZDFlag(TextConstants.COMMON_TEXT_COLD_CHAIN_XP);
        }
        /*** 产品类型为ll-m-0018时:医药大票*/
        if(Constants.PRODUCT_TYPE_MEDICINE_DP.equals(productType)){
            target.setjZDFlag(TextConstants.COMMON_TEXT_MEDICINE_DP);
        }
        /*** 产品类型为md-m-0002时:医冷零担*/
        if(Constants.PRODUCT_TYPE_MEDICINE_COLD.equals(productType)){
            target.setjZDFlag(TextConstants.COMMON_TEXT_MEDICINE_COLD);
        }
	    //sendPay146位为3时，打传字标
	    if(BusinessUtil.isSignChar(waybill.getSendPay(),146,'3')){
            target.appendSpecialMark(ComposeService.SPECIAL_MARK_TRANSFER);
        }
        //waybill_sign标识位，第四十六位为2或3，打安字标
        if(BusinessUtil.isSignInChars(waybill.getWaybillSign(), 46, '2','3')){
        	target.appendSpecialMark(ComposeService.SPECIAL_MARK_VALUABLE);
        }
        //waybil_sign标识位，第五十五位为1，打鲜字标（c网操作的外单）
        if(!BusinessUtil.isB2b(waybill.getWaybillSign()) &&
                BusinessUtil.isSignChar(waybill.getWaybillSign(),55,'1')){
            target.appendSpecialMark(ComposeService.SPECIAL_MARK_FRESH);
        }
        //设置运输方式
        setTransportTypeText(target,waybill);
        //waybill_sign标识位，第十六位为1且第三十一位为2且第五十五位为0，打同字标
        if(!BusinessUtil.isB2b(waybill.getWaybillSign()) &&
                BusinessUtil.isSignChar(waybill.getWaybillSign(),16,'1') &&
                BusinessUtil.isSignChar(waybill.getWaybillSign(),31,'2') &&
                BusinessUtil.isSignChar(waybill.getWaybillSign(),55,'0') ){
            target.appendSpecialMark(ComposeService.SPECIAL_MARK_SAME);
        }
        //waybill_sign标识位，第五十七位为1，打优字标
        if(BusinessUtil.isSignChar(waybill.getWaybillSign(),57,'1')){
            target.appendSpecialMark(ComposeService.SPECIAL_MARK_FIRST);
        }
        //waybill_sign标识位，第三十一位为3，打城际标
        if(BusinessUtil.isSignChar(waybill.getWaybillSign(),31,'3')){
            target.appendSpecialMark(ComposeService.SPECIAL_MARK_INTERCITY);
        }
        //半退
        if(BusinessUtil.isPartReverse(waybill.getWaybillSign())){
            target.appendSpecialMark(ComposeService.SPECIAL_MARK_PART_REVERSE);
        }
        //尊 、碎
        appendRespectTypeText(target,waybill.getWaybillSign(),waybillExt);

        //waybill_sign标识位，第九十二位为2，一体化面单显示"器"
        if(BusinessUtil.isSignChar(waybill.getWaybillSign(), Constants.WAYBILL_SIGN_POSITION_92, Constants.WAYBILL_SIGN_POSITION_92_2)){
            target.appendSpecialMark(ComposeService.SPECIAL_MARK_UTENSIL);
        }
        //waybill_sign标识位，第九十二位为3，一体化面单显示"箱"
        if(BusinessUtil.isSignChar(waybill.getWaybillSign(),Constants.WAYBILL_SIGN_POSITION_92,Constants.WAYBILL_SIGN_POSITION_92_3)){
            target.appendSpecialMark(ComposeService.SPECIAL_MARK_BOX);
        }
        // 设置面单路区信息
        setRoadCode(target, waybill);
        //特殊商家处理
        if(BusinessUtil.isYHD(waybill.getSendPay())){
        	//一号店订单:设置商家别名YHD，商家logo标识yhd4949.gif
        	target.setDmsBusiAlias(Constants.BUSINESS_ALIAS_YHD);
        	target.setBrandImageKey(Constants.BRAND_IMAGE_KEY_YHD);
        }else if(BusinessUtil.isCMBC(waybill.getWaybillSign())){
        	//招商银行业务：运费字段、货款字段显示 “无”,商家标识设置为 CMBC
        	target.setDmsBusiAlias(Constants.BUSINESS_ALIAS_CMBC);
        	target.setFreightText(TextConstants.COMMON_TEXT_NOTHING);
        	target.setGoodsPaymentText(TextConstants.COMMON_TEXT_NOTHING);
        }
		if(BusinessUtil.isSopJZD(waybill.getWaybillSign())){
			target.appendSpecialMark(TextConstants.TEXT_JZD_SPECIAL_MARK);
		}
        //无接触面单，追加标识‘代’
        if(BusinessUtil.isNoTouchService(waybill.getSendPay(),waybill.getWaybillSign())){
        	target.appendSpecialMark(TextConstants.NO_TOUCH_FLAG);
        	//无接触面单，追加waybillExt.contactlessPlace到printAddressRemark中
    		if(waybill.getWaybillExt()!= null
    				&& StringUtils.isNotBlank(waybill.getWaybillExt().getContactlessPlace())){
    			target.setPrintAddressRemark(waybill.getWaybillExt().getContactlessPlace());
    		}
        }
        //根据sendpay第293位=1，在面单上打印海运标记“H”
        if(BusinessUtil.isSignChar(waybill.getSendPay(), SendPayConstants.POSITION_293, SendPayConstants.CHAR_293_1)){
        	target.appendSpecialMark(TextConstants.TRANSPORT_SEA_FLAG);
        }
        //设置特殊需求
        loadSpecialRequirement(target,waybill.getWaybillSign(),waybill);

        //处理特殊的distributTypeText
        processSpecialDistributTypeText(target,waybill.getWaybillSign());

        //物品名称
        if(waybill.getWaybillExt()!=null && StringUtils.isNotBlank(waybill.getWaybillExt().getConsignWare())){
            //如果是B网(66为3、2) goodsName不赋值
            if(!BusinessUtil.needPrintPackageName(waybill.getWaybillSign())){
                target.setGoodsName(waybill.getWaybillExt().getConsignWare());
            }
        }
        //大件路区
        if(BusinessUtil.isHeavyCargo(waybill.getWaybillSign())){
            target.setBackupRoadCode(waybill.getRoadCode());
        }

        // 设置面单水印
        setWaterMark(target, waybill);
        // 设置面单aoi信息
        setAoiCode(target, waybill);
        return target;
    }
    /**
     * 设置路区号及拆包员号
     * @param target
     * @param waybill
     */
    private void setRoadCode(BasePrintWaybill target, com.jd.etms.waybill.domain.Waybill waybill) {
        String roadCode = target.getRoadCode();
    	//现场调度标识-设置路区为0
        if(DmsConstants.LOCAL_SCHEDULE.equals(target.getLocalSchedule())){
        	roadCode = DmsConstants.LOCAL_SCHEDULE_ROAD_CODE;
        }
    	//拆包面单打印拆包员号码,拆包号不为空则路区号位置显示拆包号
        if(waybill.getWaybillExt() != null && StringUtils.isNotBlank(waybill.getWaybillExt().getUnpackClassifyNum())){
        	roadCode = waybill.getWaybillExt().getUnpackClassifyNum();
        	target.setUnpackClassifyNum(waybill.getWaybillExt().getUnpackClassifyNum());
        }
        target.setRoad(roadCode);
        target.setRoadCode(roadCode);
	}

	/**
     * 设置模板分组编码
     * @param waybill
     * @param commonWaybill
     */
    private void setPrintTemplateGroup(com.jd.etms.waybill.domain.Waybill waybill, BasePrintWaybill commonWaybill) {
        String waybillSign = waybill.getWaybillSign();
        if(BusinessUtil.isTc(waybillSign)){
            commonWaybill.setTemplateGroupCode(TemplateGroupEnum.TEMPLATE_GROUP_CODE_TC);
        }else if(BusinessUtil.isB2b(waybillSign)){
            commonWaybill.setTemplateGroupCode(TemplateGroupEnum.TEMPLATE_GROUP_CODE_B);
        }else{
            commonWaybill.setTemplateGroupCode(TemplateGroupEnum.TEMPLATE_GROUP_CODE_C);
        }
    }
    /**
     * 设置面单水印
     * <ul>
     *     <li>B网面单设置waterMark显示KA</li>
     *     <li>非B网面单设置bcSign显示KA</li>
     * </ul>
     * @param target
     * @param waybill
     */
    private void setWaterMark(BasePrintWaybill target, com.jd.etms.waybill.domain.Waybill waybill) {

        // 【KA保障面单】，转运面单显示“KA”水印
        if (Objects.equals(TemplateGroupEnum.TEMPLATE_GROUP_CODE_B, target.getTemplateGroupCode())) {
            if (BusinessUtil.isSignChar(waybill.getWaybillSign(), WaybillSignConstants.POSITION_57, WaybillSignConstants.CHAR_57_2)) {
                target.setWaterMark(TextConstants.KA_FLAG);
            }
        }
        else {
            //waybill_sign第57位= 2，代表“KA运营特殊保障”，追加“KA”
            if(BusinessUtil.isSignChar(waybill.getWaybillSign(), WaybillSignConstants.POSITION_57, WaybillSignConstants.CHAR_57_2)){
                //新模板，提出KA标识
                target.setBcSign(TextConstants.KA_FLAG);
                target.appendSpecialMark(TextConstants.KA_FLAG,false);
            }

        }

        // 纯配B2B运单或纯配C转B运单，面单上加“B-”+【运单号】后4位
        if(BusinessUtil.isSignChar(waybill.getWaybillSign(),WaybillSignConstants.POSITION_40,WaybillSignConstants.CHAR_40_2)
                ||BusinessUtil.isSignChar(waybill.getWaybillSign(),WaybillSignConstants.POSITION_97,WaybillSignConstants.CHAR_97_1)
                ||BusinessUtil.isSignChar(waybill.getSendPay(),SendPayConstants.POSITION_146,SendPayConstants.CHAR_146_1)){
            if(StringHelper.isNotEmpty(target.getWaybillCodeLast())){
                target.setBcSign(TextConstants.PECIAL_B_MARK1.concat(target.getWaybillCodeLast()));
            }else{
                target.setBcSign(TextConstants.PECIAL_B_MARK);
            }
        }

        //SendPay第297位等于1或2时，为预售订单， 面单打印【预】字
        if(BusinessUtil.isPreSell(waybill.getSendPay())){
            target.setBcSign(TextConstants.PRE_SELL_FLAG);
        }
    }
    /**
     * 设置面单aoiCode
     * @param target
     * @param waybill
     */
    private void setAoiCode(BasePrintWaybill target, com.jd.etms.waybill.domain.Waybill waybill) {
    	String waybillCode = target.getWaybillCode();
    	String aoiId = null;
    	String aoiCode = null;
    	//现场反调度不设置aoiCode
        if(DmsConstants.LOCAL_SCHEDULE.equals(target.getLocalSchedule())){
        	log.info("现场反调度{}不设置aoiCode！",waybillCode);
        	return;
        }
    	//调用运单接口查询围栏信息获取aoiId
    	JdResult<List<WaybillFenceDto>> fenceResult = this.waybillQueryManager.getWaybillFenceInfoByWaybillCode(waybillCode);
    	if(fenceResult != null
    		 && fenceResult.isSucceed()
    		 && !CollectionUtils.isEmpty(fenceResult.getData())) {
    		//取派送aoiId
    		for(WaybillFenceDto fenceData : fenceResult.getData()) {
    			if(DmsConstants.WAYBILL_FENCE_TYPE_AOI.equals(fenceData.getFenceType())
    					&& DmsConstants.WAYBILL_FENCE_DELIVERY_STAGE_2.equals(fenceData.getDeliveryStage())
    					&& StringUtils.isNotBlank(fenceData.getFenceId())) {
    				aoiId = fenceData.getFenceId();
    				break;
    			}
    		}
    	}
    	if(StringUtils.isBlank(aoiId)){
    		log.warn("查询运单{}电子围栏aoiId为空！",waybillCode);
    		return;
    	}
    	//调用Gis接口查询aoiCode
    	AoiBindRoadMappingQuery gisQuery = new AoiBindRoadMappingQuery();
    	gisQuery.setAoiId(aoiId);
    	if(target.getPrepareSiteCode() != null) {
    		gisQuery.setSiteCode(target.getPrepareSiteCode().toString());
    	}
    	JdResult<List<AoiBindRoadMappingData>> aoiResult = aoiServiceManager.aoiBindRoadMappingExactSearch(gisQuery);
    	if(aoiResult != null
    		 && aoiResult.isSucceed()
    		 && !CollectionUtils.isEmpty(aoiResult.getData())) {
    		aoiCode = aoiResult.getData().get(0).getAoiCode();
    	}
    	if(StringUtils.isBlank(aoiCode)){
    		log.warn("查询Gis运单{}派送aoiCode为空！",waybillCode);
    		return;
    	}
    	target.setAoiCode(aoiCode);
    }
    /**
     * 获取附属地址
     * @param data
     * @return
     */
    private String getAttachmentUrl(List<WaybillAttachmentDto> data) {
        for (int i =0;i < data.size();i++){
            WaybillAttachmentDto tmp = data.get(i);
            if (CUSTOMER_VIDEO.equals(tmp.getAttachmentType())){
                return tmp.getAttachmentUrl();
            }
        }
        return null;
    }

    /**
     * 设置运费和货款信息
     * @param target
     * @param waybill
     */
    private void setFreightAndGoodsPayment(BasePrintWaybill target,com.jd.etms.waybill.domain.Waybill waybill) {
    	String freightText = "";
        String goodsPaymentText = "";
        if(BusinessUtil.isB2b(waybill.getWaybillSign())){
            //运费：waybillSign 25位为2【到付】
            if(BusinessUtil.isSignInChars(waybill.getWaybillSign(), WaybillSignConstants.POSITION_25, WaybillSignConstants.CHAR_25_2)){
                freightText = TextConstants.FREIGHT_PAY;
            }
        	//货款字段金额等于0时，则货款位置不显示
        	//货款字段金额大于0时，则货款位置显示为【代收货款】
        	if(NumberHelper.gt0(waybill.getCodMoney())){
        		goodsPaymentText = TextConstants.GOODS_PAYMENT_NEED_PAY;
        	}else{
        		goodsPaymentText = "";
        	}
        }else{
            //运费：waybillSign 25位为2、5时【到付】
            if(BusinessUtil.isSignInChars(waybill.getWaybillSign(), WaybillSignConstants.POSITION_25, WaybillSignConstants.CHAR_25_2,WaybillSignConstants.CHAR_25_5)){
                freightText = TextConstants.FREIGHT_PAY;
            }
            //c2c运费：waybillSign 25位为0或1或3或4时【寄付】
            if(BusinessUtil.isC2C(waybill.getWaybillSign())
            		&& BusinessUtil.isSignInChars(waybill.getWaybillSign(), WaybillSignConstants.POSITION_25,
            				WaybillSignConstants.CHAR_25_0,WaybillSignConstants.CHAR_25_1,WaybillSignConstants.CHAR_25_3,WaybillSignConstants.CHAR_25_4)){
            	freightText = TextConstants.FREIGHT_SEND;
            }
            //C网货款
            //货款：货款大于0时，满足在线支付时显示【在线支付】，否则显示【货到付款￥】
        	//货款：货款等于0时，则货款位置不显示
            if(NumberHelper.gt0(waybill.getCodMoney())){
                if (ComposeService.ONLINE_PAYMENT_SIGN.equals(waybill.getPayment())) {
                    goodsPaymentText = TextConstants.GOODS_PAYMENT_ONLINE;
                }else{
                	goodsPaymentText = TextConstants.GOODS_PAYMENT_COD;
                }
            } else{
                goodsPaymentText = "";
            }
        }
        String codMoneyText = "";
        String totalChargeText = "";
        String codMoney = null;
        String totalCharge = null;
        //大于0才展示
        if(waybill.getCodMoney() != null && NumberHelper.gt0(waybill.getCodMoney())){
        	String codMoneyF = NumberHelper.formatMoney(waybill.getCodMoney());
        	if(codMoneyF != null){
        		codMoney = codMoneyF;
        	}
        }
        //运费合计计算(topayTotalReceivable减去codMoney)
        Double topayTotalReceivable = waybill.getTopayTotalReceivable();
        if(topayTotalReceivable != null && topayTotalReceivable.doubleValue() >= 0){
        	BigDecimal totalChargeVal = BigDecimal.valueOf(topayTotalReceivable);
        	if(NumberHelper.isBigDecimal(codMoney)){
        		BigDecimal codMoneyVal = new BigDecimal(codMoney);
        		if(totalChargeVal.compareTo(codMoneyVal) >= 0){
        			totalChargeVal = totalChargeVal.subtract(codMoneyVal);
        		}else{
        			log.warn("运单{}金额topayTotalReceivable={}小于codMoney={}的值", target.getWaybillCode(),topayTotalReceivable,codMoney);
        			//运费小于总额，赋值为null
        			totalChargeVal = null;
        		}
        	}
        	//大于0才展示
        	if(totalChargeVal != null && totalChargeVal.compareTo(BigDecimal.ZERO)>0){
        		totalCharge = NumberHelper.formatMoney(totalChargeVal);
        	}
        }
        //代收货款、运费合计格式化
        if(codMoney != null){
        	codMoneyText = MessageFormat.format(TextConstants.CODMONEY_FORMAT,codMoney);
        }
        if(totalCharge != null){
        	totalChargeText = MessageFormat.format(TextConstants.TOTAL_CHARGE_FORMAT,totalCharge);
        }
        //运费、货款赋值
        target.setFreightText(freightText);
        target.setGoodsPaymentText(goodsPaymentText);
        target.setCodMoneyText(codMoneyText);
        target.setTotalChargeText(totalChargeText);
	}

	/**
     * 设置面单运输标识transportTypeText
     * @param target
     * @param waybill
     */
    private void setTransportTypeText(BasePrintWaybill target,
			com.jd.etms.waybill.domain.Waybill waybill) {
        //根据始发道口号类型，判断打‘航’还是‘航填’
        //此处始发道口类型是根据waybillSign 或 sendPay判断的，道口类型也会影响获取基础资料大全表信息，请谨慎使用
        if(Constants.ORIGINAL_CROSS_TYPE_AIR.equals(target.getOriginalCrossType())){
        	target.setTransportTypeText(ComposeService.SPECIAL_MARK_AIRTRANSPORT);
        }else if(Constants.ORIGINAL_CROSS_TYPE_FILL.equals(target.getOriginalCrossType())){
        	target.setTransportTypeText(ComposeService.SPECIAL_MARK_AIRTRANSPORT_FILL);
        }
		if(BusinessUtil.isSignChar(waybill.getWaybillSign(),WaybillSignConstants.POSITION_84,WaybillSignConstants.CHAR_84_2)){
        	//waybil_sign标识位，第八十四位为2，打‘铁’字标
        	target.setTransportTypeText(ComposeService.SPECIAL_MARK_RAIL);
        }
        //运输方式追加到specialMark中
        target.appendSpecialMark(target.getTransportTypeText(), false);
	}

	/**
     * 获取称重数据
     * @param waybillCode 运单号
     * @return
     */
    @Override
    public InvokeResult<List<PackageWeigh>> getPackListByCode(String waybillCode) {
        CallerInfo info = null;
        InvokeResult<List<PackageWeigh>> result = new InvokeResult<List<PackageWeigh>>();
        try{
            info = Profiler.registerInfo( "DMSWEB.WaybillCommonServiceImpl.getPackListByCode",false, true);
            BaseEntity<List<PackageWeigh>> packListByCode = waybillPackageApi.getPackListByCode(waybillCode);
            int code = packListByCode.getResultCode();
            String message =  packListByCode.getMessage();

                /*		1,"接口调用成功"
                        -1,"接口调用失败"
                        -2,"参数非法"
                        -3,"不存在的数据"	*/

            if(code == 1){
                //成功
                result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
                result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
                result.setData(packListByCode.getData());
            }else{
                //失败
                result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
                result.setMessage(message);

            }

        }catch(Exception e){
            log.error("异常getPackListByCode",e);
            Profiler.functionError(info);
        }finally{
            Profiler.registerInfoEnd(info);
        }
        return result;
    }
    /**
     * 先校验运单是否已录入总重量,否则查询分拣是否存在录入重量记录
     */
	@Override
	public boolean hasTotalWeight(String waybillCode) {
		if(StringHelper.isNotEmpty(waybillCode)){
			 BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode, true, true, true, false);
			 if(baseEntity != null
					 && baseEntity.getData() != null
					 && baseEntity.getData().getWaybill() != null){
				 //先校验运单是否已录入总重量
				 if(NumberHelper.gt0(baseEntity.getData().getWaybill().getAgainWeight())){
					 return true;
				 }else{
					 //查询该运单是否已录入总重量

				 }
			 }
		}
		return false;
	}

	@Override
    @JProfiler(jKey = "DMSWEB.waybillCommonService.getPickupTask", jAppName = Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP,JProEnum.FunctionError})
	public BaseEntity<PickupTask> getPickupTask(String oldWaybillCode){
	    return waybillPickupTaskApi.getPickTaskByPickCode(oldWaybillCode);
    }

    /**
     * 通过运单号获取履约单号
     * @param waybillCode
     * @return 不存在时返回null
     */
    @Override
    public String getPerformanceCode(String waybillCode) {
        if(StringHelper.isNotEmpty(waybillCode)){
            BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode, true, false, false, false);
            if(baseEntity != null
                    && baseEntity.getData() != null
                    && baseEntity.getData().getWaybill() != null){
                //是加履中心的订单 才可以去查
                if(BusinessUtil.isPerformanceOrder(baseEntity.getData().getWaybill().getWaybillSign())){
                    if(StringHelper.isNotEmpty(baseEntity.getData().getWaybill().getParentOrderId())){
                        return baseEntity.getData().getWaybill().getParentOrderId();
                    }
                }
            }
        }
        return null;
    }

    /**
     * 通过运单号获取履约单号
     * @param waybillCode
     * @return 不存在时返回null
     */
    @Override
    public boolean isPerformanceWaybill(String waybillCode) {
        if(StringHelper.isNotEmpty(waybillCode)){
            BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode, true, false, false, false);
            if(baseEntity != null
                    && baseEntity.getData() != null
                    && baseEntity.getData().getWaybill() != null){
                //是加履中心的订单 才可以去查
                if(BusinessUtil.isPerformanceOrder(baseEntity.getData().getWaybill().getWaybillSign())){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否快运暂存运单
     * <p>
     *     增值服务是"fr-a-0009"表示暂存
     * </p>
     *
     * @param waybillCode
     * @return
     */
    @Override
    @JProfiler(jKey = "DMS.BASE.WaybillCommonServiceImpl.isStorageWaybill" , jAppName = Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean isStorageWaybill(String waybillCode) {
        boolean flag = false;
        try {
            String key = STORAGEWAYBILL_REDIS_KEY_PREFIX.concat(waybillCode);
            String redisValue = jimdbCacheService.get(key);
            if(StringUtils.isNotEmpty(redisValue)){
                return Boolean.valueOf(redisValue);
            }
            flag = getStorageWaybill(waybillCode);
            jimdbCacheService.setEx(key,String.valueOf(flag),2*24*20*60,TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("是否快运暂存运单查询-waybillCode[{}]",waybillCode,e);
            return flag;
        }
        return flag;
    }

    private boolean getStorageWaybill(String waybillCode) {
        BaseEntity<List<WaybillVasDto>> baseEntity = waybillQueryManager.getWaybillVasInfosByWaybillCode(waybillCode);
        if(baseEntity == null || baseEntity.getResultCode() != EnumBusiCode.BUSI_SUCCESS.getCode()
                || CollectionUtils.isEmpty(baseEntity.getData())){
            return false;
        }
        List<WaybillVasDto> list = baseEntity.getData();
        for(WaybillVasDto dto : list){
            if(Constants.STORAGE_INCRE_SERVICE.equals(dto.getVasNo())){
                return true;
            }
        }
        return false;
    }

    /**
     * 获取始发站点逻辑
     * c2c单子特殊处理
     * 传入的分拣中心编码为空时
     * 1、获取运单中仓库号绑定的分拣中心basicMixedWS.getStoreBindDms
     * 2、获取运单中揽收站点对应的分拣中心basicPrimaryWS.getBaseSiteBySiteId
     * 3、根据运单寄件地址调预分拣接口计算寄件城市，获取城市绑定的分拣中心basicPrimaryWS.getValidDataDict
     * @param printWaybill
     * @param bigWaybillDto
     */
    @JProfiler(jKey = "DMS.BASE.WaybillCommonServiceImpl.loadOriginalDmsInfo", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public void loadOriginalDmsInfo(WaybillPrintContext context,BasePrintWaybill printWaybill, BigWaybillDto bigWaybillDto) {
        Integer dmsCode = printWaybill.getOriginalDmsCode();
        String waybillCode = printWaybill.getWaybillCode();
        if(bigWaybillDto == null) {
        	return;
        }
        com.jd.etms.waybill.domain.Waybill etmsWaybill = bigWaybillDto.getWaybill();
        WaybillManageDomain waybillState = bigWaybillDto.getWaybillState();
        WaybillPickup waybillPickup = bigWaybillDto.getWaybillPickup();
        //c2c特殊处理始发
        if(BusinessHelper.isC2c(etmsWaybill.getWaybillSign())) {
        	Integer dmsCodeC2c = this.dealC2cDmsSiteCode(context,printWaybill,waybillPickup);
        	//判断是否有效分拣中心
        	if(isVaildDms(dmsCodeC2c)) {
        		dmsCode = dmsCodeC2c;
        	}
        }

//        when(baseMajorManager.getBaseSiteBySiteId(5)).thenReturn(pickSite);
//
//        BaseStaffSiteOrgDto userSite = new BaseStaffSiteOrgDto();
//        userSite.setCollectionDmsId(390);
//        userSite.setSiteType(4);
//        when(baseMajorManager.getBaseSiteBySiteId(39)).thenReturn(userSite);
        if (!isVaildDms(dmsCode)) {
            log.debug("参数中无始发分拣中心编码，从外部系统获取.运单号:{}" , waybillCode);

            //判断有没有仓Id
            if (etmsWaybill != null && etmsWaybill.getDistributeStoreId() != null && waybillState.getCky2() != null) {
                dmsCode = basicSafInterfaceManager.getStoreBindDmsCode(STORE_TYPE_WMS, waybillState.getCky2(), etmsWaybill.getDistributeStoreId());
                log.debug("运单号:{}.库房类型:wms+cky2:{}库房号:{}","对应的分拣中心:{}" ,waybillCode,waybillState.getCky2(),etmsWaybill.getDistributeStoreId(), dmsCode);
            }

            //判断有没有揽收站点
            if (!isVaildDms(dmsCode) && waybillPickup != null && waybillPickup.getPickupSiteId() != null && waybillPickup.getPickupSiteId() > 0) {
                BaseStaffSiteOrgDto dto = baseMajorManager.getBaseSiteBySiteId(waybillPickup.getPickupSiteId());
                if (dto != null) {
                    dmsCode = dto.getDmsId();
                }
                log.debug("运单号:{}.揽收站点:{}对应的分拣中心:{}" ,waybillCode,waybillPickup.getPickupSiteId(), dmsCode);
            }

            //判断有没有寄件城市
            if (!isVaildDms(dmsCode)) {
                Integer consignerCityId = null;
                if (waybillPickup != null && waybillPickup.getConsignerCityId() != null) {
                    consignerCityId = waybillPickup.getConsignerCityId();
                    log.debug("运单号：{}在运单中获取的寄件城市为：{}" ,waybillCode, consignerCityId);
                } else if (etmsWaybill != null && StringUtils.isNotBlank(etmsWaybill.getConsignerAddress())) {
                    //调gis接口
                    ExternalAddressRequest request = new ExternalAddressRequest();
                    request.setFullAddress(etmsWaybill.getConsignerAddress());
                    DmsExternalJDAddressResponse addressResult = externalAddressToJDAddressServiceManager.getJDDistrict(request);
                    if (addressResult != null) {
                        consignerCityId = addressResult.getCityCode();
                    }
                    log.debug("运单号：{} 根据寄件人地址获取到的寄件城市为:{} " ,waybillCode, consignerCityId);
                }
                if (consignerCityId != null && consignerCityId > 0) {
                    dmsCode = siteService.getCityBindDmsCode(consignerCityId);
                    log.debug("运单号:{} 寄件城市对应的分拣中心为：{}" ,waybillCode, dmsCode);
                }
            }
            if(!isVaildDms(dmsCode)) {
                log.warn("组装包裹标签始发分拣中心信息，运单号：{} 对应的始发分拣中心:{}，无效分拣中心编码" ,waybillCode, dmsCode);
            }else{
                log.warn("组装包裹标签始发分拣中心信息，运单号：{} 对应的始发分拣中心:{}，有效分拣中心编码" ,waybillCode, dmsCode);
            }
        }
        printWaybill.setOriginalDmsCode(dmsCode);
    }
    /**
     * C2C获取始发分拣中心
     * 1、传入始发分拣和操作站点一致，不做处理（网点就是分拣中心）
     * 2、预分拣站点和操作站点一致，不做处理（末端网点打印）
     * 3、传入操作站点并且是营业部，先取运单中揽收站点对应的揽收分拣中心、分拣中心，取不到则取操作网点对应的揽收分拣中心、分拣中心
     * 4、未传站点和分拣中心，取运单中揽收站点对应的揽收分拣中心、分拣中心
     * @param context
     * @param printWaybill
     * @param waybillPickup
     * @return
     */
    private Integer dealC2cDmsSiteCode(WaybillPrintContext context,BasePrintWaybill printWaybill,WaybillPickup waybillPickup) {
        CallerInfo callerInfo = Profiler.registerInfo("dms.web.WaybillCommonServiceImpl.dealC2cDmsSiteCode",
                Constants.UMP_APP_NAME_DMSWEB, false, true);
        try {
            Integer prepareSiteCode = printWaybill.getPrepareSiteCode();
            Integer siteCode = null;
            Integer dmsSiteCode = null;
            if(context != null && context.getRequest() != null) {
                siteCode = context.getRequest().getSiteCode();
                dmsSiteCode = context.getRequest().getDmsSiteCode();
            }
            //1、传入始发分拣和操作站点一致，不做处理（网点就是分拣中心）
            if(dmsSiteCode != null && dmsSiteCode.equals(siteCode)) {
                return null;
            }
            //2、预分拣站点和操作站点一致，不做处理（末端网点打印）
            if(prepareSiteCode != null && prepareSiteCode.equals(siteCode)) {
                return null;
            }
            BaseStaffSiteOrgDto userSiteInfo = null;
            //3、传入操作站点并且是营业部，先取运单中揽收站点对应的揽收分拣中心、分拣中心，取不到则取操作网点对应的揽收分拣中心、分拣中心
            if(NumberHelper.gt0(siteCode)){
                //客户端和站长工作台一定会传
                userSiteInfo = baseMajorManager.getBaseSiteBySiteId(siteCode);
                //操作站点是营业部
                if(userSiteInfo != null && BusinessHelper.isSiteType(userSiteInfo.getSiteType())) {
                    Integer dmsCode = getPickDmsCode(printWaybill,waybillPickup);
                    if(log.isDebugEnabled()) {
                        log.debug("3、传入操作站点是营业部,运单号：{} 取揽收站点对应的分拣中心:[{}] " ,printWaybill.getWaybillCode(), dmsCode);
                    }
                    if(isVaildDms(dmsCode)) {
                        return dmsCode;
                    }
                    dmsCode = getDmsIdFormSiteInfo(userSiteInfo);
                    if(log.isDebugEnabled()) {
                        log.debug("3、传入操作站点是营业部,运单号：{} 取操作站点对应的分拣中心:[{}] " ,printWaybill.getWaybillCode(), dmsCode);
                    }
                    if(isVaildDms(dmsCode)) {
                        return dmsCode;
                    }
                }
            }
            //4、未传站点和分拣中心，取运单中揽收站点对应的揽收分拣中心、分拣中心
            if (!isVaildDms(dmsSiteCode)) {
                Integer dmsCode = getPickDmsCode(printWaybill,waybillPickup);
                if(log.isDebugEnabled()) {
                    log.debug("4、未传站点和分拣中心,运单号：{} 取揽收站点对应的分拣中心:[{}] " ,printWaybill.getWaybillCode(), dmsCode);
                }
                if(isVaildDms(dmsCode)) {
                    return dmsCode;
                }
            }
            return null;
        } finally {
            Profiler.registerInfoEnd(callerInfo);
        }
    }
    /**
     * 获取揽收站点对应的分拣中心
     * @param printWaybill
     * @param waybillPickup
     * @return
     */
    private Integer getPickDmsCode(BasePrintWaybill printWaybill,WaybillPickup waybillPickup) {
        //判断有没有揽收站点
        if (waybillPickup != null && waybillPickup.getPickupSiteId() != null && waybillPickup.getPickupSiteId() > 0) {
	        BaseStaffSiteOrgDto dto = baseMajorManager.getBaseSiteBySiteId(waybillPickup.getPickupSiteId());
	        Integer dmsCode = getDmsIdFormSiteInfo(dto);
	        if(isVaildDms(dmsCode)) {
	        	return dmsCode;
	        }
        }
        return null;
    }
    /**
     * 获取站点对应的分拣中心，优先取揽收站点
     * @param siteInfo
     * @return
     */
    private Integer getDmsIdFormSiteInfo(BaseStaffSiteOrgDto siteInfo) {
        Integer dmsCode = null;
        if (siteInfo != null) {
        	dmsCode = siteInfo.getCollectionDmsId();
	        if(!isVaildDms(dmsCode)) {
	        	dmsCode = siteInfo.getDmsId();
	        }
        }
        return dmsCode;
    }
    /**
     * 判断是否是有效的分拣中心编码
     * @param dmsCode
     * @return
     */
    private boolean isVaildDms(Integer dmsCode){
        return dmsCode != null && dmsCode > 0;
    }

    /**
     * B网根据始发和目的获取路由信息
     * @param printWaybill
     */
    @Override
    @JProfiler(jKey = "DMS.BASE.WaybillCommonServiceImpl.loadWaybillRouter", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public void loadWaybillRouter(WaybillPrintRequest request, BasePrintWaybill printWaybill, Integer originalDmsCode, Integer destinationDmsCode, String waybillSign){

        //京象或者京管家调用直接添加路由信息
        log.info("loadWaybillRouter-request {}",JSON.toJSONString(request));
        if(request != null && WaybillPrintOperateTypeEnum.SMS_REPRINT.getType().equals(request.getOperateType())){
            log.info("直接获取路由，跳过B网检查 {}",printWaybill.getWaybillCode());
        }else {
            //非B网的不用查路由
            if(!BusinessUtil.isB2b(waybillSign)){
                log.info("非B网的不用查路由 {}",waybillSign);
                return;
            }
        }
        //调路由的接口获取路由节点
        List<String> routerNameList = null;
        try {
            routerNameList = vrsRouteTransferRelationManager.loadWaybillRouter(originalDmsCode, destinationDmsCode,
                    vrsRouteTransferRelationManager.obtainRouterProduct(waybillSign), new Date());
        } catch (Exception e) {
            log.error("获取路由环节信息失败waybillCode[{}]originalDmsCode[{}]destinationDmsCode[{}]",printWaybill.getWaybillCode(),originalDmsCode,destinationDmsCode,e);
        }
        log.info("加载路由信息判断 routerNameList 后--{}",JSON.toJSONString(routerNameList));
        log.debug("获取到的城市名列表为:{}" , routerNameList);
        if(routerNameList != null && routerNameList.size() > 0){
            for(int i=0;i<routerNameList.size();i++){
                try {
                    ObjectHelper.setValue(printWaybill,"routerNode" + (i + 1),routerNameList.get(i));
                    log.debug("设置router{}的值为:{}" ,(i+1), routerNameList.get(i));
                }catch (Exception e){
                    log.error("获取路由信息,设置路由节点失败.",e);
                }
            }
        }
        // B网转运执行新模板逻辑
        executeNewRouterLogic(printWaybill, originalDmsCode, destinationDmsCode, waybillSign);
    }

    /**
     * 新的路由执行逻辑
     *
     * @param printWaybill
     * @param originalDmsCode
     * @param destinationDmsCode
     * @param waybillSign
     */
    private void executeNewRouterLogic(BasePrintWaybill printWaybill, Integer originalDmsCode, Integer destinationDmsCode, String waybillSign) {
        if(!printWaybill.getExecuteNewRouterLogic()){
            return;
        }
        CallerInfo callerInfo = Profiler.registerInfo("DMS.BASE.WaybillCommonServiceImpl.executeNewRouterLogic", Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            // B网路由节点设置
            boolean isExecuteHasRouter = setBTemplateRouter(printWaybill, originalDmsCode, destinationDmsCode, waybillSign);
            // 始发目的处理
            dealOriginalAndDest(printWaybill, originalDmsCode, destinationDmsCode, isExecuteHasRouter);
        }catch (Exception e){
            log.error("B网转运面单设置'分拣代码'和'货区编码'异常!", e);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
    }

    /**
     * 始发目的处理
     *  1、始发目的是分拣中心则设置'滑道号-笼车号'
     *  2、始发目的是转运中心则设置'货区编码'
     *
     * @param printWaybill
     * @param originalDmsCode
     * @param destinationDmsCode
     * @param isExecuteHasRouter
     */
    private void dealOriginalAndDest(BasePrintWaybill printWaybill, Integer originalDmsCode, Integer destinationDmsCode, boolean isExecuteHasRouter) {
        BaseSiteInfoDto originalDms = baseMajorManager.getBaseSiteInfoBySiteId(originalDmsCode);
        BaseSiteInfoDto destDms = baseMajorManager.getBaseSiteInfoBySiteId(destinationDmsCode);
        if(originalDms == null || destDms == null){
            return;
        }
        boolean originalIsTransport = BusinessUtil.isTransportSite(originalDms.getSortType(), originalDms.getSortSubType());
        boolean destIsTransport = BusinessUtil.isTransportSite(destDms.getSortType(), destDms.getSortSubType());
        if(!originalIsTransport){
            // 始发是分拣：设置'滑道号-笼车号'
            printWaybill.setOriginalSectionAreaNo(printWaybill.getOriginalCrossCode() + Constants.SEPARATOR_HYPHEN + printWaybill.getOriginalTabletrolleyCode());
        }
        if(!destIsTransport){
            // 目的是分拣：设置'滑道号-笼车号'
            printWaybill.setDestinationSectionAreaNo(printWaybill.getDestinationCrossCode() + Constants.SEPARATOR_HYPHEN + printWaybill.getDestinationTabletrolleyCode());
        }else if(!isExecuteHasRouter){
            // 目的是转运：设置'货区编码'
            printWaybill.setDestinationSectionAreaNo(basicGoodsAreaManager.getGoodsAreaNextSite(destinationDmsCode, printWaybill.getPrepareSiteCode()));
        }
    }

    /**
     * 设置B网模板路由
     *  （存在路由节点）
     * @param printWaybill
     * @param originalDmsCode
     * @param destinationDmsCode
     * @param waybillSign
     * @return
     * @throws Exception
     */
    private boolean setBTemplateRouter(BasePrintWaybill printWaybill, Integer originalDmsCode, Integer destinationDmsCode, String waybillSign) throws Exception {
        RouteProductEnum routeProductEnum = vrsRouteTransferRelationManager.obtainRouterProduct(waybillSign);
        if(routeProductEnum == null){
            return false;
        }
        BaseSiteInfoDto originalDms = baseMajorManager.getBaseSiteInfoBySiteId(originalDmsCode);
        if (originalDms == null){
            return false;
        }
        BaseSiteInfoDto destinationDms = baseMajorManager.getBaseSiteInfoBySiteId(destinationDmsCode);
        if (destinationDms == null){
            return false;
        }
        String routerStr = vrsRouteTransferRelationManager.queryRecommendRoute(originalDms.getDmsCode(), destinationDms.getDmsCode(), new Date(), routeProductEnum);
        if (StringUtils.isEmpty(routerStr)){
            return false;
        }
        List<String> siteList = Arrays.asList(routerStr.split(Constants.WAYBILL_ROUTER_SPLIT));
        // 包含始发目的路由节点 > 2
        if (siteList.size() < 2 || !siteList.contains(originalDms.getDmsCode()) || !siteList.contains(destinationDms.getDmsCode())){
            return false;
        }
        // B网面单展示路由数据
        // 1、始发、中间节点、目的（加上始发目的总共最多6个节点，中间节点 > 4 则只取前4个）
        // 2、始发和目的的道口和笼车由'货区编码'替换，中间节点由'分拣代码'和'货区编码'组成
        // 3、'货区编码'由当前站点和下一站点获取，目的站点的'货区编码'由目的站点和预分拣站点获取
        int maxRouterSize = 6;
        List<String> subList = siteList.subList(siteList.indexOf(originalDms.getDmsCode()), siteList.lastIndexOf(destinationDms.getDmsCode()) + 1);
        List<String> finalSiteList = new ArrayList<>(subList);
        String reverseSecondNextSite = destinationDms.getDmsCode();// 倒数第二个节点的下一节点
        if(subList.size() > maxRouterSize){
            reverseSecondNextSite = subList.get(maxRouterSize - 1);
            finalSiteList = new ArrayList<>(subList.subList(0, maxRouterSize - 1));
            finalSiteList.add(destinationDms.getDmsCode());
        }
        if(log.isInfoEnabled()){
            log.info("根据始发:{}目的:{}路由产品类型:{}查询到的路由节点为:{},转换为B网面单的节点为:{}", originalDms.getDmsCode(), destinationDms.getDmsCode(), routeProductEnum.getValue(),
                    JsonHelper.toJson(siteList), JsonHelper.toJson(finalSiteList));
        }
        int reverseSecondIndex = finalSiteList.size() - 2;// 倒数第二个下标
        int lastIndex = finalSiteList.size() - 1;// 最后一个下标
        for(int i = 0; i < finalSiteList.size(); i ++){
            BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteByDmsCode(finalSiteList.get(i));
            Integer siteCode = baseSite.getSiteCode();
            BaseSiteInfoDto distributedSite = baseMajorManager.getBaseSiteInfoBySiteId(siteCode);
            if(i == lastIndex){
                // 最后一个节点处理
                printWaybill.setDestinationSectionAreaNo(basicGoodsAreaManager.getGoodsAreaNextSite(siteCode, printWaybill.getPrepareSiteCode()));
            }else if(finalSiteList.size() > 2 && i == reverseSecondIndex){
                // 倒数第二个节点处理（前提：节点数 > 2）
                ObjectHelper.setValue(printWaybill,"routerSectionNo" + (i + 1), distributedSite == null ? null : distributedSite.getDistributeCode());
                BaseStaffSiteOrgDto nextSite = baseMajorManager.getBaseSiteByDmsCode(reverseSecondNextSite);
                ObjectHelper.setValue(printWaybill,"routerSectionAreaNo" + (i + 1),
                        basicGoodsAreaManager.getGoodsAreaNextSite(siteCode, nextSite == null ? null : nextSite.getSiteCode()));
            }else if(i == 0){
                // 第一个节点处理
                BaseStaffSiteOrgDto currentNextSite = baseMajorManager.getBaseSiteByDmsCode(finalSiteList.get(i + 1));
                printWaybill.setOriginalSectionAreaNo(basicGoodsAreaManager.getGoodsAreaNextSite(siteCode, currentNextSite == null ? null : currentNextSite.getSiteCode()));
            }else {
                ObjectHelper.setValue(printWaybill,"routerSectionNo" + (i + 1), distributedSite == null ? null : distributedSite.getDistributeCode());
                BaseStaffSiteOrgDto currentNextSite = baseMajorManager.getBaseSiteByDmsCode(finalSiteList.get(i + 1));
                ObjectHelper.setValue(printWaybill,"routerSectionAreaNo" + (i + 1),
                        basicGoodsAreaManager.getGoodsAreaNextSite(siteCode, currentNextSite == null ? null : currentNextSite.getSiteCode()));
            }
        }
        return true;
    }

    /**
     * 通过运单号获取包裹数量
     * @param waybillCode
     * @return
     */
    @Override
    public InvokeResult<Integer> getPackNum(String waybillCode) {
        InvokeResult<Integer> result = new InvokeResult<Integer>();
        Integer packNum = 0;
        try{
            BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode, true, true, true, false);
            if(baseEntity != null && baseEntity.getData() != null && baseEntity.getData().getWaybill() != null){
                packNum = baseEntity.getData().getWaybill().getGoodNumber() == null?0:baseEntity.getData().getWaybill().getGoodNumber();
                result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
            }else{
                this.log.warn("未获取到该运单{}信息",waybillCode);
                result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
                result.setMessage("未获取到该运单"+waybillCode+"信息");
            }
        }catch(Exception e){
            this.log.error("通过运单号:{}获取运单信息失败!",waybillCode,e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        result.setData(packNum);
        return result;
    }

    /**
     * 校验包裹是否存在
     *
     * @param packCode
     * @return
     */
    @Override
    public boolean checkPackExist(String packCode) {
        if(StringUtils.isBlank(packCode) || !WaybillUtil.isPackageCode(packCode)){
            return false;
        }

        Waybill waybill = this.findWaybillAndPack(WaybillUtil.getWaybillCode(packCode));
        if(waybill!=null && waybill.getPackList()!=null){
            for(Pack pack :waybill.getPackList()){
                if(packCode.equals(pack.getPackageCode())){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 加载特殊要求信息
     * @param printWaybill
     */
    public void loadSpecialRequirement(BasePrintWaybill printWaybill,String waybillSign,com.jd.etms.waybill.domain.Waybill waybill){
        String specialRequirement = "";
        //货款是货到付款，设置COD
        if(TextConstants.GOODS_PAYMENT_COD.equals(printWaybill.getGoodsPaymentText())){
        	specialRequirement = specialRequirement + TextConstants.GOODS_PAYMENT_COD_FLAG + ",";
        }
        if(StringUtils.isNotBlank(waybillSign)){
            //签单返还
            if(BusinessUtil.isSignBack(waybillSign)){
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
            // 精准送仓
            WaybillExt waybillExt = waybill.getWaybillExt();
            String productType = null;
            if(waybillExt != null){
                productType = waybillExt.getProductType();
            }
            //精准送仓 优先于 送货入仓
            if(BusinessUtil.isColdChainKB(waybill.getWaybillSign(),productType)&& WaybillVasUtil.isJZSC(printWaybill.getWaybillVasSign())){
                specialRequirement = specialRequirement + SPECIAL_REQUIRMENT_JZSC + ",";
            }else{
                //送货入仓
                if(BusinessUtil.isSignChar(waybillSign,42,'1')){
                    if(BusinessUtil.isSignInChars(waybillSign,89,'1','2')){
                        if(BusinessUtil.isSignChar(waybillSign,80,'B')){
                            specialRequirement = specialRequirement + SPECIAL_REQUIRMENT_SPEED_DELIVERY_WAREHOUSE + ",";
                        }
                    } else {
                        specialRequirement = specialRequirement + SPECIAL_REQUIRMENT_DELIVERY_WAREHOUSE + ",";
                    }
                }
            }

            //装车
            if(BusinessUtil.isSignChar(waybillSign,41,'1')){
                specialRequirement = specialRequirement + SPECIAL_REQUIRMENT_LOAD_CAR + ",";
            }
            //卸车
            if(BusinessUtil.isSignChar(waybillSign,41,'2')){
                specialRequirement = specialRequirement + SPECIAL_REQUIRMENT_UNLOAD_CAR + ",";
            }
            //装卸车
            if(BusinessUtil.isSignChar(waybillSign,41,'3')){
                specialRequirement = specialRequirement + SPECIAL_REQUIRMENT_LOAD_UNLOAD_CAR + ",";
            }
            //特安送
            if (waybill != null && StringHelper.isNotEmpty(waybill.getWaybillCode()) && waybillService.isSpecialRequirementTeAnSongService(waybill.getWaybillCode(), waybillSign)) {
                specialRequirement = specialRequirement + SPECIAL_REQUIREMENT_TE_AN_SONG + ",";
            }
        }
        if(StringUtils.isNotBlank(specialRequirement)){
            printWaybill.setSpecialRequirement(specialRequirement.substring(0,specialRequirement.length()-1));
        }
    }

    /**
     * 修改取件单换单后新单的包裹数
     * @param newWaybillCode 新单号
     * @param packNum 新单包裹数量
     * @return
     */
    @Override
    public InvokeResult batchUpdatePackageByWaybillCode(String newWaybillCode, Integer packNum){
        log.debug("{}调用运单接口batchUpdatePackageByWaybillCode,修改运单包裹数量:{}" ,newWaybillCode, packNum);
        InvokeResult result = new InvokeResult();
        result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);

        if(!WaybillUtil.isWaybillCode(newWaybillCode) || packNum == null || packNum <= 0 || packNum > 99){
            log.warn("参数不能为空!");
            result.setMessage(InvokeResult.PARAM_ERROR);
            return result;
        }

        try {
            //判断是否操作过修改包裹数
            PopPrint popPrint = popPrintService.findByWaybillCode(newWaybillCode);
            if(popPrint != null){
                result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
                result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
                return result;
            }
            List<PackageUpdateDto> packageList = new ArrayList<>();
            Date createTime = new Date();
            List<String> list = WaybillUtil.generateAllPackageCodesByPackNum(newWaybillCode, packNum);
            for(int i = 0; i < list.size(); i++){
                PackageUpdateDto dto = new PackageUpdateDto();
                dto.setWaybillCode(newWaybillCode);
                dto.setPackageBarcode(list.get(i));
                dto.setCreateTime(createTime);
                packageList.add(dto);
            }
            BaseEntity<Boolean> entity = waybillQueryManager.batchUpdatePackageByWaybillCode(newWaybillCode, packageList);
            if(log.isDebugEnabled()){
                log.debug("修改运单{}包裹数：{}",newWaybillCode, JsonHelper.toJson(entity));
            }
            if(entity.getResultCode() == EnumBusiCode.BUSI_SUCCESS.getCode()){
                result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
                result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
            }else{
                log.warn("修改{}的包裹数失败:{}",newWaybillCode,entity.getMessage());
                result.setMessage("修改"+newWaybillCode+"的包裹数失败!");
            }
        }catch (Exception e){
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
            log.error("batchUpdatePackageByWaybillCode异常",e);
        }
        return result;
    }

    //处理特殊的distributTypeText
    //当waybill_sign第54位等于4时，表示为冷链医药运单，面单显示医药温层，
    //显示的字样读取waybill_sign第43位对应的枚举值，面单显示括号中的汉字：
    private void processSpecialDistributTypeText(BasePrintWaybill printWaybill,String waybillSign){
        if(BusinessUtil.isBMedicine(waybillSign)) {
            if (BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_43, WaybillSignConstants.CHAR_43_1)) {
                printWaybill.setDistributTypeText(DISTRIBUTE_TYPE_COLD);
            }else if(BusinessUtil.isSignChar(waybillSign,WaybillSignConstants.POSITION_43,WaybillSignConstants.CHAR_43_2)){
                printWaybill.setDistributTypeText(DISTRIBUTE_TYPE_COOL);
            }else if(BusinessUtil.isSignChar(waybillSign,WaybillSignConstants.POSITION_43,WaybillSignConstants.CHAR_43_3)){
                printWaybill.setDistributTypeText(DISTRIBUTE_TYPE_CONTROL_TEMP);
            }else if(BusinessUtil.isSignInChars(waybillSign,WaybillSignConstants.POSITION_43,WaybillSignConstants.CHAR_43_4)){
                printWaybill.setDistributTypeText(DISTRIBUTE_TYPE_NORMAL);
            }else if(BusinessUtil.isSignInChars(waybillSign,WaybillSignConstants.POSITION_43,WaybillSignConstants.CHAR_43_5)){
                printWaybill.setDistributTypeText(DISTRIBUTE_TYPE_FREEZING);
                printWaybill.appendSpecialMark(DISTRIBUTE_TYPE_FREEZING);
            }else if(BusinessUtil.isSignInChars(waybillSign,WaybillSignConstants.POSITION_43,WaybillSignConstants.CHAR_43_6)){
                printWaybill.setDistributTypeText(DISTRIBUTE_TYPE_PRECISION_COLD);
                printWaybill.appendSpecialMark(DISTRIBUTE_TYPE_PRECISION_COLD);
            }
        }
    }

    /**
     * 根据运单号获得增值服务列表
     * @param waybillCode
     * @return
     */
    @Override
    public List<WaybillVasDto> getWaybillVasList(String waybillCode) {
        BaseEntity<List<WaybillVasDto>> baseEntity = waybillQueryManager.getWaybillVasInfosByWaybillCode(waybillCode);
        if(baseEntity == null || baseEntity.getResultCode() != EnumBusiCode.BUSI_SUCCESS.getCode()
                || CollectionUtils.isEmpty(baseEntity.getData())){
            return null;
        }
        return baseEntity.getData();
    }

    @Override
    public boolean isMatchGetCrossOfAir(String waybillSign, String sendPay,Integer prepareSiteCode,Integer dmsSiteCode,String waybillCode) {
        log.info("isMatchGetCrossOfAir 入参-{}-{}-{}-{}-{}",waybillSign,sendPay,prepareSiteCode,dmsSiteCode,waybillCode);
        if (BusinessUtil.checkCanAirToRoad(waybillSign, sendPay) || BusinessUtil.isAirFill(waybillSign)) {
            log.info("航空/航填面单---");
//            预分拣派送站点
//            Integer prepareSiteCode = printWaybill.getPrepareSiteCode();
            //根据网点信息查询绑定的分拣中心
            BaseStaffSiteOrgDto baseSiteInfo = baseMajorManager.getBaseSiteBySiteId(prepareSiteCode);
            Integer dmsId = null;
            if (baseSiteInfo != null) {
                dmsId = baseSiteInfo.getDmsId();
            }
            //校验绑定的分拣中心和当前操作场地是否相同
            log.info("绑定的分拣中心-{}-当前操作的分拣中心编码-{}", dmsId, dmsSiteCode);
            if (Objects.equals(dmsId, dmsSiteCode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 处理“尊” 标识位逻辑
     * @param
     * @param waybillSign
     * @return
     */
    private void appendRespectTypeText(BasePrintWaybill target,String waybillSign,WaybillExt waybillExt){
        // 产品类型为md-m-0005时:医药专送,展示药
        if(waybillExt != null && Constants.PRODUCT_TYPE_MEDICINE_SPECIAL_DELIVERY.equals(waybillExt.getProductType())) {
            target.setRespectTypeText(TextConstants.COMMON_TEXT_MEDICINE);
        }
        //waybill_sign标识位，第三十五位为1，一体化面单显示"尊"
        if(BusinessUtil.isSignChar(waybillSign,35,'1')){
            //提出-尊标识
            target.setRespectTypeText(TextConstants.SPECIAL_MARK_SENIOR );
        }
        //“碎” 在 “尊” 的标识位追加
        String sendPayMap = waybillExt == null ? null :waybillExt.getSendPayMap();
        if(BusinessHelper.isFragile(JsonHelper.json2MapByJSON(sendPayMap))){
            target.setRespectTypeText(StringHelper.append(target.getRespectTypeText(), TextConstants.SPECIAL_MARK_NC_TEXT) );
        }
        //waybill_sign标识位 135=2 判断是否为NC易碎件 (与尊字进行拼接，展示优先级为尊NC)
        if(BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_135, WaybillSignConstants.CHAR_135_2)){
            target.setRespectTypeText(StringHelper.append(target.getRespectTypeText(), TextConstants.SPECIAL_MARK_NC) );
        }
        log.info("appendRespectTypeText-{}",target.getRespectTypeText());
        target.appendSpecialMark(target.getRespectTypeText(),false);
    }


    /**
     * 获取预分拣站点ID
     * @param waybillCode
     * @return
     */
    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "WaybillCommonServiceImpl.fetchOldSiteId",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<Integer> fetchOldSiteId(String waybillCode) {
        String methodDesc = "WaybillCommonServiceImpl.fetchOldSiteId:";
        InvokeResult<Integer> result = new InvokeResult<>();
        result.success();
        try {
            com.jd.etms.waybill.domain.Waybill waybill = null;
            /* 获取包裹的运单数据，如果单号正确的话 */
            BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode, true, false, false, false);
            if(Objects.isNull(baseEntity)
                    || Objects.isNull(baseEntity.getData())
                    || Objects.isNull(baseEntity.getData().getWaybill())
                    || Objects.isNull(baseEntity.getData().getWaybill().getOldSiteId())
                    || Objects.isNull(baseEntity.getData().getWaybill().getOldSiteId() < 0 )){
                log.warn("获取预分拣站点为空，单号={}， res={}", waybillCode, JsonHelper.toJson(baseEntity));
                result.error("获取运单预分拣站点失败");
                return result;
            }
            result.setData(baseEntity.getData().getWaybill().getOldSiteId());
            return result;
        } catch (Exception e) {
            log.error("{}运单接口调用异常,单号为：{},errMsg={}" , methodDesc, waybillCode, e.getMessage(), e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            String msg = HintService.getHint(HintCodeConstants.GET_WAYBILL_CHOICE_ERROR);
            result.setMessage(StringUtils.isBlank(msg) ? "查运单获取预分拣场地服务失败" : msg);
            return result;
        }
    }

    /**
     * 查询增值服务
     * @param waybillCode 运单号
     * @param waybillVasEnum 自定义增值服务枚举
     * @return 是否包含增值服务
     * @author fanggang7
     * @time 2023-08-10 10:55:02 周四
     */
    @Override
    public Result<Boolean> checkWaybillVas(String waybillCode, WaybillVasEnum waybillVasEnum) {
        log.info("WaybillCommonServiceImpl_checkWaybillVas {} {}", waybillCode, waybillVasEnum);
        Result<Boolean> result = Result.success(false);

        try {
            BaseEntity<List<WaybillVasDto>> baseEntity = waybillQueryManager.getWaybillVasInfosByWaybillCode(waybillCode);
            if(baseEntity == null){
                log.error("WaybillCommonServiceImpl_checkWaybillVas getWaybillVasInfosByWaybillCode null {} {}", waybillCode, waybillVasEnum);
                return result.toFail("查询运单增值服务数据为空");
            }
            if(baseEntity.getResultCode() != EnumBusiCode.BUSI_SUCCESS.getCode()){
                log.error("WaybillCommonServiceImpl_checkWaybillVas getWaybillVasInfosByWaybillCode fail {} {}", waybillCode, waybillVasEnum);
                return result.toFail("查询运单增值服务数据失败");
            }
            if(CollectionUtils.isEmpty(baseEntity.getData())){
                return result.setData(false);
            }
            final List<WaybillVasDto> waybillVasList = baseEntity.getData();
            if(waybillVasEnum.equals(WaybillVasEnum.WAYBILL_VAS_SPECIAL_SAFEGUARD_COLD_FRESH_OPERATION)){
                result = this.checkWaybillVas4SpecialSafeguardColdFresh(waybillCode, waybillVasEnum, waybillVasList);
            }
            return result;

        } catch (Exception e) {
            log.error("WaybillCommonServiceImpl_checkWaybillVas {} {}", waybillCode, waybillVasEnum, e);
        }
        return result;
    }

    private Result<Boolean> checkWaybillVas4SpecialSafeguardColdFresh(String waybillCode, WaybillVasEnum waybillVasEnum, List<WaybillVasDto> waybillVasList){
        Result<Boolean> result = Result.success(false);
        boolean matchVas = false;
        for(WaybillVasDto waybillVasDto : waybillVasList){
            if(Objects.equals(WaybillVasEnum.WAYBILL_VAS_SPECIAL_SAFEGUARD.getCode(), waybillVasDto.getVasNo())){
                matchVas = true;
                break;
            }
        }
        if(!matchVas){
            return result.setData(false);
        }
        // 查询增值服务扩展属性
        final BaseEntity<WaybillVasDto> waybillVasWithExtendInfoResult = waybillQueryManager.getWaybillVasWithExtendInfoByWaybillCode(waybillCode, WaybillVasEnum.WAYBILL_VAS_SPECIAL_SAFEGUARD.getCode());
        if (waybillVasWithExtendInfoResult == null) {
            log.error("WaybillCommonServiceImpl_checkWaybillVas getWaybillVasWithExtendInfoByWaybillCode null {} {}", waybillCode, WaybillVasEnum.WAYBILL_VAS_SPECIAL_SAFEGUARD.getCode());
            return result.toFail("查询运单增值服务数据为空");
        }
        if(waybillVasWithExtendInfoResult.getResultCode() != EnumBusiCode.BUSI_SUCCESS.getCode()){
            log.error("WaybillCommonServiceImpl_checkWaybillVas getWaybillVasWithExtendInfoByWaybillCode fail {} {}", waybillCode, waybillVasEnum);
            return result.toFail("查询运单增值服务数据失败");
        }
        if (waybillVasWithExtendInfoResult.getData() == null) {
            return result;
        }
        final WaybillVasDto waybillVasDto = waybillVasWithExtendInfoResult.getData();
        final Map<String, String> extendMap = waybillVasDto.getExtendMap();
        if(extendMap == null){
            return result;
        }
        for (String key : extendMap.keySet()) {
            if(Objects.equals(key, WaybillVasEnum.WaybillVasOtherParamEnum.GUARANTEE_TYPE_COLD_FRESH_OPERATION.getCode()) && Objects.equals(extendMap.get(key), WaybillVasEnum.WaybillVasOtherParamEnum.GUARANTEE_TYPE_COLD_FRESH_OPERATION.getValue())){
                return result.setData(true);
            }
        }
        return result;
    }

    /**
     * 查询增值服务
     *
     * @param waybillCode       运单号
     * @param waybillVasEnum    自定义增值服务枚举
     * @param waybillVasDtoList 增值服务列表
     * @return 是否包含增值服务
     * @author fanggang7
     * @time 2023-08-10 10:55:02 周四
     */
    @Override
    public Result<Boolean> checkWaybillVas(String waybillCode, WaybillVasEnum waybillVasEnum, List<WaybillVasDto> waybillVasDtoList) {
        log.info("WaybillCommonServiceImpl_checkWaybillVas {} {}", waybillCode, waybillVasEnum);
        Result<Boolean> result = Result.success(false);

        try {
            if(CollectionUtils.isEmpty(waybillVasDtoList)){
                return result.setData(false);
            }
            // 生鲜特保
            if(waybillVasEnum.equals(WaybillVasEnum.WAYBILL_VAS_SPECIAL_SAFEGUARD_COLD_FRESH_OPERATION)){
                return this.checkWaybillVas4SpecialSafeguardColdFresh(waybillCode, waybillVasEnum, waybillVasDtoList);
            }
            // 特安
            if(waybillVasEnum.equals(WaybillVasEnum.WAYBILL_VAS_SPECIAL_SAFETY)){
                return this.checkWaybillVas4SpecialSafety(waybillCode, waybillVasEnum, waybillVasDtoList);
            }
            return result;

        } catch (Exception e) {
            log.error("WaybillCommonServiceImpl_checkWaybillVas {} {}", waybillCode, waybillVasEnum, e);
        }
        return result;
    }

    /**
     * 特安增值类型判断
     * @param waybillCode 运单号
     * @param waybillVasEnum 增值服务枚举
     * @param waybillVasList 增值服务列表
     * @return 判断结果
     */
    private Result<Boolean> checkWaybillVas4SpecialSafety(String waybillCode, WaybillVasEnum waybillVasEnum, List<WaybillVasDto> waybillVasList){
        Result<Boolean> result = Result.success(false);
        boolean matchVas = false;
        for(WaybillVasDto waybillVasDto : waybillVasList){
            if(Objects.equals(WaybillVasEnum.WAYBILL_VAS_SPECIAL_SAFETY.getCode(), waybillVasDto.getVasNo())){
                matchVas = true;
                break;
            }
        }
        if(!matchVas){
            return result.setData(false);
        }
        return result;
    }
}
