package com.jd.bluedragon.distribution.reverse.service;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.*;
import com.jd.bluedragon.core.exception.OrderCallTimeoutException;
import com.jd.bluedragon.core.exception.StockCallPayTypeException;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.kuguan.domain.KuGuanDomain;

import com.jd.bluedragon.distribution.log.BusinessLogProfilerBuilder;
import com.jd.bluedragon.distribution.order.domain.InternationOrderDto;
import com.jd.bluedragon.utils.log.BusinessLogConstans;
import com.jd.dms.logger.external.LogEngine;
import com.jd.bluedragon.distribution.order.domain.OrderBankResponse;
import com.jd.bluedragon.distribution.order.service.OrderBankService;
import com.jd.bluedragon.distribution.order.ws.OrderWebService;
import com.jd.bluedragon.distribution.product.domain.Product;
import com.jd.bluedragon.distribution.product.service.ProductService;
import com.jd.bluedragon.distribution.reverse.domain.ReceiveRequest;
import com.jd.bluedragon.distribution.reverse.domain.ReverseReceive;
import com.jd.bluedragon.distribution.systemLog.domain.SystemLog;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.ConstantEnums;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.bluedragon.utils.SystemLogContants;
import com.jd.bluedragon.utils.SystemLogUtil;
import com.jd.bluedragon.utils.XmlHelper;
import com.jd.common.util.StringUtils;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.fastjson.JSONObject;
import com.jd.ioms.jsf.export.domain.Order;
import com.jd.ipc.csa.model.*;
import com.jd.ql.basic.domain.BaseOrg;
import com.jd.stock.iwms.export.param.ChuguanParam;
import com.jd.stock.iwms.export.vo.ChuguanDetailVo;
import com.jd.stock.iwms.export.vo.ChuguanVo;
import com.jd.stock.iwms.export.vo.StockDetailVO;
import com.jd.ufo.domain.ufo.Organization;
import com.jd.ufo.domain.ufo.SendpayOrdertype;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.avro.data.Json;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;


/**
 * 备件库收货推出管  
 * @author huangliang
 *
 */
