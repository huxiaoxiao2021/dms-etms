package com.jd.bluedragon.common.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.TextConstants;
import com.jd.bluedragon.common.domain.Pack;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BasicSafInterfaceManager;
import com.jd.bluedragon.core.base.PreseparateWaybillManager;
import com.jd.bluedragon.core.base.VrsRouteTransferRelationManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.order.ws.OrderWebService;
import com.jd.bluedragon.distribution.popPrint.domain.PopPrint;
import com.jd.bluedragon.distribution.popPrint.service.PopPrintService;
import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import com.jd.bluedragon.distribution.print.service.ComposeService;
import com.jd.bluedragon.distribution.print.service.HideInfoService;
import com.jd.bluedragon.distribution.print.service.WaybillPrintService;
import com.jd.bluedragon.distribution.product.domain.Product;
import com.jd.bluedragon.distribution.product.service.ProductService;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.dms.utils.SendPayConstants;
import com.jd.bluedragon.dms.utils.WaybillSignConstants;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BigDecimalHelper;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.api.common.enums.RouteProductEnum;
import com.jd.etms.cache.util.EnumBusiCode;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.etms.waybill.api.WaybillPackageApi;
import com.jd.etms.waybill.api.WaybillPickupTaskApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.Goods;
import com.jd.etms.waybill.domain.PackageWeigh;
import com.jd.etms.waybill.domain.PickupTask;
import com.jd.etms.waybill.domain.WaybillExt;
import com.jd.etms.waybill.domain.WaybillManageDomain;
import com.jd.etms.waybill.domain.WaybillPickup;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.PackOpeFlowDto;
import com.jd.etms.waybill.dto.PackageUpdateDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.etms.waybill.dto.WaybillVasDto;
import com.jd.preseparate.vo.external.AnalysisAddressResult;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

