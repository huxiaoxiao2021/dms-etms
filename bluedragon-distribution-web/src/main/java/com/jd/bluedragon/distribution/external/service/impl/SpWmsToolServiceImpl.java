package com.jd.bluedragon.distribution.external.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.busineCode.sendCode.service.SendCodeService;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeAttributeKey;
import com.jd.bluedragon.distribution.external.domain.SpWmsCreateInProduct;
import com.jd.bluedragon.distribution.external.domain.SpWmsCreateInRequest;
import com.jd.bluedragon.distribution.external.service.SpWmsToolService;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.product.domain.Product;
import com.jd.bluedragon.distribution.reverse.domain.ReverseSendSpwmsOrder;
import com.jd.bluedragon.distribution.reverse.domain.ReverseSpare;
import com.jd.bluedragon.distribution.reverse.domain.WaybillOrderCodeDto;
import com.jd.bluedragon.distribution.reverse.service.ReverseReceiveNotifyStockService;
import com.jd.bluedragon.distribution.reverse.service.ReverseSendServiceImpl;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.spare.domain.Spare;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.rd.unpack.jsf.distributionReceive.in.InOrderDto;
import com.jd.rd.unpack.jsf.distributionReceive.in.OrderDetailDto;
import com.jd.rd.unpack.jsf.distributionReceive.result.MessageResult;
import com.jd.rd.unpack.jsf.distributionReceive.service.DistributionReceiveJsfService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.jd.bluedragon.distribution.businessCode.BusinessCodeFromSourceEnum.JY_APP;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2023/2/17
 * @Description: 备件库小工具
 */
@Service("spWmsToolService")
public class SpWmsToolServiceImpl implements SpWmsToolService {


    private static Logger log = LoggerFactory.getLogger(SpWmsToolServiceImpl.class);


    @Autowired
    private WaybillCommonService waybillCommonService;
    @Autowired
    private DistributionReceiveJsfService distributionReceiveJsfService;//备件库配送入

    @Autowired
    WaybillQueryManager waybillQueryManager;

    @Autowired
    SendCodeService sendCodeService;

    @Autowired
    BaseMajorManager baseMajorManager;

    @Autowired
    private ReverseReceiveNotifyStockService reverseReceiveNotifyStockService;

    /**
     * 虚拟操作创建备件库入库单
     *
     * @param request
     * @return
     */
    @Override
    @JProfiler(jKey = "com.jd.bluedragon.distribution.external.service.SpWmsToolService.virtualSpWmsCreateIn",jAppName=Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<Boolean> virtualSpWmsCreateIn(SpWmsCreateInRequest request) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        result.setData(Boolean.TRUE);
        log.info("virtualSpWmsCreateIn request:{}",JsonHelper.toJson(request));
        if (request.getRdWmsCode() == null || request.getRdWmsCode() <= 0) {
            //默认设置成大兴退货组
            request.setRdWmsCode(733578);
        }

        //参数校验
        if (request == null ||
                request.getRdWmsCode() == null ||
                request.getRdWmsCode() <= 0 ||
                request.getSpWmsCode() == null ||
                request.getSpWmsCode() <= 0 ||
                request.getOpeTime() == null ||
                StringUtils.isBlank(request.getWaybillCode()) ||
                CollectionUtils.isEmpty(request.getSpareCodes())) {
            result.setData(Boolean.FALSE);
            result.customMessage(InvokeResult.SERVICE_ERROR_CODE, InvokeResult.PARAMETER_ERROR_MESSAGE);
            return result;
        }

        try {
            //创建入库单
            if (!sendReverseMessageToSpwms(request)) {
                result.setData(Boolean.FALSE);
                result.customMessage(InvokeResult.SERVICE_ERROR_CODE, "入库单创建失败!");
                return result;
            }
            //写备件库收货全程跟踪 暂时不写了

            //写出管
            String orderId = waybillQueryManager.getOrderCodeByWaybillCode(request.getWaybillCode(), true);
            if(!StringUtils.isBlank(orderId)){
                reverseReceiveNotifyStockService.nodifyStock(Long.valueOf(orderId));
            }
        } catch (Exception e) {
            log.error("virtualSpWmsCreateIn error! {}", JsonHelper.toJson(request), e);
            result.setData(Boolean.FALSE);
            result.customMessage(InvokeResult.SERVICE_ERROR_CODE, InvokeResult.SERVICE_ERROR_MESSAGE);
            return result;
        }finally {
            log.info("virtualSpWmsCreateIn req:{},resp{}",JsonHelper.toJson(request),JsonHelper.toJson(result));
        }
        return result;
    }

