package com.jd.bluedragon.common.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.jd.etms.waybill.api.WaybillPickupTaskApi;
import com.jd.etms.waybill.domain.PickupTask;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.TextConstants;
import com.jd.bluedragon.common.domain.Pack;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.order.ws.OrderWebService;
import com.jd.bluedragon.distribution.popPrint.domain.PopPrint;
import com.jd.bluedragon.distribution.popPrint.service.PopPrintService;
import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import com.jd.bluedragon.distribution.print.service.ComposeService;
import com.jd.bluedragon.distribution.print.service.WaybillPrintService;
import com.jd.bluedragon.distribution.product.domain.Product;
import com.jd.bluedragon.distribution.product.service.ProductService;
import com.jd.bluedragon.utils.BigDecimalHelper;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.api.WaybillPackageApi;
import com.jd.etms.waybill.api.WaybillQueryApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.Goods;
import com.jd.etms.waybill.domain.PackageWeigh;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.PackOpeFlowDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;


@Service("waybillCommonService")
public class WaybillCommonServiceImpl implements WaybillCommonService {

    private final Log logger = LogFactory.getLog(this.getClass());
    
    private static final int REST_CODE_SUC = 1; 

    @Autowired
    private ProductService productService;

    /* 运单查询 */
    @Autowired
    private WaybillQueryApi waybillQueryApi;
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
    
    
    public Waybill findByWaybillCode(String waybillCode) {
        Waybill waybill = null;

        try {
            WChoice wChoice = new WChoice();
            wChoice.setQueryWaybillC(true);
            wChoice.setQueryWaybillE(true);
            wChoice.setQueryWaybillM(true);
            BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryApi.getDataByChoice(
                    waybillCode, wChoice);
            if (baseEntity != null && baseEntity.getData() != null) {
                waybill = this.convWaybillWS(baseEntity.getData(), false, false);
            }
            this.logger.info("运单号【 " + waybillCode + "】调用运单WSS数据成功，运单【" + waybill + "】");
        } catch (Exception e) {
            this.logger.error("运单号【 " + waybillCode + "】调用运单WSS异常：", e);
        }
        if (waybill == null) {
            // 无数据
            this.logger.info("运单号【 " + waybillCode + "】的调用运单WSS数据为空，调用订单中间件开始");
            waybill = this.getWaybillFromOrderService(waybillCode);
            this.logger
                    .info("运单号【 " + waybillCode + "】的调用运单WSS数据为空，调用订单中间件结束，返回值【" + waybill + "】");
        }
        return waybill;
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
            BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryApi.getDataByChoice(
                    waybillCode, wChoice);
            if (baseEntity != null && baseEntity.getData() != null) {
                waybill = this.convWaybillWS(baseEntity.getData(), true, true);
                if (Waybill.isInvalidWaybill(waybill)) {
                    this.logger.warn("运单号【 " + waybillCode + "】验证运单数据缺少必要字段，运单【" + waybill + "】");
                    return null;
                }
            }
            this.logger.info("运单号【 " + waybillCode + "】调用运单WSS数据成功");

        } catch (Exception e) {
            this.logger.error("运单号【 " + waybillCode + "】调用运单WSS异常：", e);
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
            BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryApi.getDataByChoice(
                    waybillCode, wChoice);
            if (baseEntity != null && baseEntity.getData() != null) {
                waybill = this.convWaybillWS(baseEntity.getData(), true, true);
                if (Waybill.isInvalidWaybill(waybill)) {
                    this.logger.warn("运单号【 " + waybillCode + "】验证运单数据缺少必要字段，运单【" + waybill + "】");
                    return null;
                }
            }
            this.logger.info("运单号【 " + waybillCode + "】调用运单WSS数据成功");

        } catch (Exception e) {
            this.logger.error("运单号【 " + waybillCode + "】调用运单WSS异常：", e);
        }