@Service("reverseReceiveNotifyStockService")
public class ReverseReceiveNotifyStockService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	// 先款支付类型
	private static final Integer PAY_TYPE_PRE = 1;
	// 后款支付类型
	private static final Integer PAY_TYPE_POST = 2;
	// 不明确支付类型
	private static final Integer PAY_TYPE_UNKNOWN = 3;
	// 获得支付类型错误
	private static final Integer PAY_TYPE_ERROR = 4;
			
	private static final Integer STOCK_TYPE_1601 = 1601; // 逆向物流后款退货-虚入
	private static final Integer STOCK_TYPE_1602 = 1602; // 逆向物流-虚出
	private static final Integer STOCK_TYPE_1603 = 1603; // 逆向物流先款退货-虚入

    private static final String JING_BAN = "ql.dms";

	private static final List<Integer> needRetunWaybillTypes = Lists.newArrayList(11, 13, 15, 16, 18, 19, 42, 56, 61);

    //waybill_type=0 && sendpay 252位=3,4,5,6,7 电信行业合约订单也需要推送出管数据
	private static final List<String> TELECOM_WAYBILL_SEND_PAYS = Lists.newArrayList("3","4","5","6","7");

	private static final int TELECOM_WAYBILL_SEND_PAY_INDEX = 251;

	private static final Integer TELECOM_WAYBILL_TYPE = 0;

    private static final String JING_BAN_SYSCODE = "ql.dms";

    public static final String CHUGUAN_FIELD_QITAFANGSHI = "逆向物流";

    //出管 其他方式字段值，依旧换新 需要传此值
    private static final String CHUGUAN_FIELD_STILL_NEW_QTFS = "trade-in";

    @Autowired
    private LogEngine logEngine;

	@Autowired
	private OrderWebService orderWebService;

	@Autowired
	private ProductService productService;

	@Autowired
	private OrderBankService orderBankService;

	@Qualifier("wmsStockChuGuanMQ")
    @Autowired
    private DefaultJMQProducer wmsStockChuGuanMQ;
	
	@Autowired
	private BaseMajorManager baseMajorManager;
	
	@Autowired
	private StockExportManager stockExportManager;

    @Autowired
    private WaybillQueryManager waybillQueryManager;
	@Autowired
    private SearchOrganizationOtherManager searchOrganizationOtherManager;

    @Autowired
	private ChuguanExportManager chuguanExportManager;
    @Autowired
	private GeneralStockAllotOutInterfaceManager generalStockAllotOutInterfaceManager;

    @Resource
    protected UccPropertyConfiguration uccPropertyConfiguration;

	public Long receive(String message) {
		
		if (XmlHelper.isXml(message, ReceiveRequest.class, null)) {
			ReceiveRequest request = (ReceiveRequest) XmlHelper.toObject(message,
					ReceiveRequest.class);

			if (request == null) {
				this.log.warn("消息序列化出现异常, 消息：{}" , message);
			} else if (ReverseReceive.RECEIVE_TYPE_SPWMS.toString().equals(request.getReceiveType())
					&& ReverseReceive.RECEIVE.toString().equals(request.getCanReceive())) {
				return getCompatibleOrderId(request.getOrderId());
			} else {
				this.log.info("消息来源：{}" , request.getReceiveType());
			}
		}
		return -1L;
	}

    /**
     * 获取订单号，兼容 jd
     * @param code
     * @return
     */
    private Long getCompatibleOrderId(String code){
        if(NumberUtils.isDigits(code)){
            return Long.valueOf(code);
        }
        if(StringUtils.isEmpty(code)){
            return -1L;
        }
        String orderId = waybillQueryManager.getOrderCodeByWaybillCode(code,true);
        if(!NumberUtils.isDigits(orderId)){
            log.warn("根据运单号查询的订单号是非数字code[{}]orderId[{}]",code,orderId);
            return -1L;
        }
        return Long.valueOf(orderId);
    }


	public Boolean nodifyStock(Long orderId) throws Exception {
        long startTime= System.currentTimeMillis();

        this.log.info("备件库消费处理出管orderId[{}]" , orderId);

		if (!NumberHelper.isPositiveNumber(orderId)) {
			this.log.info("备件库消费处理出管-订单号非法orderId[{}]" , orderId);
			return Boolean.TRUE;
		}
		SystemLog sysLog = new SystemLog();//日志对象
		sysLog.setKeyword1(orderId.toString());
		sysLog.setType(SystemLogContants.TYPE_REVERSE_STOCK);//设置日志类型
		try{
			InternationOrderDto order = orderWebService.getInternationOrder(orderId);
			List<Product> products =  productService.getInternationProducts(orderId); //订单详情

			//修改逻辑当order获取不到时，取归档历史信息。
			//原抛异常逻辑if(order==null || products==null) 即有一项为空即抛出，更改后的逻辑等价于if( (order==null&&hisOrder==null) || products==null )
			if (CollectionUtils.isEmpty(products)) {
				this.log.error("备件库消费处理出管-无商品信息orderId[{}]" , orderId);
				sysLog.setContent("无商品信息");
				throw new OrderCallTimeoutException("order has no products.");
			}else if(order == null){//为空时，取一下历史的订单信息
				this.log.debug("订单可能转历史，获取历史订单, 运单号 为：{}" , orderId);
				jd.oom.client.orderfile.Order hisOrder = this.orderWebService.getHistoryOrder(orderId);
				
				if (hisOrder == null) {
                    this.log.error("备件库消费处理出管-订单信息为空orderId[{}]" , orderId);
					sysLog.setContent("运单信息为空");
					throw new OrderCallTimeoutException("order is not exist.");
				}else {//如果历史订单信息不为空，则拷贝属性值
					order = new InternationOrderDto();
					order.setId(hisOrder.getId());
					order.setIdCompanyBranchName(hisOrder.getIdCompanyBranchName());
					order.setIdCompanyBranch(hisOrder.getIdCompanyBranch());
					/*order.setTotalFee(hisOrder.getTotalFee());*/
					order.setCustomerName(hisOrder.getCustomerName());
					order.setDeliveryCenterID(hisOrder.getDeliveryCenterID());
					order.setStoreId(hisOrder.getStoreId());
					order.setOrderType(hisOrder.getOrderType());
					if(log.isDebugEnabled()){
                        this.log.debug("历史订单信息orderId：{}.order:{}"+orderId,JsonHelper.toJson(order));
                    }
                }
			}
			
			{
				//FIXME:======================================增加获得财务部机构名的逻辑，需要缓存的支持=================================
			}

			//校验机构名称是否有效，如无效则从基础资料获得
			if (StringUtils.isBlank(order.getIdCompanyBranchName())) {
	            BaseOrg bo = baseMajorManager.getBaseOrgByOrgId(order.getIdCompanyBranch());
	            if (bo != null) {
	            	order.setIdCompanyBranchName(bo.getOrgName());//需要调用基本资料接口根据机构ID获取机构Name
	            }
	            this.log.info("原机构名为空，从基础资料重新获得订单{}机构名 IdCompanyBranchName:{}",orderId,order.getIdCompanyBranchName());
	        }
			
			sysLog.setKeyword2(String.valueOf(order.getOrderType()));//设置订单的类型
			
			
			//此区域:符合主动推送的条件的单子判断是否推送过,获得支付类型
			KuGuanDomain kuguanDomain = null;
			Integer payType = PAY_TYPE_UNKNOWN;
			if (!isPushChuguan(order)){
                this.log.info("备件库消费处理出管-订单类型不需要回传库存中间件orderId[{}]OrderType[{}]" , orderId,order.getOrderType());
                this.log.warn("运单号：{}, 不需要回传库存中间件。",orderId);
                return Boolean.TRUE;

			}

            kuguanDomain = queryKuguanDomainByWaybillCode(String.valueOf(orderId));
            if(kuguanDomain==null) {
                log.warn("备件库消费处理出管-查询出管无信息orderId[{}]",orderId);
                return Boolean.FALSE;
            }
            if(isReverseLogistics(kuguanDomain)){
                log.warn("备件库消费处理出管-不是逆向物流orderId[{}]",orderId);
                return Boolean.TRUE;
            }
            payType = getPayType(kuguanDomain);

			//开始根据类型的不同推送
			if (needRetunWaybillTypes.contains(Integer.valueOf(order.getOrderType())) || isTelecomOrder(order.getOrderType(),order.getSendPay())) {
                log.warn("备件库消费处理出管-推送出管消息orderId[{}]",orderId);
				if (isPrePay(payType)) {

					this.wmsStockChuGuanMQ.send(String.valueOf(orderId),this.stockMessage(order, products, STOCK_TYPE_1602, payType));
					this.wmsStockChuGuanMQ.send(String.valueOf(orderId),this.stockMessage(order, products, STOCK_TYPE_1603, payType));
				} else {
					this.wmsStockChuGuanMQ.send(String.valueOf(orderId),this.stockMessage(order, products, STOCK_TYPE_1601, payType));
					this.wmsStockChuGuanMQ.send(String.valueOf(orderId),this.stockMessage(order, products, STOCK_TYPE_1602, payType));
				}
				
				sysLog.setKeyword3("MQ");
                sysLog.setContent("推出管成功!");
			}else if (Waybill.TYPE_GENERAL.equals(order.getOrderType()) || Waybill.TYPE_POP_FBP.equals(order.getOrderType())) {
                return insertChuguan(orderId, sysLog, order, products, payType);
            }
            log.info("备件库消费处理出管-不符合写出管逻辑orderId[{}]",orderId);
        }catch(Exception e){
			this.log.error("运单号：{}, 推出管失败。",orderId, e);
			if(StringHelper.isEmpty(sysLog.getContent())){
				sysLog.setContent(e.getMessage());
			}
			return Boolean.FALSE;
		}finally{
            long endTime = System.currentTimeMillis();

            JSONObject request=new JSONObject();
            request.put("waybillCode",sysLog.getKeyword1());

            JSONObject response=new JSONObject();
            response.put("keyword1", sysLog.getKeyword1());
            response.put("keyword2", sysLog.getKeyword2());
            response.put("keyword3", sysLog.getKeyword3());
            response.put("keyword4", sysLog.getKeyword4());
            response.put("content", sysLog.getContent());

            BusinessLogProfiler businessLogProfiler=new BusinessLogProfilerBuilder()
                    .operateTypeEnum(BusinessLogConstans.OperateTypeEnum.REVERSE_SPARE_CHUGUAN)
                    .operateResponse(response)
                    .operateRequest(request)
                    .methodName("ReverseReceiveNotifyStockService#nodifyStock")
                    .processTime(endTime,startTime)
                    .build();

//            logEngine.addLog(businessLogProfiler);
            SystemLogUtil.log(sysLog);
		}
		
		return Boolean.TRUE;
	}

    private Boolean insertChuguan(Long orderId, SystemLog sysLog, InternationOrderDto order, List<Product> products, Integer payType) {
        log.info("备件库消费处理出管-调用写出管逻辑开始orderId[{}]",orderId);
        //判断是否是已旧换新
        boolean isOldForNewType = BusinessHelper.isYJHX(order.getSendPay());
        OrderBankResponse orderBank = orderBankService.getOrderBankResponse(String.valueOf(orderId));
        List<ChuguanDetailVo> chuguanDetailVos = getChuguanDetailVos(orderId);
        if(uccPropertyConfiguration.isChuguanPurchaseAndSaleSwitch() && CollectionUtils.isNotEmpty(chuguanDetailVos)) {
            boolean purchaseFlag = purchaseAndSaleInsertChuguan(orderId,products, order, payType, isOldForNewType, orderBank, chuguanDetailVos);
            if(!purchaseFlag){
                chuguanDetailVos.clear();
            }
        }
        log.info("备件库消费处理出管-开始剔除商品-orderId[{}]products[{}]chuguanDetailVos[{}]",orderId,JsonHelper.toJson(products),JsonHelper.toJson(chuguanDetailVos));
        removePurchaseAndSaleVO(products, chuguanDetailVos);
        log.info("备件库消费处理出管-剔除商品结束-orderId[{}]products[{}]chuguanDetailVos[{}]",orderId,JsonHelper.toJson(products),JsonHelper.toJson(chuguanDetailVos));
        if(CollectionUtils.isEmpty(products)){
            log.info("备件库消费处理出管-订单下的sku全部是采销控orderId[{}]",orderId);
            return true;
        }
        long result = 0;
        List<ChuguanDetailVo> intChuguanDetailVoList = getInChuguanDetailVoList(products,payType);
        List<ChuguanDetailVo> outChuguanDetailVoList = getOutChuguanDetailVoList(products);
        result = insertNewChuguan(orderId, isOldForNewType, order, payType,orderBank,intChuguanDetailVoList,outChuguanDetailVoList);
        /** 新逻辑结束 */
        log.info("备件库消费处理出管-调用写出管逻辑结束orderId[{}]",orderId);
        try {
            //业务流程监控, 备件库埋点
            Map<String, String> data = new HashMap<String, String>();
            data.put("orderId", orderId.toString());
            Profiler.bizNode("Reverse_mq_dms2stock", data);
        } catch (Exception e) {
            this.log.error("推送UMP发生异常orderId[{}]", orderId,e);
        }

        this.log.debug("运单号：{}, 库存中间件返回结果：{}" , orderId, result);

        sysLog.setKeyword3("WEBSERVICE");
        sysLog.setKeyword4(result);
        if(result!=0){
            sysLog.setContent("推出管成功!");
            return Boolean.TRUE;
        }
        sysLog.setContent("推出管失败!");
        return Boolean.FALSE;
    }

    /**
     * 删除渠道化的sku
     * @param products
     * @param chuguanDetailVos
     */
    private static void removePurchaseAndSaleVO(List<Product> products, List<ChuguanDetailVo> chuguanDetailVos) {
        Map<Long,ChuguanDetailVo> chuguanDetailVosMap = Maps.uniqueIndex(chuguanDetailVos.iterator(), new Function<ChuguanDetailVo, Long>() {
            @Nullable
            @Override
            public Long apply(@Nullable ChuguanDetailVo detailVo) {
                return detailVo.getSkuId();
            }
        });
        Iterator<Product> productsIterator = products.iterator();
        while (productsIterator.hasNext()){
            Product product = productsIterator.next();
            /*
            根据日志看 product skuid 有为0的情况，ProductId有值，不能确定是不是查询接口返的有问题，暂时这样处理
             */
            if(product.getSkuId() == 0 ){
                if(NumberUtils.isDigits(product.getProductId()) && chuguanDetailVosMap.containsKey(Long.valueOf(product.getProductId()))){
                    productsIterator.remove();
                }
            }else {
                if(chuguanDetailVosMap.containsKey(product.getSkuId())){
                    productsIterator.remove();
                }
            }

        }
    }

    public static void main(String[] args) {
        List<Product> products = Lists.newArrayList();
        List<ChuguanDetailVo> chuguanDetailVos = Lists.newArrayList();
        Product product = new Product();
        product.setSkuId(100030716207L);
        products.add(product);
        ChuguanDetailVo  chuguanDetailVo = new ChuguanDetailVo();
        chuguanDetailVo.setSkuId(100030716207L);
        chuguanDetailVos.add(chuguanDetailVo);
        removePurchaseAndSaleVO(products,chuguanDetailVos);
        System.out.println(products);
    }

    private List<ChuguanDetailVo> getChuguanDetailVos(Long orderId) {
        List<ChuguanVo> chuguanVos = chuguanExportManager.queryChuGuan(String.valueOf(orderId), ConstantEnums.ChuGuanTypeId.ORDER_MONEY_OUT);
        Collections.sort(chuguanVos, new Comparator<ChuguanVo>() {
            @Override
            public int compare(ChuguanVo o1, ChuguanVo o2) {
                if(o1.getBusinessTime() == null ){
                    return 0;
                }
                if(o2.getBusinessTime() == null){
                    return 0;
                }
                return  o2.getBusinessTime().compareTo(o1.getBusinessTime());
            }
        });
        ChuguanVo chuguanVo = chuguanVos.get(0);
        List<ChuguanDetailVo> chuguanDetailVoList = chuguanVo.getChuguanDetailVos();
        List<ChuguanDetailVo> result = Lists.newArrayList();
        for(ChuguanDetailVo item : chuguanDetailVoList){
            if(isPurchaseAndSale(item)){
                result.add(item);
            }
        }

        return result;
    }

    /**
     * 供应链二期插入出管
     * @param orderId
     * @param order
     * @param products
     * @param payType
     * @param isOldForNewType
     * @param orderBank
     * @param chuguanDetailVos
     * @return
     */
    private Boolean purchaseAndSaleInsertChuguan(Long orderId,List<Product> products, InternationOrderDto order,Integer payType, boolean isOldForNewType, OrderBankResponse orderBank, List<ChuguanDetailVo> chuguanDetailVos) {
        CallerInfo info = Profiler.registerInfo("DMS.BASE.ReverseReceiveNotifyStockService.purchaseAndSaleInsertChuguan", Constants.UMP_APP_NAME_DMSWEB, false, true);
        try {
            log.info("供应链中台二期写出管orderId-开始orderId[{}]chuguanDetailVos[{}]",orderId, JsonHelper.toJson(chuguanDetailVos));
            Map<Long, ChuguanDetailVo> skuMappingChuguanDetailVo = getSkuIdChuguanDetailVoMap(chuguanDetailVos);
            List<AllotRequestDetail> allotRequestDetails = getAllotRequestDetailList(orderId, chuguanDetailVos,order);
            Map<Long,Product>  productsMap = getProductsMap(products);
            AllotScenarioEnum scenario = AllotScenarioEnum.SO_BACK;
            String bizUniqueKey = "qldms".concat("-").concat(String.valueOf(orderId));
            boolean isIdempotent = true;
            String sysName = "(J-one)bluedragon-distribution-worker";
            List<ChuguanDetailVo> chuguanDetailVoList = new ArrayList<>();
            List<AllotResponseDetail> allotResponseDetailList = generalStockAllotOutInterfaceManager.batchAllotStock(allotRequestDetails,scenario,bizUniqueKey,isIdempotent,sysName);
            if(CollectionUtils.isEmpty(allotResponseDetailList)){
                log.info("供应链中台二期写出管-渠道库存分配接口无返回结果-orderId[{}]chuguanDetailVos[{}]",orderId, JsonHelper.toJson(allotRequestDetails));
                return false;
            }

            for(AllotResponseDetail allotResponseDetail : allotResponseDetailList){
                List<DimAllotResult> allotResultList = allotResponseDetail.getDimAllotResultList();
                for(DimAllotResult dimAllotResult : allotResultList){
                    Long skuId = allotResponseDetail.getSkuId();
                    String dimValue = dimAllotResult.getDimValue();
                    if(org.apache.commons.lang3.StringUtils.isNotEmpty(dimValue) && dimValue.split("-").length>1){
                        dimValue = dimAllotResult.getDimValue().split("-")[1];
                    }
                    Product product = productsMap.get(skuId);
                    BigDecimal zongJinE = null;
                    BigDecimal jiaGe = null;
                    if(product != null){
                        zongJinE = product.getPrice().multiply(new BigDecimal(product.getQuantity()));
                        jiaGe = product.getPrice();
                    }
                    Integer quantity = dimAllotResult.getAllotQty();
                    String profitLossId = null;
                    List<String> distriOrderIds = null;
                    if(skuMappingChuguanDetailVo.get(skuId) != null ){
                        profitLossId = skuMappingChuguanDetailVo.get(skuId).getProfitLossId();
                        distriOrderIds = skuMappingChuguanDetailVo.get(skuId).getDistriOrderIds();
                    }
                    chuguanDetailVoList.add(getChuguanDetailVo(skuId,zongJinE,quantity,jiaGe,profitLossId,distriOrderIds,dimValue));
                }
            }
            // TODO: 2022/7/18  ChuguanParam zongJinE 怎么传？
            int result = insertNewChuguan(orderId, isOldForNewType, order, payType, orderBank,chuguanDetailVoList,chuguanDetailVoList);
            boolean resultBoolean = (result == 1 || result == -2) ? true : false;
            log.info("供应链中台二期写出管orderId-结束result[{}]orderId[{}]chuguanDetailVos[{}]",resultBoolean,orderId, JsonHelper.toJson(chuguanDetailVos),resultBoolean);
            return resultBoolean;
        } catch (Exception e) {
            log.error("供应链二期插入出管报错orderId[{}]",orderId,e);
            Profiler.functionError(info);
        } finally {
            Profiler.registerInfoEnd(info);
        }
        return false;
    }

    private Map<Long,Product> getProductsMap(List<Product> products) {
        Map<Long,Product> productsMap = Maps.uniqueIndex(products.iterator(), new Function<Product, Long>() {
            @Nullable
            @Override
            public Long apply(@Nullable Product detailVo) {
                if(detailVo.getSkuId() != 0){
                    return detailVo.getSkuId();
                }
                if(NumberUtils.isDigits(detailVo.getProductId())){
                    return new Long(detailVo.getProductId());
                }
                return detailVo.getSkuId();
            }
        });

        return productsMap;
    }

    private Map<Long, ChuguanDetailVo> getSkuIdChuguanDetailVoMap(List<ChuguanDetailVo> chuguanDetailVos) {
        Map<Long, ChuguanDetailVo> skuMappingChuguanDetailVo = Maps.newHashMap();
        for(ChuguanDetailVo detailVo : chuguanDetailVos){
            skuMappingChuguanDetailVo.put(detailVo.getSkuId(),detailVo);
        }
        return skuMappingChuguanDetailVo;
    }

    private List<AllotRequestDetail> getAllotRequestDetailList(Long orderId, List<ChuguanDetailVo> chuguanDetailVos, InternationOrderDto order) {
        List<AllotRequestDetail> allotRequestDetails = Lists.newArrayList();
        for(ChuguanDetailVo detailVo : chuguanDetailVos){
            Integer num = detailVo.getNum();
            String profitLossId = detailVo.getProfitLossId();//损益渠道ID
            Long skuId = detailVo.getSkuId();
            Map<String, String>  salesModelMap = detailVo.getSalesModel();
            String purchaseChannel = detailVo.getPurchaseChannel();//采购渠道
            Map<String, String> paramExtMap = detailVo.getParamExtMap();
            AllotRequestDetail allotRequestDetail = new AllotRequestDetail();
            allotRequestDetail.setSkuId(skuId);
            allotRequestDetail.setAllotQty(detailVo.getNum());
            allotRequestDetail.setRule(4);
            allotRequestDetail.setRowKey("qldms".concat(UUID.randomUUID().toString()));
            allotRequestDetail.setDcId(order.getDeliveryCenterID());
            allotRequestDetail.setStoreId(order.getStoreId());
            Map<String, Object> extAttrMap = Maps.newHashMap();
            extAttrMap.put("profitLossId",profitLossId);
            extAttrMap.put("purchaseChannel",purchaseChannel);
            extAttrMap.put("salesOrderId",String.valueOf(orderId));
            List<StockOutInfo> stockOutInfoList = Lists.newArrayList();
            for(Map.Entry<String,String> entry : salesModelMap.entrySet()){
                StockOutInfo stockOutInfo = new StockOutInfo();
                stockOutInfo.setPurchaseChannel(purchaseChannel);
                stockOutInfo.setSalesType(entry.getKey());
                stockOutInfo.setSalesChannel(profitLossId);
                stockOutInfo.setStockOutNum(Integer.valueOf(entry.getValue()));
                stockOutInfoList.add(stockOutInfo);
            }
            extAttrMap.put("stockOutInfoList",stockOutInfoList);
            allotRequestDetail.setExtAttrMap(extAttrMap);
            allotRequestDetails.add(allotRequestDetail);
        }
        return allotRequestDetails;
    }

    /**
     * 判断是否是采销控
     * @param detailVo
     * @return
     */
    private boolean isPurchaseAndSale(ChuguanDetailVo detailVo) {
        String purchaseChannel = detailVo.getPurchaseChannel();//采购渠道
        if(StringUtils.isNotEmpty(purchaseChannel)){
            return true;
        }
        return false;
    }

    private boolean isPushChuguan(InternationOrderDto order) {
        return Waybill.TYPE_GENERAL.equals(order.getOrderType()) || Waybill.TYPE_POP_FBP.equals(order.getOrderType())
                || needRetunWaybillTypes.contains(Integer.valueOf(order.getOrderType())) || isTelecomOrder(order.getOrderType(), order.getSendPay());
    }

    private boolean isReverseLogistics(KuGuanDomain kuguanDomain) {
        if(CHUGUAN_FIELD_QITAFANGSHI.equals(kuguanDomain.getLblOtherWay())){
            return Boolean.TRUE;
        }
        if(ConstantEnums.ChuGuanTypeId.iSLogisticsTypeId(kuguanDomain.getTypeId())){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private KuGuanDomain queryKuguanDomainByWaybillCode(String orderId){
        return chuguanExportManager.queryByWaybillCode(orderId);
    }

    public int insertNewChuguan(Long waybillCode, boolean isOldForNewType,InternationOrderDto order,
                                  Integer payType,OrderBankResponse orderBank,List<ChuguanDetailVo> chuguanDetailVoListIn,List<ChuguanDetailVo> chuguanDetailVoListOut){
        List<ChuguanParam> chuGuanParamList = Lists.newArrayList();// 需要放两个 一个出一个入；他们会根据 typeId 区分

        ConstantEnums.ChuGuanChuruId chuGuanParam = isPrePay(payType) ? ConstantEnums.ChuGuanChuruId.IN_KU : ConstantEnums.ChuGuanChuruId.OUT_KU;

        ConstantEnums.ChuGuanTypeId inTypeId = isPrePay(payType) ? ConstantEnums.ChuGuanTypeId.REVERSE_LOGISTICS_MONEY_REJECTION :
                ConstantEnums.ChuGuanTypeId.REVERSE_LOGISTICS_GOODS_REJECTION;

        ConstantEnums.ChuGuanFenLei chuGuanFenLei = isPrePay(payType) ? ConstantEnums.ChuGuanFenLei.RETURN_GOODS : ConstantEnums.ChuGuanFenLei.PUT_GOODS;
        BigDecimal zongJinE = isPrePay(payType) ? orderBank.getShouldPay() : orderBank.getShouldPay().negate();
        ChuguanParam inChuguanParam = getChuguanParam(waybillCode,
                getRfid(waybillCode,inTypeId,ConstantEnums.ChuGuanRfId.IN),isOldForNewType, order,  payType, orderBank,
                ConstantEnums.ChuGuanRfType.IN,
                chuGuanParam,
                inTypeId,
                chuGuanFenLei,
                BigDecimal.valueOf(1),
                zongJinE);

        inChuguanParam.setChuguanDetailVoList(chuguanDetailVoListIn);

        //出
        ConstantEnums.ChuGuanTypeId outTypeId = ConstantEnums.ChuGuanTypeId.REVERSE_LOGISTICS_OUT;
        ChuguanParam outChuguanParam = getChuguanParam(waybillCode,
                getRfid(waybillCode,outTypeId,ConstantEnums.ChuGuanRfId.OUT),isOldForNewType, order,  payType, orderBank,
                ConstantEnums.ChuGuanRfType.Out,
                ConstantEnums.ChuGuanChuruId.OUT_KU,
                outTypeId,
                ConstantEnums.ChuGuanFenLei.OTHER,
                BigDecimal.valueOf(0),
                orderBank.getShouldPay());

        outChuguanParam.setChuguanDetailVoList(chuguanDetailVoListOut);

        chuGuanParamList.add(inChuguanParam);
        chuGuanParamList.add(outChuguanParam);
        return chuguanExportManager.insertChuguan(chuGuanParamList);
    }

    private String getRfid(Long waybillCode,ConstantEnums.ChuGuanTypeId typeId,ConstantEnums.ChuGuanRfId chuGuanRfId) {
        return JING_BAN_SYSCODE.concat("-").concat(String.valueOf(waybillCode)).concat("-")
                .concat(typeId.getType().toString()).concat("-").concat(chuGuanRfId.getText());
    }

    /**
     * 拼装 写入出管的 参数 ChuguanParam
     * 参考 https://cf.jd.com/pages/viewpage.action?pageId=165577134 新接口
     * 与老接口属性 映射 https://cf.jd.com/pages/viewpage.action?pageId=165578101
     * @return
     */
    private ChuguanParam getChuguanParam(Long waybillCode,String rfid,boolean isOldForNewType, InternationOrderDto order, Integer payType,
                                         OrderBankResponse orderBank, ConstantEnums.ChuGuanRfType rfType, ConstantEnums.ChuGuanChuruId churu, ConstantEnums.ChuGuanTypeId typeId,
                                         ConstantEnums.ChuGuanFenLei fenLei,BigDecimal qiTaFeiYong,
                                         BigDecimal zongJinE) {
        ChuguanParam chuguanParam = new ChuguanParam();
        chuguanParam.setRfId(rfid);
        chuguanParam.setRfType(rfType.getType());
        chuguanParam.setChuruId(churu.getType());
        chuguanParam.setChuru(churu.getText());
        chuguanParam.setOrderId(waybillCode);
        chuguanParam.setYuanDanHao(0);//经沟通 写0
        chuguanParam.setBusiNo(String.valueOf(waybillCode));
        chuguanParam.setTypeId(typeId.getType());
        chuguanParam.setSubType(typeId.getText());
        chuguanParam.setExtType(CHUGUAN_FIELD_QITAFANGSHI);
        chuguanParam.setDcId(order.getDeliveryCenterID());
        chuguanParam.setSid(order.getStoreId());
        chuguanParam.setToDcId(0);
        chuguanParam.setToSid(0);
//        chuguanParam.setSiteId(0);
//        chuguanParam.setToSiteId(0);
        chuguanParam.setIsAdjust(0);
        chuguanParam.setIsVirtual(1);
        chuguanParam.setFenLeiId(fenLei.getType());
        chuguanParam.setFenLei(fenLei.getText());
        chuguanParam.setJingBan(JING_BAN_SYSCODE);
        chuguanParam.setLaiYuan("备件库");
        if(isOldForNewType){
            //开票机构ID
            chuguanParam.setLaiYuanCode(getKPJGID(order));
            chuguanParam.setQiTaFangShi(CHUGUAN_FIELD_STILL_NEW_QTFS);
        }else{

            chuguanParam.setLaiYuanCode(order.getCustomerName());
            chuguanParam.setQiTaFangShi(CHUGUAN_FIELD_QITAFANGSHI);
        }
        chuguanParam.setOrgId(order.getIdCompanyBranch());
        chuguanParam.setOrgName(StringHelper.substring(order.getIdCompanyBranchName(),0,19));
        chuguanParam.setZongJinE(zongJinE);
        chuguanParam.setBusinessTime(DateHelper.formatDateTime(new Date()));
        chuguanParam.setKuanXiang(isPrePay(payType) ? "已收" : "未收");
        chuguanParam.setMoneyn(1);
        chuguanParam.setQianZi(0);// 内配业务传具体值，其他业务传值0
        /*chuguanParam.setYunFei(order.getTotalFee());*/
        chuguanParam.setYouHui(orderBank.getDiscount());
        chuguanParam.setQiTaFeiYong(qiTaFeiYong);
        chuguanParam.setPeiHuoDanHao(0);
        chuguanParam.setNationalTypeId(typeId.getType());
        return chuguanParam;
    }

    // TODO: 2022/7/13
    private ChuguanDetailVo getChuguanDetailVo(Long skuId,BigDecimal zongJinE,Integer quantity,BigDecimal jiaGe,String profitLossId,List<String> distriOrderIds,String dimValue) {
        ChuguanDetailVo chuguanDetailVo = new ChuguanDetailVo();
        chuguanDetailVo.setSkuId(skuId);
        chuguanDetailVo.setNum(quantity);
        chuguanDetailVo.setZongJinE(zongJinE);
        chuguanDetailVo.setJiaGe(jiaGe);
        chuguanDetailVo.setYn(1);
        chuguanDetailVo.setCaiGouRenNo(skuId);
        chuguanDetailVo.setBiLv(1);
        chuguanDetailVo.setProfitLossId(profitLossId);
        chuguanDetailVo.setDistriOrderIds(distriOrderIds);
        chuguanDetailVo.setPurchaseChannel(dimValue);
        Map<String, String> paramExtMap = new HashMap<>();
        paramExtMap.put("skuLevelData","1");
        chuguanDetailVo.setParamExtMap(paramExtMap);
        return chuguanDetailVo;
    }


    private List<ChuguanDetailVo> getChuguanDetailVoList(List<Product> products,Integer paidType) {
        List<ChuguanDetailVo> chuguanDetailVoList = new ArrayList<>();
        for(Product item:products){
            ChuguanDetailVo chuguanDetailVo = new ChuguanDetailVo();
            chuguanDetailVo.setSkuId(new Long(item.getProductId()));
            BigDecimal zongJinE = item.getPrice().multiply(new BigDecimal(item.getQuantity()));
            if(paidType == null){//out
                chuguanDetailVo.setNum(item.getQuantity());
                chuguanDetailVo.setZongJinE(zongJinE);
            }else{//in
                chuguanDetailVo.setNum(this.isPrePay(paidType) ? item.getQuantity() : this.negate(item.getQuantity()));
                chuguanDetailVo.setZongJinE(this.isPrePay(paidType) ? zongJinE : zongJinE.negate());
            }
            chuguanDetailVo.setJiaGe(item.getPrice());
            chuguanDetailVo.setYn(1);
            chuguanDetailVo.setCaiGouRenNo(item.getSkuId());
            chuguanDetailVo.setBiLv(1);
            if(item.getProfitChannelId()!=null){
                chuguanDetailVo.setProfitLossId(String.valueOf(item.getProfitChannelId()));
            }
            Map<String, String> paramExtMap = new HashMap<>();
            paramExtMap.put("skuLevelData","1");
            chuguanDetailVo.setParamExtMap(paramExtMap);
            chuguanDetailVoList.add(chuguanDetailVo);
        }
        return chuguanDetailVoList;
    }

    private List<ChuguanDetailVo> getInChuguanDetailVoList(List<Product> products,Integer paidType){
        return getChuguanDetailVoList(products,paidType);
    }

    private List<ChuguanDetailVo> getOutChuguanDetailVoList(List<Product> products){
        return getChuguanDetailVoList(products,null);
    }

    public String stockMessage(InternationOrderDto order, List<Product> products, Integer stockType, Integer payType) {
		StringBuilder message = new StringBuilder();

		message.append("<ChuguanMsg><ChuguanInfo><RfType>16</RfType><Qtfs>5</Qtfs><Flag>1</Flag>"
				+ "<JingBan>ql.dms</JingBan><SysTag>ql.dms</SysTag><LaiYuan>ql.dms</LaiYuan><Ydanhao>0</Ydanhao><RfKey>"
				+ order.getId() + "</RfKey><SubType>" + stockType + "</SubType><RfNo>" + order.getId()
				+ "</RfNo><RfDate>" + DateHelper.formatDateTime(new Date()) + "</RfDate><ChuRu>"
				+ this.getStockChuRu(stockType) + "</ChuRu><LocalId>" + order.getDeliveryCenterID() + "</LocalId><SId>"
				+ order.getStoreId() + "</SId></ChuguanInfo><QingdanInfos>");

		//出管信息增加skuid字段
		for (Product product : products) {
			message.append("<QingdanInfo><Wid>" + product.getProductId() + "</Wid><Ware>" + product.getName()
					+ "</Ware><Num>" + this.getQuantity(stockType, payType, product.getQuantity())
					+ "</Num></QingdanInfo>");
		}

		message.append("</QingdanInfos></ChuguanMsg>");

		return message.toString();
	}

	private Integer getQuantity(Integer stockType, Integer payType, Integer quantity) {
		if (STOCK_TYPE_1601.equals(stockType) && !this.isPrePay(payType)) {
			return this.negate(quantity);
		}

		return quantity;
	}

	private Integer getStockChuRu(Integer stockType) {
		Assert.notNull(stockType, "stockType must not be null");

		if (STOCK_TYPE_1601.equals(stockType) || STOCK_TYPE_1602.equals(stockType)) {
			return 2;
		} else if (STOCK_TYPE_1603.equals(stockType)) {
			return 1;
		} else {
			return -1;
		}
	}
	
	private Integer negate(Integer num) {
		return num == null ? 0 : 0 - num;
	}
	
	/**
	 * 根据出管判断先款还是后款使用方法：
	 * 入参StockParamter 对象的Orderid 属性赋值要查询的订单号 该方法会返回这个订单号的所有出管记录，然后请在返回的出管记录中匹配：
	 * 如果 churu=‘出库’ 且 feilei=‘放货’ 且 qite=0 即为后款订单（先货后款）， 如果 churu=‘出库’ 且
	 * feilei=‘销售’ 即为先款订单
	 * 
	 * @return
	 * @throws StockCallPayTypeException 
	 */
	public Integer getPayType(KuGuanDomain domain) throws StockCallPayTypeException {
		Integer result = PAY_TYPE_UNKNOWN;
		String waybillCode = null;
		String churu = null;
		String feifei = null;
		BigDecimal qite = null;
		
		if (domain != null) {// 校验参数,如果为空则不能判断是什么类型的
			waybillCode = domain.getWaybillCode();
			churu = domain.getLblWay();
			feifei = domain.getLblType();
			qite = NumberHelper.parseBigDecimalNullToZero(domain.getLblOther());
			if (ConstantEnums.ChuGuanChuruId.OUT_KU.getText().equals(churu)
                    && ConstantEnums.ChuGuanFenLei.PUT_GOODS.getText().equals(feifei) && (qite != null && new BigDecimal(0).compareTo(qite) == 0)) {
				result = PAY_TYPE_POST;
			} else if (ConstantEnums.ChuGuanChuruId.OUT_KU.getText().equals(churu) && ConstantEnums.ChuGuanFenLei.SALE.getText().equals(feifei)) {
				result = PAY_TYPE_PRE;
			}
			// 异常情况日志记录方便定位问题
			log.info("getPayType waybillCode:{}detail: churu:{},feifei:{},qite:{}" ,waybillCode,churu,feifei,qite);
		}
		
		if (result.equals(PAY_TYPE_UNKNOWN)) {
			// 异常情况日志记录方便定位问题
            log.warn("getPayType waybillCode:{}detail: churu:{},feifei:{},qite:{}" ,waybillCode,churu,feifei,qite);
            log.warn("不能判断订单是先款还是后款:{} " , waybillCode);
			throw new StockCallPayTypeException("不能判断订单是先款还是后款: " + waybillCode);
		}
		return result;
	}

	private boolean isPrePay(Integer payType) {
		return payType.equals(PAY_TYPE_PRE);
	}

	/**
	 *
	 * @return
	 */
	private String getKPJGID(InternationOrderDto order){
		try{
			SendpayOrdertype queryParam = new SendpayOrdertype();
			queryParam.setDcId( order.getDeliveryCenterID());
			queryParam.setWareERPId(order.getStoreId());
			queryParam.setOrderType(order.getOrderType());
			queryParam.setOther1(String.valueOf(order.getProvince()));
			queryParam.setOther2(String.valueOf(order.getCity()));
			queryParam.setSendPay(order.getSendPay());
			Organization organization = searchOrganizationOtherManager.findFinancialOrg(queryParam);
			if(organization!=null && organization.getOrgId()!=null){
				return organization.getOrgId().toString();
			}
		}catch (Exception e){
			log.error("获取开票机构ID异常:{}",JsonHelper.toJson(order),e);
		}
		return StringUtils.EMPTY;
	}

    /**
     * 判断是否是新电信行业合约订单
     * 【waybill_type=0 && sendpay 252位=3,4,5,6,7】
     * @param waybillType
     * @param sendPay
     * @return
     */
	private boolean isTelecomOrder(Integer waybillType,String sendPay){

	    if(waybillType == null || StringUtils.isBlank(sendPay) || sendPay.length() <= TELECOM_WAYBILL_SEND_PAY_INDEX){
	        return false;
        }

        String sendPay252 = String.valueOf(sendPay.charAt(TELECOM_WAYBILL_SEND_PAY_INDEX));

	    return TELECOM_WAYBILL_TYPE.equals(waybillType) && TELECOM_WAYBILL_SEND_PAYS.contains(sendPay252);

    }

}