    /**
     * 批量插入
     * @param requests
     * @return
     */
    @Override
    public InvokeResult<List<String>> batchVirtualSpWmsCreateIn(List<SpWmsCreateInRequest> requests) {
        InvokeResult<List<String>> result = new InvokeResult<>();
        List<String> errorList = new ArrayList<>();
        result.setData(errorList);
        if(CollectionUtils.isEmpty(requests)){
            return result;
        }
        for(SpWmsCreateInRequest request :requests ){
            try {
                //一条失败跳过 记录
                InvokeResult<Boolean> invokeResult =  virtualSpWmsCreateIn(request);
                if(!invokeResult.codeSuccess()){
                    errorList.add(request.getWaybillCode());
                    result.setCode(InvokeResult.SERVICE_FAIL_CODE);
                    log.warn("batchVirtualSpWmsCreateIn one waybill {} fail !,msg:{} ",request.getWaybillCode(),invokeResult.getMessage());
                }
            }catch (Exception e){
                errorList.add(request.getWaybillCode());
                log.error("batchVirtualSpWmsCreateIn one waybill {} error ! ",request.getWaybillCode(),e);
            }

        }
        return result;
    }


    private String makeSendCode(SpWmsCreateInRequest request) {
        Map<BusinessCodeAttributeKey.SendCodeAttributeKeyEnum, String> attributeKeyEnumObjectMap = new HashMap<>();
        attributeKeyEnumObjectMap.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.from_site_code, String.valueOf(request.getRdWmsCode()));
        attributeKeyEnumObjectMap.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.to_site_code, String.valueOf(request.getSpWmsCode()));
        return sendCodeService.createSendCode(attributeKeyEnumObjectMap, JY_APP, "sys");
    }


    private List<SendDetail> makeSendDetail(SpWmsCreateInRequest request, String sendCode) {
        List<SendDetail> sendDetails = new ArrayList<>();

        for (SpWmsCreateInProduct product : request.getSpareCodes()) {

            SendDetail sendDetail = new SendDetail();
            sendDetail.setSendType(20);
            sendDetail.setBoxCode(product.getSpareCode());
            sendDetail.setPackageBarcode(product.getSpareCode());
            sendDetail.setWaybillCode(request.getWaybillCode());
            sendDetail.setSendCode(sendCode);
            sendDetail.setCreateTime(request.getOpeTime());
            sendDetail.setUpdateTime(request.getOpeTime());
            sendDetail.setCreateUser("sys");
            sendDetail.setCreateUserCode(1);
            sendDetail.setCreateSiteCode(request.getRdWmsCode());
            sendDetail.setReceiveSiteCode(request.getSpWmsCode());
            sendDetail.setIsCancel(0);
            sendDetail.setIsLoss(0);
            sendDetails.add(sendDetail);
        }


        return sendDetails;

    }

    private Map<String, String> makeSparesMap(SpWmsCreateInRequest request, List<Product> products) {
        Map<String, String> map = new HashMap<>();
        for (SpWmsCreateInProduct product : request.getSpareCodes()) {
            map.put(product.getProductCode(), product.getSpareCode());
        }
        //多个商品时动态如果没有传入逗号则动态生成备件条码
        /*for(Product product : products){
            if(product.getQuantity() > 1){
                if(map.get(product.getProductId()).indexOf(',') != -1){
                    continue;
                }else{
                    //不用他给的数据了。直接自己创建
                    if()
                }
            }
        }*/
        return map;
    }

    private String makeSpare(){
        String Pre = "YCPS";
        Random ro = new Random();
        Date now = new Date();
        Pre += DateUtil.formatDate(now, "yyMMddssSSS") + ro.nextInt(9999);
        return Pre;
    }

    private boolean sendReverseMessageToSpwms(SpWmsCreateInRequest request) throws Exception {


        Integer siteType = 0;
        Integer baseOrgId = 0;
        String baseStoreId = "";


        BaseStaffSiteOrgDto bDto = null;

        bDto = this.baseMajorManager.getBaseSiteBySiteId(request.getSpWmsCode());

        if (null != bDto) {
            siteType = bDto.getSiteType();
            baseOrgId = bDto.getOrgId();
            baseStoreId = bDto.getCustomCode();
            this.log.debug("站点类型为:{}", siteType);
            this.log.debug("baseOrgId:{}", baseOrgId);//区域号
            this.log.debug("baseStoreId:{}", baseStoreId);//仓储号
        } else {
            log.error("sendReverseMessageToSpwms getBaseSiteBySiteId error {}", JsonHelper.toJson(request));
            return false;
        }
        //如果没指定批次号则自动创建
        String sendCode = request.getSendCode();
        if(StringUtils.isBlank(sendCode)){
            sendCode = makeSendCode(request);
        }
        List<SendDetail> sendDetails = makeSendDetail(request, sendCode);

        // 增加判断d表中数据为逆向数据
        Map<String, SendDetail> sendDetailMap = new ConcurrentHashMap<String, SendDetail>();
        for (SendDetail aSendDetail : sendDetails) {
            ReverseSendServiceImpl.addMapSpwms(sendDetailMap, aSendDetail.getWaybillCode(), aSendDetail);
        }

        Iterator<Map.Entry<String, SendDetail>> iterator = sendDetailMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            String waybillCode = (String) entry.getKey();
            SendDetail sendDetail = (SendDetail) entry.getValue();
            try {
                Waybill waybill = this.getWaybill(waybillCode);
                if (null == waybill) {
                    this.log.warn("{}||ReverseSendServiceImpl -- > sendReverseMessageToSpwms 获取订单数据失败", waybillCode);
                    continue;
                }

                List<ReverseSpare> sendReverseSpare = new ArrayList<ReverseSpare>();
                // 如果D表的包裹号不为备件库条码时，需要新增spare表数据并关联备件库条码
                //if (!WaybillUtil.isReverseSpareCode(sendDetail.getPackageBarcode())) {
                List<ReverseSpare> reverseSpare = null;
                List<Product> products = waybill.getProList();
                if (products == null || products.size() == 0) {
                    this.log.warn("{}||ReverseSendServiceImpl -- > sendReverseMessageToSpwms 获取商品明细为空", waybillCode);
                    /**
                     * products在查运单信息时默认给力空对象，这里避免上游逻辑更改，判断为null时赋一个空List，避免报空指针，此处更改不影响后续逻辑
                     */
                    if (products == null) {
                        products = new ArrayList<Product>();
                    }
                }
                //List<Spare> spares = this.getSpare(baseOrgId, Integer.parseInt(baseStoreId), sendDetail, products);
                Map<String, String> sparesMap = makeSparesMap(request,products);

                if (sparesMap == null || sparesMap.isEmpty()) {
                    this.log.warn("{}||ReverseSendServiceImpl -- > sendReverseMessageToSpwms,获取备件条码为空", waybillCode);
                    continue;
                }
                int spare_num = 0;

                List<ReverseSendSpwmsOrder> spwmsOrders = new ArrayList<ReverseSendSpwmsOrder>();
                for (int i = 0; i < products.size(); i++) {
                    for (int j = 0; j < products.get(i).getQuantity(); j++) {
                        ReverseSendSpwmsOrder spwmsOrder = new ReverseSendSpwmsOrder();
                        spwmsOrder.setWaybillCode(waybillCode);
                        spwmsOrder.setSendCode(sendDetail.getSendCode());
                        if(j > 0){
                            //存在多个 自己生成备件条码
                            spwmsOrder.setSpareCode(makeSpare());
                        }else{
                            //一单一品，一个备件条码
                            spwmsOrder.setSpareCode(sparesMap.get(products.get(i).getProductId()));

                        }
                        spwmsOrder.setProductId(products.get(i).getProductId());
                        spwmsOrder.setProductName(products.get(i).getName());
                        spwmsOrder.setProductPrice(products.get(i).getPrice() == null ? Double.valueOf(0) :products.get(i).getPrice().doubleValue());
                        spwmsOrders.add(spwmsOrder);
                    }
                }

                List<ReverseSpare> reverseSpares = this.setReverseSpares(sendDetail, spwmsOrders);
                if (null == reverseSpare || reverseSpare.size() == 0) {
                    sendReverseSpare = reverseSpares;
                } else {
                    for (ReverseSpare rs1 : reverseSpare) {
                        for (ReverseSpare rs2 : reverseSpares) {
                            if (rs1.getWaybillCode().equals(rs2.getWaybillCode())
                                    && rs1.getProductId().equals(rs2.getProductId())) {
                                if (null != rs1.getSpareTranCode()) {
                                    reverseSpares.remove(rs2);
                                    break;
                                } else {
                                    String temp = rs2.getSpareCode();
                                    org.apache.commons.beanutils.BeanUtils.copyProperties(rs2, rs1);
                                    rs2.setSpareCode(temp);
                                    break;
                                }
                            }
                        }
                    }

                    sendReverseSpare = reverseSpares;
                    this.log.info("{}-处理备件库退货,List<ReverseSpare>={}", waybillCode, sendReverseSpare.size());
                }
                /*} else {
                    sendReverseSpare = this.reverseSpareService.queryByWayBillCode(waybillCode,
                        sendDetail.getSendCode());
                    if (null == sendReverseSpare || sendReverseSpare.size() == 0) {
                        this.log.warn("spwms处理备件库退货出错waybillCode={}", waybillCode);
                        continue;
                    }
                }*/
                dealWithWaybillCode(sendDetails);

                String waybillSendCode = sendCode + "-" + waybillCode;
                // 开始包装发货对象 (改用unpack包下的实体类)
                InOrderDto order = new InOrderDto();
                order.setSourceId(sendDetail.getCreateSiteCode());
                order.setFromPin(sendDetail.getCreateUserCode().toString());
                order.setFromName(sendDetail.getCreateUser());
                order.setCreateReason(sendDetail.getSpareReason());
                order.setOrderId(Long.parseLong(waybill.getOrderId()));
                order.setAimOrgId(baseOrgId);
                // 逆向退备件库报文兼容性更改，唯一标识，用于备件库新老系统兼容并行
                order.setWaybillSendCode(waybillSendCode);
                order.setWaybillSign(waybill.getWaybillSign());//SOP逆向单据拒收外呼需求,需要传waybillsign

                try {
                    order.setAimStoreId(Integer.parseInt(baseStoreId));
                } catch (Exception e1) {
                    this.log.error("处理备件库退货,baseStoreId转换出错:{}", baseStoreId);
                    continue;
                }

                if (waybill.getType() >= 21 && waybill.getType() <= 25) {
                    // 200pop订单
                    order.setOrderType(200);
                } else if (waybill.getType() == 2) {
                    // 100夺宝岛订单
                    order.setOrderType(100);
                } else {
                    // 其他值其他订单
                    order.setOrderType(300);
                }

                // 判断是否奢侈品订单
                // 还需要再次确认运单返回的结果
                List<OrderDetailDto> de = new ArrayList<OrderDetailDto>();
                for (ReverseSpare reverseSpare2 : sendReverseSpare) {
                    OrderDetailDto orderDetail = new OrderDetailDto();
                    orderDetail.setWareId(Long.parseLong(reverseSpare2.getProductId()));
                    orderDetail.setWareName(reverseSpare2.getProductName());
                    orderDetail.setLossType(4); //配送损 默认

                    if (reverseSpare2.getArrtCode3() == null || reverseSpare2.getArrtCode3().intValue() == 0) {
                        orderDetail.setMainWareFunctionType(1024);
                    } else {
                        orderDetail.setMainWareFunctionType(Integer.parseInt(ReverseSendServiceImpl.tempMap.get(reverseSpare2
                                .getArrtCode3().toString())));
                    }

                    if (reverseSpare2.getArrtCode2() == null || reverseSpare2.getArrtCode2().intValue() == 0) {
                        orderDetail.setMainWareAppearance(2);
                    } else {
                        orderDetail.setMainWareAppearance(Integer.parseInt(ReverseSendServiceImpl.tempMap.get(reverseSpare2
                                .getArrtCode2().toString())));
                    }

                    if (reverseSpare2.getArrtCode1() == null || reverseSpare2.getArrtCode1().intValue() == 0) {
                        orderDetail.setMainWarePackage(1);
                    } else {
                        orderDetail.setMainWarePackage(Integer.parseInt(ReverseSendServiceImpl.tempMap.get(reverseSpare2.getArrtCode1()
                                .toString())));
                    }

                    if (reverseSpare2.getArrtCode4() == null || reverseSpare2.getArrtCode4().intValue() == 0) {
                        orderDetail.setAttachments(8);
                    } else {
                        orderDetail.setAttachments(Integer.parseInt(ReverseSendServiceImpl.tempMap.get(reverseSpare2.getArrtCode4()
                                .toString())));
                    }

                    WChoice wChoice = new WChoice();
                    wChoice.setQueryWaybillC(true);
                    wChoice.setQueryWaybillE(true);
                    wChoice.setQueryWaybillM(true);
                    try {
                        Integer consignerId = this.waybillQueryManager.getDataByChoice(waybillCode, wChoice).getData()
                                .getWaybill().getConsignerId();

                        if (null != consignerId) {
                            orderDetail.setSupplierId(consignerId.toString());
                        } else {
                            orderDetail.setSupplierId("0");
                        }
                    } catch (Exception e) {
                        this.log.error("{}-处理备件库退货,waybillQueryWSProxy出错", waybillCode, e);
                        orderDetail.setSupplierId("0");
                    }
                    orderDetail.setPartCode(reverseSpare2.getSpareCode());

                    this.log.info("{}-处理备件库退货,reverseSpare.getSpareCode={}", waybillCode, reverseSpare2.getSpareCode());
                    orderDetail.setIsLuxury(this.isLuxury(waybill.getSendPay()));
                    orderDetail.setSerialNo(null);
                    de.add(orderDetail);
                }
                order.setOrderDetail(de);

                order.setFlashOrgId(waybill.getOrgId());
                order.setFlashStoreId(waybill.getStoreId());

                //调用备货仓 jsf 接口 distributionReceiveJsfService
                MessageResult msgResult = distributionReceiveJsfService.createIn(order);
                //记录日志
                log.info("虚拟退备件库操作，入参：{}出参：{}", JsonHelper.toJson(order), JsonHelper.toJson(msgResult));

                if (null == msgResult) {
                    this.log.warn("distributionReceiveJsfService接口返回 msgResult 为空");
                    return false;
                }
                //getReturnFlag ：100 - 成功 ，200 - 异常
                if (null != msgResult.getReturnFlag() && msgResult.getReturnFlag() != 100) {
                    this.log.warn("msgResult.getReturnFlag() != 100 [{}]", msgResult.getReturnFlag());
                    return false;
                }
                String transferId = StringHelper.getStringValue(msgResult.getTransferId());
                this.log.info("{}-返回 transferId 结果:{}", waybillCode, transferId);
                if (StringHelper.isEmpty(transferId)) {
                    this.log.warn("{}-spwms发货备件库失败，返回 ErrorMessage 结果:{}", waybillCode, msgResult.getErrorMessage());
                    return false;
                }

            } catch (RuntimeException e) {
                this.log.error("运单号=[{}]send_d_id=[{}]send_code[{}][spwms发货备件库失败]",
                        waybillCode, sendDetail.getSendDId(), sendDetail.getSendCode(), e);
                return false;
                //throw e; 异常失败后也无人感知，没有重置处理过任务，阻碍后续任务执行，决定吞掉异常只记录日志
            } catch (Exception ex) {
                this.log.error("运单号=[{}]send_d_id=[{}]send_code[{}][spwms发货备件库失败]",
                        waybillCode, sendDetail.getSendDId(), sendDetail.getSendCode(), ex);
                return false;
            }
        }
        return true;
    }


    private Boolean isLuxury(String sendPay) {
        if (StringHelper.isEmpty(sendPay)) {
            return false;
        }

        String identifier = sendPay.substring(19, 20);
        return "1".equals(identifier) ? true : false;
    }


    private Map<String, WaybillOrderCodeDto> dealWithWaybillCode(List<SendDetail> sendList) {
        Map<String, WaybillOrderCodeDto> operCodeMap = new HashMap<String, WaybillOrderCodeDto>();

        for (SendDetail sendDetail : sendList) {
            String operCode = sendDetail.getWaybillCode();
            WaybillOrderCodeDto waybillOrderCodeDto = new WaybillOrderCodeDto();
            //兼容T单
            if (sendDetail.getWaybillCode().startsWith(Constants.T_WAYBILL)) {
                sendDetail.setWaybillCode(sendDetail.getWaybillCode().replaceFirst(Constants.T_WAYBILL, ""));
                waybillOrderCodeDto.setOrderId(sendDetail.getWaybillCode());
                waybillOrderCodeDto.setNewWaybillCode(operCode);
                waybillOrderCodeDto.setOldWaybillCode(sendDetail.getWaybillCode());
                operCodeMap.put(sendDetail.getWaybillCode(), waybillOrderCodeDto);//保存原操作单号

                if (sendDetail.getPackageBarcode().startsWith(Constants.T_WAYBILL))
                    sendDetail.setPackageBarcode(sendDetail.getPackageBarcode().replaceFirst(Constants.T_WAYBILL, ""));
            } else if (SerialRuleUtil.isMatchNumeric(sendDetail.getWaybillCode())) {
                //兼容 原自营订单返仓场景
                waybillOrderCodeDto.setNewWaybillCode(operCode);
                waybillOrderCodeDto.setOrderId(operCode);
                waybillOrderCodeDto.setOldWaybillCode(operCode);
                operCodeMap.put(sendDetail.getWaybillCode(), waybillOrderCodeDto);//保存原操作单号
            } else {
                //获取老运单号信息
                BaseEntity<com.jd.etms.waybill.domain.Waybill> oldWaybill = waybillQueryManager.getWaybillByReturnWaybillCode(operCode);
                if (oldWaybill != null && oldWaybill.getData() != null && StringUtils.isNotBlank(oldWaybill.getData().getWaybillCode())) {
                    //存入老运单号
                    String oldWaybillCode = oldWaybill.getData().getWaybillCode();
                    waybillOrderCodeDto.setOldWaybillCode(oldWaybillCode);
                    waybillOrderCodeDto.setNewWaybillCode(operCode);
                    waybillOrderCodeDto.setOrderId(oldWaybill.getData().getVendorId());
                    operCodeMap.put(operCode, waybillOrderCodeDto);//保存原操作单号
                } else {
                    waybillOrderCodeDto.setOldWaybillCode(operCode);
                    waybillOrderCodeDto.setNewWaybillCode(operCode);
                    Waybill waybill = waybillCommonService.findByWaybillCode(operCode);
                    if (waybill != null && StringUtils.isNotBlank(waybill.getOrderId())) {
                        waybillOrderCodeDto.setOrderId(waybill.getOrderId());
                    }
                    operCodeMap.put(operCode, waybillOrderCodeDto);//如果现场使用老单返仓场景则直接存入即可
                }
            }


        }
        return operCodeMap;
    }


    private Waybill getWaybill(String waybillCode) {
        Waybill waybill = this.waybillCommonService.findWaybillAndGoods(waybillCode);
        if (null == waybill) {
            this.log.warn("{}-ReverseSendServiceImpl --> getWaybill 获取运单数据失败", waybillCode);
        }
        return waybill;
    }


    private List<ReverseSpare> setReverseSpares(SendDetail sendDetail, List<ReverseSendSpwmsOrder> spwmsOrders) {
        List<ReverseSpare> reverseSpares = new ArrayList<ReverseSpare>();
        for (ReverseSendSpwmsOrder reverseSendSpwmsOrder : spwmsOrders) {
            ReverseSpare aReverseSpare = new ReverseSpare();
            aReverseSpare.setSpareCode(reverseSendSpwmsOrder.getSpareCode());
            aReverseSpare.setSendCode(reverseSendSpwmsOrder.getSendCode());
            aReverseSpare.setWaybillCode(reverseSendSpwmsOrder.getWaybillCode());
            aReverseSpare.setProductId(reverseSendSpwmsOrder.getProductId());
            aReverseSpare.setProductCode(null);
            aReverseSpare.setProductPrice(reverseSendSpwmsOrder.getProductPrice());
            aReverseSpare.setProductName(reverseSendSpwmsOrder.getProductName());

            // 三方七折 根据send_d 新增字段判断
            if (sendDetail.getFeatureType() != null && 2 == sendDetail.getFeatureType().intValue()) {
                aReverseSpare.setArrtCode1(102);
                aReverseSpare.setArrtDesc1("商品外包装：有/非新");
                aReverseSpare.setArrtCode2(201);
                aReverseSpare.setArrtDesc2("主商品外观：好");
                aReverseSpare.setArrtCode3(303);
                aReverseSpare.setArrtDesc3("主商品功能：好");
                aReverseSpare.setArrtCode4(401);
                aReverseSpare.setArrtDesc4("附件情况：完整");
            } else {
                // 按订单分拣商品属性置为默认值
                aReverseSpare.setArrtCode1(101);
                aReverseSpare.setArrtDesc1("商品外包装：新");
                aReverseSpare.setArrtCode2(201);
                aReverseSpare.setArrtDesc2("主商品外观：新");
                aReverseSpare.setArrtCode3(302);
                aReverseSpare.setArrtDesc3("未检测");
                aReverseSpare.setArrtCode4(401);
                aReverseSpare.setArrtDesc4("完整");
            }
            reverseSpares.add(aReverseSpare);
        }
        return reverseSpares;
    }
}