        return waybill;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.waybillCommonService.getReverseWaybill", mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<Waybill> getReverseWaybill(String oldWaybillCode) {
        InvokeResult<Waybill> result = new InvokeResult<Waybill>();
        Waybill waybill = null;

        try {
            WChoice wChoice = new WChoice();
            wChoice.setQueryWaybillC(true);
            wChoice.setQueryWaybillE(true);
            wChoice.setQueryWaybillM(true);
            wChoice.setQueryPackList(true);
            BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryApi.getReturnWaybillByOldWaybillCode(
                    oldWaybillCode, wChoice);
            if (baseEntity != null && baseEntity.getData() != null) {
                waybill = this.convWaybillWS(baseEntity.getData(), true, true);
                /*if (Waybill.isInvalidWaybill(waybill)) {
                    this.logger.error("运单号【 " + oldWaybillCode + "】验证运单数据缺少必要字段，运单【" + waybill + "】");
                    result.customMessage(-1,"运单缺少必要数据");
                    return result;
                }*/
            }
            if (logger.isInfoEnabled()) {
                this.logger.info("运单号【 " + oldWaybillCode + "】调用运单JSF数据成功");
            }
        } catch (Throwable e) {
            this.logger.error("运单号【 " + oldWaybillCode + "】调用运单JSF异常：", e);
            result.error(e);
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
            this.logger.warn("通过运单号调用非运单接口获取运单数据，传入参数为空");
            return null;
        }
        if (!StringUtils.isNumeric(waybillCode.trim())) {
            this.logger.warn("通过运单号调用非运单接口获取运单数据，传入参数为非数字,立即返回NULL");
            return null;
        }
        this.logger.info("通过运单号调用非运单接口获取运单数据，调用运单中间件开始");
        Waybill waybill = this.orderWebService.getWaybillByOrderId(Long.valueOf(waybillCode));
        List<Product> products = this.productService.getOrderProducts(Long.parseLong(waybillCode));

