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
import com.jd.bluedragon.distribution.order.domain.OrderBankResponse;
import com.jd.bluedragon.distribution.order.service.OrderBankService;
import com.jd.bluedragon.distribution.order.ws.OrderWebService;
import com.jd.bluedragon.distribution.product.domain.Product;
import com.jd.bluedragon.distribution.product.service.ProductService;
import com.jd.bluedragon.distribution.reverse.domain.ReceiveRequest;
import com.jd.bluedragon.distribution.reverse.domain.ReverseReceive;
import com.jd.bluedragon.distribution.systemLog.domain.SystemLog;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.ContantsEnum;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.bluedragon.utils.SystemLogContants;
import com.jd.bluedragon.utils.SystemLogUtil;
import com.jd.bluedragon.utils.XmlHelper;
import com.jd.common.util.StringUtils;
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.MessageFormat;
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

	private final Log logger = LogFactory.getLog(this.getClass());

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

    private static final String JING_BAN_SYSCODE = "ql.dms";

    private static final String CHUGUAN_FIELD_QITAFANGSHI = "逆向物流";

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
				this.logger.warn("消息序列化出现异常, 消息：" + message);
			} else if (ReverseReceive.RECEIVE_TYPE_SPWMS.toString().equals(request.getReceiveType())
					&& ReverseReceive.RECEIVE.toString().equals(request.getCanReceive())) {
				return getCompatibleOrderId(request.getOrderId());
			} else {
				this.logger.info("消息来源：" + request.getReceiveType());
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
            logger.info(MessageFormat.format("根据运单号查询的订单号是非数字code[{0}]orderId[{1}]",code,orderId));
            return -1L;
        }
        return Long.valueOf(orderId);
    }


	public Boolean nodifyStock(Long waybillCode) throws Exception {
		this.logger.info("运单号：" + waybillCode);

		if (!NumberHelper.isPositiveNumber(waybillCode)) {
			this.logger.warn("运单号非法, 运单号 为：" + waybillCode);
			return Boolean.TRUE;
		}
		SystemLog sysLog = new SystemLog();//日志对象
		sysLog.setKeyword1(waybillCode.toString());
		sysLog.setType(SystemLogContants.TYPE_REVERSE_STOCK);//设置日志类型
		boolean isOldForNewType = false;
		try{
			Order order = this.orderWebService.getOrder(waybillCode);
			List<Product> products = this.productService.getOrderProducts(waybillCode);
			
			//修改逻辑当order获取不到时，取归档历史信息。
			//原抛异常逻辑if(order==null || products==null) 即有一项为空即抛出，更改后的逻辑等价于if( (order==null&&hisOrder==null) || products==null )
			if (products.size() == 0) {
				this.logger.warn("无商品信息!");
				sysLog.setContent("无商品信息");
				throw new OrderCallTimeoutException("order has no products.");
			}else if(order == null){//为空时，取一下历史的订单信息
				this.logger.info("订单可能转历史，获取历史订单, 运单号 为：" + waybillCode);
				jd.oom.client.orderfile.Order hisOrder = this.orderWebService.getHistoryOrder(waybillCode);
				
				if (hisOrder == null) {
					this.logger.warn("运单信息为空!订单号:"+waybillCode);
					sysLog.setContent("运单信息为空");
					throw new OrderCallTimeoutException("order is not exist.");
				}else {//如果历史订单信息不为空，则拷贝属性值
					order = new Order();
					order.setId(hisOrder.getId());
					order.setIdCompanyBranchName(hisOrder.getIdCompanyBranchName());
					order.setIdCompanyBranch(hisOrder.getIdCompanyBranch());
					order.setTotalFee(hisOrder.getTotalFee());
					order.setCustomerName(hisOrder.getCustomerName());
					order.setDeliveryCenterID(hisOrder.getDeliveryCenterID());
					order.setStoreId(hisOrder.getStoreId());
					order.setOrderType(hisOrder.getOrderType());
					
					this.logger.info("历史订单信息orderId："+waybillCode+", IdCompanyBranchName:"+order.getIdCompanyBranchName()+
							",IdCompanyBranch:"+order.getIdCompanyBranch()+",CustomerName:"+order.getCustomerName()+
							",DeliveryCenterID:"+order.getDeliveryCenterID()+",StoreId:"+order.getStoreId()+
							",OrderType:"+order.getOrderType()+",TotalFee:"+order.getTotalFee());
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
	            this.logger.info("原机构名为空，从基础资料重新获得订单"+waybillCode+"机构名 IdCompanyBranchName:"+order.getIdCompanyBranchName());
	        }
			
			sysLog.setKeyword2(String.valueOf(order.getOrderType()));//设置订单的类型
			
			
			//此区域:符合主动推送的条件的单子判断是否推送过,获得支付类型
			KuGuanDomain kuguanDomain = null;
			Integer payType = PAY_TYPE_UNKNOWN;
			if (Waybill.TYPE_GENERAL.equals(order.getOrderType()) || Waybill.TYPE_POP_FBP.equals(order.getOrderType())||needRetunWaybillTypes.contains(Integer.valueOf(order.getOrderType()))){
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
				this.logger.info("运单号：" + waybillCode + ", 不需要回传库存中间件。");
				return Boolean.TRUE;
			}
			
			//开始根据类型的不同推送
			if (Waybill.TYPE_GENERAL.equals(order.getOrderType()) || Waybill.TYPE_POP_FBP.equals(order.getOrderType())) {
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
					this.logger.error("推送UMP发生异常.", e);
				}
				
				this.logger.info("运单号：" + waybillCode + ", 库存中间件返回结果：" + result);
				
				sysLog.setKeyword3("WEBSERVICE");
				sysLog.setKeyword4(result);
				if(result!=0)
					sysLog.setContent("推出管成功!");
				else{
					sysLog.setContent("推出管失败!");
					return Boolean.FALSE;
				}
			} else if (needRetunWaybillTypes.contains(Integer.valueOf(order.getOrderType()))) {
				if (isPrePay(payType)) {

					this.wmsStockChuGuanMQ.send(String.valueOf(waybillCode),this.stockMessage(order, products, STOCK_TYPE_1602, payType));
					this.wmsStockChuGuanMQ.send(String.valueOf(waybillCode),this.stockMessage(order, products, STOCK_TYPE_1603, payType));
				} else {
					this.wmsStockChuGuanMQ.send(String.valueOf(waybillCode),this.stockMessage(order, products, STOCK_TYPE_1601, payType));
					this.wmsStockChuGuanMQ.send(String.valueOf(waybillCode),this.stockMessage(order, products, STOCK_TYPE_1602, payType));
				}
				
				sysLog.setKeyword3("MQ");
				sysLog.setContent("推出管成功!");
			}
			
		}catch(Exception e){
			this.logger.error("运单号：" + waybillCode + ", 推出管失败。", e);
			if(StringHelper.isEmpty(sysLog.getContent())){
				sysLog.setContent(e.getMessage());
			}
			return Boolean.FALSE;
		}finally{
			SystemLogUtil.log(sysLog);
		}
		
		return Boolean.TRUE;
	}

    private boolean isReverseLogistics(KuGuanDomain kuguanDomain) {
        if(CHUGUAN_FIELD_QITAFANGSHI.equals(kuguanDomain.getLblOtherWay())){
            return Boolean.TRUE;
        }
        if(ContantsEnum.ChuGuanTypeId.hasTypeId(kuguanDomain.getTypeId())){
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

    private long insertChuguan(Long waybillCode, boolean isOldForNewType, Order order, List<Product> products, Integer payType,
                               OrderBankResponse orderBank) {
        if(uccPropertyConfiguration.isChuguanNewInterfaceInsertSwitch()){
            return insertNewChuguan(waybillCode,isOldForNewType,order,products,payType,orderBank);
        }
        return insertOldChuguan(waybillCode, isOldForNewType, order, products, payType,orderBank);
    }

    public int insertNewChuguan(Long waybillCode, boolean isOldForNewType,Order order, List<Product> products,
                                  Integer payType,OrderBankResponse orderBank){
        List<ChuguanParam> chuGuanParamList = Lists.newArrayList();// 需要放两个 一个出一个入；他们会根据 typeId 区分

        //入
        ContantsEnum.ChuGuanChuruId chuGuanParam = isPrePay(payType) ? ContantsEnum.ChuGuanChuruId.IN_KU : ContantsEnum.ChuGuanChuruId.OUT_KU;

        ContantsEnum.ChuGuanTypeId inTypeId = isPrePay(payType) ? ContantsEnum.ChuGuanTypeId.REVERSE_LOGISTICS_MONEY_REJECTION :
                ContantsEnum.ChuGuanTypeId.REVERSE_LOGISTICS_MONEY_REJECTION;

        ContantsEnum.ChuGuanFenLei chuGuanFenLei = isPrePay(payType) ? ContantsEnum.ChuGuanFenLei.RETURN_GOODS : ContantsEnum.ChuGuanFenLei.PUT_GOODS;
        BigDecimal zongJinE = isPrePay(payType) ? orderBank.getShouldPay() : orderBank.getShouldPay().negate();
        ChuguanParam inChuguanParam = getChuguanParam(waybillCode,
                getRfid(waybillCode,inTypeId,ContantsEnum.ChuGuanRfId.IN),isOldForNewType, order,  payType, orderBank,
                ContantsEnum.ChuGuanRfType.IN,
                chuGuanParam,
                inTypeId,
                chuGuanFenLei,
                BigDecimal.valueOf(1),
                zongJinE);
        List<ChuguanDetailVo> intChuguanDetailVoList = getInChuguanDetailVoList(products,payType);
        inChuguanParam.setChuguanDetailVoList(intChuguanDetailVoList);

        //出
        ContantsEnum.ChuGuanTypeId outTypeId = ContantsEnum.ChuGuanTypeId.REVERSE_LOGISTICS_OUT;
        ChuguanParam outChuguanParam = getChuguanParam(waybillCode,
                getRfid(waybillCode,outTypeId,ContantsEnum.ChuGuanRfId.OUT),isOldForNewType, order,  payType, orderBank,
                ContantsEnum.ChuGuanRfType.Out,
                ContantsEnum.ChuGuanChuruId.OUT_KU,
                outTypeId,
                ContantsEnum.ChuGuanFenLei.OTHER,
                BigDecimal.valueOf(0),
                orderBank.getShouldPay());
        List<ChuguanDetailVo> outChuguanDetailVoList = getOutChuguanDetailVoList(products);
        outChuguanParam.setChuguanDetailVoList(outChuguanDetailVoList);

        chuGuanParamList.add(inChuguanParam);
        chuGuanParamList.add(outChuguanParam);
        return chuguanExportManager.insertChuguan(chuGuanParamList);
    }

    private String getRfid(Long waybillCode,ContantsEnum.ChuGuanTypeId typeId,ContantsEnum.ChuGuanRfId chuGuanRfId) {
        return JING_BAN_SYSCODE.concat("-").concat(String.valueOf(waybillCode)).concat("-")
                .concat(typeId.getType().toString()).concat("-").concat(chuGuanRfId.getText());
    }

    /**
     * 拼装 写入出管的 参数 ChuguanParam
     * 参考 https://cf.jd.com/pages/viewpage.action?pageId=165577134 新接口
     * 与老接口属性 映射 https://cf.jd.com/pages/viewpage.action?pageId=165578101
     * @return
     */
    private ChuguanParam getChuguanParam(Long waybillCode,String rfid,boolean isOldForNewType, Order order, Integer payType,
                                         OrderBankResponse orderBank, ContantsEnum.ChuGuanRfType rfType, ContantsEnum.ChuGuanChuruId churu, ContantsEnum.ChuGuanTypeId typeId,
                                         ContantsEnum.ChuGuanFenLei fenLei,BigDecimal qiTaFeiYong,
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
            chuguanParam.setQiTaFangShi("trade-in");
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
        chuguanParam.setYunFei(order.getTotalFee());
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


    private long insertOldChuguan(Long waybillCode, boolean isOldForNewType, Order order, List<Product> products,
                                  Integer payType,OrderBankResponse orderBank) {
        long result;
        this.logger.info("运单号：" + waybillCode + ", 使用推库管新接口");
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
        inWmsStock0.setYun(order.getTotalFee());
        inWmsStock0.setYouhui(orderBank.getDiscount());
        inWmsStock0.setQite(BigDecimal.valueOf(1));//与潘文华确认改为1，已不用区分先款先货
        inWmsStock0.setPhdanhao(0);
        inWmsStock0.setCgdanhao(0);

        if(isOldForNewType){
            inWmsStock0.setQtfs("trade-in");
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
        outSpwmsStock0.setYun(order.getTotalFee());
        outSpwmsStock0.setYouhui(orderBank.getDiscount());
        outSpwmsStock0.setQite(BigDecimal.valueOf(0));
        outSpwmsStock0.setPhdanhao(0);
        outSpwmsStock0.setCgdanhao(0);
        if(isOldForNewType){
            outSpwmsStock0.setQtfs("trade-in");
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

    public String stockMessage(Order order, List<Product> products, Integer stockType, Integer payType) {
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
			qite = new BigDecimal(domain.getLblOther());
			if (ContantsEnum.ChuGuanChuruId.OUT_KU.getText().equals(churu)
                    && ContantsEnum.ChuGuanFenLei.PUT_GOODS.getText().equals(feifei) && (new BigDecimal(0).compareTo(qite) == 0)) {
				result = PAY_TYPE_POST;
			} else if (ContantsEnum.ChuGuanChuruId.OUT_KU.getText().equals(churu) && ContantsEnum.ChuGuanFenLei.SALE.getText().equals(feifei)) {
				result = PAY_TYPE_PRE;
			}
			// 异常情况日志记录方便定位问题
			logger.info("getPayType waybillCode:" + waybillCode + "detail: churu: " + churu + ",feifei" + feifei
					+ ",qite" + qite);
		}
		
		if (result.equals(PAY_TYPE_UNKNOWN)) {
			// 异常情况日志记录方便定位问题
			logger.error("getPayType waybillCode:" + waybillCode + "detail: churu: " + churu + ",feifei" + feifei
					+ ",qite" + qite);
			logger.error("不能判断订单是先款还是后款: " + waybillCode);
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
	private String getKPJGID(Order order){
		try{
			SendpayOrdertype queryParam = new SendpayOrdertype();
			queryParam.setDcId( order.getDeliveryCenterID());
			queryParam.setWareERPId(order.getStoreId());
			queryParam.setOrderType(order.getOrderType());
			queryParam.setOther1(String.valueOf(order.getProvince()));
			queryParam.setOther2(String.valueOf(order.getCity()));
			queryParam.setSendPay(order.getSendPay());
			Organization organization = searchOrganizationOtherManager.findFinancialOrg(queryParam);
			logger.info("getKPJGID"+JsonHelper.toJson(queryParam)+"|"+JsonHelper.toJson(organization));
			if(organization!=null && organization.getOrgId()!=null){
				return organization.getOrgId().toString();
			}
		}catch (Exception e){
			logger.error("获取开票机构ID异常"+JsonHelper.toJson(order),e);
		}
		return StringUtils.EMPTY;
	}
	
}
