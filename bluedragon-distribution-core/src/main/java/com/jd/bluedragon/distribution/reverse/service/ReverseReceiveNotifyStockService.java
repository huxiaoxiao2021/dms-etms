package com.jd.bluedragon.distribution.reverse.service;

import com.google.common.collect.Lists;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.ChuguanExportManager;
import com.jd.bluedragon.core.base.SearchOrganizationOtherManager;
import com.jd.bluedragon.core.base.StockExportManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.exception.OrderCallTimeoutException;
import com.jd.bluedragon.core.exception.StockCallPayTypeException;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.kuguan.domain.KuGuanDomain;

import com.jd.bluedragon.distribution.log.BusinessLogProfilerBuilder;
import com.jd.bluedragon.distribution.order.domain.InternationDetailOrderDto;
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
import com.alibaba.fastjson.JSONObject;
import com.jd.ioms.jsf.export.domain.Order;
import com.jd.ql.basic.domain.BaseOrg;
import com.jd.stock.iwms.export.param.ChuguanParam;
import com.jd.stock.iwms.export.param.StockVOParam;
import com.jd.stock.iwms.export.vo.ChuguanDetailVo;
import com.jd.stock.iwms.export.vo.StockDetailVO;
import com.jd.stock.iwms.export.vo.StockExtVO;
import com.jd.ufo.domain.ufo.Organization;
import com.jd.ufo.domain.ufo.SendpayOrdertype;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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


	public Boolean nodifyStock(Long waybillCode) throws Exception {
        long startTime=new Date().getTime();

        this.log.debug("运单号：{}" , waybillCode);

		if (!NumberHelper.isPositiveNumber(waybillCode)) {
			this.log.warn("运单号非法, 运单号 为：{}" , waybillCode);
			return Boolean.TRUE;
		}
		SystemLog sysLog = new SystemLog();//日志对象
		sysLog.setKeyword1(waybillCode.toString());
		sysLog.setType(SystemLogContants.TYPE_REVERSE_STOCK);//设置日志类型
		boolean isOldForNewType = false;
		try{
			InternationOrderDto order = orderWebService.getInternationOrder(waybillCode);
			List<Product> products =  productService.getInternationProducts(waybillCode); //订单详情

			//修改逻辑当order获取不到时，取归档历史信息。
			//原抛异常逻辑if(order==null || products==null) 即有一项为空即抛出，更改后的逻辑等价于if( (order==null&&hisOrder==null) || products==null )
			if (products.size() == 0) {
				this.log.warn("无商品信息!");
				sysLog.setContent("无商品信息");
				throw new OrderCallTimeoutException("order has no products.");
			}else if(order == null){//为空时，取一下历史的订单信息
				this.log.debug("订单可能转历史，获取历史订单, 运单号 为：{}" , waybillCode);
				jd.oom.client.orderfile.Order hisOrder = this.orderWebService.getHistoryOrder(waybillCode);
				
				if (hisOrder == null) {
					this.log.warn("运单信息为空!订单号:{}", waybillCode);
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
                        this.log.debug("历史订单信息orderId：{}.order:{}"+waybillCode,JsonHelper.toJson(order));
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
	            this.log.info("原机构名为空，从基础资料重新获得订单{}机构名 IdCompanyBranchName:{}",waybillCode,order.getIdCompanyBranchName());
	        }
			
			sysLog.setKeyword2(String.valueOf(order.getOrderType()));//设置订单的类型
			
			
			//此区域:符合主动推送的条件的单子判断是否推送过,获得支付类型
			KuGuanDomain kuguanDomain = null;
			Integer payType = PAY_TYPE_UNKNOWN;
			if (Waybill.TYPE_GENERAL.equals(order.getOrderType()) || Waybill.TYPE_POP_FBP.equals(order.getOrderType())
                    ||needRetunWaybillTypes.contains(Integer.valueOf(order.getOrderType())) || isTelecomOrder(order.getOrderType(),order.getSendPay())){
				kuguanDomain = queryKuguanDomainByWaybillCode(String.valueOf(waybillCode));
				if(kuguanDomain==null) {
					return Boolean.FALSE;
				}else if(isReverseLogistics(kuguanDomain)){
					return Boolean.TRUE;
				}else{
					payType = getPayType(kuguanDomain);
				}
			} else {
				sysLog.setContent("订单类型不需要回传库存中间件"+order.getOrderType());
				this.log.warn("运单号：{}, 不需要回传库存中间件。",waybillCode);
				return Boolean.TRUE;
			}
			
			//开始根据类型的不同推送
			if (needRetunWaybillTypes.contains(Integer.valueOf(order.getOrderType())) || isTelecomOrder(order.getOrderType(),order.getSendPay())) {
				if (isPrePay(payType)) {

					this.wmsStockChuGuanMQ.send(String.valueOf(waybillCode),this.stockMessage(order, products, STOCK_TYPE_1602, payType));
					this.wmsStockChuGuanMQ.send(String.valueOf(waybillCode),this.stockMessage(order, products, STOCK_TYPE_1603, payType));
				} else {
					this.wmsStockChuGuanMQ.send(String.valueOf(waybillCode),this.stockMessage(order, products, STOCK_TYPE_1601, payType));
					this.wmsStockChuGuanMQ.send(String.valueOf(waybillCode),this.stockMessage(order, products, STOCK_TYPE_1602, payType));
				}
				
				sysLog.setKeyword3("MQ");
                sysLog.setContent("推出管成功!");
			}else if (Waybill.TYPE_GENERAL.equals(order.getOrderType()) || Waybill.TYPE_POP_FBP.equals(order.getOrderType())) {
                long result = 0;
                //判断是否是已旧换新
                isOldForNewType = BusinessHelper.isYJHX(order.getSendPay());
                OrderBankResponse orderBank = orderBankService.getOrderBankResponse(String.valueOf(waybillCode));
                result = insertChuguan(waybillCode, isOldForNewType, order, products, payType,orderBank);
                /** 新逻辑结束 */

                try {
                    //业务流程监控, 备件库埋点
                    Map<String, String> data = new HashMap<String, String>();
                    data.put("orderId", waybillCode.toString());
                    Profiler.bizNode("Reverse_mq_dms2stock", data);
                } catch (Exception e) {
                    this.log.error("推送UMP发生异常.", e);
                }

                this.log.debug("运单号：{}, 库存中间件返回结果：{}" ,waybillCode, result);

                sysLog.setKeyword3("WEBSERVICE");
                sysLog.setKeyword4(result);
                if(result!=0)
                    sysLog.setContent("推出管成功!");
                else{
                    sysLog.setContent("推出管失败!");
                    return Boolean.FALSE;
                }
            }

        }catch(Exception e){
			this.log.error("运单号：{}, 推出管失败。",waybillCode, e);
			if(StringHelper.isEmpty(sysLog.getContent())){
				sysLog.setContent(e.getMessage());
			}
			return Boolean.FALSE;
		}finally{
            long endTime = new Date().getTime();

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

            logEngine.addLog(businessLogProfiler);
            SystemLogUtil.log(sysLog);
		}
		
		return Boolean.TRUE;
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

    private KuGuanDomain queryKuguanDomainByWaybillCode(String waybillCode){
        if(uccPropertyConfiguration.isChuguanNewInterfaceQuerySwitch()){
            return chuguanExportManager.queryByWaybillCode(waybillCode);
        }
        return stockExportManager.queryByWaybillCode(waybillCode);
    }

    private long insertChuguan(Long waybillCode, boolean isOldForNewType, InternationOrderDto order, List<Product> products, Integer payType,
                               OrderBankResponse orderBank) {
        if(uccPropertyConfiguration.isChuguanNewInterfaceInsertSwitch()){
            return insertNewChuguan(waybillCode,isOldForNewType,order,products,payType,orderBank);
        }
        return insertOldChuguan(waybillCode, isOldForNewType, order, products, payType,orderBank);
    }

    public int insertNewChuguan(Long waybillCode, boolean isOldForNewType,InternationOrderDto order, List<Product> products,
                                  Integer payType,OrderBankResponse orderBank){
        List<ChuguanParam> chuGuanParamList = Lists.newArrayList();// 需要放两个 一个出一个入；他们会根据 typeId 区分

        //入
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
        List<ChuguanDetailVo> intChuguanDetailVoList = getInChuguanDetailVoList(products,payType);
        inChuguanParam.setChuguanDetailVoList(intChuguanDetailVoList);

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
        List<ChuguanDetailVo> outChuguanDetailVoList = getOutChuguanDetailVoList(products);
        outChuguanParam.setChuguanDetailVoList(outChuguanDetailVoList);

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
            chuguanDetailVo.setProfitLossId(String.valueOf(item.getProfitChannelId()));
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


    private long insertOldChuguan(Long waybillCode, boolean isOldForNewType, InternationOrderDto order, List<Product> products,
                                  Integer payType,OrderBankResponse orderBank) {
        long result;
        this.log.debug("运单号：{}, 使用推库管新接口",waybillCode);
        /** 新逻辑开始 */
        Date creatDate = new Date();//给扩展属性使用的创建时间
        //设置扩展属性
        StockExtVO stockExtVO0 = new StockExtVO();
        stockExtVO0.setTypeID(isPrePay(payType) ? 1402 : 1401);//保留 原先逻辑
        stockExtVO0.setBusiType("订单退货");
        stockExtVO0.setSubType(isPrePay(payType) ? "先款拒收" : "先货拒收");
        stockExtVO0.setExtType(CHUGUAN_FIELD_QITAFANGSHI);
        stockExtVO0.setBusiNo(String.valueOf(waybillCode));
        stockExtVO0.setSys("ql.dms");
        stockExtVO0.setCreatDate(creatDate);
        stockExtVO0.setAdjust(0);
        stockExtVO0.setVirtual(1);

        StockVOParam inWmsStock0 = new StockVOParam();
        inWmsStock0.setRfId(String.valueOf(waybillCode));
        inWmsStock0.setRfType(6);
        inWmsStock0.setOrderId(waybillCode);
        inWmsStock0.setMoneyn(1);
        inWmsStock0.setCity(DateHelper.formatDateTime(new Date()));
        inWmsStock0.setJigou(order.getIdCompanyBranchName());
        inWmsStock0.setLaiyuan(order.getIdCompanyBranchName());
        inWmsStock0.setShanghai(order.getIdCompanyBranch());
        inWmsStock0.setActor(BigDecimal.valueOf(0));
        inWmsStock0.setQianzi(1);
        /*inWmsStock0.setYun(order.getTotalFee());*/
        inWmsStock0.setYouhui(orderBank.getDiscount());
        inWmsStock0.setQite(BigDecimal.valueOf(1));//与潘文华确认改为1，已不用区分先款先货
        inWmsStock0.setPhdanhao(0);
        inWmsStock0.setCgdanhao(0);

        if(isOldForNewType){
            inWmsStock0.setQtfs(CHUGUAN_FIELD_STILL_NEW_QTFS);
            //开票机构ID
            inWmsStock0.setLaiyuancode(getKPJGID(order));
        }else{
            inWmsStock0.setQtfs(CHUGUAN_FIELD_QITAFANGSHI);
            inWmsStock0.setLaiyuancode(order.getCustomerName());
        }
        inWmsStock0.setOrgId(order.getDeliveryCenterID());
        inWmsStock0.setSid(order.getStoreId());
        inWmsStock0.setChuruId(isPrePay(payType) ? 1 : 2);
        inWmsStock0.setFeileiId(isPrePay(payType) ? 2 : 1);
        inWmsStock0.setKuanx(isPrePay(payType) ? "已收" : "未收");
        inWmsStock0.setZjine(isPrePay(payType) ? orderBank.getShouldPay()
                : orderBank.getShouldPay().negate());
        inWmsStock0.setStockDetails(this.getStockDetailVO(products, payType));
        inWmsStock0.setStockExtVO(stockExtVO0);

        //设置扩展属性
        StockExtVO stockExtVO1 = new StockExtVO();
        stockExtVO1.setTypeID(1403);
        stockExtVO1.setBusiType("大库出备件库");
//						stockExtVO1.setSubType(isPrePay(payType) ? "先款拒收" : "先货拒收");
        stockExtVO1.setExtType(CHUGUAN_FIELD_QITAFANGSHI);
        stockExtVO1.setBusiNo(String.valueOf(waybillCode));
        stockExtVO1.setSys("ql.dms");
        stockExtVO1.setCreatDate(creatDate);
        stockExtVO1.setAdjust(0);
        stockExtVO1.setVirtual(1);

        StockVOParam outSpwmsStock0 = new StockVOParam();
        outSpwmsStock0.setRfId(String.valueOf(waybillCode));
        outSpwmsStock0.setRfType(2);
        outSpwmsStock0.setOrderId(waybillCode);
        outSpwmsStock0.setMoneyn(1);
        outSpwmsStock0.setCity(DateHelper.formatDateTime(new Date()));
        outSpwmsStock0.setJigou(order.getIdCompanyBranchName());
        outSpwmsStock0.setLaiyuan(order.getIdCompanyBranchName());
        outSpwmsStock0.setShanghai(order.getIdCompanyBranch());
        outSpwmsStock0.setActor(BigDecimal.valueOf(0));
        outSpwmsStock0.setQianzi(1);
        /*outSpwmsStock0.setYun(order.getTotalFee());*/
        outSpwmsStock0.setYouhui(orderBank.getDiscount());
        outSpwmsStock0.setQite(BigDecimal.valueOf(0));
        outSpwmsStock0.setPhdanhao(0);
        outSpwmsStock0.setCgdanhao(0);
        if(isOldForNewType){
            outSpwmsStock0.setQtfs(CHUGUAN_FIELD_STILL_NEW_QTFS);
            //开票机构ID
            outSpwmsStock0.setLaiyuancode(getKPJGID(order));
        }else {
            outSpwmsStock0.setLaiyuancode(order.getCustomerName());
            outSpwmsStock0.setQtfs(CHUGUAN_FIELD_QITAFANGSHI);
        }
        outSpwmsStock0.setOrgId(order.getDeliveryCenterID());
        outSpwmsStock0.setSid(order.getStoreId());
        outSpwmsStock0.setChuruId(2);
        outSpwmsStock0.setFeileiId(6);
        outSpwmsStock0.setKuanx("未知 ");
        outSpwmsStock0.setZjine(orderBank.getShouldPay());
        outSpwmsStock0.setStockDetails(this.getStockDetailVO(products));
        outSpwmsStock0.setStockExtVO(stockExtVO0);

        result = stockExportManager.insertStockVirtualIntOut(inWmsStock0, outSpwmsStock0);
        return result;
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

	private List<StockDetailVO> getStockDetailVO(List<Product> products, Integer paidType) {
		List<StockDetailVO> stockDetailVOs = new ArrayList<StockDetailVO>();
		for (Product product : products) {
			StockDetailVO sdVO = new StockDetailVO();
			sdVO.setBilv(1);
			sdVO.setWareId(new Long(product.getProductId()));
			sdVO.setWare(product.getName());
			sdVO.setJiage(product.getPrice());
			sdVO.setNum(this.isPrePay(paidType) ? product.getQuantity() : this.negate(product
					.getQuantity()));
			sdVO.setZjine(this.isPrePay(paidType) ? product.getPrice().multiply(
					new BigDecimal(product.getQuantity())) : product.getPrice()
					.multiply(new BigDecimal(product.getQuantity())).negate());
			//added by zhanglei 增加影分skuid
			sdVO.setCaiguo(product.getSkuId());
			stockDetailVOs.add(sdVO);
		}

		return stockDetailVOs;
	}

	/**
	 * 将商品明细转换
	 * @param products
	 * @return
	 */
	private List<StockDetailVO> getStockDetailVO(List<Product> products) {
		List<StockDetailVO> stockDetailVOs = new ArrayList<StockDetailVO>();
		for (Product product : products) {
			StockDetailVO sdVO = new StockDetailVO();
			sdVO.setBilv(1);
			sdVO.setWareId(new Long(product.getProductId()));
			sdVO.setWare(product.getName());
			sdVO.setJiage(product.getPrice());
			sdVO.setNum(product.getQuantity());
			sdVO.setZjine(product.getPrice().multiply(new BigDecimal(product.getQuantity())));
			//added by zhanglei 增加影分skuId
			sdVO.setCaiguo(product.getSkuId());

			stockDetailVOs.add(sdVO);
		}

		return stockDetailVOs;
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