        if (waybill != null) {
            this.logger.info("通过运单号调用非运单接口获取运单数据，调用运单中间件结束，运单【" + waybill + "】，非POP，运单类型【"
                    + waybill.getType() + "】");
            waybill.setProList(products);
        } else {
            this.logger.warn("通过运单号调用非运单接口获取运单数据，调用运单中间件结束，运单为空");
        }
        return waybill;
    }

    @JProfiler(jKey = "DMSWEB.WaybillCommonServiceImpl.getHisWaybillFromOrderService", mState = {JProEnum.TP})
    public Waybill getHisWaybillFromOrderService(String waybillCode) {
        if (StringUtils.isBlank(waybillCode)) {
            this.logger.warn("通过运单号调用非运单接口获取运单数据，传入参数为空");
            return null;
        }

        this.logger.info("通过运单号调用非运单接口获取运单数据，调用运单中间件开始");

        Waybill waybill = this.findByWaybillCode(waybillCode);

        if (waybill == null) {
            waybill = this.orderWebService.getHisWaybillByOrderId(Long.valueOf(waybillCode));
        }

        List<Product> products = this.productService.getProductsByWaybillCode(waybillCode);
        if (waybill != null) {
            this.logger.info("通过运单号调用非运单接口获取运单数据，调用运单中间件结束，运单【" + waybill + "】，非POP，运单类型【"
                    + waybill.getType() + "】");
            waybill.setProList(products);
        } else {
            this.logger.warn("通过运单号调用非运单接口获取运单数据，调用运单中间件结束，运单为空");
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
                	logger.error("加载运单包裹打印信息失败！waybillCode="+waybillCode,ex);
                }
        	}
        	//获取该运单号的复重信息
        	if(loadPweight){
        		packOpeFlows = new HashMap<String,PackOpeFlowDto>();
                try {
                	packOpeFlows = this.getPackOpeFlowsByOpeType(waybillCode, Constants.PACK_OPE_FLOW_TYPE_PSY_REC);
                }catch(Exception ex){
                	logger.error("加载运单包裹打印信息失败！waybillCode="+waybillCode,ex);
                }
        	}
            List<DeliveryPackageD> ds = bigWaybillDto.getPackageList();
            if (ds == null || ds.size() <= 0) {
                this.logger.warn("转换包裹信息 --> 运单号【" + waybill.getWaybillCode() + "】,原始运单数据集bigWaybillDto为空或size为空");
            } else {
                // 转换包裹信息
                this.logger.debug("转换包裹信息 --> 运单号：" + waybill.getWaybillCode()
                        + "转换包裹信息, 包裹数量为:" + ds.size());
                if (BusinessHelper.checkIntNumRange(ds.size())) {
                	waybill.setPackageNum(ds.size());
                    List<Pack> packList = new ArrayList<Pack>();
                    for (DeliveryPackageD d : ds) {
                        Pack pack = new Pack();
                        pack.setWaybillCode(d.getWaybillCode());
                        pack.setPackCode(d.getPackageBarcode());
                        if (d.getGoodWeight() != null) {
                            pack.setWeight(String.valueOf(d.getGoodWeight()));
                        }
                        if (StringUtils.isNotEmpty(d.getPackageBarcode())) {
                            String[] pcs = d.getPackageBarcode().split("[-NS]");
                            pack.setPackSerial(Integer.valueOf(pcs[1]));
                        } else {
                            this.logger.info("转换包裹信息 --> 运单号："
                                    + waybill.getWaybillCode() + "生成包裹号,包裹号为空");
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
                    this.logger.warn("转换包裹信息【运单返回】 --> 运单号："
                            + waybill.getWaybillCode() + ", 包裹数量为:" + ds.size());
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
        this.logger.info("运单号为【 " + waybillCode + "】 调用接口设置站点名称开始");
        if (siteCode != null) {
            waybill.setSiteCode(siteCode);
            // 根据站点ID获取站点Name
            BaseStaffSiteOrgDto baseStaffSiteOrgDto = this.baseMajorManager
                    .getBaseSiteBySiteId(siteCode);
            if (baseStaffSiteOrgDto != null) {
                waybill.setSiteName(baseStaffSiteOrgDto.getSiteName());
                this.logger.info("运单号为【 " + waybillCode + "】  调用接口设置站点名称成功【 "
                        + siteCode + "-" + waybill.getSiteName() + "】");
            } else {
                this.logger.info("运单号为【 " + waybillCode + "】  查找不到站点【" + siteCode
                        + "】相关信息");
            }
        }

        this.logger.info("运单号为【 " + waybillCode + "】 调用接口设置中转站站点名称开始");
        // 设置中转站站点名称
        Integer transferStationId = waybill.getTransferStationId();
        if (transferStationId != null) {
            waybill.setTransferStationId(transferStationId);
            // 根据中转站站点ID获取中转站站点Name
            BaseStaffSiteOrgDto baseStaffSiteOrgDto = this.baseMajorManager
                    .getBaseSiteBySiteId(transferStationId);
            if (baseStaffSiteOrgDto != null) {
                waybill.setTransferStationName(baseStaffSiteOrgDto.getSiteName());
                this.logger.info("运单号为【 " + waybillCode + "】 调用接口设置中转站站点名称成功【 "
                        + transferStationId + "-"
                        + waybill.getTransferStationName() + "】");
            } else {
                this.logger
                        .info("运单号为【 " + waybillCode + "】  查找不到中转站站点【" + transferStationId + "】 相关信息");
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

            BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryApi.getDataByChoice(
                    waybillCode, wChoice);
            if (baseEntity != null && baseEntity.getData() != null) {
                waybill = this.convWaybillWS(baseEntity.getData(), false, false);

                if (waybill == null) {
                    this.logger.info("运单号【 " + waybillCode + "】调用运单接口成功，运单【" + waybillCode + "】不存在");
                    return null;
                }

                logger.info("获取运单StoreId");
                if (baseEntity.getData().getWaybillState() != null && baseEntity.getData().getWaybillState().getStoreId() != null) {
                    this.logger.info("获取运单StoreId失败");
                    waybill.setStoreId(baseEntity.getData().getWaybillState().getStoreId());
                }
                List<Goods> goodses = baseEntity.getData().getGoodsList();
                if (goodses == null || goodses.size() == 0) {
                    this.logger.info("调用运单接口获得商品明细数据为空");
                }
                for (Goods goods : goodses) {
                    com.jd.bluedragon.distribution.product.domain.Product product = new com.jd.bluedragon.distribution.product.domain.Product();
                    product.setProductId(goods.getSku());
                    product.setName(goods.getGoodName());
                    product.setQuantity(goods.getGoodCount());
                    product.setPrice(BigDecimalHelper.toBigDecimal(goods.getGoodPrice()));
                    products.add(product);
                }

                waybill.setProList(products);
            }
            this.logger.info("运单号【 " + waybillCode + "】调用运单数据成功，运单【" + waybill + "】");
        } catch (Exception e) {
            this.logger.error("运单号【 " + waybillCode + "】调用运单异常：", e);
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
							newData.setpWeight(newData.getpWeight());
							newData.setWeighTime(newData.getWeighTime());
						}else if(newData.getpWeight()!=null&&newData.getpWeight()>0&&newData.getWeighTime().after(oldData.getWeighTime())){
							oldData.setpWeight(newData.getpWeight());
							oldData.setWeighTime(newData.getWeighTime());
						}
					}
				}
			}
		}else{
			this.logger.warn(String.format("没有获取到包裹称重信息，{code:{},msg:{}}", rest.getResultCode(),rest.getMessage()));
		}
		return res;
	}
    /**
     * 通过运单对象，设置基础打印信息
     * <p>设置商家id和name(busiId、busiName)
     * <p>以始发分拣中心获取始发城市code和名称(originalCityCode、originalCityName)
     * <p>设置寄件人、电话、手机号、地址信息(consigner、consignerTel、consignerMobile、consignerAddress)
     * <p>设置设置价格保护标识和显示值：(priceProtectFlag、priceProtectText)
     * <p>设置打标信息：签单返还、配送类型、运输产品(signBackText、distributTypeText、transportMode)
     * <p>设置打标信息：运输产品类型、收件公司、寄件公司(signBackText、distributTypeText、transportMode)
     * @param target 目标对象(BasePrintWaybill类型)
     * @param waybill 原始运单对象
     */
    public BasePrintWaybill setBasePrintInfoByWaybill(BasePrintWaybill target, com.jd.etms.waybill.domain.Waybill waybill){
    	if(target==null||waybill==null){
    		return target;
    	}
		logger.info("包裹标签打印-waybillSign及sendPay打标处理");
		waybillPrintService.dealSignTexts(waybill.getWaybillSign(), target, Constants.DIC_NAME_WAYBILL_SIGN_CONFIG);
		waybillPrintService.dealSignTexts(waybill.getSendPay(), target, Constants.DIC_NAME_SEND_PAY_CONFIG);
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
        //Waybillsign的15位打了3的取件单，并且订单号非“QWD”开头的单子getSpareColumn3  ----产品：luochengyi  2017年8月29日16:37:21
        if(BusinessHelper.isSignChar(waybill.getWaybillSign(),15,'3') && !BusinessHelper.isQWD(waybill.getWaybillSign()))
        {
            target.setBusiOrderCode(waybill.getSpareColumn3());
        }
        else{
            target.setBusiOrderCode(waybill.getBusiOrderCode());
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
        target.setSopOrExternalFlg(BusinessHelper.isSopOrExternal(waybill.getWaybillSign()));
        //判断始发分拣中心是否属于北京
        target.setBjCheckFlg(siteService.getBjDmsSiteCodes()
        		.contains(target.getOriginalDmsCode()));
        //打印时间,取后台服务器时间
        String printTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        target.setPrintTime(printTime);
        //设置运费及货款信息
        String freightText = "";
        String goodsPaymentText = "";

        //读取waybill_sign第25位，25位等于2时，面单显示【到付现结】
        if(BusinessHelper.isB2b(waybill.getWaybillSign())){
            //B网运费和货款
        	//读取waybill_sign第25位，25位等于2时，面单显示【到付现结】
        	if(BusinessHelper.isSignChar(waybill.getWaybillSign(), 25, '2')){
        		freightText = TextConstants.FREIGHT_PAY_CASH;
        	}
        	//货款字段金额等于0时，则货款位置显示为【在线支付】
        	//货款字段金额大于0时，则货款位置显示为【货到付款】
        	if(NumberHelper.gt0(waybill.getCodMoney())){
        		goodsPaymentText = TextConstants.GOODS_PAYMENT_COD;
        	}else{
        		goodsPaymentText = TextConstants.GOODS_PAYMENT_ONLINE;
        	}
            target.setTemplateName("dms-nopaper-b2b-m");
        }else{
            //C网运费和货款
            //运费：waybillSign 25位为2时【到付现结】；25位为3时【寄付现结】
            if(BusinessHelper.isSignChar(waybill.getWaybillSign(), 25, '2')){
                freightText = TextConstants.FREIGHT_PAY_CASH;
            } else if(BusinessHelper.isSignChar(waybill.getWaybillSign(), 25, '3')){
                freightText = TextConstants.FREIGHT_CONSIGER_CLEAR;
            }

            //货款：waybillSign 货款大于0时，25位为2或3时显示【货到付款】，25位不为2且不为3时显示应收金额，
            //货款等于0时，显示【在线支付】
            if(NumberHelper.gt0(waybill.getCodMoney())){
                if(BusinessHelper.isSignInChars(waybill.getWaybillSign(),25,'2','3')){
                    goodsPaymentText = TextConstants.GOODS_PAYMENT_COD;
                }else{
                    goodsPaymentText = "￥"+ waybill.getCodMoney();
                    if (ComposeService.ONLINE_PAYMENT_SIGN.equals(waybill.getPayment())) {
                        goodsPaymentText = TextConstants.GOODS_PAYMENT_ONLINE;
                    }
                }
            } else{
                goodsPaymentText = TextConstants.GOODS_PAYMENT_ONLINE;
            }
        }
        target.setFreightText(freightText);
        target.setGoodsPaymentText(goodsPaymentText);
        //waybill_sign标识位，第四十六位为2或3，打安字标
        if(BusinessHelper.isSignInChars(waybill.getWaybillSign(), 46, '2','3')){
        	target.appendSpecialMark(ComposeService.SPECIAL_MARK_VALUABLE);
        }
        //waybil_sign标识位，第五十五位为1，打鲜字标（c网操作的外单）
        if(!BusinessHelper.isB2b(waybill.getWaybillSign()) &&
                BusinessHelper.isSignChar(waybill.getWaybillSign(),55,'1')){
            target.appendSpecialMark(ComposeService.SPECIAL_MARK_FRESH);
            //一体化面单，显示生鲜专送
            target.setTransportMode(ComposeService.PREPARE_SITE_NAME_FRESH_SEND);
        }
        //waybill_sign标识位，第五十七位为1，打优字标
        if(BusinessHelper.isSignChar(waybill.getWaybillSign(),57,'1')){
            target.appendSpecialMark(ComposeService.SPECIAL_MARK_FIRST);
        }
        return target;
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
            logger.error("异常getPackListByCode " +e.getMessage());
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
    @JProfiler(jKey = "DMSWEB.waybillCommonService.getPickupTask", mState = {JProEnum.TP,JProEnum.FunctionError})
	public BaseEntity<PickupTask> getPickupTask(String oldWaybillCode){
	    return waybillPickupTaskApi.getDataBySfCode(oldWaybillCode);
    }
}