@Service("waybillCommonService")
public class WaybillCommonServiceImpl implements WaybillCommonService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
    private static final int REST_CODE_SUC = 1;

    @Autowired
    private ProductService productService;

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
    private WaybillService waybillService;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    private static final String STORAGEWAYBILL_REDIS_KEY_PREFIX = "STORAGEWAY_KEY_";


    @Value("${WaybillCommonServiceImpl.additionalComment:http://www.jdwl.com   客服电话：950616}")
    private String additionalComment;

    @Value("${WaybillCommonServiceImpl.popularizeMatrixCode:http://weixin.qq.com/q/02ixD6QH52bQO100000074}")
    private String popularizeMatrixCode;

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
    private static final String SPECIAL_REQUIRMENT_DELIVERY_WAREHOUSE="送货入仓";
    private static final String SPECIAL_REQUIRMENT_PRICE_PROTECT_MONEY = "保价";
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
                        if (d.getGoodWeight() != null) {
                            pack.setWeight(String.valueOf(d.getGoodWeight()));
                            pack.setPackageWeight(d.getGoodWeight() + "kg");
                        }
                        if (StringUtils.isNotEmpty(d.getPackageBarcode())) {
                            String[] pcs = d.getPackageBarcode().split("[-NS]");
                            pack.setPackSerial(Integer.valueOf(pcs[1]));
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
                    this.log.warn("调用运单接口获得商品明细数据为空");
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
		Map<String,PackOpeFlowDto> res = new TreeMap<String,PackOpeFlowDto>();
		//校验传入参数是否合法
		if(StringHelper.isEmpty(waybillCode)||opeType==null){
			return res;
		}
		//调用运单接口，获取称重流水信息
		BaseEntity<List<PackOpeFlowDto>> rest = this.waybillPackageApi.getPackOpeByWaybillCode(waybillCode);
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
    public BasePrintWaybill setBasePrintInfoByWaybill(BasePrintWaybill target, com.jd.etms.waybill.domain.Waybill waybill){
    	if(target==null||waybill==null){
    		return target;
    	}
		waybillPrintService.dealSignTexts(waybill.getWaybillSign(), target, Constants.DIC_NAME_WAYBILL_SIGN_CONFIG);
		waybillPrintService.dealSignTexts(waybill.getSendPay(), target, Constants.DIC_NAME_SEND_PAY_CONFIG);

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
        }else{
            target.setJdLogoImageKey(LOGO_IMAGE_KEY_JD);
            target.setPopularizeMatrixCode(popularizeMatrixCode);
            target.setAdditionalComment(additionalComment);
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
        String freightText = "";
        String goodsPaymentText = "";
        //运费：waybillSign 25位为2时【到付】,需求R2020072253033：只保留到付
        if(BusinessUtil.isSignChar(waybill.getWaybillSign(), 25, '2')){
            freightText = TextConstants.FREIGHT_PAY;
        }
        if(BusinessUtil.isB2b(waybill.getWaybillSign())){
        	//货款字段金额等于0时，则货款位置不显示
        	//货款字段金额大于0时，则货款位置显示为【代收货款】
        	if(NumberHelper.gt0(waybill.getCodMoney())){
        		goodsPaymentText = TextConstants.GOODS_PAYMENT_NEED_PAY;
        	}else{
        		goodsPaymentText = "";
        	}
        }else{
            //C网货款
            //货款：waybillSign 货款大于0时，25位为2或3时显示【货到付款】，25位不为2且不为3时显示应收金额，
            //货款等于0时，显示0
            if(NumberHelper.gt0(waybill.getCodMoney())){
                if(BusinessUtil.isSignInChars(waybill.getWaybillSign(),25,'2','3')){
                    goodsPaymentText = TextConstants.GOODS_PAYMENT_COD;
                }else{
                    goodsPaymentText = "￥"+ waybill.getCodMoney();
                    if (ComposeService.ONLINE_PAYMENT_SIGN.equals(waybill.getPayment())) {
                        goodsPaymentText = TextConstants.GOODS_PAYMENT_ONLINE;
                    }
                }
            } else{
                goodsPaymentText = "0";
            }
        }
        target.setFreightText(freightText);
        target.setGoodsPaymentText(goodsPaymentText);

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

        /**
         * 纯配B2B运单或纯配C转B运单，面单上加“B-”+【运单号】后4位
         */
        if(BusinessUtil.isSignChar(waybill.getWaybillSign(),WaybillSignConstants.POSITION_40,WaybillSignConstants.CHAR_40_2)
                ||BusinessUtil.isSignChar(waybill.getWaybillSign(),WaybillSignConstants.POSITION_97,WaybillSignConstants.CHAR_97_1)
                ||BusinessUtil.isSignChar(waybill.getSendPay(),SendPayConstants.POSITION_146,SendPayConstants.CHAR_146_1)){
        	if(StringHelper.isNotEmpty(target.getWaybillCodeLast())){
        		target.setBcSign(TextConstants.PECIAL_B_MARK1.concat(target.getWaybillCodeLast()));
        	}else{
        		target.setBcSign(TextConstants.PECIAL_B_MARK);
        	}
        }

        /* waybill_sign标识位，第七十九位为2，打提字标
           标位变更 ：2020-4-29
           详细见方法释义
         */
        if(BusinessUtil.isSignChar(waybill.getWaybillSign(), 79,'2') || BusinessUtil.isZiTiByWaybillSign(waybill.getWaybillSign())){
            target.appendSpecialMark(ComposeService.SPECIAL_MARK_ARAYACAK_SITE);
        }
        //waybill_sign标识位，第二十九位为8，打C字标
        if(BusinessUtil.isSignChar(waybill.getWaybillSign(),29,'8')){
            target.appendSpecialMark(ComposeService.SPECIAL_MARK_C);
            //一体化面单中商家ID，商家订单不显示
            target.setBusiCode("");
            target.setBusiOrderCode("");
        }

         /*
         * 1. waybill_sign第31位=1 且 116位=3 且 16位=4 ，打印【特快送 次晨】
         * 2. waybill_sign第31位=1 且 116位=3 且 16位不等于4 ，打印【特快送】
         * 3. waybill_sign第31位=4 且 16位=4 ，打印【特快送 次晨】
         * 4. waybill_sign第31位=4 且 16位不等于4 ，打印【特快送】
         * 5. waybill_sign第31位=1 且 116位=2，打印【特快送 同城】
         * 6. waybill_sign第31位=2，打印【特快送 同城】
         * 7.以上都不满足时，waybill_sign第31位=1，打印【特快送】
         */
        if(BusinessUtil.isExpressDeliveryNextMorning(waybill.getWaybillSign())){
            target.setTransportMode(TextConstants.EXPRESS_DELIVERY_NEXT_MORNING);
        }else if(BusinessUtil.isExpressDeliverySameCity(waybill.getWaybillSign())){
            target.setTransportMode(TextConstants.EXPRESS_DELIVERY_SAME_CITY);
        }else if(BusinessUtil.isExpressDelivery(waybill.getWaybillSign())
            ||BusinessUtil.isSignChar(waybill.getWaybillSign(),WaybillSignConstants.POSITION_31,WaybillSignConstants.CHAR_31_1)){
            target.setTransportMode(TextConstants.EXPRESS_DELIVERY);
        }

        /*
        识别waybillsign116位=2，面单“时效”字段处展示“同城”；
	    识别waybillsign116位=3，面单“时效”字段处展示“次晨”； 回改 20200203
        */
        if(!BusinessUtil.isExpressDeliverySameCity(waybill.getWaybillSign())
                && !BusinessUtil.isExpressDelivery(waybill.getWaybillSign())
                && BusinessUtil.isSameCity(waybill.getWaybillSign())){
            target.appendSpecialMark(ComposeService.SPECIAL_MARK_SAME_CITY);
        }
        if(!BusinessUtil.isExpressDeliveryNextMorning(waybill.getWaybillSign())
                && !BusinessUtil.isExpressDelivery(waybill.getWaybillSign())
                && BusinessUtil.isNextMorning(waybill.getWaybillSign())){
            target.appendSpecialMark(ComposeService.SPECIAL_MARK_NEXT_DAY);
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
        
        //sendpay167位不等于0时，面单模板打印【京准达快递到车】
	    if(StringHelper.isNotEmpty(waybill.getSendPay())
	    		&& waybill.getSendPay().length() >= 167
	    		&& !BusinessUtil.isSignChar(waybill.getSendPay(), 167, '0')){
	    	target.setjZDFlag(TextConstants.TEXT_TRANSPORT_KDDC);
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
            //一体化面单，显示生鲜专送
            target.setTransportMode(ComposeService.PREPARE_SITE_NAME_FRESH_SEND);
        }
        //设置运输方式
        setTransportTypeText(target,waybill);
        //waybill_sign标识位，第十六位为1且第三十一位为2且第五十五位为0，打同字标
        if(!BusinessUtil.isB2b(waybill.getWaybillSign()) &&
                BusinessUtil.isSignChar(waybill.getWaybillSign(),16,'1') &&
                BusinessUtil.isSignChar(waybill.getWaybillSign(),31,'2') &&
                BusinessUtil.isSignChar(waybill.getWaybillSign(),55,'0') ){
            target.appendSpecialMark(ComposeService.SPECIAL_MARK_SAME);
            //一体化面单，显示同城当日达
            target.setTransportMode(ComposeService.PREPARE_SITE_NAME_SAMECITY_ARRIVE);
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
        //waybill_sign标识位，第三十一位为5，一体化面单显示"微小件"
        if(BusinessUtil.isSignChar(waybill.getWaybillSign(),31,'5')){
            target.setTransportMode(ComposeService.PREPARE_SITE_NAME_SMALL_PACKAGE);
        }
        //waybill_sign标识位，第三十五位为1，一体化面单显示"尊"
        if(BusinessUtil.isSignChar(waybill.getWaybillSign(),35,'1')){
        	target.appendSpecialMark(TextConstants.SPECIAL_MARK_SENIOR);
        }

        //waybill_sign标识位，第九十二位为2，一体化面单显示"器"
        if(BusinessUtil.isSignChar(waybill.getWaybillSign(), Constants.WAYBILL_SIGN_POSITION_92, Constants.WAYBILL_SIGN_POSITION_92_2)){
            target.appendSpecialMark(ComposeService.SPECIAL_MARK_UTENSIL);
        }
        //waybill_sign标识位，第九十二位为3，一体化面单显示"箱"
        if(BusinessUtil.isSignChar(waybill.getWaybillSign(),Constants.WAYBILL_SIGN_POSITION_92,Constants.WAYBILL_SIGN_POSITION_92_3)){
            target.appendSpecialMark(ComposeService.SPECIAL_MARK_BOX);
        }
        //拆包面单打印拆包员号码,拆包号不为空则路区号位置显示拆包号
        if(waybill.getWaybillExt() != null && StringUtils.isNotBlank(waybill.getWaybillExt().getUnpackClassifyNum())){
            target.setRoad(waybill.getWaybillExt().getUnpackClassifyNum());
            target.setRoadCode(waybill.getWaybillExt().getUnpackClassifyNum());
        	target.setUnpackClassifyNum(waybill.getWaybillExt().getUnpackClassifyNum());
        }
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
        //waybill_sign第57位= 2，代表“KA运营特殊保障”，追加“KA”
        if(BusinessUtil.isSignChar(waybill.getWaybillSign(), WaybillSignConstants.POSITION_57, WaybillSignConstants.CHAR_57_2)){
        	target.appendSpecialMark(TextConstants.KA_FLAG);
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
            target.setGoodsName(waybill.getWaybillExt().getConsignWare());
        }
        //大件路区
        if(BusinessUtil.isHeavyCargo(waybill.getWaybillSign())){
            target.setBackupRoadCode(waybill.getRoadCode());
        }      
        return target;
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
        //waybil_sign标识位，第八十四位为2，打‘高’字标
        if(BusinessUtil.isSignChar(waybill.getWaybillSign(),WaybillSignConstants.POSITION_84,WaybillSignConstants.CHAR_84_2)){
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
     * 获取始发站点
     * @param printWaybill
     * @param bigWaybillDto
     */
    public void loadOriginalDmsInfo(BasePrintWaybill printWaybill, BigWaybillDto bigWaybillDto) {
        Integer dmsCode = printWaybill.getOriginalDmsCode();
        String waybillCode = printWaybill.getWaybillCode();
        if (!isVaildDms(dmsCode)) {
            log.debug("参数中无始发分拣中心编码，从外部系统获取.运单号:{}" , waybillCode);
            com.jd.etms.waybill.domain.Waybill etmsWaybill = bigWaybillDto.getWaybill();
            WaybillManageDomain waybillState = bigWaybillDto.getWaybillState();
            WaybillPickup waybillPickup = bigWaybillDto.getWaybillPickup();

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
                    //调预分拣接口
                    AnalysisAddressResult addressResult = preseparateWaybillManager.analysisAddress(etmsWaybill.getConsignerAddress());
                    if (addressResult != null) {
                        consignerCityId = addressResult.getCityId();
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
    public void loadWaybillRouter(BasePrintWaybill printWaybill,Integer originalDmsCode,Integer destinationDmsCode,String waybillSign){
        //非B网的不用查路由
        if(!BusinessUtil.isB2b(waybillSign)){
            return;
        }

        //调路由的接口获取路由节点
        Date predictSendTime = new Date();
        RouteProductEnum routeProduct = null;

        /**
         * 1.waybill_sign第80位等于1时，产品类型为“特惠运”--TB1
         * 2.waybill_sign第80位等于2时，产品类型为“特准运”--TB2
         * 3.waybill_sign第80位等于7时，产品类型为“冷链卡板”--TLL1
         * 4.waybill_sign第80位等于9时，产品类型为“特准包裹”--TB2（特准运）
         */

        if(BusinessUtil.isSignChar(waybillSign,80,'1')){
            routeProduct = RouteProductEnum.TB1;
        }else if(BusinessUtil.isSignChar(waybillSign,80,'2')){
            routeProduct = RouteProductEnum.TB2;
        }else if(BusinessUtil.isSignChar(waybillSign,80,'7')){
            routeProduct = RouteProductEnum.TLL1;
        }else if(BusinessUtil.isSignChar(waybillSign,80,'9')){
            routeProduct = RouteProductEnum.TB2;
        }


        List<String> routerNameList = null;
        try {
            routerNameList = vrsRouteTransferRelationManager.loadWaybillRouter(originalDmsCode,destinationDmsCode,routeProduct,predictSendTime);
        } catch (Exception e) {
            log.error("获取路由环节信息失败waybillCode[{}]originalDmsCode[{}]destinationDmsCode[{}]",printWaybill.getWaybillCode(),originalDmsCode,destinationDmsCode,e);
        }
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
            //保价
            if(waybill != null && NumberHelper.gt0(waybill.getPriceProtectMoney())){
                specialRequirement = specialRequirement + SPECIAL_REQUIRMENT_PRICE_PROTECT_MONEY + ",";
            }
            //签单返还
            if(BusinessUtil.isSignInChars(waybillSign,4,'1','2','3','4','9')){
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
                printWaybill.appendSpecialMark(DISTRIBUTE_TYPE_COLD);
            }else if(BusinessUtil.isSignChar(waybillSign,WaybillSignConstants.POSITION_43,WaybillSignConstants.CHAR_43_2)){
                printWaybill.setDistributTypeText(DISTRIBUTE_TYPE_COOL);
                printWaybill.appendSpecialMark(DISTRIBUTE_TYPE_COOL);
            }else if(BusinessUtil.isSignChar(waybillSign,WaybillSignConstants.POSITION_43,WaybillSignConstants.CHAR_43_3)){
                printWaybill.setDistributTypeText(DISTRIBUTE_TYPE_CONTROL_TEMP);
                printWaybill.appendSpecialMark(DISTRIBUTE_TYPE_CONTROL_TEMP);
            }else if(BusinessUtil.isSignInChars(waybillSign,WaybillSignConstants.POSITION_43,WaybillSignConstants.CHAR_43_4)){
                printWaybill.setDistributTypeText(DISTRIBUTE_TYPE_NORMAL);
                printWaybill.appendSpecialMark(DISTRIBUTE_TYPE_NORMAL);
            }else if(BusinessUtil.isSignInChars(waybillSign,WaybillSignConstants.POSITION_43,WaybillSignConstants.CHAR_43_5)){
                printWaybill.setDistributTypeText(DISTRIBUTE_TYPE_FREEZING);
                printWaybill.appendSpecialMark(DISTRIBUTE_TYPE_FREEZING);
            }else if(BusinessUtil.isSignInChars(waybillSign,WaybillSignConstants.POSITION_43,WaybillSignConstants.CHAR_43_6)){
                printWaybill.setDistributTypeText(DISTRIBUTE_TYPE_PRECISION_COLD);
                printWaybill.appendSpecialMark(DISTRIBUTE_TYPE_PRECISION_COLD);
            }
        }
    }
}